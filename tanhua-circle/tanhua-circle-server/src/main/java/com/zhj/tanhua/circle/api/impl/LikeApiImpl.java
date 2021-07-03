package com.zhj.tanhua.circle.api.impl;

import com.zhj.tanhua.circle.api.LikeApi;
import com.zhj.tanhua.circle.enums.QueryTypeEnum;
import com.zhj.tanhua.circle.pojo.po.Like;
import com.zhj.tanhua.circle.pojo.po.Moment;
import com.zhj.tanhua.common.exception.ParameterInvalidException;
import com.zhj.tanhua.common.exception.ResourceHasExistException;
import com.zhj.tanhua.common.exception.ResourceNotFoundException;
import com.zhj.tanhua.common.result.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * 点赞dubbo接口实现
 *
 * @author huanjie.zhuang
 * @date 2021/7/3
 */
@Slf4j
@DubboService(version = "1.0")
public class LikeApiImpl implements LikeApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 点赞或取消点赞
     *
     * @param userId 用户ID
     * @param momentId 动态ID
     * @param isLike true为点赞，false为取消点赞
     */
    @Override
    public void likeOrUnlike(Long userId, String momentId, Boolean isLike) {

        Moment moment = mongoTemplate.findOne(Query.query(Criteria.where("id")
                .is(new ObjectId(momentId))), Moment.class);
        if (null == moment) {
            log.error("momentId: {}, not found", momentId);
            throw new ResourceNotFoundException("momentId: " + momentId + ", not found");
        }

        Query query = Query.query(Criteria.where("likerId").is(userId).and("momentId").is(momentId));
        // 点赞
        if (isLike) {
            // 已点赞
            if (mongoTemplate.exists(query, Like.class)) {
                log.error("like has exist, userId: {}, momentId: {}", userId, momentId);
                throw new ResourceHasExistException("like has exist, userId: "
                        + userId + ", momentId: " + momentId);
            }
            // 写入点赞表
            Like like = new Like();
            like.setLiker(userId);
            like.setBeLiked(moment.getUserId());
            like.setMomentId(new ObjectId(momentId));
            like.setCreated(System.currentTimeMillis());
            mongoTemplate.save(like);

            // 取消点赞
        } else {
            // 没有点赞过
            if (!mongoTemplate.exists(query, Like.class)) {
                log.error("never have like, userId: {}, momentId: {}", userId, moment);
                throw new ParameterInvalidException("never have like, userId: "
                        + userId + ", momentId: " + momentId);
            }
            mongoTemplate.remove(query, Like.class);
        }
    }

    /**
     * 查询我的点赞消息
     *
     * @param type 查询类型
     * @param userId 用户ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回点赞分页结果
     */
    @Override
    public PageResult<Like> queryLikeWithType(QueryTypeEnum type, Long userId,
                                              Integer pageNum, Integer pageSize) {

        // 获取喜欢信息
        Query query = Query.query(Criteria.where(
                QueryTypeEnum.QUERY_MY_MESSAGE.equals(type) ? "beLiked" : "liker").is(userId));
        long total = mongoTemplate.count(query, Like.class);
        List<Like> likes = mongoTemplate.find(
                query.with(PageRequest.of(pageNum, pageSize, Sort.by(Sort.Order.desc("created")))),
                Like.class);

        return PageResult.<Like>builder().total(total).pageNum((long)pageNum).pageSize((long)pageSize)
                .hasNext((long)pageNum * pageSize < total).data(likes).build();
    }
}
