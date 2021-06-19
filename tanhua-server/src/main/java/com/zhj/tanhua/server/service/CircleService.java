package com.zhj.tanhua.server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhj.tanhua.circle.api.CircleApi;
import com.zhj.tanhua.circle.pojo.dto.PublishDto;
import com.zhj.tanhua.common.result.UploadFileResult;
import com.zhj.tanhua.server.pojo.vo.circle.PublishFailedVo;
import com.zhj.tanhua.server.pojo.vo.circle.PublishVo;
import com.zhj.tanhua.server.web.threadlocal.UserThreadLocal;
import com.zhj.tanhua.user.pojo.po.User;
import lombok.SneakyThrows;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 好友圈模块的服务层
 *
 * @author huanjie.zhuang
 * @date 2021/6/19
 */
@Service
public class CircleService {

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
        List<UploadFileResult> results = circleApi.uploadFiles(
                Arrays.asList(publishVo.getMedias()), publishVo.getFileType());

        // 若为重传，获取已上传成功的文件
        List<String> filesHasUpload = new ArrayList<>();
        if (publishVo.getIsRePublish()) {
            filesHasUpload.addAll(MAPPER.readValue(redisTemplate.opsForValue().get("FILES_HAS_UPLOAD_" +
                    publishVo.getRePublishId()), new TypeReference<List<String>>(){}));
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
            return null;
        } else {
            String rePublishId = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set("FILES_HAS_UPLOAD_" + rePublishId,
                                            MAPPER.writeValueAsString(filesHasUpload),
                                            Duration.ofHours(1));
            publishFailedVo.setRePublishId(rePublishId);
            publishFailedVo.setUploadFailedFiles(uploadFailedFiles);
        }

        return publishFailedVo;
    }
}
