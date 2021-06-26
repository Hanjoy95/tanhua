package com.zhj.tanhua.server.pojo.vo.circle;

import com.zhj.tanhua.common.enums.FileTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 发布动态请求体
 *
 * @author huanjie.zhuang
 * @date 2021/6/18
 */
@Data
@ApiModel("发布动态")
public class MomentVo {

    @ApiModelProperty(value = "文本内容")
    private String text;
    @ApiModelProperty(value = "图片、视频")
    private List<MultipartFile> medias;
    @ApiModelProperty(value = "文件类型")
    private FileTypeEnum fileType;

    @ApiModelProperty(value = "谁可以看，0-公开，1-私密，2-部分可见，3-不给谁看")
    private Integer seeType;
    @ApiModelProperty(value = "部分可见的用户ID列表")
    private List<Long> seeList;
    @ApiModelProperty(value = "不给谁看的用户ID列表")
    private List<Long> notSeeList;

    @ApiModelProperty(value = "经度")
    private String longitude;
    @ApiModelProperty(value = "纬度")
    private String latitude;
    @ApiModelProperty(value = "位置")
    private String location;

    @ApiModelProperty(value = "是否重新发布")
    private Boolean isRePublish = false;
    @ApiModelProperty(value = "重新上传ID")
    private String rePublishId;
}
