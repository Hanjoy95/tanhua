package com.zhj.tanhua.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author huanjie.zhuang
 * @date 2021/6/12
 */
@Data
@Builder
@ApiModel("用户信息")
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {

    @ApiModelProperty(value = "用户ID")
    private Long id;
    @ApiModelProperty(value = "手机号")
    private String phone;
    @ApiModelProperty(value = "是否新用户")
    private Boolean isNew;
    @ApiModelProperty(value = "验证码")
    private String checkCode;
    @ApiModelProperty(value = "token")
    private String token;
}
