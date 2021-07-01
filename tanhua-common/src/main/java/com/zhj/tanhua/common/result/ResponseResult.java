package com.zhj.tanhua.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zhj.tanhua.common.exception.BaseException;
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

    public ResponseResult(ResponseStatus status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static<T> ResponseResult<T> ok() {
        return new ResponseResult<>();
    }

    public static<T> ResponseResult<T> ok(T data) {
        return new ResponseResult<>(ResponseStatus.SUCCESS, null, data);
    }

    public static<T> ResponseResult<T> fail(T data) {
        return new ResponseResult<>(ResponseStatus.INTERNAL_SERVER_ERROR, null, data);
    }

    public static<T> ResponseResult<T> fail(String message) {
        return new ResponseResult<>(ResponseStatus.INTERNAL_SERVER_ERROR, message, null);
    }

    public static<T> ResponseResult<T> fail(BaseException e) {
        return new ResponseResult<>(e.getStatus(), e.getMessage(), null);
    }
}
