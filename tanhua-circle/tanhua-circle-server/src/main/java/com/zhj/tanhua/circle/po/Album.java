package com.zhj.tanhua.circle.po;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 相册表，用于存储自己发布的动态，每个用户存储一张表
 *
 * @author huanjie.zhuang
 * @date 2021/6/17
 */
@Data
@Document(collection = "circle_album")
public class Album implements Serializable {

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
     * 发布时间
     */
    private Long created;
}