package me.wuwenbin.dao.factory;

import me.wuwenbin.dao.ancestor.AncestorDao;
import me.wuwenbin.dao.exception.DataSourceKeyNotExistException;
import me.wuwenbin.dao.factory.business.DataSourceX;
import me.wuwenbin.dao.posterity.h2.H2Template;
import me.wuwenbin.dao.posterity.sqlite.SqliteTemplate;
import me.wuwenbin.dao.factory.business.DbType;
import me.wuwenbin.dao.posterity.mysql.MysqlTemplate;
import me.wuwenbin.dao.posterity.oracle.OracleTemplate;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Dealing something while spring is initializing
 * <p>
 * Created by wuwenbin on 2017/1/2.
 *
 * @author wuwenbin
 * @since 1.0.0
 */
public class DaoFactory implements InitializingBean {

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
    public Map<String, AncestorDao> daoMap = new HashMap<>();

    /**
     * this default value is equals to defaultDao
     * use this param commonly,it is enough
     */
    public AncestorDao dynamicDao;


    public void setDataSourceMap(Map<String, DataSourceX> dataSourceMap) {
        this.dataSourceMap = dataSourceMap;
    }

    public void setDefaultDao(DataSourceX dataSourceX) {
        if (dataSourceX.getInitDbType() == DbType.H2) {
            this.defaultDao = new H2Template(dataSourceX.getDataSource());
        } else if (dataSourceX.getInitDbType() == DbType.Oracle) {
            this.defaultDao = new OracleTemplate(dataSourceX.getDataSource());
        } else if (dataSourceX.getInitDbType() == DbType.Sqlite) {
            this.defaultDao = new SqliteTemplate(dataSourceX.getDataSource());
        } else {
            this.defaultDao = new MysqlTemplate(dataSourceX.getDataSource());
        }
        this.dynamicDao = this.defaultDao;
    }

    public void setDynamicDao(String key) throws DataSourceKeyNotExistException {
        this.dynamicDao = getAncestorDaoByKey(key);
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
     * get dao by key in xml you set just now
     *
     * @param #key the key of dataSource you set in xml
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
            } else {
                return new MysqlTemplate(dataSourceX.getDataSource());
            }
        } else throw new DataSourceKeyNotExistException();
    }


}
