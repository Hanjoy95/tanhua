package com.zhj.tanhua.circle.pojo.po;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 喜欢表
 *
 * @author huanjie.zhuang
 * @date 2021/6/22
 */
@Data
@Document(collection = "circle_love")
public class Love implements Serializable {

    /**
     * 主键ID
     */
    @Id
    private ObjectId id;
    /**
     * 用户ID
     */
    @Indexed
    private Long loverId;
    /**
     * 被喜欢的用户ID
     */
    @Indexed
    private Long beLoverId;
    /**
     * 创建时间
     */
    @Indexed
    private Long created;
}
