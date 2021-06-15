package com.zhj.tanhua.common.vo;

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

    @ApiModelProperty(value = "状态, 成功为ture, 失败为false")
    private Boolean status;
    @ApiModelProperty(value = "状态码")
    private String code;
    @ApiModelProperty(value = "数据")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private T data;
    @ApiModelProperty(value = "报错信息")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String message;

    public static final String SUCCESS = "success";
    public static final String INTERNAL_ERROR = "internal_error";
    public static final String NOT_FOUND = "not_found";
    public static final String ALREADY_EXIST = "already_exist";
    public static final String INVALID_PARAMETER = "invalid_parameter";

    public ResponseResult() {
        this.status = true;
        this.code = SUCCESS;
    }

    public ResponseResult(T data) {
        this.status = true;
        this.code = SUCCESS;
        this.data = data;
    }

    public ResponseResult(String message) {
        this.status = false;
        this.code = INTERNAL_ERROR;
        this.message = message;
    }

    public ResponseResult(String code, String message) {
        this.status = true;
        this.code = code;
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

    public static<T> ResponseResult<T> fail(String code, String message) {
        return new ResponseResult<>(code, message);
    }
}
