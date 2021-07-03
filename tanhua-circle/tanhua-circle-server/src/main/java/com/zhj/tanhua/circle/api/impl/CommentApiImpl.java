package com.zhj.tanhua.circle.api.impl;

import com.zhj.tanhua.circle.api.CommentApi;
import com.zhj.tanhua.circle.enums.QueryTypeEnum;
import com.zhj.tanhua.circle.pojo.po.Comment;
import com.zhj.tanhua.circle.pojo.po.Moment;
import com.zhj.tanhua.circle.pojo.to.CommentTo;
import com.zhj.tanhua.common.exception.ResourceNotFoundException;
import com.zhj.tanhua.common.result.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论dubbo接口实现
 *
 * @author huanjie.zhuang
 * @date 2021/7/3
 */
@Slf4j
@DubboService(version = "1.0")
public class CommentApiImpl implements CommentApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 评论某个动态或评论
     *
     * @param userId 用户ID
     * @param momentId 动态ID
     * @param commentId 被评论的评论ID
     * @param content 评论内容
     * @return 返回评论ID
     */
    @Override
    public String addComment(Long userId, String momentId, String commentId, String content) {

        // 获取动态
        Moment moment = mongoTemplate.findOne(Query.query(Criteria
                .where("id").is(new ObjectId(momentId))), Moment.class);
        if (null == moment) {
            log.error("momentId: {}, not found", momentId);
            throw new ResourceNotFoundException("momentId: " + momentId + ", not found");
        }

        Comment comment = new Comment();
        comment.setCommenter(userId);
        comment.setCommentedBy(moment.getUserId());
        comment.setMomentId(new ObjectId(momentId));
        comment.setContent(content);
        comment.setCreated(System.currentTimeMillis());

        // commentId不为空则是评论某个评论，否则是评论某个动态
        if (StringUtils.isNotBlank(commentId)) {

            // 获取父评论
            Comment pComment = mongoTemplate.findOne(Query.query(Criteria
                    .where("parentId").is(new ObjectId(commentId))), Comment.class);
            if (null == pComment) {
                log.error("commentId: {}, not found", commentId);
                throw new ResourceNotFoundException("commentId: " + commentId + ", not found");
            }
            // 设置父评论ID
            comment.setParentId(new ObjectId(commentId));
            // 设置被评论人
            comment.setCommentedBy(pComment.getCommenter());
        }

        mongoTemplate.save(comment);

        return comment.getId().toHexString();
    }

    /**
     * 删除评论，子评论也会被删除
     *
     * @param commentId 评论ID
     * @return 返回被删除的评论ID列表
     */
    @Override
    public List<String> deleteComment(String commentId) {
        List<String> deleteCommentIds = new ArrayList<>();
        deleteRecursion(commentId, deleteCommentIds);
        mongoTemplate.remove(new Query(Criteria.where("id").in(deleteCommentIds)), Comment.class);

        return deleteCommentIds;
    }

    /**
     * 递归的添加要删除的评论
     * @param commentId 评论ID
     * @param deleteCommentIds 要删除的评论ID列表
     */
    private void deleteRecursion(String commentId, List<String> deleteCommentIds) {
        deleteCommentIds.add(commentId);
        mongoTemplate.find(new Query(Criteria.where("parentId").is(new ObjectId(commentId))), Comment.class)
                .forEach(comment -> deleteRecursion(comment.getId().toHexString(), deleteCommentIds));
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
    @Override
    public PageResult<CommentTo> queryComment(String momentId, String commentId, Integer pageNum, Integer pageSize) {

        Criteria criteria = Criteria.where("momentId").is(new ObjectId(momentId));

        // commentId不为空则为查询某个评论的子评论，否则为查询某个动态的评论
        if (StringUtils.isNotBlank(commentId)) {
            criteria.and("parentId").is(new ObjectId(commentId));
        }
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Order.desc("created")));
        Query query = Query.query(criteria);

        long total = mongoTemplate.count(query, Comment.class);
        List<Comment> comments = mongoTemplate.find(query.with(pageable), Comment.class);

        List<CommentTo> commentTos = new ArrayList<>();
        for (Comment comment : comments) {
            CommentTo commentTo = new CommentTo();
            BeanUtils.copyProperties(comment, commentTo);
            commentTo.setCommentId(comment.getId().toHexString());
            commentTo.setMomentId(comment.getMomentId().toHexString());
            if (null != comment.getParentId()) {
                commentTo.setParentId(comment.getParentId().toHexString());
            }
            // 设置子评论数
            commentTo.setSubCommentNum(mongoTemplate
                    .count(Query.query(Criteria.where("parentId").is(comment.getId())), Comment.class));
            commentTos.add(commentTo);
        }

        return PageResult.<CommentTo>builder().total(total).pageNum((long)pageNum).pageSize((long)pageSize)
                .hasNext((long)pageNum * pageSize < total).data(commentTos).build();
    }

    /**
     * 根据查询类型查询评论
     *
     * @param type 查询类型
     * @param userId 用户ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回评论分页结果
     */
    @Override
    public PageResult<CommentTo> queryCommentsWithType(
            QueryTypeEnum type, Long userId, Integer pageNum, Integer pageSize) {

        // 获取评论
        Query query = Query.query(Criteria.where(
                QueryTypeEnum.QUERY_MY_ACTION.equals(type) ? "commenter" : "commentedBy").is(userId));
        long total = mongoTemplate.count(query, Comment.class);
        List<Comment> comments = mongoTemplate.find(query.with(PageRequest.of(pageNum - 1, pageSize,
                        Sort.by(Sort.Order.desc("created")))), Comment.class);

        // 设置评论属性
        List<CommentTo> commentTos = new ArrayList<>();
        for (Comment comment : comments) {
            CommentTo commentTo = new CommentTo();
            commentTo.setCommentId(comment.getId().toHexString());
            commentTo.setMomentId(comment.getMomentId().toHexString());
            commentTo.setUserId(QueryTypeEnum.QUERY_MY_ACTION.equals(type) ?
                    comment.getCommentedBy() : comment.getCommenter());
            commentTo.setCreated(comment.getCreated());
            commentTos.add(commentTo);
        }

        return PageResult.<CommentTo>builder().total(total).pageNum((long)pageNum).pageSize((long)pageSize)
                .hasNext((long)pageNum * pageSize < total).data(commentTos).build();
    }
}
