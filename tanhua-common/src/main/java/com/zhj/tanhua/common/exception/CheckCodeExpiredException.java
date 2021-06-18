package com.zhj.tanhua.common.exception;

import com.zhj.tanhua.common.result.ResponseStatus;

/**
 * @author huanjie.zhuang
 * @date 2021/6/18
 */
public class CheckCodeExpiredException extends BaseException {

    public CheckCodeExpiredException(String message) {
        super(message);
        this.setStatus(ResponseStatus.CHECK_CODE_EXPIRED);
    }
}
