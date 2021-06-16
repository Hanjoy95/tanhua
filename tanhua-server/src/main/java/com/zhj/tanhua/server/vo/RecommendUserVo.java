package com.zhj.tanhua.server.vo;

import com.zhj.tanhua.user.enums.SexEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huanjie.zhuang
 * @date 2021/6/13
 */
@ApiModel("推荐用户")
@Data
public class RecommendUserVo {

    @ApiModelProperty(value = "当前页, 默认为1")
    private Integer pageNum = 1;
    @ApiModelProperty(value = "页大小, 默认为10")
    private Integer pageSize = 10;
    @ApiModelProperty(value = "性别, 填man或woman")
    private SexEnum sex;
    @ApiModelProperty(value = "最近登录时间", hidden = true)
    private String lastLogin;
    @ApiModelProperty(value = "年龄")
    private Integer age;
    @ApiModelProperty(value = "居住城市")
    private String city;
    @ApiModelProperty(value = "学历")
    private String edu;
}
