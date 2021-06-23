package com.zhj.tanhua.circle.service;

import com.aliyun.oss.OSS;
import com.zhj.tanhua.circle.api.CircleApi;
import com.zhj.tanhua.circle.pojo.dto.MomentDto;
import com.zhj.tanhua.circle.pojo.po.*;
import com.zhj.tanhua.circle.pojo.to.CommentTo;
import com.zhj.tanhua.circle.pojo.to.FeedTo;
import com.zhj.tanhua.common.enums.FileTypeEnum;
import com.zhj.tanhua.common.enums.ImageTypeEnum;
import com.zhj.tanhua.common.enums.VideoTypeEnum;
import com.zhj.tanhua.common.exception.NotFoundException;
import com.zhj.tanhua.common.exception.UpdateRepeatException;
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
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 好友圈模块的dubbo接口实现
 *
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
     * 添加用户动态
     *
     * @param momentDto 动态内容
     * @return String 动态ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addMoment(MomentDto momentDto) {

        // 写入动态表中
        Moment moment = new Moment();
        BeanUtils.copyProperties(momentDto, moment);
        moment.setCreated(System.currentTimeMillis());
        mongoTemplate.save(momentDto);

        // 写入相册表表中
        Album album = new Album();
        album.setMomentId(moment.getId());
        album.setCreated(System.currentTimeMillis());
        mongoTemplate.save(album, Album.TABLE_NAME_PREFIX + moment.getUserId());

        // 查询当前用户好友，将动态数据写入到好友的好友动态表中
        Query query = Query.query(Criteria.where("userId").is(moment.getUserId()));
        List<Friend> friends = mongoTemplate.find(query, Friend.class);
        for (Friend friend : friends) {
            Feed feed = new Feed();
            feed.setUserId(moment.getUserId());
            feed.setMomentId(moment.getId());
            feed.setCreated(System.currentTimeMillis());
            mongoTemplate.save(feed, Feed.TABLE_NAME_PREFIX + friend.getFriendId());
        }

        return moment.getId().toHexString();
    }

    /**
     * 查询好友或推荐动态
     *
     * @param userId 用户ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return PageResult<FeedTo>
     */
    @Override
    public PageResult<FeedTo> queryFeeds(Long userId, Integer pageNum, Integer pageSize) {

        String tableName;
        if (null == userId) {
            // 推荐动态表名
            tableName = Feed.RECOMMEND_TABLE_NAME;
        } else {
            // 好友动态表名
            tableName = Feed.TABLE_NAME_PREFIX + userId;
        }

        // 查询好友或推荐动态表
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Order.desc("created")));
        Query queryFeed = new Query().with(pageable);
        long total = mongoTemplate.count(queryFeed, Feed.class);
        if (0 == total) {
            return PageResult.<FeedTo>builder().total(0L).pageNum((long) pageNum).pageSize((long) pageSize)
                    .hasNext(false).data(null).build();
        }
        List<Feed> feeds = mongoTemplate.find(queryFeed, Feed.class, tableName);
        Map<ObjectId, FeedTo> feedMap = new HashMap<>();
        for (Feed feed : feeds) {
            FeedTo feedTo = new FeedTo();
            BeanUtils.copyProperties(feed, feedTo);
            feedTo.setFeedId(feed.getId().toHexString());
            feedMap.put(feed.getMomentId(), feedTo);
        }

        // 查询动态信息
        Query queryPublish = Query.query(Criteria.where("id").in(feedMap.keySet()))
                .with(Sort.by(Sort.Order.desc("created")));
        List<Moment> momentList = mongoTemplate.find(queryPublish, Moment.class);
        for (Moment moment : momentList) {
            BeanUtils.copyProperties(moment, feedMap.get(moment.getId()));
            FeedTo feedTo = feedMap.get(moment.getId());
            feedTo.setMomentId(moment.getId().toHexString());
            feedTo.setCommentNum(mongoTemplate.count(new Query(Criteria.where("momentId")
                    .is(moment.getId())), Comment.class));
        }

        return PageResult.<FeedTo>builder().total(total).pageNum((long) pageNum).pageSize((long) pageSize)
                .hasNext((long) pageNum * pageSize < total).data(new ArrayList<>(feedMap.values())).build();
    }

    /**
     * 批量上传文件
     *
     * @param files 需要上传的文件
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
            String fileUrl = fileType.getType() +
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
     * 点赞或取消点赞
     *
     * @param userId 用户ID
     * @param momentId 动态ID
     * @param isLike true为点赞，false为取消点赞
     */
    @Override
    public void likeOrUnlike(Long userId, String momentId, Boolean isLike) {

        String tableName = Feed.TABLE_NAME_PREFIX + userId;
        Feed feed = mongoTemplate.findOne(Query.query(Criteria.where("momentId").is(new ObjectId(momentId))),
                Feed.class, tableName);

        // 好友动态表中没找到
        if (null == feed) {
            tableName = Feed.RECOMMEND_TABLE_NAME;
            feed = mongoTemplate.findOne(Query.query(Criteria.where("momentId").is(new ObjectId(momentId))),
                    Feed.class, tableName);
        }
        // 推荐动态表中没找到
        if (null == feed) {
            throw new NotFoundException("momentId: " + momentId + ", not found");
        }

        // 点赞
        if (isLike) {
            // 已点赞
            if (feed.getHasLike()) {
                throw new UpdateRepeatException("feed update repeat, hasLike is true");
            }
            // 更新点赞
            mongoTemplate.updateFirst(Query.query(Criteria.where("momentId").is(new ObjectId(momentId))),
                    Update.update("hasLike", true), Feed.class, tableName);
            // 更新好友的相册表
            mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(feed.getMomentId())),
                    new Update().inc("likeNum", 1).push("likeUsers", userId), Moment.class);

        // 取消点赞
        } else {
            // 已取消点赞
            if (!feed.getHasLike()) {
                throw new UpdateRepeatException("feed update repeat, hasLike is false");
            }
            // 更新点赞
            mongoTemplate.updateFirst(Query.query(Criteria.where("momentId").is(new ObjectId(momentId))),
                    Update.update("hasLike", false), Feed.class, tableName);
            // 更新好友的相册表
            mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(feed.getMomentId())),
                    new Update().inc("likeNum", -1).pull("likeUsers", userId), Moment.class);
        }
    }

    /**
     * 评论某个动态或评论
     *
     * @param userId 用户ID
     * @param momentId 动态ID
     * @param commentId 被评论的评论ID
     * @param content 评论内容
     * @return String 评论ID
     */
    @Override
    public String addComment(Long userId, String momentId, String commentId, String content) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setMomentId(new ObjectId(momentId));
        comment.setContent(content);
        // commentId不为空则是评论某个评论，否则是评论某个动态
        if (StringUtils.isNotBlank(commentId)) {
            comment.setParentId(new ObjectId(commentId));
        }
        mongoTemplate.save(comment);

        return comment.getId().toHexString();
    }

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @return List<String>
     */
    @Override
    public List<String> deleteComment(String commentId) {
        List<String> deleteCommentIds = new ArrayList<>();
        deleteRecursion(commentId, deleteCommentIds);
        mongoTemplate.remove(new Query(Criteria.where("id").in(deleteCommentIds)), Comment.class);

        return deleteCommentIds;
    }

    /**
     * 递归的添加要删除的评论
     */
    private void deleteRecursion(String commentId, List<String> deleteCommentIds) {
        deleteCommentIds.add(commentId);
        mongoTemplate.find(new Query(Criteria.where("parentId").is(new ObjectId(commentId))), Comment.class)
                .forEach(comment -> deleteRecursion(comment.getId().toHexString(), deleteCommentIds));
    }

    /**
     * 查询评论
     *
     * @param momentId 动态ID
     * @param commentId 评论ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return PageResult<CommentTo>
     */
    @Override
    public PageResult<CommentTo> queryComment(String momentId, String commentId, Integer pageNum, Integer pageSize) {

        return null;
    }

    @Override
    public String addLove(Long loveUserId, Long belovedUserId) {
        return null;
    }

    @Override
    public String deleteLove(Long loveUserId, Long belovedUserId) {
        return null;
    }

    @Override
    public List<String> queryLove(Long userId) {
        return null;
    }

    @Override
    public List<String> queryBeLoved(Long userId) {
        return null;
    }
}
