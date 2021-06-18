package com.zhj.tanhua.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huanjie.zhuang
 * @date 2021/6/6
 */
@ApiModel("响应报文")
@Data
public class ResponseResult<T> {

    @ApiModelProperty(value = "状态")
    private ResponseStatus status;
    @ApiModelProperty(value = "反馈信息")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String message;
    @ApiModelProperty(value = "数据")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private T data;

    public ResponseResult() {
        this.status = ResponseStatus.SUCCESS;
    }

    public ResponseResult(T data) {
        this.status = ResponseStatus.SUCCESS;
        this.data = data;
    }

    public ResponseResult(String message) {
        this.status = ResponseStatus.SERVER_ERROR;
        this.message = message;
    }

    public ResponseResult(ResponseStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public static<T> ResponseResult<T> ok() {
        return new ResponseResult<>();
    }

    public static<T> ResponseResult<T> ok(T data) {
        return new ResponseResult<>(data);
    }

    public static<T> ResponseResult<T> fail(String message) {
        return new ResponseResult<>(message);
    }

    public static<T> ResponseResult<T> fail(ResponseStatus status, String message) {
        return new ResponseResult<>(status, message);
    }
}
