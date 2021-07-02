package com.zhj.tanhua.common.exception;

import com.zhj.tanhua.common.enums.ResponseStatus;

/**
 * @author huanjie.zhuang
 * @date 2021/7/1
 */
public class EnumConvertException extends BaseException {

    public EnumConvertException(String message) {
        super(message);
        this.setStatus(ResponseStatus.ENUM_CONVERT_ERROR);
    }
}
