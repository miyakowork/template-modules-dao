package org.templateproject.dao.posterity.sqlite;

import org.springframework.util.Assert;
import org.templateproject.dao.posterity.PosterityDao;
import org.templateproject.pojo.page.Page;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * the implements of sqlite
 * Created by wuwenbin on 2017/3/27.
 */
public class SqliteTemplate extends PosterityDao {
    public SqliteTemplate(DataSource dataSource) {
        super(dataSource);
    }

    private static String getSqlOfSqlite(final String sql, Page page) {
        String querySql = sql;
        if (page.isFirstSetted() && page.isPageSizeSetted()) {
            querySql = querySql.concat(" LIMIT " + page.getPageSize() + " OFFSET " + page.getFirst());
        }
        return querySql;
    }

    @Override
    public Page findPageListMapByArray(String sql, Page page, Object... arrayParameters) {
        Assert.notNull(page, "分页信息不能为空");
        Assert.hasText(sql, "sql语句不正确!");
        long count;
        if (page.isAutoCount()) {
            count = queryNumberByArray(getCountSql(sql), Long.class, arrayParameters);
            page.setTotalCount((int) count);
        }
        List list = findListMapByArray(getSqlOfSqlite(sql, page), arrayParameters);
        page.setRawResult(list);
        return page;
    }

    @Override
    public Page findPageListMapByMap(String sql, Page page, Map<String, Object> mapParameter) {
        Assert.notNull(page, "分页信息不能为空");
        Assert.hasText(sql, "sql语句不正确!");
        long count;
        if (page.isAutoCount()) {
            count = queryNumberByMap(getCountSql(sql), Long.class, mapParameter);
            page.setTotalCount((int) count);
        }
        List list = findListMapByMap(getSqlOfSqlite(sql, page), mapParameter);
        page.setRawResult(list);
        return page;
    }

    @Override
    public <T> Page<T> findPageListBeanByArray(String sql, Class<T> clazz, Page<T> page, Object... arrayParameters) {
        Assert.notNull(page, "分页信息不能为空");
        Assert.hasText(sql, "sql语句不正确!");
        long count;
        if (page.isAutoCount()) {
            count = queryNumberByArray(getCountSql(sql), Long.class, arrayParameters);
            page.setTotalCount((int) count);
        }
        List list = findListBeanByArray(getSqlOfSqlite(sql, page), clazz, arrayParameters);
        page.setResult(list);
        return page;
    }

    @Override
    public <T> Page<T> findPageListBeanByMap(String sql, Class<T> clazz, Page<T> page, Map<String, Object> mapParameter) {
        Assert.notNull(page, "分页信息不能为空");
        Assert.hasText(sql, "sql语句不正确!");
        long count;
        if (page.isAutoCount()) {
            count = queryNumberByMap(getCountSql(sql), Long.class, mapParameter);
            page.setTotalCount((int) count);
        }
        List list = findListBeanByMap(getSqlOfSqlite(sql, page), clazz, mapParameter);
        page.setResult(list);
        return page;
    }

    @Override
    public <T> Page<T> findPageListBeanByBean(String sql, Class<T> clazz, Page<T> page, Object beanParameter) {
        Assert.notNull(page, "分页信息不能为空");
        Assert.hasText(sql, "sql语句不正确!");
        long count;
        if (page.isAutoCount()) {
            count = queryNumberByBean(getCountSql(sql), Long.class, beanParameter);
            page.setTotalCount((int) count);
        }
        List list = findListBeanByBean(getSqlOfSqlite(sql, page), clazz, beanParameter);
        page.setResult(list);
        return page;
    }
}
