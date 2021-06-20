package com.zhj.tanhua.circle.pojo.to;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author huanjie.zhuang
 * @date 2021/6/20
 */
@Data
public class FeedTo implements Serializable {

    private String publishId;
    private Long userId;
    private String text;
    private List<String> medias;
    private String longitude;
    private String latitude;
    private String location;
    private Long created;

    private Integer likeNum;
    private Integer commentNum;
    private Boolean hasLike;
    private Boolean hasComment;
}
