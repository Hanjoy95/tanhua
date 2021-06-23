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

    private String feedId;
    private String momentId;
    private Long userId;
    private String text;
    private List<String> medias;
    private String longitude;
    private String latitude;
    private String location;
    private Long created;

    private Long likeNum;
    private Long commentNum;
    private Boolean hasLike;
    private Boolean hasComment;
}
