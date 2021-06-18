package com.zhj.tanhua.server.vo.circle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author huanjie.zhuang
 * @date 2021/6/18
 */
@Data
@ApiModel("发布动态")
public class PublishVo {

    @ApiModelProperty(value = "文本内容")
    private String text;
    @ApiModelProperty(value = "图片、视频")
    private MultipartFile[] medias;
    @ApiModelProperty(value = "经度")
    private String longitude;
    @ApiModelProperty(value = "纬度")
    private String latitude;
    @ApiModelProperty(value = "位置")
    private String location;
}
