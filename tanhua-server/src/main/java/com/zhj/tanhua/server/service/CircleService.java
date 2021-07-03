package com.zhj.tanhua.server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhj.tanhua.circle.api.CommentApi;
import com.zhj.tanhua.circle.api.LikeApi;
import com.zhj.tanhua.circle.api.LoveApi;
import com.zhj.tanhua.circle.api.MomentApi;
import com.zhj.tanhua.circle.enums.QueryTypeEnum;
import com.zhj.tanhua.circle.pojo.dto.MomentDto;
import com.zhj.tanhua.circle.pojo.po.Like;
import com.zhj.tanhua.circle.pojo.po.Love;
import com.zhj.tanhua.circle.pojo.to.CommentTo;
import com.zhj.tanhua.circle.pojo.to.MomentTo;
import com.zhj.tanhua.circle.pojo.to.FeedTo;
import com.zhj.tanhua.common.constant.ThConstant;
import com.zhj.tanhua.common.exception.ParameterInvalidException;
import com.zhj.tanhua.common.result.PageResult;
import com.zhj.tanhua.common.result.UploadFileResult;
import com.zhj.tanhua.server.pojo.bo.circle.*;
import com.zhj.tanhua.server.pojo.vo.circle.MomentVo;
import com.zhj.tanhua.server.web.threadlocal.UserThreadLocal;
import com.zhj.tanhua.user.pojo.to.UserInfoTo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 好友圈模块的服务层
 *
 * @author huanjie.zhuang
 * @date 2021/6/19
 */
@Slf4j
@Service
public class CircleService {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @DubboReference(version = "1.0", url = ThConstant.CIRCLE_URL)
    MomentApi momentApi;
    @DubboReference(version = "1.0", url = ThConstant.CIRCLE_URL)
    LikeApi likeApi;
    @DubboReference(version = "1.0", url = ThConstant.CIRCLE_URL)
    CommentApi commentApi;
    @DubboReference(version = "1.0", url = ThConstant.CIRCLE_URL)
    LoveApi loveApi;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 发布动态
     *
     * @param momentVo 动态参数
     * @return 返回发布动态结果
     */
    @SneakyThrows
    public MomentBo addMoment(MomentVo momentVo) {

//        // 文件上传
//        List<UploadFileResult> results = momentApi.uploadFiles(momentVo.getMedias(), momentVo.getFileType());

//        // 若为重传，获取已上传成功的文件
        List<String> filesHasUpload = new ArrayList<>();
//        if (momentVo.getIsRePublish()) {
//            filesHasUpload.addAll(MAPPER.readValue(redisTemplate.opsForValue().get("FILES_HAS_UPLOAD_" +
//                    momentVo.getRePublishId()), new TypeReference<List<String>>(){}));
//            log.info("files is re uploading");
//        }

        // 检查是否有上传失败的
        List<UploadFileResult> uploadFailedFiles = new ArrayList<>();
//        for (UploadFileResult result : results) {
//            if (result.getStatus()) {
//                filesHasUpload.add(result.getFileUrl());
//            } else {
//                uploadFailedFiles.add(result);
//            }
//        }

        MomentBo momentBo = new MomentBo();
        if (CollectionUtils.isEmpty(uploadFailedFiles)) {
            // 上传成功
            MomentDto momentDto = new MomentDto();
            BeanUtils.copyProperties(momentVo, momentDto);
            momentDto.setUserId(UserThreadLocal.get().getId());
            momentDto.setSeeType(momentVo.getSeeType());
            momentDto.setMedias(filesHasUpload);
            momentBo.setMomentId(momentApi.addMoment(momentDto));
            log.info("files upload success");
            return momentBo;
        } else {
            // 上传失败
            String rePublishId = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set("FILES_HAS_UPLOAD_" + rePublishId,
                                            MAPPER.writeValueAsString(filesHasUpload),
                                            Duration.ofHours(1));
            momentBo.setRePublishId(rePublishId);
            momentBo.setUploadFailedFiles(uploadFailedFiles);
            log.error("files upload fail, files:{}", MAPPER.writeValueAsString(uploadFailedFiles));
        }

        return momentBo;
    }

    /**
     * 查询某条动态
     *
     * @param momentId 动态ID
     * @return 返回动态
     */
    public MomentTo queryMoment(String momentId) {
        return momentApi.queryMoment(momentId);
    }

