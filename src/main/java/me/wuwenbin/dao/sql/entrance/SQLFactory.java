package me.wuwenbin.dao.sql.entrance;

import me.wuwenbin.dao.sql.factory.SQLStrBuilder;
import me.wuwenbin.dao.sql.factory.SQLBeanBuilder;

/**
 * entrance for sql util
 * Created by wuwenbin on 2017/1/12.
 *
 * @author wuwenbin
 * @since 1.1.0
 */
public final class SQLFactory {

    private volatile static SQLBeanBuilder sqlBeanBuilder;
    private volatile static SQLStrBuilder sqlStrBuilder;

    private SQLFactory() {
    }

    /**
     * generate {@link SQLBeanBuilder} instance
     *
     * @param #clazz 对应实体类
     * @return {@link SQLBeanBuilder}
     */
    public static SQLBeanBuilder builder(Class<?> clazz) {
        if (sqlBeanBuilder == null) {
            synchronized (SQLBeanBuilder.class) {
                if (sqlBeanBuilder == null) {
                    sqlBeanBuilder = new SQLBeanBuilder(clazz);
                }
            }
        } else if (clazz != sqlBeanBuilder.getBeanClass()) {
            sqlBeanBuilder = new SQLBeanBuilder(clazz);
        }
        return sqlBeanBuilder;
    }

    /**
     * generate {@link SQLStrBuilder} instance
     *
     * @return {@link SQLStrBuilder}
     */
    public static SQLStrBuilder builder() {
        if (sqlStrBuilder == null) {
            synchronized (SQLStrBuilder.class) {
                if (sqlStrBuilder == null) {
                    sqlStrBuilder = new SQLStrBuilder();
                }
            }
        }
        return sqlStrBuilder;
    }

}
