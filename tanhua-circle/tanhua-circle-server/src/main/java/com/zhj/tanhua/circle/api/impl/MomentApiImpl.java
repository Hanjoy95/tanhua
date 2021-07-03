package com.zhj.tanhua.circle.api.impl;

import com.aliyun.oss.OSS;
import com.zhj.tanhua.circle.api.MomentApi;
import com.zhj.tanhua.circle.enums.SeeTypeEnum;
import com.zhj.tanhua.circle.pojo.dto.MomentDto;
import com.zhj.tanhua.circle.pojo.po.*;
import com.zhj.tanhua.circle.pojo.to.MomentTo;
import com.zhj.tanhua.circle.pojo.to.FeedTo;
import com.zhj.tanhua.common.enums.FileTypeEnum;
import com.zhj.tanhua.common.enums.ImageTypeEnum;
import com.zhj.tanhua.common.enums.VideoTypeEnum;
import com.zhj.tanhua.common.exception.ResourceNotFoundException;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 动态dubbo接口实现
 *
 * @author huanjie.zhuang
 * @date 2021/7/3
 */
@Slf4j
@DubboService(version = "1.0")
public class MomentApiImpl implements MomentApi {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private OSS oss;

    @Value("${aliyun.bucketName}")
    private String bucketName;
    @Value("${aliyun.urlPrefix}")
    private String urlPrefix;

    /**
     * 添加用户动态
     *
     * @param momentDto 动态内容
     * @return 返回动态ID
     */
    @Override
    public String addMoment(MomentDto momentDto) {

        // 写入动态表中
        Moment moment = new Moment();
        BeanUtils.copyProperties(momentDto, moment);
        moment.setSeeType(momentDto.getSeeType().getVal());
        moment.setCreated(System.currentTimeMillis());
        mongoTemplate.save(moment);

        // 写入相册表表中
        Album album = new Album();
        album.setMomentId(moment.getId());
        album.setCreated(System.currentTimeMillis());
        mongoTemplate.save(album, Album.TABLE_NAME_PREFIX + moment.getUserId());

        if (!SeeTypeEnum.PRIVATE.equals(momentDto.getSeeType())) {
            // 查询当前用户好友，将动态数据写入到好友的好友动态表中
            List<Friend> friends = mongoTemplate.find(new Query(), Friend.class,
                    Friend.TABLE_NAME_PREFIX + momentDto.getUserId());

            Set<Long> userIds = friends.stream().map(Friend::getUserId).collect(Collectors.toSet());
            // 可看类型为部分可看
            if (SeeTypeEnum.WHO_CAN_SEE.equals(momentDto.getSeeType())) {
                friends = friends.stream().filter(friend -> userIds.contains(friend.getUserId()))
                        .collect(Collectors.toList());
            // 可看类型为部分不可看
            } else if (SeeTypeEnum.WHO_CANNOT_SEE.equals(momentDto.getSeeType())) {
                friends = friends.stream().filter(friend -> !userIds.contains(friend.getUserId()))
                        .collect(Collectors.toList());
            }

            // 写入好友表中
            for (Friend friend : friends) {
                Feed feed = new Feed();
                feed.setUserId(moment.getUserId());
                feed.setMomentId(moment.getId());
                feed.setCreated(System.currentTimeMillis());
                mongoTemplate.save(feed, Feed.TABLE_NAME_PREFIX + friend.getUserId());
            }
        }

        return moment.getId().toHexString();
    }

    /**
     * 批量上传文件
     *
     * @param files 需要上传的文件
     * @param fileType 文件类型
     * @return 返回上传文件结果列表
     */
    @Override
    public List<UploadFileResult> uploadFiles(List<MultipartFile> files, FileTypeEnum fileType) {

        List<UploadFileResult> results = new ArrayList<>();

        for (MultipartFile file : files) {

            UploadFileResult result = new UploadFileResult();
            result.setFileName(file.getOriginalFilename());

            // 校验媒体文件后缀名
            if (FileTypeEnum.image.equals(fileType)) {
                if (ImageTypeEnum.UNKNOWN.equals(ImageTypeEnum.getType(StringUtils
                        .substringAfterLast(file.getOriginalFilename(), ".")))) {
                    result.setStatus(false);
                    result.setMessage("image type error, only support jpg, jpeg, gif, png");
                    results.add(result);
                    log.error("image type error, file:{}", file.getOriginalFilename());
                    continue;
                }
            } else {
                if (VideoTypeEnum.UNKNOWN.equals(VideoTypeEnum.getType(StringUtils
                        .substringAfterLast(file.getOriginalFilename(), ".")))) {
                    result.setStatus(false);
                    result.setMessage("video type error, only support avi, mp4, rmvb, mpeg");
                    results.add(result);
                    log.error("video type error, file:{}", file.getOriginalFilename());
                    continue;
                }
            }

            // 文件路径, {fileType}/{yyyy}/{MM}/{dd}/{currentTimeMillis}.{mediaType}
            String fileUrl = fileType.toString() +
                    new SimpleDateFormat("/yyyy/MM/dd/").format(new Date()) +
                    System.currentTimeMillis() + "." +
                    StringUtils.substringAfterLast(file.getOriginalFilename(), ".");

            // 上传阿里云OSS
            try {
                oss.putObject(bucketName, fileUrl, new ByteArrayInputStream(file.getBytes()));
            } catch (Exception e) {
                result.setStatus(false);
                result.setMessage(e.getMessage());
                results.add(result);
                log.error("upload file fail, file:{}", file.getOriginalFilename(), e);
                continue;
            }

            // 上传成功
            result.setStatus(true);
            result.setFileUrl(urlPrefix + fileUrl);
            results.add(result);
            log.info("upload file success, file:{}", file.getOriginalFilename());
        }

        return results;
    }

