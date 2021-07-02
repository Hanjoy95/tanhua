package com.zhj.tanhua.common.exception;

import com.zhj.tanhua.common.enums.ResponseStatus;

/**
 * @author huanjie.zhuang
 * @date 2021/6/14
 */
public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String message) {
        super(message);
        this.setStatus(ResponseStatus.RESOURCE_NOT_FOUND);
    }
}
