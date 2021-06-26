package com.zhj.tanhua.server.pojo.vo.circle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zhj.tanhua.common.result.UploadFileResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 发布动态失败时会返回
 *
 * @author huanjie.zhuang
 * @date 2021/6/19
 */
@Data
@ApiModel("发布动态失败结果")
public class MomentFailedVo {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ApiModelProperty(value = "动态ID")
    private String momentId;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ApiModelProperty(value = "重新发布ID")
    private String rePublishId;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ApiModelProperty(value = "上传失败的文件")
    private List<UploadFileResult> uploadFailedFiles;
}
