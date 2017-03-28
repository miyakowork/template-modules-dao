package me.wuwenbin.dao.sql.exception;

/**
 * 异常类
 * <p>
 * Created by wuwenbin on 2017/1/11.
 */
public class NotSetPrimaryKeyException extends Exception {
    public NotSetPrimaryKeyException(Class<?> clazz) {
        super(clazz.getName() + " 中没有设置主键对应的属性字段!");
    }
}
