package org.templateproject.dao.posterity;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.templateproject.dao.ancestor.AncestorDao;
import me.wuwenbin.pojo.page.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * the implement of {@link AncestorDao}
 * only for mysql database operations
 * <p>
 * Created by wuwenbin on 2017/1/1.
 *
 * @author wuwenbin
 * @see AncestorDao
 * @since 1.0.0
 */
public abstract class PosterityDao implements AncestorDao {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private DataSource dataSource;

    protected JdbcTemplate jdbcTemplate;
    protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    protected SimpleJdbcCall jdbcCall;
    protected SimpleJdbcInsert jdbcInsert;

    public PosterityDao(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource);
        this.jdbcCall = new SimpleJdbcCall(dataSource);
    }

    protected RowMapper generateRowMapper(Class clazz) {
        return BeanPropertyRowMapper.newInstance(clazz);
    }

    protected BeanPropertySqlParameterSource generateSqlParamSource(Object o) {
        return new BeanPropertySqlParameterSource(o);
    }

    protected static String getCountSql(String nativeSQL) {
        final String countSQL = "COUNT(0)";
        Assert.hasText(nativeSQL, "sql is not correct!");
        String sql = nativeSQL.toUpperCase();
        if (sql.indexOf("DISTINCT(") >= 0 || sql.indexOf(" GROUP BY ") >= 0) {
            return "SELECT " + countSQL + " FROM (" + nativeSQL + ") TEMP_COUNT_TABLE";
        }
        String[] froms = sql.split(" FROM ");
        String tempSql = "";
        for (int i = 0; i < froms.length; i++) {
            if (i != froms.length - 1) {
                tempSql = tempSql.concat(froms[i] + " FROM ");
            } else {
                tempSql = tempSql.concat(froms[i]);
            }
            int left = tempSql.split("\\(").length;
            int right = tempSql.split("\\)").length;
            if (left == right) {
                break;
            }
        }
        tempSql = " FROM " + nativeSQL.substring(tempSql.length(), sql.length());
        int orderBy = tempSql.toUpperCase().indexOf(" ORDER BY ");
        if (orderBy >= 0) {
            tempSql = tempSql.substring(0, orderBy);
        }
        return "SELECT " + countSQL + " ".concat(tempSql);
    }

    @Override
    public DataSource getCurrentDataSource() {
        return this.dataSource;
    }

    @Override
    public JdbcTemplate getJdbcTemplateObj() {
        return jdbcTemplate;
    }

    @Override
    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplateObj() {
        return this.namedParameterJdbcTemplate;
    }

    @Override
    @Deprecated
    public SimpleJdbcInsert getSimpleJdbcInsertObj() {
        return this.jdbcInsert;
    }

    @Override
    public SimpleJdbcCall getSimpleJdbcCall() {
        return this.jdbcCall;
    }

    @Override
    public Connection getConnection() throws Exception {
        return this.dataSource.getConnection();
    }

    @Override
    public void callProcedure(String procedureName) {
        jdbcCall = jdbcCall.withProcedureName(procedureName);
        jdbcCall.execute();
    }

    @Override
    public void callProcedure(String procedureName, Map<String, Object> inParameters) {
        jdbcCall = jdbcCall.withProcedureName(procedureName);
        jdbcCall.execute(inParameters);
    }

    @Override
    public Map<String, Object> callProcedureQueryOut(String procedureName, Map<String, Object> inParameters) {
        jdbcCall = jdbcCall.withProcedureName(procedureName);
        return jdbcCall.execute(inParameters);
    }

    @Override
    public List<Map<String, Object>> callProcedureQueryListBeans(String procedureName, Map<String, Object> inParameters, Class<?> outBeansType) {
        jdbcCall = jdbcCall.withProcedureName(procedureName);
        if (outBeansType != null)
            jdbcCall = jdbcCall.returningResultSet("list_beans", generateRowMapper(outBeansType));
        List list = (List) jdbcCall.execute(inParameters).get("list_beans");
        logger.info("执行响应结果数：" + list.size());
        return list;
    }

    @Override
    @Deprecated
    public long insertBeanGetGeneratedKey(String tableName, String keyName, Object beanParameter) throws Exception {
        Assert.notNull(tableName, "表名不能为空");
        Assert.notNull(keyName, "自增字段名称不能为空");
        Assert.notNull(beanParameter, "对象bean不能为空");
        jdbcInsert = jdbcInsert.withTableName(tableName).usingGeneratedKeyColumns(keyName);
        Number number = jdbcInsert.executeAndReturnKey(generateSqlParamSource(beanParameter));
        logger.info("插入生成的key：" + keyName);
        return number.longValue();
    }

    @Override
    public <T> int insertBeanAutoGenKey(String sql, T beanParameter) throws Exception {
        Assert.hasText(sql, "sql语句不正确！");
        Assert.notNull(beanParameter, "对象bean不能为空");
        logger.info("SQL:" + sql);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        return namedParameterJdbcTemplate.update(sql, generateSqlParamSource(beanParameter), keyHolder);
    }

    @Override
    public <T> long insertBeanAutoGenKeyOut(String sql, T beanParameter) throws Exception {
        Assert.hasText(sql, "sql语句不正确！");
        Assert.notNull(beanParameter, "对象bean不能为空");
        logger.info("SQL:" + sql);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, generateSqlParamSource(beanParameter), keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public <T> T insertBeanAutoGenKeyOutBean(String insertSQL, T beaParameter, Class<T> clazz, String tableName) throws Exception {
        long key = insertBeanAutoGenKeyOut(insertSQL, beaParameter);
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
        T bean = findBeanByArray(sql, clazz, key);
        return bean;
    }

    @Override
    @Deprecated
    public long insertMapGetGeneratedKey(String tableName, String keyName, Map<String, Object> mapParameter) throws Exception {
        Assert.notNull(tableName, "表名不能为空!");
        Assert.notNull(keyName, "自增字段名称不能为空!");
        Assert.notNull(mapParameter, "对象map不能为空!");
        jdbcInsert = jdbcInsert.withTableName(tableName).usingGeneratedKeyColumns(keyName);
        Number number = jdbcInsert.executeAndReturnKey(mapParameter);
        logger.info("generated-key:" + number);
        return number.longValue();
    }

    @Override
    public int executeArray(String sql, Object... arrayParameters) throws Exception {
        Assert.hasText(sql, "sql语句不正确!");
        logger.info("SQL:" + sql);
        int affectCount;
        if (arrayParameters != null && arrayParameters.length > 0) {
            affectCount = jdbcTemplate.update(sql, arrayParameters);
        } else {
            affectCount = jdbcTemplate.update(sql);
        }
        logger.info("查询SQL影响条目:" + affectCount);
        return affectCount;
    }

    @Override
    public int executeMap(String sql, Map<String, Object> mapParameter) throws Exception {
        Assert.hasText(sql, "sql语句不正确!");
        logger.info("SQL:" + sql);
        int affectCount;
        if (mapParameter != null && mapParameter.size() > 0) {
            affectCount = namedParameterJdbcTemplate.update(sql, mapParameter);
        } else {
            affectCount = executeArray(sql);
        }
        logger.info("查询SQL影响条目:" + affectCount);
        return affectCount;
    }

    @Override
    public int executeBean(String sql, Object beanParameter) throws Exception {
        Assert.hasText(sql, "sql语句不正确!");
        logger.info("SQL:" + sql);
        int affectCount;
        if (beanParameter != null) {
            affectCount = namedParameterJdbcTemplate.update(sql, generateSqlParamSource(beanParameter));
        } else {
            affectCount = executeArray(sql);
        }
        logger.info("影响条目:" + affectCount);
        return affectCount;
    }

    @Override
    public int[] executeBatchByArrayMaps(String sql, Map<String, Object>... mapParameters) throws Exception {
        Assert.hasText(sql, "sql语句不正确!");
        logger.info("SQL:" + sql);
        if (mapParameters != null && mapParameters.length > 0) {
            SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(mapParameters);
            return namedParameterJdbcTemplate.batchUpdate(sql, batch);
        }
        return null;
    }

    @Override
    public int[] executeBatchByArrayBeans(String sql, Object... beanParameters) throws Exception {
        Assert.hasText(sql, "sql语句不正确!");
        logger.info("SQL:" + sql);
        if (beanParameters != null && beanParameters.length > 0) {
            SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(beanParameters);
            return namedParameterJdbcTemplate.batchUpdate(sql, batch);
        }
        return null;
    }

    @Override
    public int[] executeBatchByCollectionMaps(String sql, Collection<Map<String, Object>> mapParameters) throws Exception {
        Assert.hasText(sql, "sql语句不正确!");
        logger.info("SQL:" + sql);
        if (mapParameters != null && !mapParameters.isEmpty()) {
            Map<String, Object>[] mps = (Map<String, Object>[]) mapParameters.toArray(new Map[mapParameters.size()]);
            return namedParameterJdbcTemplate.batchUpdate(sql, mps);
        }
        return null;
    }

    @Override
    public int[] executeBatchByCollectionBeans(String sql, Collection<Object> beanParameters) throws Exception {
        Assert.hasText(sql, "sql语句不正确!");
        logger.info("SQL:" + sql);
        if (beanParameters != null && !beanParameters.isEmpty()) {
            SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(beanParameters.toArray());
            return namedParameterJdbcTemplate.batchUpdate(sql, batch);
        }
        return null;
    }

    @Override
    public Number findNumberByArray(String sql, Object... arrayParameters) {
        try {
            Assert.hasText(sql, "sql语句不正确!");
            logger.info("SQL:" + sql);
            if (arrayParameters != null && arrayParameters.length > 0) {
                return jdbcTemplate.queryForObject(sql, Number.class, arrayParameters);
            } else {
                return jdbcTemplate.queryForObject(sql, Number.class);
            }
        } catch (EmptyResultDataAccessException e) {
            logger.error("查询SQL无结果------" + e);
            return 0;
        } catch (Exception e) {
            logger.error("查询SQL异常 no result! {}" + e);
            return 0;
        }
    }

    @Override
    public <T extends Number> T queryNumberByArray(String sql, Class<? extends Number> numberClass, Object... arrayParameters) {
        try {
            Assert.hasText(sql, "sql语句不正确!");
            logger.info("SQL:" + sql);
            if (arrayParameters != null && arrayParameters.length > 0) {
                Number n = jdbcTemplate.queryForObject(sql, numberClass, arrayParameters);
                if (n == null)
                    return (T) NumberUtils.parseNumber("0", numberClass);
                return (T) n;
            } else {
                Number n = jdbcTemplate.queryForObject(sql, numberClass);
                if (n == null)
                    return (T) NumberUtils.parseNumber("0", numberClass);
                else return (T) n;
            }
        } catch (EmptyResultDataAccessException e) {
            logger.error("查询SQL无结果------" + e);
            return (T) NumberUtils.parseNumber("0", numberClass);
        } catch (Exception e) {
            logger.error("查询SQL异常 no result! {}" + e);
            return (T) NumberUtils.parseNumber("0", numberClass);
        }
    }

    @Override
    public Number findNumberByMap(String sql, Map<String, Object> mapParameter) {
        try {
            Assert.hasText(sql, "sql语句不正确!");
            logger.info("SQL:" + sql);
            if (mapParameter != null) {
                return namedParameterJdbcTemplate.queryForObject(sql, mapParameter, Number.class);
            } else {
                return findNumberByArray(sql);
            }
        } catch (EmptyResultDataAccessException ere) {
            return 0;
        } catch (Exception e) {
            logger.error("not result!{}", e);
            return 0;
        }
    }

    @Override
    public <T extends Number> T queryNumberByMap(String sql, Class<? extends Number> numberClass, Map<String, Object> mapParameter) {
        try {
            Assert.hasText(sql, "sql语句不正确!");
            logger.info("SQL:" + sql);
            if (mapParameter != null && mapParameter.size() > 0) {
                Number n = namedParameterJdbcTemplate.queryForObject(sql, mapParameter, numberClass);
                if (n == null)
                    return (T) NumberUtils.parseNumber("0", numberClass);
                else return (T) n;
            } else {
                Number n = queryNumberByArray(sql, numberClass);
                if (n == null)
                    return (T) NumberUtils.parseNumber("0", numberClass);
                else return (T) n;
            }
        } catch (EmptyResultDataAccessException e) {
            logger.error("查询SQL无结果------" + e);
            return (T) NumberUtils.parseNumber("0", numberClass);
        } catch (Exception e) {
            logger.error("查询SQL异常 no result! {}" + e);
            return (T) NumberUtils.parseNumber("0", numberClass);
        }
    }

    @Override
    public Number findNumberByBean(String sql, Object beanParameter) {
        try {
            Assert.hasText(sql, "sql语句不正确!");
            logger.info("SQL:" + sql);
            if (beanParameter != null) {
                return namedParameterJdbcTemplate.queryForObject(sql, generateSqlParamSource(beanParameter), Number.class);
            } else {
                return findNumberByArray(sql);
            }
        } catch (EmptyResultDataAccessException ere) {
            return 0;
        } catch (Exception e) {
            logger.error("not result!{}", e);
            return 0;
        }
    }

    @Override
    public <T extends Number> T queryNumberByBean(String sql, Class<? extends Number> numberClass, Object beanParameter) {
        try {
            Assert.hasText(sql, "sql语句不正确!");
            logger.info("SQL:" + sql);
            if (beanParameter != null) {
                Number n = namedParameterJdbcTemplate.queryForObject(sql, generateSqlParamSource(beanParameter), numberClass);
                if (n == null)
                    return (T) NumberUtils.parseNumber("0", numberClass);
                else return (T) n;
            } else {
                Number n = queryNumberByArray(sql, numberClass);
                if (n == null)
                    return (T) NumberUtils.parseNumber("0", numberClass);
                else return (T) n;
            }
        } catch (EmptyResultDataAccessException e) {
            logger.error("查询SQL无结果------" + e);
            return (T) NumberUtils.parseNumber("0", numberClass);
        } catch (Exception e) {
            logger.error("查询SQL异常 no result! {}" + e);
            return (T) NumberUtils.parseNumber("0", numberClass);
        }
    }

    @Override
    public Map<String, Object> findMapByArray(String sql, Object... arrayParameters) {
        try {
            Assert.hasText(sql, "sql语句不正确!");
            logger.info("SQL:" + sql);
            if (arrayParameters != null && arrayParameters.length > 0) {
                return jdbcTemplate.queryForMap(sql, arrayParameters);
            } else {
                return jdbcTemplate.queryForMap(sql);
            }
        } catch (EmptyResultDataAccessException e) {
            logger.error("查询SQL无结果------ " + e);
            return null;
        } catch (Exception e) {
            logger.error("查询SQL异常 no result! {}" + e);
            return null;
        }
    }

    @Override
    public Map<String, Object> findMapByMap(String sql, Map<String, Object> mapParameter) {
        try {
            Assert.hasText(sql, "sql语句不正确!");
            logger.info("SQL:" + sql);
            if (mapParameter != null && mapParameter.size() > 0) {
                return namedParameterJdbcTemplate.queryForMap(sql, mapParameter);
            } else {
                return findMapByArray(sql);
            }
        } catch (EmptyResultDataAccessException ere) {
            return null;
        } catch (Exception e) {
            logger.error("not result!{}", e);
            return null;
        }
    }

    @Override
    public <T> T findBeanByArray(String sql, Class<T> clazz, Object... arrayParameters) {
        try {
            Assert.hasText(sql, "sql语句不正确!");
            Assert.notNull(clazz, "类集合中对象类型不能为空!");
            logger.info("SQL:" + sql);
            if (arrayParameters != null && arrayParameters.length > 0) {
                return (T) jdbcTemplate.queryForObject(sql, generateRowMapper(clazz), arrayParameters);
            } else {
                return (T) jdbcTemplate.queryForObject(sql, generateRowMapper(clazz));
            }
        } catch (EmptyResultDataAccessException e) {
            logger.error("查询SQL无结果------" + e);
            return null;
        } catch (Exception e) {
            logger.error("查询SQL异常 no result! {}" + e);
            return null;
        }
    }

    @Override
    public <T> T findBeanByMap(String sql, Class<T> clazz, Map<String, Object> mapParameter) {
        try {
            Assert.hasText(sql, "sql语句不正确!");
            Assert.notNull(clazz, "集合中对象类型不能为空!");
            logger.info("SQL:" + sql);
            if (mapParameter != null && mapParameter.size() > 0) {
                return (T) namedParameterJdbcTemplate.queryForObject(sql, mapParameter, generateRowMapper(clazz));
            } else {
                return findBeanByArray(sql, clazz);
            }
        } catch (EmptyResultDataAccessException ere) {
            return null;
        } catch (Exception e) {
            logger.error("not result!{}", e);
            return null;
        }
    }

    @Override
    public <T> T findBeanByBean(String sql, Class<T> clazz, Object beanParameter) {
        try {
            Assert.hasText(sql, "sql语句不正确！");
            Assert.notNull(clazz, "集合中对象类型不能为空！");
            logger.info("SQL:" + sql);
            List<T> list;
            if (beanParameter != null) {
                list = findListBeanByBean(sql, clazz, beanParameter);
                if (list != null) {
                    if (list.isEmpty()) return null;
                    else {
                        if (list.size() > 1) throw new RuntimeException("返回结果中对象集合不是唯一");
                        else return list.get(0);
                    }
                } else {
                    return null;
                }
            } else {
                return findBeanByArray(sql, clazz);
            }
        } catch (EmptyResultDataAccessException ere) {
            return null;
        } catch (Exception e) {
            logger.error("not result{}", e);
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> findListMapByArray(String sql, Object... arrayParameters) {
        try {
            Assert.hasText(sql, "sql语句不正确!");
            logger.info("SQL:" + sql);
            List<Map<String, Object>> list;
            if (arrayParameters != null && arrayParameters.length > 0) {
                list = jdbcTemplate.queryForList(sql, arrayParameters);
            } else {
                list = jdbcTemplate.queryForList(sql);
            }
            logger.info("查询SQL响应条目:" + list.size());
            return list;
        } catch (EmptyResultDataAccessException e) {
            logger.error("查询SQL无结果------" + e);
            return null;
        } catch (Exception e) {
            logger.error("查询SQL异常 no result! {}" + e);
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> findListMapByMap(String sql, Map<String, Object> mapParameter) {
        try {
            Assert.hasText(sql, "sql语句不正确!");
            logger.info("SQL:" + sql);
            List<Map<String, Object>> list;
            if (mapParameter != null && mapParameter.size() > 0) {
                list = namedParameterJdbcTemplate.queryForList(sql, mapParameter);
            } else {
                list = findListMapByArray(sql);
            }
            logger.info("响应条目:" + list.size());
            return list;
        } catch (EmptyResultDataAccessException ere) {
            return null;
        } catch (Exception e) {
            logger.error("not result!{}", e);
            return null;
        }
    }

    @Override
    public <T> List<T> findListBeanByArray(String sql, Class<T> clazz, Object... arrayParameters) {
        try {
            Assert.hasText(sql, "sql语句不正确!");
            Assert.notNull(clazz, "集合中对象类型不能为空!");
            logger.info("SQL:" + sql);
            List list;
            if (arrayParameters != null && arrayParameters.length > 0) {
                list = jdbcTemplate.query(sql, generateRowMapper(clazz), arrayParameters);
            } else {
                list = jdbcTemplate.query(sql, generateRowMapper(clazz));
            }
            logger.info("-- [findListBeanByArray] -- 响应条目:" + list.size());
            return list;
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            logger.error("not result!{}", e);
            return null;
        }
    }

    @Override
    public <T> List<T> findListBeanByMap(String sql, Class<T> clazz, Map<String, Object> mapParameter) {
        try {
            Assert.hasText(sql, "sql语句不正确!");
            Assert.notNull(clazz, "集合中对象类型不能为空!");
            logger.info("SQL:" + sql);
            List list;
            if (mapParameter != null && mapParameter.size() > 0) {
                list = jdbcTemplate.query(sql, generateRowMapper(clazz), mapParameter);
            } else {
                list = findListBeanByArray(sql, clazz);
            }
            logger.info("响应条目:" + list.size());
            return list;
        } catch (EmptyResultDataAccessException ere) {
            return null;
        } catch (Exception e) {
            logger.error("not result!{}", e);
            return null;
        }
    }

    @Override
    public <T> List<T> findListBeanByBean(String sql, Class<T> clazz, Object beanParameter) {
        try {
            Assert.hasText(sql, "sql语句不正确!");
            Assert.notNull(clazz, "集合中对象类型不能为空!");
            logger.info("SQL:" + sql);
            List list;
            if (beanParameter != null) {
                list = namedParameterJdbcTemplate.query(sql, generateSqlParamSource(beanParameter), generateRowMapper(clazz));
            } else {
                list = findListBeanByArray(sql, clazz);
            }
            logger.info("响应条目:" + list.size());
            return list;
        } catch (EmptyResultDataAccessException ere) {
            return null;
        } catch (Exception e) {
            logger.error("not result!{}", e);
            return null;
        }
    }


    @Override
    public abstract Page findPageListMapByArray(String sql, Page page, Object... arrayParameters);

    @Override
    public abstract Page findPageListMapByMap(String sql, Page page, Map<String, Object> mapParameter);

    @Override
    public abstract <T> Page findPageListBeanByArray(String sql, Class<T> clazz, Page page, Object... arrayParameters);

    @Override
    public abstract <T> Page findPageListBeanByMap(String sql, Class<T> clazz, Page page, Map<String, Object> mapParameter);

    @Override
    public abstract <T> Page findPageListBeanByBean(String sql, Class<T> clazz, Page page, Object beanParameter);

}
