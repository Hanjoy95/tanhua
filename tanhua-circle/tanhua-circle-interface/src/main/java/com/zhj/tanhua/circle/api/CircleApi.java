package com.zhj.tanhua.circle.api;

import com.zhj.tanhua.circle.pojo.dto.MomentDto;
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
     * 保存用户动态
     *
     * @param momentDto 动态内容
     */
    void saveMoment(MomentDto momentDto);

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

    /**
     * 点赞或取消点赞
     *
     * @param userId 用户ID
     * @param momentId 动态ID
     * @param isLike true为点赞，false为取消点赞
     */
    void likeOrUnlike(Long userId, String momentId, Boolean isLike);
}
