package org.templateproject.dao.annotation;

import java.lang.annotation.*;

/**
 * Created by wuwenbin on 2017/1/3.
 *
 * @author wuwenbin
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DynamicDataSource {

    /**
     * the name of dataSource's key
     *
     * @return {@link java.lang.String}
     */
    String value() default "";
}
