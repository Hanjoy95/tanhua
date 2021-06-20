package com.zhj.tanhua.server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhj.tanhua.circle.api.CircleApi;
import com.zhj.tanhua.circle.pojo.dto.PublishDto;
import com.zhj.tanhua.circle.pojo.to.FeedTo;
import com.zhj.tanhua.common.result.PageResult;
import com.zhj.tanhua.common.result.UploadFileResult;
import com.zhj.tanhua.server.pojo.vo.circle.FeedVo;
import com.zhj.tanhua.server.pojo.vo.circle.PublishFailedVo;
import com.zhj.tanhua.server.pojo.vo.circle.PublishVo;
import com.zhj.tanhua.server.web.threadlocal.UserThreadLocal;
import com.zhj.tanhua.user.pojo.po.User;
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
     * 保存用户发布动态
     *
     * @param publishVo 发布动态参数
     * @return PublishFailedVo
     */
    @SneakyThrows
    public PublishFailedVo savePublish(PublishVo publishVo) {

        User user = UserThreadLocal.get();

        PublishDto publishDto = new PublishDto();
        BeanUtils.copyProperties(publishVo, publishDto);
        publishDto.setUserId(user.getId());

        // 文件上传
        List<UploadFileResult> results = circleApi.uploadFiles(publishVo.getMedias(), publishVo.getFileType());

        // 若为重传，获取已上传成功的文件
        List<String> filesHasUpload = new ArrayList<>();
        if (publishVo.getIsRePublish()) {
            filesHasUpload.addAll(MAPPER.readValue(redisTemplate.opsForValue().get("FILES_HAS_UPLOAD_" +
                    publishVo.getRePublishId()), new TypeReference<List<String>>(){}));
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

        PublishFailedVo publishFailedVo = new PublishFailedVo();
        if (CollectionUtils.isEmpty(uploadFailedFiles)) {
            publishDto.setMedias(filesHasUpload);
            circleApi.savePublish(publishDto);
            log.info("files upload success");
            return null;
        } else {
            String rePublishId = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set("FILES_HAS_UPLOAD_" + rePublishId,
                                            MAPPER.writeValueAsString(filesHasUpload),
                                            Duration.ofHours(1));
            publishFailedVo.setRePublishId(rePublishId);
            publishFailedVo.setUploadFailedFiles(uploadFailedFiles);
            log.error("files upload fail, files:{}", MAPPER.writeValueAsString(uploadFailedFiles));
        }

        return publishFailedVo;
    }

    /**
     * 查询好友或推荐动态
     *
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return PageResult<FeedVo>
     */
    public PageResult<FeedVo> queryFeeds(Integer pageNum, Integer pageSize) {

        PageResult<FeedTo> pageResult = circleApi.queryFeeds(UserThreadLocal.get().getId(), pageNum, pageSize);

        // 没有查询到好友或推荐动态
        if (null == pageResult.getData()) {
            log.info("not found the friend's feed, userId:{}", UserThreadLocal.get().getId());
            return PageResult.<FeedVo>builder().total(0L).pageNum((long) pageNum)
                    .pageSize((long) pageSize).hasNext(false).data(null).build();
        }

        // 完善发布动态的信息
        List<FeedTo> feedTos = pageResult.getData();
        Map<Long, List<FeedVo>> feedMap = new HashMap<>();
        List<FeedVo> result = new ArrayList<>();
        for (FeedTo feedTo : feedTos) {
            FeedVo feedVo = new FeedVo();
            BeanUtils.copyProperties(feedTo, feedVo);
            List<FeedVo> feedVos = feedMap.getOrDefault(feedTo.getUserId(), new ArrayList<>());
            feedVos.add(feedVo);
            feedMap.put(feedTo.getUserId(), feedVos);
            result.add(feedVo);
        }

        // 完善发布动态的用户信息
        List<UserInfoTo> userInfos = userService.getUserInfos(
                new ArrayList<>(feedMap.keySet()), null,null,null);
        for (UserInfoTo userInfo : userInfos) {
            for (FeedVo feed : feedMap.get(userInfo.getUserId())) {
                BeanUtils.copyProperties(userInfo, feed);
            }
        }

        return PageResult.<FeedVo>builder().total(pageResult.getTotal()).pageNum(pageResult.getPageNum())
                .pageSize(pageResult.getPageSize()).hasNext(pageResult.getHasNext()).data(result).build();
    }
}
