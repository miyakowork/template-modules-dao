package me.wuwenbin.dao.sql.exception;

/**
 * 异常类
 *
 * Created by wuwenbin on 2017/1/12.
 */
public class UpdateColumnNullException extends Exception {
    public UpdateColumnNullException() {
        super("update语句更新字段不能为空!");
    }
}
