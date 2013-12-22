package com.alipay.zdal.client.jdbc.dbselector;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.alipay.zdal.client.jdbc.DBSelector;
import com.alipay.zdal.client.jdbc.ZdalStatement.DB_OPERATION_TYPE;
import com.alipay.zdal.common.DBType;
import com.alipay.zdal.common.jdbc.sorter.ExceptionSorter;
import com.alipay.zdal.common.jdbc.sorter.MySQLExceptionSorter;
import com.alipay.zdal.common.jdbc.sorter.OracleExceptionSorter;

public abstract class AbstractDBSelector implements DBSelector {
    private static final Map<DBType, ExceptionSorter> exceptionSorters = new HashMap<DBType, ExceptionSorter>(
                                                                           2);
    static {
        exceptionSorters.put(DBType.ORACLE, new OracleExceptionSorter());
        exceptionSorters.put(DBType.MYSQL, new MySQLExceptionSorter());
    }
    private DBType                                    dbType           = DBType.MYSQL;
    protected ExceptionSorter                         exceptionSorter  = exceptionSorters
                                                                           .get(dbType);
    private String                                    id;

    /**
     * added by fanzeng.
     * 表示同一组dbSeletor中最终选择到的数据源标识；
     */
    private String                                    selectedDSName;

    //数据源名称，方便输出信息，add by 冰魂 20130903
    private String                                    appDsName;

    public AbstractDBSelector(String id) {
        this.id = id;
    }

    public String getSelectedDSName() {
        return selectedDSName;
    }

    public void setSelectedDSName(String setSelectedDSName) {
        this.selectedDSName = setSelectedDSName;
    }

    public <T> T tryExecute(DataSourceTryer<T> tryer, int times, DB_OPERATION_TYPE operationType,
                            Object... args) throws SQLException {
        return this.tryExecute(new LinkedHashMap<DataSource, SQLException>(0), tryer, times,
            operationType, args);
    }

    public DBType getDbType() {
        return dbType;
    }

    public void setDbType(DBType dbType) {
        this.dbType = dbType;
        this.exceptionSorter = exceptionSorters.get(this.dbType);
    }

    public final void setExceptionSorter(ExceptionSorter exceptionSorter) {
        //add by shenxun:主要还是方便测试。。构造整个dbSelector结构太复杂
        this.exceptionSorter = exceptionSorter;
    }

    public String getId() {
        return id;
    }

    public void setAppDsName(String appDsName) {
        this.appDsName = appDsName;
    }

    public String getAppDsName() {
        return appDsName;
    }
}
