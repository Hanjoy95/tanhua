package com.zhj.tanhua.circle.pojo.po;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 好友表
 *
 * @author huanjie.zhuang
 * @date 2021/6/17
 */
@Data
@Document(collection = "circle_friend")
public class Friend implements Serializable {

    /**
     * 主键ID
     */
    @Id
    private ObjectId id;
    /**
     * 用户ID
     */
    @Indexed
    private Long userId;
    /**
     * 好友ID
     */
    private Long friendId;
    /**
     * 创建时间
     */
    private Long created;
}
