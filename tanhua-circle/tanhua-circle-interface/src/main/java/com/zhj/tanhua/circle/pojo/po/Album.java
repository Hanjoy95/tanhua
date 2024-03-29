package com.zhj.tanhua.circle.pojo.po;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 相册表，用于存储自己的动态，每个用户存储一张表
 * 表名: circle_album_{userId}
 *
 * @author huanjie.zhuang
 * @date 2021/6/17
 */
@Data
@Document(collection = "circle_album")
public class Album implements Serializable {

    public static final String TABLE_NAME_PREFIX = "circle_album_";

    /**
     * 主键ID
     */
    @Id
    private ObjectId id;
    /**
     * 动态ID
     */
    private ObjectId momentId;
    /**
     * 发布时间
     */
    @Indexed
    private Long created;
}
