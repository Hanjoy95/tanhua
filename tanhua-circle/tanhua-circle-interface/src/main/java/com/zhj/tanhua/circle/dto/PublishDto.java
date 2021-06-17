package com.zhj.tanhua.circle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

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
    @ApiModelProperty(value = "媒体数据，图片或小视频（url）")
    private List<String> medias;
    @ApiModelProperty(value = "谁可以看，1-公开，2-私密，3-部分可见，4-不给谁看")
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
    @ApiModelProperty(value = "发布时间", hidden = true)
    private Long created;
}
