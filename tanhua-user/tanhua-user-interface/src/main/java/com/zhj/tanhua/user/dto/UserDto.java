package com.zhj.tanhua.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Long id;
    @ApiModelProperty(value = "手机号")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String phone;
    @ApiModelProperty(value = "是否新用户")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Boolean isNew;
    @ApiModelProperty(value = "验证码")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String checkCode;
    @ApiModelProperty(value = "token")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String token;
}
