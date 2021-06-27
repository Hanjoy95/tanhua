package com.zhj.tanhua.recommend.service;

import com.zhj.tanhua.common.exception.ResourceNotFoundException;
import com.zhj.tanhua.common.result.PageResult;
import com.zhj.tanhua.recommend.api.RecommendUserApi;
import com.zhj.tanhua.recommend.pojo.po.RecommendUser;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author huanjie.zhuang
 * @date 2021/6/12
 */
@DubboService(version = "1.0")
public class RecommendUserService implements RecommendUserApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 获取最佳推荐用户
     *
     * @param userId 用户ID
     * @return RecommendUser
     */
    @Override
    public RecommendUser getBestRecommendUser(Long userId) {

        Criteria criteria = Criteria.where("toUserId").is(userId);
        Query query = Query.query(criteria).with(Sort.by(Sort.Order.desc("fate"))).limit(1);

        RecommendUser recommendUser = mongoTemplate.findOne(query, RecommendUser.class);
        if (null == recommendUser) {
            throw new ResourceNotFoundException("the best recommendUser not found to userId: " + userId);
        }

        return recommendUser;
    }

    /**
     * 获取推荐用户列表
     *
     * @param userId 用户ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return PageResult<RecommendUser>
     */
    @Override
    public PageResult<RecommendUser> getRecommendUsers(Long userId, Integer pageNum, Integer pageSize) {

        Criteria criteria = Criteria.where("toUserId").is(userId);
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Order.desc("fate")));
        Query query = Query.query(criteria);

        long total = mongoTemplate.count(query, RecommendUser.class);
        List<RecommendUser> recommendUsers = mongoTemplate.find(query.with(pageable), RecommendUser.class);
        if (CollectionUtils.isEmpty(recommendUsers)) {
            throw new ResourceNotFoundException("the recommendUser list not found to userId: " + userId);
        }

        return PageResult.<RecommendUser>builder()
                .total(total)
                .pageNum((long) pageNum)
                .pageSize((long) pageSize)
                .hasNext((long) pageNum * pageSize < total)
                .data(recommendUsers)
                .build();
    }
}
