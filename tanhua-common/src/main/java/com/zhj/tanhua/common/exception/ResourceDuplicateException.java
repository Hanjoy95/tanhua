package com.zhj.tanhua.common.exception;

import com.zhj.tanhua.common.result.ResponseStatus;

/**
 * @author huanjie.zhuang
 * @date 2021/6/18
 */
public class ResourceDuplicateException extends BaseException {

    public ResourceDuplicateException(String message) {
        super(message);
        this.setStatus(ResponseStatus.RESOURCE_DUPLICATE);
    }
}
