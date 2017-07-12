package org.templateproject.dao.posterity.mysql;

import org.springframework.util.Assert;
import org.templateproject.dao.posterity.PosterityDao;
import org.templateproject.pojo.page.Page;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * the implement of mysql
 * <p>
 * Created by wuwenbin on 2017/3/27.
 */
public class MysqlTemplate extends PosterityDao {

    public MysqlTemplate(DataSource dataSource) {
        super(dataSource);
    }

    private static String getSqlOfMySQL(final String sql, Page page) {
        String querySql = sql;
        if (page.isFirstSetted() && page.isPageSizeSetted()) {
            querySql = querySql.concat(" LIMIT " + page.getFirst() + "," + page.getPageSize());
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
        List<Map<String, Object>> list = findListMapByArray(getSqlOfMySQL(sql, page), arrayParameters);
        page.setResult(list);
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
        List<Map<String, Object>> list = findListMapByMap(getSqlOfMySQL(sql, page), mapParameter);
        page.setResult(list);
        return page;
    }

    @Override
    public <T> Page findPageListBeanByArray(String sql, Class<T> clazz, Page page, Object... arrayParameters) {
        Assert.notNull(page, "分页信息不能为空");
        Assert.hasText(sql, "sql语句不正确!");
        long count;
        if (page.isAutoCount()) {
            count = queryNumberByArray(getCountSql(sql), Long.class, arrayParameters);
            page.setTotalCount((int) count);
        }
        List<T> list = findListBeanByArray(getSqlOfMySQL(sql, page), clazz, arrayParameters);
        page.setResult(list);
        return page;
    }

    @Override
    public <T> Page findPageListBeanByMap(String sql, Class<T> clazz, Page page, Map<String, Object> mapParameter) {
        Assert.notNull(page, "分页信息不能为空");
        Assert.hasText(sql, "sql语句不正确!");
        long count;
        if (page.isAutoCount()) {
            count = queryNumberByMap(getCountSql(sql), Long.class, mapParameter);
            page.setTotalCount((int) count);
        }
        List<T> list = findListBeanByMap(getSqlOfMySQL(sql, page), clazz, mapParameter);
        page.setResult(list);
        return page;
    }

    @Override
    public <T> Page findPageListBeanByBean(String sql, Class<T> clazz, Page page, Object beanParameter) {
        Assert.notNull(page, "分页信息不能为空");
        Assert.hasText(sql, "sql语句不正确!");
        long count;
        if (page.isAutoCount()) {
            count = queryNumberByBean(getCountSql(sql), Long.class, beanParameter);
            page.setTotalCount((int) count);
        }
        List<T> list = findListBeanByBean(getSqlOfMySQL(sql, page), clazz, beanParameter);
        page.setResult(list);
        return page;
    }
}
