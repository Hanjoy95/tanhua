package com.zhj.tanhua.recommend.pojo.po;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @author huanjie.zhuang
 * @date 2021/6/10
 */
@Data
@Document(collection = "recommend_user")
public class RecommendUser implements Serializable {

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
     * 推荐用户ID
     */
    private Long toUserId;
    /**
     * 缘分值
     */
    @Indexed
    private Double fate;
    /**
     * 创建时间
     */
    private Long created;
}