    /**
     * 查询相册
     *
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回相册的分页结果
     */
    public PageResult<MomentTo> queryAlbums(Integer pageNum, Integer pageSize) {
        return momentApi.queryAlbums(UserThreadLocal.get().getId(), pageNum, pageSize);
    }

    /**
     * 查询好友动态
     *
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回好友动态分页结果
     */
    public PageResult<FeedBo> queryFeeds(Integer pageNum, Integer pageSize) {

        PageResult<FeedTo> pageResult = momentApi.queryFeeds(UserThreadLocal.get().getId(), pageNum, pageSize);

        // 没有查询到好友动态
        if (null == pageResult.getData()) {
            log.info("not found the friend's feed, userId:{}", UserThreadLocal.get().getId());
            return PageResult.<FeedBo>builder().total(0L).pageNum((long) pageNum)
                    .pageSize((long) pageSize).hasNext(false).data(null).build();
        }

        // 完善发布动态的信息
        List<FeedTo> feedTos = pageResult.getData();
        Map<Long, List<FeedBo>> feedMap = new HashMap<>();
        List<FeedBo> result = new ArrayList<>();
        for (FeedTo feedTo : feedTos) {
            FeedBo feedBo = new FeedBo();
            BeanUtils.copyProperties(feedTo, feedBo);
            List<FeedBo> feedBos = feedMap.getOrDefault(feedTo.getUserId(), new ArrayList<>());
            feedBos.add(feedBo);
            feedMap.put(feedTo.getUserId(), feedBos);
            result.add(feedBo);
        }

        // 完善发布动态的用户信息
        List<UserInfoTo> userInfos = userService.getUserInfos(
                new ArrayList<>(feedMap.keySet()), null,null,null);
        for (UserInfoTo userInfo : userInfos) {
            for (FeedBo feed : feedMap.get(userInfo.getUserId())) {
                BeanUtils.copyProperties(userInfo, feed);
            }
        }

        return PageResult.<FeedBo>builder().total(pageResult.getTotal()).pageNum(pageResult.getPageNum())
                .pageSize(pageResult.getPageSize()).hasNext(pageResult.getHasNext()).data(result).build();
    }

    /**
     * 点赞或取消点赞
     *
     * @param momentId 动态ID
     * @param isLike true为点赞，false为取消点赞
     */
    public void likeOrUnlike(String momentId, Boolean isLike) {
        likeApi.likeOrUnlike(UserThreadLocal.get().getId(), momentId, isLike);
    }

    /**
     * 查询我的点赞
     *
     * @param type 查询类型
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回点赞分页结果
     */
    public PageResult<LikeBo> queryLikeWithType(QueryTypeEnum type, Integer pageNum, Integer pageSize) {

        // 获取点赞列表
        PageResult<Like> likes = likeApi.queryLikeWithType(type,
                UserThreadLocal.get().getId(), pageNum, pageSize);

        // 获取用户信息
        Set<Long> userIds = likes.getData().stream()
                .map(QueryTypeEnum.QUERY_MY_MESSAGE.equals(type) ? Like::getLiker : Like::getBeLiked)
                .collect(Collectors.toSet());
        Map<Long, UserInfoTo> userInfoMap = userService.getUserInfos(
                new ArrayList<>(userIds), null, null, null)
                .stream().collect(Collectors.toMap(UserInfoTo::getUserId, userInfo -> userInfo));

        List<LikeBo> likeBos = new ArrayList<>();
        for (Like like : likes.getData()) {
            LikeBo likeBo = new LikeBo();
            BeanUtils.copyProperties(like, likeBo);
            likeBo.setLikeId(like.getId().toHexString());
            likeBo.setMomentId(like.getMomentId().toHexString());

            // 设置用户昵称和头像
            UserInfoTo userInfo = userInfoMap.get(
                    QueryTypeEnum.QUERY_MY_MESSAGE.equals(type) ? like.getLiker() : like.getBeLiked());
            likeBo.setNickName(userInfo.getNickName());
            likeBo.setAvatar(userInfo.getAvatar());
            likeBos.add(likeBo);
        }

        return PageResult.<LikeBo>builder().total(likes.getTotal()).pageNum((long)pageNum)
                .pageSize((long)pageSize).hasNext(likes.getHasNext()).data(likeBos).build();
    }

    /**
     * 评论某个动态或评论
     *
     * @param momentId 动态ID
     * @param commentId 被评论的评论ID
     * @param content 评论内容
     * @return 返回评论ID
     */
    public String addComment(String momentId, String commentId, String content) {
        return commentApi.addComment(UserThreadLocal.get().getId(), momentId, commentId, content);
    }

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @return 返回被删除的评论ID列表
     */
    public List<String> deleteComment(String commentId) {
        return commentApi.deleteComment(commentId);
    }

