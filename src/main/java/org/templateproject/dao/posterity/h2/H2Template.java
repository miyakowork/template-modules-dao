package org.templateproject.dao.posterity.h2;

import org.templateproject.dao.posterity.PosterityDao;
import me.wuwenbin.pojo.page.Page;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * the implements of h2
 * <p>
 * Created by wuwenbin on 2017/3/27.
 */
public class H2Template extends PosterityDao {
    public H2Template(DataSource dataSource) {
        super(dataSource);
    }

    private static String getSqlOfH2(final String sql, Page page) {
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
        List list = findListMapByArray(getSqlOfH2(sql, page), arrayParameters);
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
        List list = findListMapByMap(getSqlOfH2(sql, page), mapParameter);
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
        List list = findListBeanByArray(getSqlOfH2(sql, page), clazz, arrayParameters);
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
        List list = findListBeanByMap(getSqlOfH2(sql, page), clazz, mapParameter);
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
        List list = findListBeanByBean(getSqlOfH2(sql, page), clazz, beanParameter);
        page.setResult(list);
        return page;
    }
}
