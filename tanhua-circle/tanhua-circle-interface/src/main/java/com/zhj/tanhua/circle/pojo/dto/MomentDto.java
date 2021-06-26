package com.zhj.tanhua.circle.pojo.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.zhj.tanhua.circle.enums.SeeTypeEnum;
import lombok.Data;

/**
 * @author huanjie.zhuang
 * @date 2021/6/17
 */
@Data
public class MomentDto implements Serializable {

    private Long userId;
    private String text;
    private List<String> medias;
    private SeeTypeEnum seeType;
    private List<Long> seeList = Collections.emptyList();
    private List<Long> notSeeList = Collections.emptyList();
    private Long likeNum = 0L;
    private List<Long> likeUsers = Collections.emptyList();
    private String longitude;
    private String latitude;
    private String location;
}
