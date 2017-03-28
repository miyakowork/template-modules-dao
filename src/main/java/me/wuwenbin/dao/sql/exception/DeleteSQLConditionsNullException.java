package me.wuwenbin.dao.sql.exception;

/**
 * 异常类
 * <p>
 * Created by wuwenbin on 2017/1/12.
 */
public class DeleteSQLConditionsNullException extends Exception {
    public DeleteSQLConditionsNullException() {
        super("删除语句中条件不能为空!");
    }
}
