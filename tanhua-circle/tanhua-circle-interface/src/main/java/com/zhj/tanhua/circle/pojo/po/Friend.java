package com.zhj.tanhua.circle.pojo.po;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 好友表，每个用户存储一张表
 * 表名: circle_friend_{userId}
 *
 * @author huanjie.zhuang
 * @date 2021/6/17
 */
@Data
@Document(collection = "circle_friend")
public class Friend implements Serializable {

    public static final String TABLE_NAME_PREFIX = "circle_friend_";

    /**
     * 主键ID
     */
    @Id
    private ObjectId id;
    /**
     * 好友ID
     */
    private Long userId;
    /**
     * 添加时间
     */
    @Indexed
    private Long created;
}
