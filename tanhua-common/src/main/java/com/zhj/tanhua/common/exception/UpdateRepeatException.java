package com.zhj.tanhua.common.exception;

import com.zhj.tanhua.common.result.ResponseStatus;

/**
 * @author huanjie.zhuang
 * @date 2021/6/22
 */
public class UpdateRepeatException extends BaseException {

    public UpdateRepeatException(String message) {
        super(message);
        this.setStatus(ResponseStatus.UPDATE_REPEAT_ERROR);
    }
}
