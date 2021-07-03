package com.zhj.tanhua.circle.api;

import com.zhj.tanhua.circle.enums.QueryTypeEnum;
import com.zhj.tanhua.circle.pojo.to.CommentTo;
import com.zhj.tanhua.common.result.PageResult;

import java.util.List;

/**
 * 评论dubbo接口
 *
 * @author huanjie.zhuang
 * @date 2021/7/3
 */
public interface CommentApi {

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
    PageResult<CommentTo> queryComment(String momentId, String commentId, Integer pageNum, Integer pageSize);

    /**
     * 查询评论消息
     *
     * @param type 查询类型
     * @param userId 用户ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回评论分页结果
     */
    PageResult<CommentTo> queryCommentsWithType(QueryTypeEnum type, Long userId,
                                                Integer pageNum, Integer pageSize);
}
