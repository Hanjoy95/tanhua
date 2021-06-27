package com.zhj.tanhua.circle.api;

import com.zhj.tanhua.circle.pojo.dto.MomentDto;
import com.zhj.tanhua.circle.pojo.po.Comment;
import com.zhj.tanhua.circle.pojo.to.AlbumTo;
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
     * 查询自己的相册
     *
     * @param userId 用户ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回相册的分页结果
     */
    PageResult<AlbumTo> queryAlbums(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 查询好友动态
     *
     * @param userId 好友ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回好友动态的分页结果
     */
    PageResult<FeedTo> queryFeeds(Long userId, Integer pageNum, Integer pageSize);

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
     * @return 返回评论ID
     */
    String addComment(Long userId, String momentId, String commentId, String content);

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @return 返回被删除的评论ID列表
     */
    List<String> deleteComment(String commentId);

    /**
     * 查询评论
     *
     * @param momentId 动态ID
     * @param commentId 评论ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回评论分页结果
     */
    PageResult<Comment> queryComment(String momentId, String commentId, Integer pageNum, Integer pageSize);

    /**
     * 喜欢某个用户
     *
     * @param loverId 用户ID
     * @param beLoverId 被喜欢的用户ID
     * @return 返回是否匹配成功
     */
    boolean addLove(Long loverId, Long beLoverId);

    /**
     * 取消喜欢某个用户
     *
     * @param loverId 用户ID
     * @param beLoverId 被喜欢的用户ID
     */
    void deleteLove(Long loverId, Long beLoverId);

    /**
     * 查询我喜欢或喜欢我的用户
     *
     * @param userId 用户ID
     * @param isLove true为查询我喜欢的用户，false为查询喜欢我的用户
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回用户ID分页结果
     */
    PageResult<Long> queryLove(Long userId, Boolean isLove, Integer pageNum, Integer pageSize);
}
