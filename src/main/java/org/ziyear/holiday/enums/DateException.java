package org.ziyear.holiday.enums;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 功能描述 : 日期异常
 *
 * @author zhaorui 2021-10-29 15:09
 */
public class DateException extends RuntimeException {

    public DateException(Throwable e) {
        super(ExceptionUtil.getMessage(e), e);
    }

    public DateException(String message) {
        super(message);
    }

    public DateException(String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params));
    }

    public DateException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DateException(Throwable throwable, String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params), throwable);
    }
}