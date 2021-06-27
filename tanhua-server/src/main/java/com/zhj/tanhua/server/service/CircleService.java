package com.zhj.tanhua.server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhj.tanhua.circle.api.CircleApi;
import com.zhj.tanhua.circle.enums.SeeTypeEnum;
import com.zhj.tanhua.circle.pojo.dto.MomentDto;
import com.zhj.tanhua.circle.pojo.po.Comment;
import com.zhj.tanhua.circle.pojo.po.Moment;
import com.zhj.tanhua.circle.pojo.to.AlbumTo;
import com.zhj.tanhua.circle.pojo.to.FeedTo;
import com.zhj.tanhua.common.result.PageResult;
import com.zhj.tanhua.common.result.UploadFileResult;
import com.zhj.tanhua.server.pojo.bo.circle.FeedBo;
import com.zhj.tanhua.server.pojo.bo.circle.MomentBo;
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

    @DubboReference(version = "1.0", url = "dubbo://127.0.0.1:19300")
    CircleApi circleApi;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 发布动态
     *
     * @param momentVo 动态参数
     * @return 返回发布动态结果
     */
    @SneakyThrows
    public MomentBo addMoment(MomentVo momentVo) {

        // 文件上传
        List<UploadFileResult> results = circleApi.uploadFiles(momentVo.getMedias(), momentVo.getFileType());

        // 若为重传，获取已上传成功的文件
        List<String> filesHasUpload = new ArrayList<>();
        if (momentVo.getIsRePublish()) {
            filesHasUpload.addAll(MAPPER.readValue(redisTemplate.opsForValue().get("FILES_HAS_UPLOAD_" +
                    momentVo.getRePublishId()), new TypeReference<List<String>>(){}));
            log.info("files is re uploading");
        }

        // 检查是否有上传失败的
        List<UploadFileResult> uploadFailedFiles = new ArrayList<>();
        for (UploadFileResult result : results) {
            if (result.getStatus()) {
                filesHasUpload.add(result.getFileUrl());
            } else {
                uploadFailedFiles.add(result);
            }
        }

        MomentBo momentBo = new MomentBo();
        if (CollectionUtils.isEmpty(uploadFailedFiles)) {
            // 上传成功
            MomentDto momentDto = new MomentDto();
            BeanUtils.copyProperties(momentVo, momentDto);
            momentDto.setUserId(UserThreadLocal.get().getId());
            momentDto.setSeeType(SeeTypeEnum.getType(momentVo.getSeeType()));
            momentDto.setMedias(filesHasUpload);
            momentBo.setMomentId(circleApi.addMoment(momentDto));
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
    public Moment queryMoment(String momentId) {
        return circleApi.queryMoment(momentId);
    }

    /**
     * 查询相册
     *
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回相册的分页结果
     */
    public PageResult<AlbumTo> queryAlbums(Integer pageNum, Integer pageSize) {
        return circleApi.queryAlbums(UserThreadLocal.get().getId(), pageNum, pageSize);
    }

    /**
     * 查询好友动态
     *
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回好友动态分页结果
     */
    public PageResult<FeedBo> queryFeeds(Integer pageNum, Integer pageSize) {

        PageResult<FeedTo> pageResult = circleApi.queryFeeds(UserThreadLocal.get().getId(), pageNum, pageSize);

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
        circleApi.likeOrUnlike(UserThreadLocal.get().getId(), momentId, isLike);
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
        return circleApi.addComment(UserThreadLocal.get().getId(), momentId, commentId, content);
    }

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @return 返回被删除的评论ID列表
     */
    public List<String> deleteComment(String commentId) {
        return circleApi.deleteComment(commentId);
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
    public PageResult<Comment> queryComment(String momentId, String commentId, Integer pageNum, Integer pageSize) {
        return circleApi.queryComment(momentId, commentId, pageNum, pageSize);
    }

    /**
     * 喜欢某个用户
     *
     * @param userId 喜欢的用户ID
     * @return 匹配成功返回对方信息
     */
    public UserInfoTo addLove(Long userId) {

        if (circleApi.addLove(UserThreadLocal.get().getId(), userId)) {
            return userService.getUserInfo(userId);
        }
        return null;
    }

    /**
     * 取消喜欢某个用户
     *
     * @param loveUserId 用户ID
     * @param belovedUserId 评论ID
     */
    public void deleteLove(Long loveUserId, Long belovedUserId) {
        circleApi.deleteLove(loveUserId, belovedUserId);
    }

    /**
     * 查询我喜欢或喜欢我的用户
     *
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @param isLove true为查询我喜欢的用户，false为查询喜欢我的用户
     * @return 返回用户信息分页结果
     */
    public PageResult<UserInfoTo> queryLove(Integer pageNum, Integer pageSize, Boolean isLove) {

        PageResult<Long> pageResult = circleApi.queryLove(UserThreadLocal.get().getId(), isLove, pageNum, pageSize);
        List<UserInfoTo> userInfos = userService.getUserInfos(pageResult.getData(), null, null, null);

        return PageResult.<UserInfoTo>builder().total(pageResult.getTotal()).pageNum(pageResult.getPageNum())
                .pageSize(pageResult.getPageSize()).hasNext(pageResult.getHasNext()).data(userInfos).build();
    }
}
