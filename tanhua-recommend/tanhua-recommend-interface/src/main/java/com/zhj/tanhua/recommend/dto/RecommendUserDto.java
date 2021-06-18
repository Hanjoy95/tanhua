package com.zhj.tanhua.recommend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author huanjie.zhuang
 * @date 2021/6/14
 */
@Data
@ApiModel("推荐用户")
public class RecommendUserDto implements Serializable {

    @ApiModelProperty(value = "推荐用户ID")
    private Long userId;
    @ApiModelProperty(value = "用户ID")
    private Long toUserId;
    @ApiModelProperty(value = "缘分值")
    private Double fate;
    @ApiModelProperty(value = "日期")
    private Long created;
}
