package com.zhj.tanhua.circle.pojo.po;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 评论表
 *
 * @author huanjie.zhuang
 * @date 2021/6/17
 */
@Data
@Document(collection = "circle_comment")
public class Comment implements Serializable {

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
     * 评论人ID
     */
    private Long userId;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 是否为父节点
     */
    private Boolean isParent = false;
    /**
     * 父节点ID
     */
    private ObjectId parentId;
    /**
     * 发表时间
     */
    private Long created;
}
