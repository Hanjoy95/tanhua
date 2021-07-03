package com.zhj.tanhua.circle.api.impl;

import com.zhj.tanhua.circle.api.LoveApi;
import com.zhj.tanhua.circle.enums.QueryTypeEnum;
import com.zhj.tanhua.circle.pojo.po.*;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 喜欢dubbo接口实现
 *
 * @author huanjie.zhuang
 * @date 2021/7/3
 */
@Slf4j
@DubboService(version = "1.0")
public class LoveApiImpl implements LoveApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 喜欢某个用户
     *
     * @param lover 用户ID
     * @param beLoved 被喜欢的用户ID
     * @return 返回是否匹配成功
     */
    @Override
    public boolean addLove(Long lover, Long beLoved) {

        if (mongoTemplate.exists(Query.query(Criteria.where("lover").is(lover)
                .and("beLoved").is(beLoved)), Love.class)) {
            log.error("love has exist, lover: {}, beLoved: {}", lover, beLoved);
            throw new ResourceHasExistException("love has exist, lover: "
                    + lover + ", beLoved: " + beLoved);
        }

        // 写入喜欢表
        Love love = new Love();
        love.setLover(lover);
        love.setBeLoved(beLoved);
        love.setCreated(System.currentTimeMillis());
        mongoTemplate.save(love);

        // 匹配成功，写入好友表
        if (mongoTemplate.exists(Query.query(Criteria.where("lover").is(beLoved)
                .and("beLoved").is(lover)), Love.class)) {

            Friend friend1 = new Friend();
            friend1.setUserId(beLoved);
            friend1.setCreated(System.currentTimeMillis());
            mongoTemplate.save(friend1, Friend.TABLE_NAME_PREFIX + lover);

            Friend friend2 = new Friend();
            friend2.setUserId(lover);
            friend2.setCreated(System.currentTimeMillis());
            mongoTemplate.save(friend2, Friend.TABLE_NAME_PREFIX + beLoved);

            // 将好友发布的动态写入自己好友动态表中
            addFeeds(lover, beLoved);

            // 将自己发布的动态写入对方的好友动态表中
            addFeeds(beLoved, lover);

            return true;
        }

        return false;
    }

    /**
     * 批量添加好友动态
     *
     * @param userId1 用户ID1
     * @param userId2 用户ID2
     */
    private void addFeeds(Long userId1, Long userId2) {

        List<ObjectId> momentIds = mongoTemplate
                .findAll(Album.class, Album.TABLE_NAME_PREFIX + userId2)
                .stream().map(Album::getMomentId).collect(Collectors.toList());

        List<Feed> feeds = new ArrayList<>();
        for (ObjectId momentId : momentIds) {
            Feed feed = new Feed();
            feed.setMomentId(momentId);
            feed.setUserId(userId2);
            feed.setCreated(System.currentTimeMillis());
            feeds.add(feed);
        }

        mongoTemplate.insert(feeds, Feed.TABLE_NAME_PREFIX + userId1);
    }

    /**
     * 取消喜欢某个用户
     *
     * @param lover 用户ID
     * @param beLoved 被喜欢的用户ID
     */
    @Override
    public void deleteLove(Long lover, Long beLoved) {

        Query query = Query.query(Criteria.where("lover").is(lover).and("beLoved").is(beLoved));
        if (!mongoTemplate.exists(query, Love.class)) {
            log.error("love not found, loverId: {}, beLoverId: {}", lover, beLoved);
            throw new ResourceNotFoundException("love not found, loverId: "
                    + lover + ", beLoverId: " + beLoved);
        }
        mongoTemplate.remove(query, Love.class);
    }

    /**
     * 查询我的喜欢消息
     *
     * @param type 查询类型
     * @param userId 用户ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回喜欢信息分页结果
     */
    @Override
    public PageResult<Love> queryLoveWithType(QueryTypeEnum type, Long userId,
                                              Integer pageNum, Integer pageSize) {
        // 获取喜欢信息
        Query query = Query.query(Criteria.where(
                QueryTypeEnum.QUERY_MY_MESSAGE.equals(type) ? "beLoved" : "lover").is(userId));
        long total = mongoTemplate.count(query, Love.class);
        List<Love> loves = mongoTemplate.find(
                query.with(PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Order.desc("created")))),
                Love.class);

        return PageResult.<Love>builder().total(total).pageNum((long)pageNum).pageSize((long)pageSize)
                .hasNext((long)pageNum * pageSize < total).data(loves).build();
    }
}
