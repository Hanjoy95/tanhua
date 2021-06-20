package com.zhj.tanhua.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 上传图片、视频返回的结果
 *
 * @author huanjie.zhuang
 * @date 2021/6/19
 */
@Data
@ApiModel("上传文件结果")
public class UploadFileResult {

    @ApiModelProperty(value = "状态")
    private Boolean status;
    @ApiModelProperty(value = "文件名")
    private String fileName;
    @ApiModelProperty(value = "文件路径")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String fileUrl;
    @ApiModelProperty(value = "反馈信息")
    private String message;
}
