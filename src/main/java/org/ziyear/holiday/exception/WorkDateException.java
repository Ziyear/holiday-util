package org.ziyear.holiday.exception;

/**
 * 功能描述 : 日期异常
 *
 * @author zhaorui 2021-10-29 14:40
 */
public class WorkDateException extends RuntimeException {
    public WorkDateException() {
        super();
    }

    public WorkDateException(String msg) {
        super(msg);
    }

    public WorkDateException(String msg, Throwable e) {
        super(msg, e);
    }
}
