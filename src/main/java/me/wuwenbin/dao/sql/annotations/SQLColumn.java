package me.wuwenbin.dao.sql.annotations;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SQLColumn {

    /**
     * whether is primary key,default is false
     *
     * @return {@link Boolean}
     */
    boolean pk() default false;

    /**
     * the name of column
     *
     * @return {@link String}
     */
    String value() default "";

    /**
     * conditions of sql router,default is minimal
     *
     * @return {@link Integer[]}
     */
    int[] routers() default Integer.MIN_VALUE;

}
