package com.zhj.tanhua.server.pojo.vo.circle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 评论请求体
 *
 * @author huanjie.zhuang
 * @date 2021/6/26
 */
@Data
@ApiModel("评论")
public class CommentVo {

    @ApiModelProperty(value = "动态ID")
    private String momentId;
    @ApiModelProperty(value = "评论ID，若是评论动态或查询某条动态的评论，忽略该属性")
    private String commentId = null;
    @ApiModelProperty(value = "评论内容")
    private String content;
}
