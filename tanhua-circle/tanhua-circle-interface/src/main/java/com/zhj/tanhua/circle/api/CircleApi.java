package com.zhj.tanhua.circle.api;

import com.zhj.tanhua.circle.pojo.dto.MomentDto;
import com.zhj.tanhua.circle.pojo.to.CommentTo;
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
     * 添加用户动态
     *
     * @param momentDto 动态内容
     * @return String 动态ID
     */
    String addMoment(MomentDto momentDto);

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

    /**
     * 评论某个动态或评论
     *
     * @param userId 用户ID
     * @param momentId 动态ID
     * @param commentId 被评论的评论ID
     * @param content 评论内容
     * @return String 评论ID
     */
    String addComment(Long userId, String momentId, String commentId, String content);

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @return List<String>
     */
    List<String> deleteComment(String commentId);

    /**
     * 查询评论
     *
     * @param momentId 动态ID
     * @param commentId 评论ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return PageResult<CommentTo>
     */
    PageResult<CommentTo> queryComment(String momentId, String commentId, Integer pageNum, Integer pageSize);

    /**
     * 喜欢某个用户
     *
     * @param loveUserId 用户ID
     * @param belovedUserId 评论ID
     * @return String loveId
     */
    String addLove(Long loveUserId, Long belovedUserId);

    /**
     * 取消喜欢某个用户
     *
     * @param loveUserId 用户ID
     * @param belovedUserId 评论ID
     * @return String loveId
     */
    String deleteLove(Long loveUserId, Long belovedUserId);

    /**
     * 查询我喜欢的用户
     *
     * @param userId 用户ID
     * @return List<String> 用户ID列表
     */
    List<String> queryLove(Long userId);

    /**
     * 查询喜欢我的用户
     *
     * @param userId 用户ID
     * @return List<String> 用户ID列表
     */
    List<String> queryBeLoved(Long userId);
}
