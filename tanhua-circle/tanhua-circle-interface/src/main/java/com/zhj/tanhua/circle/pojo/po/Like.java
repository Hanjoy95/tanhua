package com.zhj.tanhua.circle.pojo.po;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 点赞表
 *
 * @author huanjie.zhuang
 * @date 2021/6/27
 */
@Data
@Document(collection = "circle_like")
public class Like implements Serializable {

    /**
     * 主键ID
     */
    @Id
    private ObjectId id;
    /**
     * 点赞人ID
     */
    private Long liker;
    /**
     * 被点赞人ID
     */
    @Indexed
    private Long beLiked;
    /**
     * 动态ID
     */
    @Indexed
    private ObjectId momentId;
    /**
     * 创建时间
     */
    @Indexed
    private Long created;
}
