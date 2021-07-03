package com.zhj.tanhua.server.pojo.bo.circle;

import com.zhj.tanhua.user.enums.SexEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author huanjie.zhuang
 * @date 2021/7/3
 */
@Data
@ApiModel("喜欢")
public class LoveBo {

    @ApiModelProperty(value = "喜欢ID")
    private String loveId;
    @ApiModelProperty(value = "喜欢时间")
    private Long created;

    @ApiModelProperty(value = "用户ID")
    private Long userId;
    @ApiModelProperty(value = "昵称")
    private String nickName;
    @ApiModelProperty(value = "头像")
    private String avatar;
    @ApiModelProperty(value = "标签")
    private List<String> tags;
    @ApiModelProperty(value = "性别")
    private SexEnum sex;
    @ApiModelProperty(value = "年龄")
    private Integer age;
}
