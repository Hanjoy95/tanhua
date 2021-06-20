package com.zhj.tanhua.server.pojo.vo.circle;

import com.zhj.tanhua.user.enums.SexEnum;
import com.zhj.tanhua.user.enums.StatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 好友或推荐动态
 *
 * @author huanjie.zhuang
 * @date 2021/6/19
 */
@Data
@ApiModel("好友或推荐动态")
public class FeedVo {

    @ApiModelProperty(value = "发布ID")
    private String publishId;
    @ApiModelProperty(value = "文字动态")
    private String text;
    @ApiModelProperty(value = "图片或视频动态")
    private List<String> medias;

    @ApiModelProperty(value = "点赞数")
    private Integer likeNum;
    @ApiModelProperty(value = "评论数")
    private Integer commentNum;
    @ApiModelProperty(value = "是否点赞")
    private Boolean hasLike;
    @ApiModelProperty(value = "是否评论")
    private Boolean hasComment;
    @ApiModelProperty(value = "距离")
    private String distance;
    @ApiModelProperty(value = "发布时间")
    private String created;

    @ApiModelProperty(value = "好友ID")
    private Long userId;
    @ApiModelProperty(value = "头像")
    private String avatar;
    @ApiModelProperty(value = "昵称")
    private String nickname;
    @ApiModelProperty(value = "性别")
    private SexEnum sex;
    @ApiModelProperty(value = "年龄")
    private Integer age;
    @ApiModelProperty(value = "标签")
    private List<String> tags;
    @ApiModelProperty(value = "状态")
    private StatusEnum status;
}
