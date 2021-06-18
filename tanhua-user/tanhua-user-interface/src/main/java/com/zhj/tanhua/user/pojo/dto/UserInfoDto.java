package com.zhj.tanhua.user.pojo.dto;

import com.zhj.tanhua.user.enums.SexEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author huanjie.zhuang
 * @date 2021/6/18
 */
@Data
@ApiModel("用户信息")
public class UserInfoDto implements Serializable {

    @ApiModelProperty(value = "用户ID", hidden = true)
    private Long userId;
    @ApiModelProperty(value = "昵称")
    private String nickName;
    @ApiModelProperty(value = "头像", hidden = true)
    private String avatar;
    @ApiModelProperty(value = "标签,用逗号隔开")
    private String tags;
    @ApiModelProperty(value = "性别")
    private SexEnum sex;
    @ApiModelProperty(value = "年龄")
    private Integer age;
    @ApiModelProperty(value = "学历")
    private String edu;
    @ApiModelProperty(value = "学校")
    private String school;
    @ApiModelProperty(value = "城市")
    private String city;
    @ApiModelProperty(value = "生日")
    private String birthday;
    @ApiModelProperty(value = "封面图片", hidden = true)
    private String coverPic;
    @ApiModelProperty(value = "行业")
    private String industry;
    @ApiModelProperty(value = "收入")
    private String income;
    @ApiModelProperty(value = "单身状况")
    private String single;
}
