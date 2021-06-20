package com.zhj.tanhua.circle.pojo.dto;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huanjie.zhuang
 * @date 2021/6/17
 */
@Data
@ApiModel("用户发布动态")
public class PublishDto implements Serializable {

    @ApiModelProperty(value = "用户ID")
    private Long userId;
    @ApiModelProperty(value = "发布内容")
    private String text;
    @ApiModelProperty(value = "媒体数据，图片或小视频（url）", hidden = true)
    private List<String> medias;
    @ApiModelProperty(value = "谁可以看，0-公开，1-私密，2-部分可见，3-不给谁看")
    private Integer seeType;
    @ApiModelProperty(value = "部分可见的用户ID列表")
    private List<Long> seeList;
    @ApiModelProperty(value = "不给谁看的用户ID列表")
    private List<Long> notSeeList;
    @ApiModelProperty(value = "经度")
    private String longitude;
    @ApiModelProperty(value = "纬度")
    private String latitude;
    @ApiModelProperty(value = "位置")
    private String location;
}
