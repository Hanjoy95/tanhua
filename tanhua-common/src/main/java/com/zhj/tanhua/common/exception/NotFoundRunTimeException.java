package com.zhj.tanhua.common.exception;

import com.zhj.tanhua.common.vo.ResponseResult;

/**
 * @author huanjie.zhuang
 * @date 2021/6/14
 */
public class NotFoundRunTimeException extends BaseRunTimeException {

    public NotFoundRunTimeException(String message) {
        super(message);
        this.setErrorCode(ResponseResult.NOT_FOUND);
    }

    public NotFoundRunTimeException(Throwable cause) {
        super(cause);
        this.setErrorCode(ResponseResult.NOT_FOUND);
    }

    public NotFoundRunTimeException(String message, Throwable cause) {
        super(message, cause);
        this.setErrorCode(ResponseResult.NOT_FOUND);
    }
}