    /**
     * 查询某条动态
     *
     * @param momentId 动态ID
     * @return 返回动态
     */
    @Override
    public MomentTo queryMoment(String momentId) {

        Moment moment = mongoTemplate.findOne(Query.query(Criteria.where("id")
                .is(new ObjectId(momentId))), Moment.class);
        if (null == moment) {
            log.error("momentId: {}, not found", momentId);
            throw new ResourceNotFoundException("momentId: " + momentId + ", not found");
        }

        return buildMomentTo(moment);
    }

    /**
     * 查询自己的相册
     *
     * @param userId 用户ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回相册的分页结果
     */
    @Override
    public PageResult<MomentTo> queryAlbums(Long userId, Integer pageNum, Integer pageSize) {

        long total = mongoTemplate.count(new Query(), Album.class, Album.TABLE_NAME_PREFIX + userId);
        List<ObjectId> momentIds = mongoTemplate.find(
                new Query().with(PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Order.desc("created")))),
                Album.class, Album.TABLE_NAME_PREFIX + userId)
                .stream().map(Album::getMomentId).collect(Collectors.toList());

        List<Moment> moments = mongoTemplate.find(Query.query(Criteria.where("id").in(momentIds)), Moment.class);
        List<MomentTo> momentTos = new ArrayList<>();
        for (Moment moment : moments) {
            momentTos.add(buildMomentTo(moment));
        }

        // 按照创建时间重新排序
        momentTos.sort((o1, o2) -> o1.getCreated() < o2.getCreated() ? 1 : -1);

        return PageResult.<MomentTo>builder().total(total).pageNum((long)pageNum).pageSize((long)pageSize)
                .hasNext((long) pageNum * pageSize < total).data(momentTos).build();
    }

    /**
     * 构建MomentTo
     *
     * @param moment 动态信息
     * @return 返回 MomentTo
     */
    private MomentTo buildMomentTo(Moment moment) {

        MomentTo momentTo = new MomentTo();
        BeanUtils.copyProperties(moment, momentTo);
        momentTo.setMomentId(moment.getId().toHexString());
        momentTo.setSeeType(SeeTypeEnum.getType(moment.getSeeType()));

        // 设置点赞数
        momentTo.setLikeNum(mongoTemplate.count(new Query(Criteria.where("momentId")
                .is(moment.getId())), Like.class));
        // 设置评论数
        momentTo.setCommentNum(mongoTemplate.count(new Query(Criteria.where("momentId")
                .is(moment.getId())), Comment.class));

        return momentTo;
    }

    /**
     * 查询好友动态
     *
     * @param userId 好友ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回好友动态的分页结果
     */
    @Override
    public PageResult<FeedTo> queryFeeds(Long userId, Integer pageNum, Integer pageSize) {

        // 查询好友动态表
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Order.desc("created")));
        Query queryFeed = new Query();

        long total = mongoTemplate.count(queryFeed, Feed.class, Feed.TABLE_NAME_PREFIX + userId);
        if (0 == total) {
            return PageResult.<FeedTo>builder().total(0L).pageNum((long) pageNum).pageSize((long) pageSize)
                    .hasNext(false).data(null).build();
        }
        List<Feed> feeds = mongoTemplate.find(queryFeed.with(pageable), Feed.class,
                Feed.TABLE_NAME_PREFIX + userId);

        Map<ObjectId, FeedTo> feedMap = new HashMap<>();
        for (Feed feed : feeds) {
            FeedTo feedTo = new FeedTo();
            BeanUtils.copyProperties(feed, feedTo);
            feedTo.setFeedId(feed.getId().toHexString());
            feedTo.setMomentId(feed.getMomentId().toHexString());
            // 设置点赞数
            feedTo.setLikeNum(mongoTemplate.count(new Query(Criteria.where("momentId")
                    .is(feed.getMomentId())), Like.class));
            // 设置评论数
            feedTo.setCommentNum(mongoTemplate.count(new Query(Criteria.where("momentId")
                    .is(feed.getMomentId())), Comment.class));
            // 是否有点赞
            feedTo.setHasLike(mongoTemplate.exists(Query.query(Criteria.where("liker").is(userId)
                    .and("momentId").is(feed.getMomentId())), Like.class));
            // 是否有评论
            feedTo.setHasComment(mongoTemplate.exists(Query.query(Criteria.where("commenter").is(userId)
                    .and("momentId").is(feed.getMomentId())), Comment.class));
            feedMap.put(feed.getMomentId(), feedTo);
        }

        // 查询动态信息
        List<Moment> momentList = mongoTemplate.find(Query.query(Criteria
                .where("id").in(feedMap.keySet())), Moment.class);
        for (Moment moment : momentList) {
            BeanUtils.copyProperties(moment, feedMap.get(moment.getId()));
        }

        return PageResult.<FeedTo>builder().total(total).pageNum((long) pageNum).pageSize((long) pageSize)
                .hasNext((long) pageNum * pageSize < total).data(new ArrayList<>(feedMap.values())).build();
    }
}
