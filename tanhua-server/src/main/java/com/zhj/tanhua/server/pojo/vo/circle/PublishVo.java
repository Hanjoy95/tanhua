package com.zhj.tanhua.server.pojo.vo.circle;

import com.zhj.tanhua.common.enums.FileTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 发布动态所需要前端传的参数
 *
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
    @ApiModelProperty(value = "文件类型")
    private FileTypeEnum fileType;
    @ApiModelProperty(value = "是否重新发布")
    private Boolean isRePublish = false;
    @ApiModelProperty(value = "重新上传ID")
    private String rePublishId;
    @ApiModelProperty(value = "经度")
    private String longitude;
    @ApiModelProperty(value = "纬度")
    private String latitude;
    @ApiModelProperty(value = "位置")
    private String location;
}
