package com.zhj.tanhua.server.pojo.bo.circle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huanjie.zhuang
 * @date 2021/7/3
 */
@Data
@ApiModel("点赞")
public class LikeBo {

    @ApiModelProperty(value = "点赞ID")
    private String likeId;
    @ApiModelProperty(value = "点赞人ID")
    private Long liker;
    @ApiModelProperty(value = "被点赞人ID")
    private Long beLiked;
    @ApiModelProperty(value = "动态ID")
    private String momentId;
    @ApiModelProperty(value = "点赞时间")
    private Long created;

    @ApiModelProperty(value = "用户昵称")
    private String nickName;
    @ApiModelProperty(value = "用户头像")
    private String avatar;
}
