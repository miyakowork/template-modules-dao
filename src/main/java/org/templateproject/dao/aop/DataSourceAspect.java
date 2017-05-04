package org.templateproject.dao.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.templateproject.dao.annotation.DynamicDataSource;
import org.templateproject.dao.exception.DataSourceKeyNotExistException;
import org.templateproject.dao.factory.DaoFactory;
import org.templateproject.dao.factory.support.KeyContextHolder;

import java.lang.reflect.Method;


/**
 * Dynamic dataSource controller by AOP
 * <p>
 * Created by wuwenbin on 2017/1/3.
 *
 * @author wuwenbin
 * @since 1.0.0
 */
@Aspect
@Component
@Order(1)
public class DataSourceAspect {

    @Autowired
    DaoFactory daoFactory;

    /**
     * define point aspect,if a method has {@link DynamicDataSource} annotation,it starts aop method to filter it
     */
    @Pointcut("@annotation(org.templateproject.dao.annotation.DynamicDataSource)")
    public void DynamicDataSourceAspect() {
    }

    /**
     * before method to switch dataSource
     *
     * @param #joinPoint joinPoint
     * @throws NoSuchMethodException
     * @throws DataSourceKeyNotExistException
     */
    @Before("DynamicDataSourceAspect()")
    public void switchDataSource(JoinPoint joinPoint) throws NoSuchMethodException, DataSourceKeyNotExistException {
        Class clazz = joinPoint.getTarget().getClass();
        String methodName = joinPoint.getSignature().getName();
        Class[] argClass = ((MethodSignature) joinPoint.getSignature()).getParameterTypes();
        Method method = clazz.getMethod(methodName, argClass);
        if (method.isAnnotationPresent(DynamicDataSource.class)) {
            String dataSourceKey = method.getAnnotation(DynamicDataSource.class).value();
            KeyContextHolder.setKey(dataSourceKey);
            daoFactory.determineTargetDao();
        }
    }

    /**
     * after method to switch back dataSource to default
     *
     * @param #joinPoint
     * @throws NoSuchMethodException
     */
    @After("DynamicDataSourceAspect()")
    public void rollbackDataSource2Default(JoinPoint joinPoint) throws NoSuchMethodException {
        Class clazz = joinPoint.getTarget().getClass();
        String methodName = joinPoint.getSignature().getName();
        Class[] argClass = ((MethodSignature) joinPoint.getSignature()).getParameterTypes();
        Method method = clazz.getMethod(methodName, argClass);
        if (method.isAnnotationPresent(DynamicDataSource.class)) {
            daoFactory.dynamicDao = daoFactory.defaultDao;
            KeyContextHolder.clearKey();
        }
    }
}
