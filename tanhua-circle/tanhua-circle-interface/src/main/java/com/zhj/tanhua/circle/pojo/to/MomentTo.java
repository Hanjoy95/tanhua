package com.zhj.tanhua.circle.pojo.to;

import com.zhj.tanhua.circle.enums.SeeTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author huanjie.zhuang
 * @date 2021/6/26
 */
@Data
public class MomentTo implements Serializable {

    private String momentId;
    private Long userId;
    private String text;
    private List<String> medias;

    private SeeTypeEnum seeType;
    private List<Long> seeList;
    private List<Long> notSeeList;

    private Long likeNum;
    private Long commentNum;

    private String longitude;
    private String latitude;
    private String location;
    private Long created;
}
