package com.zhj.tanhua.circle.service;

import com.aliyun.oss.OSS;
import com.zhj.tanhua.circle.api.CircleApi;
import com.zhj.tanhua.circle.pojo.dto.PublishDto;
import com.zhj.tanhua.circle.pojo.po.Album;
import com.zhj.tanhua.circle.pojo.po.Feed;
import com.zhj.tanhua.circle.pojo.po.Friend;
import com.zhj.tanhua.circle.pojo.po.Publish;
import com.zhj.tanhua.common.enums.FileTypeEnum;
import com.zhj.tanhua.common.enums.ImageTypeEnum;
import com.zhj.tanhua.common.enums.VideoTypeEnum;
import com.zhj.tanhua.common.result.PageResult;
import com.zhj.tanhua.common.result.UploadFileResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author huanjie.zhuang
 * @date 2021/6/17
 */
@Slf4j
@DubboService(version = "1.0")
public class CircleService implements CircleApi {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private OSS oss;

    @Value("${aliyun.bucketName}")
    private String bucketName;
    @Value("${aliyun.urlPrefix}")
    private String urlPrefix;

    /**
     * 保存用户发布动态
     *
     * @param publishDto 发布内容
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePublish(PublishDto publishDto) {

        // 写入发布表中
        Publish publish = new Publish();
        BeanUtils.copyProperties(publishDto, publish);
        publish.setCreated(System.currentTimeMillis());
        mongoTemplate.save(publishDto);

        // 写入自己的相册表表中
        Album album = new Album();
        album.setPublishId(publish.getId());
        album.setCreated(System.currentTimeMillis());
        mongoTemplate.save(album, Album.TABLE_NAME_PREFIX + publish.getUserId());

        // 查询当前用户好友，将动态数据写入到好友的动态表中
        Query query = Query.query(Criteria.where("userId").is(publish.getUserId()));
        List<Friend> friends = mongoTemplate.find(query, Friend.class);
        for (Friend friend : friends) {
            Feed feed = new Feed();
            feed.setUserId(publish.getUserId());
            feed.setPublishId(publish.getId());
            feed.setCreated(System.currentTimeMillis());
            mongoTemplate.save(feed, Feed.TABLE_NAME_PREFIX + friend.getFriendId());
        }
    }

    /**
     * 查询好友动态
     *
     * @param userId 用户ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return PageResult<Publish>
     */
    @Override
    public PageResult<Publish> queryPublishList(Long userId, Integer pageNum, Integer pageSize) {

        String tableName;
        if (null == userId) {
            // 推荐动态表名
            tableName = Feed.RECOMMEND_TABLE_NAME;
        } else {
            // 好友动态表名
            tableName = Feed.TABLE_NAME_PREFIX + userId;
        }

        // 查询自己的好友动态表
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Order.desc("created")));
        Query queryFeed = new Query().with(pageable);
        long total = mongoTemplate.count(queryFeed, Feed.class);
        List<Feed> feeds = mongoTemplate.find(queryFeed, Feed.class, tableName);
        List<ObjectId> publishIds = new ArrayList<>();
        for (Feed feed : feeds) {
            publishIds.add(feed.getPublishId());
        }

        // 查询动态信息
        Query queryPublish = Query.query(Criteria.where("id").in(publishIds)).with(Sort.by(Sort.Order.desc("created")));
        List<Publish> publishList = mongoTemplate.find(queryPublish, Publish.class);

        return PageResult.<Publish>builder().total(total).pageNum((long) pageNum).pageSize((long) pageSize)
                .hasNext((long) pageNum * pageSize < total).data(publishList).build();
    }

    /**
     * 批量上传媒体文件
     *
     * @param files 需要上传的媒体文件
     * @param fileType 文件类型
     * @return List<UploadFileResult>
     */
    @Override
    public List<UploadFileResult> uploadFiles(List<MultipartFile> files, FileTypeEnum fileType) {

        List<UploadFileResult> results = new ArrayList<>();

        for (MultipartFile file : files) {

            UploadFileResult result = new UploadFileResult();
            result.setFileName(file.getOriginalFilename());

            // 校验媒体文件后缀名
            if (FileTypeEnum.IMAGE.equals(fileType)) {
                if (Arrays.stream(ImageTypeEnum.values()).noneMatch(image ->
                        StringUtils.endsWithIgnoreCase(file.getOriginalFilename(), image.getType()))) {
                    result.setStatus(false);
                    result.setMessage("image type error, only support jpg, jpeg, gif, png");
                    results.add(result);
                    continue;
                }
            } else {
                if (Arrays.stream(VideoTypeEnum.values()).noneMatch(video ->
                        StringUtils.endsWithIgnoreCase(file.getOriginalFilename(), video.getType()))) {
                    result.setStatus(false);
                    result.setMessage("video type error, only support avi, mp4, rmvb, mpeg");
                    results.add(result);
                    continue;
                }
            }

            // 文件路径, {fileType}/{yyyy}/{MM}/{dd}/{currentTimeMillis}.jpg
            String fileUrl = fileType.getType() + new SimpleDateFormat("/yyyy/MM/dd/").format(new Date()) +
                    System.currentTimeMillis() + StringUtils.substringAfterLast(file.getOriginalFilename(), ".");

            // 上传阿里云OSS
            try {
                oss.putObject(bucketName, fileUrl, new ByteArrayInputStream(file.getBytes()));
            } catch (Exception e) {
                result.setStatus(false);
                result.setMessage(e.getMessage());
                results.add(result);
                continue;
            }

            // 上传成功
            result.setStatus(true);
            result.setFileUrl(urlPrefix + fileUrl);
            results.add(result);
        }

        return results;
    }
}
