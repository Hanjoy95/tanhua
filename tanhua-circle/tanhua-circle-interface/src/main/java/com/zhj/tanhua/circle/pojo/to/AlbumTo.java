package com.zhj.tanhua.circle.pojo.to;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author huanjie.zhuang
 * @date 2021/6/26
 */
@Data
public class AlbumTo implements Serializable {

    private String momentId;
    private String text;
    private List<String> medias;

    private Integer seeType;
    private List<Long> seeList;
    private List<Long> notSeeList;

    private Long likeNum;
    private Long commentNum;

    private String longitude;
    private String latitude;
    private String location;
    private Long created;
}
