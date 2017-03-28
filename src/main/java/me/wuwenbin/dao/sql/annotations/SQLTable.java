package me.wuwenbin.dao.sql.annotations;

import java.lang.annotation.*;

/**
 * @see SQLTable
 * Created by wuwenbin on 2016/10/11.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SQLTable {

    String value() default "";

}
