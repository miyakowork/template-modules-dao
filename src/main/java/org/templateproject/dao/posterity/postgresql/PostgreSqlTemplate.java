package org.templateproject.dao.posterity.postgresql;

import org.templateproject.dao.posterity.PosterityDao;
import org.springframework.util.Assert;
import org.templateproject.pojo.page.Page;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;


/**
 * the implements of postgresql
 * Created by wuwenbin on 2017/4/13.
 */
public final class PostgreSqlTemplate extends PosterityDao {

    public PostgreSqlTemplate(DataSource dataSource) {
        super(dataSource);
    }

    private static String getSqlOfPostgresql(final String sql, Page page) {
        String querySql = sql;
        if (page.isFirstSetted() && page.isPageSizeSetted()) {
            querySql = querySql.concat(" LIMIT " + page.getPageSize() + " OFFSET " + page.getFirst());
        }
        return querySql;
    }

    public Page findPageListBeanByArray(final String sql, Class clazz, Page page, Object... arrayParameters) {
        Assert.notNull(page, "分页信息不能为空");
        Assert.hasText(sql, "sql语句不正确!");
        long count = 0;
        if (page.isAutoCount()) {
            count = queryNumberByArray(getCountSql(sql), Long.class, arrayParameters);
            page.setTotalCount((int) count);
        }
        List list = findListBeanByArray(getSqlOfPostgresql(sql, page), clazz, arrayParameters);
        page.setResult(list);
        return page;
    }

    public Page findPageListMapByArray(final String sql, Page page, Object... arrayParameters) {
        Assert.notNull(page, "分页信息不能为空");
        Assert.hasText(sql, "sql语句不正确!");
        long count = 0;
        if (page.isAutoCount()) {
            count = queryNumberByArray(getCountSql(sql), Long.class, arrayParameters);
            page.setTotalCount((int) count);
        }
        List list = findListMapByArray(getSqlOfPostgresql(sql, page), arrayParameters);
        page.setResult(list);
        return page;
    }


    public <T> Page findPageListBeanByBean(String sql, Class<T> clazz, Page page, Object beanParameters) {
        Assert.notNull(page, "分页信息不能为空");
        Assert.hasText(sql, "sql语句不正确!");
        long count = 0;
        if (page.isAutoCount()) {
            count = queryNumberByBean(getCountSql(sql), Long.class, beanParameters);
            page.setTotalCount((int) count);
        }
        List list = findListBeanByBean(getSqlOfPostgresql(sql, page), clazz, beanParameters);
        page.setResult(list);
        return page;
    }

    public <T> Page findPageListBeanByMap(String sql, Class<T> clazz, Page page, Map<String, Object> mapParameters) {
        Assert.notNull(page, "分页信息不能为空");
        Assert.hasText(sql, "sql语句不正确!");
        long count = 0;
        if (page.isAutoCount()) {
            count = queryNumberByMap(getCountSql(sql), Long.class, mapParameters);
            page.setTotalCount((int) count);
        }
        List list = findListBeanByMap(getSqlOfPostgresql(sql, page), clazz, mapParameters);
        page.setResult(list);
        return page;
    }

    public Page findPageListMapByMap(String sql, Page page, Map<String, Object> mapParameters) {
        Assert.notNull(page, "分页信息不能为空");
        Assert.hasText(sql, "sql语句不正确!");
        long count = 0;
        if (page.isAutoCount()) {
            count = queryNumberByMap(getCountSql(sql), Long.class, mapParameters);
            page.setTotalCount((int) count);
        }
        List list = findListMapByMap(getSqlOfPostgresql(sql, page), mapParameters);
        page.setResult(list);
        return page;
    }


}
