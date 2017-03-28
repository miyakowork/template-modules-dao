package me.wuwenbin.dao.sql.factory;

import me.wuwenbin.dao.sql.exception.*;
import me.wuwenbin.dao.sql.util.SQLBuilderUtils;
import me.wuwenbin.dao.sql.util.SQLDefineUtils;
import me.wuwenbin.dao.sql.annotations.SQLColumn;
import me.wuwenbin.dao.sql.annotations.SQLTable;

import java.lang.reflect.Field;

/**
 * 根据实体类和注解生成一些常用SQL
 * Created by wuwenbin on 2017/1/9.
 *
 * @author wuwenbin
 * @since 1.1.0
 */
public final class SQLBeanBuilder {

    private Class<?> beanClass;

    private Class<SQLTable> sqlTableClass = SQLTable.class;
    private Class<SQLColumn> sqlColumnClass = SQLColumn.class;

    private final String SPACE = " ";
    private final String FROM = SPACE + "FROM" + SPACE;
    private final String WHERE = SPACE + "WHERE" + SPACE;
    private final String AND = SPACE + "AND" + SPACE;
    private final String OR = SPACE + "OR" + SPACE;
    private final String selectPre = "SELECT" + SPACE;
    private final String insertPre = "INSERT INTO" + SPACE;
    private final String updatePre = "UPDATE" + SPACE;
    private final String deletePre = "DELETE FROM" + SPACE;

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public SQLBeanBuilder(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    /**
     * 无条件统计
     *
     * @return {@link String}
     */
    public String countAll() throws SQLTableNotFoundException {
        if (!SQLBuilderUtils.SQLTableIsExist(beanClass))
            throw new SQLTableNotFoundException(beanClass);
        else {
            String tableName = beanClass.getAnnotation(sqlTableClass).value();
            StringBuilder sb = new StringBuilder(selectPre);
            sb.append("COUNT(*)").append(FROM).append(tableName).toString();
            return SQLBuilderUtils.dealSQL(sb.toString());
        }
    }

    /**
     * AND条件统计一个表的记录数
     *
     * @param routers 需要and哪些字段来统计记录数
     * @return {@link String}
     * @throws SQLTableNotFoundException
     */
    public String countAndByRouters(int... routers) throws SQLTableNotFoundException {
        if (!SQLBuilderUtils.SQLTableIsExist(beanClass))
            throw new SQLTableNotFoundException(beanClass);
        else {
            String tableName = beanClass.getAnnotation(sqlTableClass).value();
            StringBuilder sb = new StringBuilder(selectPre);
            sb.append("COUNT(*)").append(FROM).append(tableName);

            if (routers != null && routers.length > 0) {
                sb.append(WHERE).append("1=1");
                Field[] fields = beanClass.getDeclaredFields();
                assembleCountSQL(sb, fields, AND, tableName, routers);
            }
            return SQLBuilderUtils.dealSQL(sb.toString());
        }
    }

    /**
     * OR条件统计一个表的记录数
     *
     * @param routers 需要or哪些字段来统计记录数
     * @return {@link String}
     * @throws SQLTableNotFoundException
     */
    public String countOrByRouters(int... routers) throws SQLTableNotFoundException {
        if (!SQLBuilderUtils.SQLTableIsExist(beanClass))
            throw new SQLTableNotFoundException(beanClass);
        else {
            String tableName = beanClass.getAnnotation(sqlTableClass).value();
            StringBuilder sb = new StringBuilder(selectPre);
            sb.append("COUNT(*)").append(FROM).append(tableName);

            if (routers != null && routers.length > 0) {
                sb.append(WHERE).append("1<>1");
                Field[] fields = beanClass.getDeclaredFields();
                assembleCountSQL(sb, fields, OR, tableName, routers);
            }
            return SQLBuilderUtils.dealSQL(sb.toString());
        }
    }

    /**
     * AND条件查询所选择的字段
     *
     * @param selectColumnsRouters 需要查询的router
     * @param conditionRouters     and条件中的router
     * @return {@link String}
     * @throws SQLTableNotFoundException
     */
    public String selectPartByRoutersAnd(int[] selectColumnsRouters, int... conditionRouters) throws SQLTableNotFoundException {
        if (!SQLBuilderUtils.SQLTableIsExist(beanClass))
            throw new SQLTableNotFoundException(beanClass);
        else {
            String tableName = beanClass.getAnnotation(sqlTableClass).value();
            StringBuilder sb = new StringBuilder(selectPre);
            Field[] fields = beanClass.getDeclaredFields();
            assembleSelectSQL(selectColumnsRouters, tableName, sb, fields);

            sb.append(FROM).append(tableName);
            if (conditionRouters != null && conditionRouters.length > 0) {
                sb.append(WHERE).append("1=1");
                assembleCountSQL(sb, fields, AND, tableName, conditionRouters);
            }
            return SQLBuilderUtils.dealSQL(sb.toString());
        }
    }

    /**
     * AND条件查询所有字段
     *
     * @param routers and条件中含有的router
     * @return {@link String}
     * @throws SQLTableNotFoundException
     */
    public String selectAllByRoutersAnd(int... routers) throws SQLTableNotFoundException {
        return selectPartByRoutersAnd(null, routers);
    }

    /**
     * 无条件查询所有字段
     *
     * @return {@link String}
     * @throws SQLTableNotFoundException
     */
    public String selectAll() throws SQLTableNotFoundException {
        return selectAllByRoutersAnd();
    }

    /**
     * 根据主键id查询所有字段
     *
     * @return {@link String}
     * @throws SQLTableNotFoundException
     * @throws NotSetPrimaryKeyException
     */
    public String selectAllByPk() throws SQLTableNotFoundException, NotSetPrimaryKeyException {
        return selectPartByPk();
    }

    /**
     * 根据主键id查询所选字段
     *
     * @param routers 需要查询的字段routers
     * @return {@link String}
     * @throws SQLTableNotFoundException
     * @throws NotSetPrimaryKeyException
     */
    public String selectPartByPk(int... routers) throws SQLTableNotFoundException, NotSetPrimaryKeyException {
        if (!SQLBuilderUtils.SQLTableIsExist(beanClass))
            throw new SQLTableNotFoundException(beanClass);
        else {
            StringBuilder sb = new StringBuilder(selectPre);
            String tableName = beanClass.getAnnotation(sqlTableClass).value();
            Field[] fields = beanClass.getDeclaredFields();
            assembleSelectSQL(routers, tableName, sb, fields);
            sb.append(FROM).append(tableName);

            boolean hasSetPk = false;
            for (Field field : fields) {
                if (field.isAnnotationPresent(sqlColumnClass)) {
                    if (field.getAnnotation(sqlColumnClass).pk()) {
                        hasSetPk = true;
                        String column = SQLDefineUtils.java2SQL(field.getAnnotation(sqlColumnClass).value(), field.getName());
                        sb.append(WHERE).append(tableName).append(".").append(column).append(" = :").append(field.getName());
                    }
                }
            }
            if (!hasSetPk) throw new NotSetPrimaryKeyException(beanClass);
            return SQLBuilderUtils.dealSQL(sb.toString());
        }
    }

    /**
     * 插入指定的routers字段
     *
     * @param insertPk 是否插入主键
     * @param routers  指定插入的哪些字段,为空则插入所有字段
     * @return {@link String}
     * @throws SQLTableNotFoundException
     */
    public String insertRoutersPk(boolean insertPk, int... routers) throws SQLTableNotFoundException {
        if (!SQLBuilderUtils.SQLTableIsExist(beanClass))
            throw new SQLTableNotFoundException(beanClass);
        else {
            StringBuilder sb = new StringBuilder(insertPre);
            String tableName = beanClass.getAnnotation(sqlTableClass).value();
            Field[] fields = beanClass.getDeclaredFields();
            sb.append(tableName);

            StringBuilder values = new StringBuilder("(");
            if (routers != null && routers.length > 0) {
                sb.append("(");
                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    if (field.isAnnotationPresent(sqlColumnClass)) {
                        if (insertPk && field.getAnnotation(sqlColumnClass).pk()) {
                            String pkColumn = SQLDefineUtils.java2SQL(field.getAnnotation(sqlColumnClass).value(), field.getName());
                            values.append(":").append(field.getName()).append(", ");
                            sb.append(pkColumn).append(", ");
                        }
                        if (SQLBuilderUtils.fieldRoutersInParamRouters(field.getAnnotation(sqlColumnClass).routers(), routers) && !field.getAnnotation(sqlColumnClass).pk()) {
                            String column = SQLDefineUtils.java2SQL(field.getAnnotation(sqlColumnClass).value(), field.getName());
                            values.append(":").append(field.getName()).append(", ");
                            sb.append(column).append(", ");
                        }
                    }
                }
                sb.append(") VALUES").append(values).append(")");
            } else {
                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    if (field.isAnnotationPresent(sqlColumnClass)) {
                        if (insertPk && field.getAnnotation(sqlColumnClass).pk()) {
                            values.append(":").append(field.getName()).append(", ");
                        }
                        if (!field.getAnnotation(sqlColumnClass).pk()) {
                            values.append(":").append(field.getName()).append(", ");
                        }
                    } else values.append(":").append(field.getName()).append(", ");
                }
                sb.append(" VALUES").append(values).append(")");
            }
            return SQLBuilderUtils.dealSQL(sb.toString());
        }
    }

    /**
     * 插入所有字段
     *
     * @param insertPk 是否连同主键一起插入
     * @return {@link String}
     * @throws SQLTableNotFoundException
     */
    public String insertAllPk(boolean insertPk) throws SQLTableNotFoundException {
        return insertRoutersPk(insertPk);
    }

    /**
     * 连同主键一起插入
     *
     * @return {@link String}
     * @throws SQLTableNotFoundException
     */
    public String insertAllWithPk() throws SQLTableNotFoundException {
        return insertRoutersPk(true);
    }

    /**
     * 插入所有字段,除主键外
     *
     * @return {@link String}
     * @throws SQLTableNotFoundException
     */
    public String insertAllWithoutPk() throws SQLTableNotFoundException {
        return insertRoutersPk(false);
    }

    /**
     * 插入指定的字段,连同id一起
     *
     * @param routers 指定字段的routers
     * @return {@link String}
     * @throws SQLTableNotFoundException
     */
    public String insertRoutersWithPk(int... routers) throws SQLTableNotFoundException {
        return insertRoutersPk(true, routers);
    }

    /**
     * 插入指定的字段,除主键外
     *
     * @param routers 指定字段的routers
     * @return {@link String}
     * @throws SQLTableNotFoundException
     */
    public String insertRoutersWithoutPk(int... routers) throws SQLTableNotFoundException {
        return insertRoutersPk(false, routers);
    }

    /**
     * 根据指定的条件routers更新指定的routers列
     *
     * @param updateRouters    指定更新列
     * @param conditionRouters 指定更新条件
     * @return {@link String}
     * @throws SQLTableNotFoundException
     * @throws UpdateColumnNullException
     */
    public String updateRoutersByRouterArray(int[] updateRouters, int[] conditionRouters) throws SQLTableNotFoundException, UpdateColumnNullException {
        if (!SQLBuilderUtils.SQLTableIsExist(beanClass))
            throw new SQLTableNotFoundException(beanClass);
        else {
            String tableName = beanClass.getAnnotation(sqlTableClass).value();
            StringBuilder sb = new StringBuilder(updatePre).append(tableName);
            Field[] fields = beanClass.getDeclaredFields();
            if (updateRouters != null && updateRouters.length > 0) {
                sb.append(" SET ");
                for (Field field : fields) {
                    if (field.isAnnotationPresent(sqlColumnClass)) {
                        if (SQLBuilderUtils.fieldRoutersInParamRouters(field.getAnnotation(sqlColumnClass).routers(), updateRouters)) {
                            String column = SQLDefineUtils.java2SQL(field.getAnnotation(sqlColumnClass).value(), field.getName());
                            sb.append(column).append(" = :").append(field.getName()).append(", ");
                        }
                    }
                }
            } else throw new UpdateColumnNullException();

            if (conditionRouters != null && conditionRouters.length > 0) {
                sb.append(WHERE);
                assembleWhereSQL(sb, fields, conditionRouters);
            }

            return SQLBuilderUtils.dealSQL(sb.toString());
        }
    }

    /**
     * 同updateRoutersByRouterArray,参数类型换为不不定式
     *
     * @param updateRouters
     * @param conditionRouters
     * @return {@link String}
     * @throws SQLTableNotFoundException
     * @throws UpdateColumnNullException
     */
    public String updateRoutersByRouters(int[] updateRouters, int... conditionRouters) throws SQLTableNotFoundException, UpdateColumnNullException {
        return updateRoutersByRouterArray(updateRouters, conditionRouters);
    }

    /**
     * 根据主键跟新指定routers列
     *
     * @param updateRouters 指定routers更新列
     * @return {@link String}
     * @throws SQLTableNotFoundException
     * @throws UpdateColumnNullException
     * @throws UpdatePkNotExistException
     */
    public String updateRoutersByPk(int... updateRouters) throws SQLTableNotFoundException, UpdateColumnNullException, UpdatePkNotExistException {
        if (!SQLBuilderUtils.SQLTableIsExist(beanClass))
            throw new SQLTableNotFoundException(beanClass);
        else {
            String tableName = beanClass.getAnnotation(sqlTableClass).value();
            StringBuilder sb = new StringBuilder(updatePre).append(tableName);
            Field[] fields = beanClass.getDeclaredFields();
            String pkColumn = null, pkField = null;
            if (updateRouters != null && updateRouters.length > 0) {
                sb.append(" SET ");
                for (Field field : fields) {
                    if (field.isAnnotationPresent(sqlColumnClass)) {
                        if (field.getAnnotation(sqlColumnClass).pk()) {
                            pkField = field.getName();
                            pkColumn = SQLDefineUtils.java2SQL(field.getAnnotation(sqlColumnClass).value(), pkField);
                        }
                        if (SQLBuilderUtils.fieldRoutersInParamRouters(field.getAnnotation(sqlColumnClass).routers(), updateRouters)) {
                            String column = SQLDefineUtils.java2SQL(field.getAnnotation(sqlColumnClass).value(), field.getName());
                            sb.append(column).append(" = :").append(field.getName()).append(", ");
                        }
                    }
                }
                sb.replace(sb.length() - 2, sb.length(), "");
            } else throw new UpdateColumnNullException();
            if (pkColumn == null || pkField == null) throw new UpdatePkNotExistException();
            sb.append(WHERE).append(pkColumn).append(" = :").append(pkField);
            return SQLBuilderUtils.dealSQL(sb.toString());
        }
    }

    /**
     * 根据主键删除表记录
     *
     * @return {@link String}
     * @throws SQLTableNotFoundException
     * @throws DeletePkNotExistException
     */
    public String deleteByPk() throws SQLTableNotFoundException, DeletePkNotExistException {
        if (!SQLBuilderUtils.SQLTableIsExist(beanClass))
            throw new SQLTableNotFoundException(beanClass);
        else {
            String tableName = beanClass.getAnnotation(sqlTableClass).value();
            StringBuilder sb = new StringBuilder(deletePre).append(tableName);
            Field[] fields = beanClass.getDeclaredFields();
            boolean hasPk = false;
            sb.append(WHERE);
            for (Field field : fields) {
                if (field.isAnnotationPresent(sqlColumnClass)) {
                    if (field.getAnnotation(sqlColumnClass).pk()) {
                        hasPk = true;
                        String pkColumn = SQLDefineUtils.java2SQL(field.getAnnotation(sqlColumnClass).value(), field.getName());
                        sb.append(pkColumn).append(" = :").append(field.getName()).append(AND);
                    }
                }
            }
            if (!hasPk) throw new DeletePkNotExistException();
            return SQLBuilderUtils.dealSQL(sb.toString());
        }
    }

    /**
     * 根据指定的routers条件删除表中的记录
     *
     * @param routers
     * @return {@link String}
     * @throws SQLTableNotFoundException
     * @throws DeleteSQLConditionsNullException
     */
    public String deleteByRouters(int... routers) throws SQLTableNotFoundException, DeleteSQLConditionsNullException {
        if (!SQLBuilderUtils.SQLTableIsExist(beanClass))
            throw new SQLTableNotFoundException(beanClass);
        else {
            String tableName = beanClass.getAnnotation(sqlTableClass).value();
            StringBuilder sb = new StringBuilder(deletePre).append(tableName);
            Field[] fields = beanClass.getDeclaredFields();
            if (routers != null && routers.length > 0) {
                sb.append(WHERE);
                assembleWhereSQL(sb, fields, routers);
            } else throw new DeleteSQLConditionsNullException();
            return SQLBuilderUtils.dealSQL(sb.toString());
        }
    }


    //==========公共通用部分=========//

    /**
     * 组装count的where部分
     *
     * @param sb
     * @param fields
     * @param andOr
     * @param routers
     */
    private void assembleCountSQL(StringBuilder sb, Field[] fields, String andOr, String tableName, int... routers) {
        for (Field field : fields) {
            if (field.isAnnotationPresent(sqlColumnClass)) {
                if (SQLBuilderUtils.fieldRoutersInParamRouters(field.getAnnotation(sqlColumnClass).routers(), routers)) {
                    String column = SQLDefineUtils.java2SQL(field.getAnnotation(sqlColumnClass).value(), field.getName());
                    sb.append(andOr).append(tableName).append(".").append(column).append("= ").append(":").append(field.getName());
                }
            }
        }
    }

    /**
     * 组装查询部分的SQL
     *
     * @param selectColumnsRouters
     * @param tableName
     * @param sb
     * @param fields
     */
    private void assembleSelectSQL(int[] selectColumnsRouters, String tableName, StringBuilder sb, Field[] fields) {
        if (selectColumnsRouters != null && selectColumnsRouters.length > 0) {
            for (Field field : fields) {
                if (field.isAnnotationPresent(sqlColumnClass)) {
                    if (SQLBuilderUtils.fieldRoutersInParamRouters(field.getAnnotation(sqlColumnClass).routers(), selectColumnsRouters)) {
                        String column = SQLDefineUtils.java2SQL(field.getAnnotation(sqlColumnClass).value(), field.getName());
                        sb.append(tableName).append(".").append(column).append(", ");
                    }
                }
            }
        } else sb.append(tableName).append(".").append("*");
    }

    /**
     * 拼接whereSQL部分
     *
     * @param sb
     * @param fields
     * @param routers
     */
    private void assembleWhereSQL(StringBuilder sb, Field[] fields, int[] routers) {
        for (Field field : fields) {
            if (field.isAnnotationPresent(sqlColumnClass)) {
                if (SQLBuilderUtils.fieldRoutersInParamRouters(field.getAnnotation(sqlColumnClass).routers(), routers)) {
                    String column = SQLDefineUtils.java2SQL(field.getAnnotation(sqlColumnClass).value(), field.getName());
                    sb.append(column).append(" = :").append(field.getName()).append(AND);
                }
            }
        }
    }

}
