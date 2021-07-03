package com.zhj.tanhua.circle.api;

import com.zhj.tanhua.circle.pojo.dto.MomentDto;
import com.zhj.tanhua.circle.pojo.to.MomentTo;
import com.zhj.tanhua.circle.pojo.to.FeedTo;
import com.zhj.tanhua.common.enums.FileTypeEnum;
import com.zhj.tanhua.common.result.PageResult;
import com.zhj.tanhua.common.result.UploadFileResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 动态dubbo接口
 *
 * @author huanjie.zhuang
 * @date 2021/7/3
 */
public interface MomentApi {

    /**
     * 添加用户动态
     *
     * @param momentDto 动态内容
     * @return 返回动态ID
     */
    String addMoment(MomentDto momentDto);

    /**
     * 批量上传文件
     *
     * @param files 需要上传的文件
     * @param fileType 文件类型
     * @return 返回上传文件结果列表
     */
    List<UploadFileResult> uploadFiles(List<MultipartFile> files, FileTypeEnum fileType);

    /**
     * 查询某条动态
     *
     * @param momentId 动态ID
     * @return 返回动态
     */
    MomentTo queryMoment(String momentId);

    /**
     * 查询自己的相册
     *
     * @param userId 用户ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回相册的分页结果
     */
    PageResult<MomentTo> queryAlbums(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 查询好友动态
     *
     * @param userId 好友ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回好友动态的分页结果
     */
    PageResult<FeedTo> queryFeeds(Long userId, Integer pageNum, Integer pageSize);
}