    /**
     * 查询评论
     *
     * @param momentId 动态ID
     * @param commentId 评论ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回评论分页结果
     */
    public PageResult<CommentTo> queryComment(String momentId, String commentId,
                                              Integer pageNum, Integer pageSize) {
        return commentApi.queryComment(momentId, commentId, pageNum, pageSize);
    }

    /**
     * 查询评论消息
     *
     * @param type 查询类型
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回评论分页结果
     */
    public PageResult<CommentBo> queryCommentsWithType(QueryTypeEnum type, Integer pageNum, Integer pageSize) {

        // 获取评论
        PageResult<CommentTo> comments = commentApi.queryCommentsWithType(
                type, UserThreadLocal.get().getId(), pageNum, pageSize);

        // 获取用户信息
        Set<Long> userIds = comments.getData().stream().map(CommentTo::getUserId).collect(Collectors.toSet());
        Map<Long, UserInfoTo> userInfoMap = userService.getUserInfos(
                new ArrayList<>(userIds), null, null, null)
                .stream().collect(Collectors.toMap(UserInfoTo::getUserId, userInfo -> userInfo));

        // 设置评论信息
        List<CommentBo> commentBos = new ArrayList<>();
        for (CommentTo comment : comments.getData()) {
            CommentBo commentBo = new CommentBo();
            BeanUtils.copyProperties(comment, commentBo);

            // 设置用户昵称和头像
            UserInfoTo userInfo = userInfoMap.get(comment.getUserId());
            commentBo.setNickName(userInfo.getNickName());
            commentBo.setAvatar(userInfo.getAvatar());
            commentBos.add(commentBo);
        }

        return PageResult.<CommentBo>builder().total(comments.getTotal()).pageNum((long)pageNum)
                .pageSize((long)pageSize).hasNext(comments.getHasNext()).data(commentBos).build();
    }

    /**
     * 喜欢某个用户
     *
     * @param userId 喜欢的用户ID
     * @return 匹配成功返回对方信息
     */
    public UserInfoTo addLove(Long userId) {

        Long lover = UserThreadLocal.get().getId();
        if (lover.equals(userId)) {
            throw new ParameterInvalidException("自己喜欢自己，可以啊兄弟");
        } else if (loveApi.addLove(lover, userId)) {
            return userService.getUserInfo(userId);
        }
        return null;
    }

    /**
     * 取消喜欢某个用户
     *
     * @param lover 用户ID
     * @param beLoved 评论ID
     */
    public void deleteLove(Long lover, Long beLoved) {
        loveApi.deleteLove(lover, beLoved);
    }

    /**
     * 查询我的喜欢消息
     *
     * @param type 查询类型
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回喜欢信息分页结果
     */
    public PageResult<LoveBo> queryLove(QueryTypeEnum type, Integer pageNum, Integer pageSize) {

        // 获取喜欢列表
        PageResult<Love> loves = loveApi.queryLoveWithType(
                type, UserThreadLocal.get().getId(), pageNum, pageSize);

        // 获取用户信息
        Set<Long> userIds = loves.getData().stream()
                .map(QueryTypeEnum.QUERY_MY_MESSAGE.equals(type) ? Love::getLover : Love::getBeLoved)
                .collect(Collectors.toSet());
        Map<Long, UserInfoTo> userInfoMap = userService.getUserInfos(
                new ArrayList<>(userIds), null, null, null)
                .stream().collect(Collectors.toMap(UserInfoTo::getUserId, userInfo -> userInfo));

        // 设置喜欢用户信息
        List<LoveBo> loveBos = new ArrayList<>();
        for (Love love : loves.getData()) {
            LoveBo loveBo = new LoveBo();
            loveBo.setLoveId(love.getId().toHexString());
            loveBo.setCreated(love.getCreated());

            // 设置用户信息
            UserInfoTo userInfo = userInfoMap.get(
                    QueryTypeEnum.QUERY_MY_MESSAGE.equals(type) ? love.getLover() : love.getBeLoved());
            BeanUtils.copyProperties(userInfo, loveBo);
            loveBos.add(loveBo);
        }

        return PageResult.<LoveBo>builder().total(loves.getTotal()).pageNum(loves.getPageNum())
                .pageSize(loves.getPageSize()).hasNext(loves.getHasNext()).data(loveBos).build();
    }
}
