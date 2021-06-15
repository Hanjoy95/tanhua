package com.zhj.tanhua.recommend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huanjie.zhuang
 * @date 2021/6/14
 */
@Data
@ApiModel("推荐用户")
public class RecommendUserDto {

    @ApiModelProperty(value = "推荐的用户id")
    private Long userId;
    @ApiModelProperty(value = "用户id")
    private Long toUserId;
    @ApiModelProperty(value = "缘分值")
    private Double fate;
    @ApiModelProperty(value = "日期")
    private String date;
}
