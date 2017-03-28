package me.wuwenbin.dao.sql.exception;

/**
 * 异常类
 * <p>
 * Created by wuwenbin on 2017/1/12.
 */
public class InsertColumnNullException extends Exception {
    public InsertColumnNullException() {
        super("插入的字段不能小于1列 !");
    }
}
