package com.zhj.tanhua.recommend.po;

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

    @Id
    private ObjectId id;
    @Indexed
    private Long userId;
    private Long toUserId;
    @Indexed
    private Double fate;
    private String date;
}
