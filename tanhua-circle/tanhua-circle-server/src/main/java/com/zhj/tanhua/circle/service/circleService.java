package com.zhj.tanhua.circle.service;

import com.zhj.tanhua.circle.api.circleApi;
import com.zhj.tanhua.circle.dto.PublishDto;
import com.zhj.tanhua.circle.po.Album;
import com.zhj.tanhua.circle.po.Feed;
import com.zhj.tanhua.circle.po.Friend;
import com.zhj.tanhua.circle.po.Publish;
import com.zhj.tanhua.common.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huanjie.zhuang
 * @date 2021/6/17
 */
@Slf4j
@DubboService(version = "1.0")
public class circleService implements circleApi {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 保存用户发布动态
     *
     * @param publishDto 发布内容
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePublish(PublishDto publishDto) {

        // 写入发布表中
        Publish publish = new Publish();
        BeanUtils.copyProperties(publishDto, publish);
        publish.setCreated(System.currentTimeMillis());
        mongoTemplate.save(publishDto);

        // 写入自己的相册表表中
        Album album = new Album();
        album.setPublishId(publish.getId());
        album.setCreated(System.currentTimeMillis());
        mongoTemplate.save(album, Album.TABLE_NAME_PREFIX + publish.getUserId());

        // 查询当前用户好友，将动态数据写入到好友的动态表中
        Query query = Query.query(Criteria.where("userId").is(publish.getUserId()));
        List<Friend> friends = mongoTemplate.find(query, Friend.class);
        for (Friend friend : friends) {
            Feed feed = new Feed();
            feed.setUserId(publish.getUserId());
            feed.setPublishId(publish.getId());
            feed.setCreated(System.currentTimeMillis());
            mongoTemplate.save(feed, Feed.TABLE_NAME_PREFIX + friend.getFriendId());
        }
    }

    /**
     * 查询好友动态
     *
     * @param userId 用户ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return PageResult<PublishDto>
     */
    @Override
    public PageResult<PublishDto> queryPublishList(Long userId, Integer pageNum, Integer pageSize) {

        String tableName;
        if (null == userId) {
            // 推荐动态表名
            tableName = Feed.RECOMMEND_TABLE_NAME;
        } else {
            // 好友动态表名
            tableName = Feed.TABLE_NAME_PREFIX + userId;
        }

        // 查询自己的好友动态表
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Order.desc("created")));
        Query queryFeed = new Query().with(pageable);
        long total = mongoTemplate.count(queryFeed, Feed.class);
        List<Feed> feeds = mongoTemplate.find(queryFeed, Feed.class, tableName);
        List<ObjectId> publishIds = new ArrayList<>();
        for (Feed feed : feeds) {
            publishIds.add(feed.getPublishId());
        }

        // 查询动态信息
        Query queryPublish = Query.query(Criteria.where("id").in(publishIds)).with(Sort.by(Sort.Order.desc("created")));
        List<Publish> publishList = mongoTemplate.find(queryPublish, Publish.class);

        // 转换动态信息
        List<PublishDto> publishDtoList = new ArrayList<>();
        for (Publish publish : publishList) {
            PublishDto publishDto = new PublishDto();
            BeanUtils.copyProperties(publish, publishDto);
            publishDtoList.add(publishDto);
        }

        return PageResult.<PublishDto>builder().total(total).pageNum((long) pageNum).pageSize((long) pageSize)
                .hasNext((long) pageNum * pageSize < total).data(publishDtoList).build();
    }
}
