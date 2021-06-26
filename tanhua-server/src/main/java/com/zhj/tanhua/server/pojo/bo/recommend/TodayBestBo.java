package com.zhj.tanhua.server.pojo.bo.recommend;

import com.zhj.tanhua.user.enums.SexEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author huanjie.zhuang
 * @date 2021/6/13
 */
@ApiModel("今日佳人")
@Data
public class TodayBestBo {

    @ApiModelProperty(value = "用户ID")
    private Long userId;
    @ApiModelProperty(value = "头像")
    private String avatar;
    @ApiModelProperty(value = "昵称")
    private String nickName;
    @ApiModelProperty(value = "性别")
    private SexEnum sex;
    @ApiModelProperty(value = "年龄")
    private Integer age;
    @ApiModelProperty(value = "标签")
    private List<String> tags;
    @ApiModelProperty(value = "缘分值")
    private Integer fate;
}
