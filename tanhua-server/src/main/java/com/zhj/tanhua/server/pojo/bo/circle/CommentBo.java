package com.zhj.tanhua.server.pojo.bo.circle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huanjie.zhuang
 * @date 2021/7/3
 */
@Data
@ApiModel("评论消息")
public class CommentBo {

    @ApiModelProperty(value = "评论ID")
    private String commentId;
    @ApiModelProperty(value = "动态ID")
    private String momentId;
    @ApiModelProperty(value = "评论时间")
    private Long created;

    @ApiModelProperty(value = "用户ID")
    private Long userId;
    @ApiModelProperty(value = "用户昵称")
    private String nickName;
    @ApiModelProperty(value = "用户头像")
    private String avatar;
}
