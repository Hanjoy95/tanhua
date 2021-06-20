package com.zhj.tanhua.circle.api;

import com.zhj.tanhua.circle.pojo.dto.PublishDto;
import com.zhj.tanhua.circle.pojo.po.Publish;
import com.zhj.tanhua.circle.pojo.to.FeedTo;
import com.zhj.tanhua.common.enums.FileTypeEnum;
import com.zhj.tanhua.common.result.PageResult;
import com.zhj.tanhua.common.result.UploadFileResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 好友圈模块的dubbo接口
 *
 * @author huanjie.zhuang
 * @date 2021/6/17
 */
public interface CircleApi {

    /**
     * 保存用户发布动态
     *
     * @param publishDto 发布内容
     */
    void savePublish(PublishDto publishDto);

    /**
     * 查询好友或推荐动态
     *
     * @param userId 用户ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return PageResult<FeedTo>
     */
    PageResult<FeedTo> queryFeeds(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 批量上传文件
     *
     * @param files 需要上传的文件
     * @param fileType 文件类型
     * @return List<UploadFileResult>
     */
    List<UploadFileResult> uploadFiles(List<MultipartFile> files, FileTypeEnum fileType);
}
