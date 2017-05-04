package org.templateproject.dao.factory;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.templateproject.dao.ancestor.AncestorDao;
import org.templateproject.dao.exception.DataSourceKeyNotExistException;
import org.templateproject.dao.factory.business.DataSourceX;
import org.templateproject.dao.factory.business.DbType;
import org.templateproject.dao.posterity.h2.H2Template;
import org.templateproject.dao.posterity.mysql.MysqlTemplate;
import org.templateproject.dao.posterity.oracle.OracleTemplate;
import org.templateproject.dao.posterity.postgresql.PostgreSqlTemplate;
import org.templateproject.dao.posterity.sqlite.SqliteTemplate;

import java.util.Hashtable;
import java.util.Map;

/**
 * Dealing something while spring is initializing
 * <p>
 * Created by wuwenbin on 2017/1/2.
 *
 * @author wuwenbin
 * @since 1.0.0
 */
@Component
public class DaoFactory implements InitializingBean {

    private static final ThreadLocal<AncestorDao> threadDynamicDao = new ThreadLocal<>();

    /**
     * multi datasource,including datasource and initDbType
     * using map to collect
     */
    private Map<String, DataSourceX> dataSourceMap;

    /**
     * default <tt>dao</tt>
     * <p>
     * before using it,define <tt>@Autowired DaoFactory daoFactory</tt> is necessary
     * using method:<tt>daFactory.defaultDao.xxxxxx()</tt> to execute it
     */
    public AncestorDao defaultDao;

    /**
     * generate <tt>daoMap</tt> by dataSources' map,the key refers to the key of dataSourceX
     */
    public Map<String, AncestorDao> daoMap = new Hashtable<>();

    /**
     * this default value is equals to defaultDao
     * use this param commonly,it is enough
     */
    public AncestorDao dynamicDao;


    public void setDataSourceMap(Map<String, DataSourceX> dataSourceMap) {
        this.dataSourceMap = dataSourceMap;
    }

    /**
     * 线程同步即可，此方法是不会再程序运行中调用，初始化即调用
     *
     * @param dataSourceX
     */
    public synchronized void setDefaultDao(DataSourceX dataSourceX) {
        if (dataSourceX.getInitDbType() == DbType.H2) {
            this.defaultDao = new H2Template(dataSourceX.getDataSource());
        } else if (dataSourceX.getInitDbType() == DbType.Oracle) {
            this.defaultDao = new OracleTemplate(dataSourceX.getDataSource());
        } else if (dataSourceX.getInitDbType() == DbType.Sqlite) {
            this.defaultDao = new SqliteTemplate(dataSourceX.getDataSource());
        } else if (dataSourceX.getInitDbType() == DbType.Postgresql) {
            this.defaultDao = new PostgreSqlTemplate(dataSourceX.getDataSource());
        } else {
            this.defaultDao = new MysqlTemplate(dataSourceX.getDataSource());
        }
        this.dynamicDao = this.defaultDao;
    }


    /**
     * 根据当前线程中的变量来确定切换的dynamicDao
     *
     * @param key
     * @return
     * @throws DataSourceKeyNotExistException
     */
    public synchronized ThreadLocal<AncestorDao> setDynamicDao(String key) throws DataSourceKeyNotExistException {
        if (threadDynamicDao.get() == null) {
            threadDynamicDao.set(getAncestorDaoByKey(key));
        }
        this.dynamicDao = threadDynamicDao.get();
        return threadDynamicDao;
    }

    /**
     * init some values
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (dataSourceMap != null && dataSourceMap.size() > 0 && !dataSourceMap.isEmpty()) {
            for (String key : dataSourceMap.keySet()) {
                if (daoMap == null || !daoMap.containsKey(key))
                    daoMap.put(key, getAncestorDaoByKey(key));
            }
        }
    }

    /**
     * get dao by key you set just now
     *
     * @param #key the key of dataSource you set
     * @return {@link AncestorDao}
     * @throws DataSourceKeyNotExistException
     */
    private AncestorDao getAncestorDaoByKey(String key) throws DataSourceKeyNotExistException {
        if (dataSourceMap.containsKey(key)) {
            DataSourceX dataSourceX = this.dataSourceMap.get(key);
            if (dataSourceX.getInitDbType() == DbType.H2) {
                return new H2Template(dataSourceX.getDataSource());
            } else if (dataSourceX.getInitDbType() == DbType.Oracle) {
                return new OracleTemplate(dataSourceX.getDataSource());
            } else if (dataSourceX.getInitDbType() == DbType.Sqlite) {
                return new SqliteTemplate(dataSourceX.getDataSource());
            } else if (dataSourceX.getInitDbType() == DbType.Postgresql) {
                return new PostgreSqlTemplate(dataSourceX.getDataSource());
            } else {
                return new MysqlTemplate(dataSourceX.getDataSource());
            }
        } else throw new DataSourceKeyNotExistException();
    }


}
