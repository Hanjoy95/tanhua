package com.zhj.tanhua.recommend.po;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author huanjie.zhuang
 * @date 2021/6/10
 */
@Data
@Document(collection = "recommend_user")
public class RecommendUser {

    @Id
    private Long id;
    @Indexed
    private Long userId;
    private Long toUserId;
    @Indexed
    private Double fate;
    private String date;
}
