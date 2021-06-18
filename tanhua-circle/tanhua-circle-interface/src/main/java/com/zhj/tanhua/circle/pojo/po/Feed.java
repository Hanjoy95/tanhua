package com.zhj.tanhua.circle.pojo.po;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 动态表，用于存储好友发布的动态或推荐动态，每个用户存储一张表
 * 好友动态表表名: circle_album_{userId}, 推荐动态表表名: circle_album_recommend
 *
 * @author huanjie.zhuang
 * @date 2021/6/17
 */
@Data
@Document(collection = "circle_feed")
public class Feed implements Serializable {

    public static final String TABLE_NAME_PREFIX = "circle_feed_";
    public static final String RECOMMEND_TABLE_NAME = "circle_feed_recommend";

    /**
     * 主键ID
     */
    @Id
    private ObjectId id;
    /**
     * 发布ID
     */
    private ObjectId publishId;
    /**
     * 好友ID
     */
    private Long userId;
    /**
     * 发布时间
     */
    @Indexed
    private Long created;
}
