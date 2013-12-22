package com.alipay.zdal.valve.test.utils;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alipay.ats.assertion.TestAssertion;
import com.alipay.zdal.datasource.LocalTxDataSourceDO;
import com.alipay.zdal.datasource.ZDataSource;
import com.alipay.zdal.valve.config.ThresholdAndPeriod;
import com.alipay.zdal.valve.util.TableNameParserImpl;

/**
 * 
 * 
 * @author riqiu
 */
public class ValveBasicTest {
    protected ZDataSource        dataSource;
    protected ZDataSource[]      dataSources             = new ZDataSource[2];
    protected Connection        connection;
    protected Connection[]      connections             = new Connection[2];
    protected Statement         statement;
    protected Statement[]       statements              = new Statement[2];
    protected PreparedStatement preparedStatement;
    protected CallableStatement callableStatement       = null;
    protected String            dataSourceBean          = true ? "valveDatasourceMysql"
                                                            : "valveDatasourceOracle";
    protected String            key                     = "configValue";
    protected String            dskey                   = "datasourceMysql";
    protected String            dskeyDS                 = "DefaultDS";
    protected String            dskeyGLOBAL             = "GLOBAL";
    protected String            dbName                  = "diamond";
    protected String            tbName                  = "zdatasource_test";
    protected String[]          tbNames                 = { "zdatasource_test", "zds_test" };
    protected Long              currentTime;
    private long                requestTime             = 0l;
    protected OSTool            oSTool                  = new OSTool();
    protected int               fileNum                 = 0;
    protected DBTool            dBTool;
    protected String            insertPreparedStatement = "insert into preparedStatementTable "
                                                          + "values(?)";
    protected String            selectPreparedStatement = "select * from preparedStatementTable";

//    protected Map<String, VDRM> tairVDRM                = new HashMap<String, VDRM>();           //保存所有tair接口和其VDRM

    protected Object            returnValueObject;
    protected boolean           returnValueBoolean;
    
    //TODO assert的处理
    public TestAssertion Assert = new TestAssertion();
    
    public final static Log Logger = LogFactory.getLog(ValveBasicTest.class);
    
    

   protected Map<String, LocalTxDataSourceDO> LocalTxDsDoMap = new HashMap<String, LocalTxDataSourceDO>();
//    @Override
//    protected String[] getConfigPaths() {
//        fileNum = oSTool.getFileNum(oSTool.getLogPath());
//        return new String[] { "/valve.xml", };
//    }

    protected void initLocalTxDsDoMap(){
    	LocalTxDataSourceDO localTxDataSourceDO = new LocalTxDataSourceDO();
    	localTxDataSourceDO.setBackgroundValidation(false);
    	localTxDataSourceDO.setBackgroundValidationMinutes(10);
    	localTxDataSourceDO.setBlockingTimeoutMillis(2000);
    	localTxDataSourceDO.setCheckValidConnectionSQL("SELECT 1 from dual");
    	localTxDataSourceDO.setConnectionURL("jdbc:mysql://mysql-1-2.bjl.alipay.net:3306/zds1");
    	localTxDataSourceDO.setDriverClass("com.mysql.jdbc.Driver");
        try {
        	localTxDataSourceDO.setEncPassword("-76079f94c1e11c89");
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		localTxDataSourceDO.setExceptionSorterClassName("com.alipay.zdal.datasource.resource.adapter.jdbc.vendor.MySQLExceptionSorter");
		localTxDataSourceDO.setIdleTimeoutMinutes(10);
		localTxDataSourceDO.setMaxPoolSize(12);
		localTxDataSourceDO.setMinPoolSize(6);
		localTxDataSourceDO.setNewConnectionSQL("SELECT 1 from dual");
		localTxDataSourceDO.setPrefill(false);
		localTxDataSourceDO.setPreparedStatementCacheSize(100);
		localTxDataSourceDO.setQueryTimeout(180);
        localTxDataSourceDO.setSharePreparedStatements(false);
        localTxDataSourceDO.setTxQueryTimeout(false);
        localTxDataSourceDO.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
        localTxDataSourceDO.setUserName("mysql");
        localTxDataSourceDO.setUseFastFail(false);
        localTxDataSourceDO.setValidateOnMatch(false);
        localTxDataSourceDO.setValidConnectionCheckerClassName("com.alipay.zdatasource.resource.adapter.jdbc.vendor.MySQLValidConnectionChecker");
        localTxDataSourceDO.setDsName("ds-mysql");
		
		LocalTxDsDoMap.put("ds-mysql", localTxDataSourceDO);
    }
//    protected void initDB() {
//        dBTool = new DBTool((DataSource) super.applicationContext.getBean(getDataSourceBean(6)));
//        dBTool.init();
//    }
//
//    /**
//     * 
//     * @param createStatement 建表sql语句
//     */
//    protected void initDB(String createStatement) {
//        dBTool = new DBTool((DataSource) super.applicationContext.getBean(getDataSourceBean(6)));
//        dBTool.init(createStatement);
//    }
//
//    /**
//     * 初始化H2库statement
//     * @throws Exception
//     */
//    protected void initStatementH2() throws Exception {
//        initDB();
//        dataSource = (ZDataSource) super.applicationContext.getBean(getDataSourceBean(6));
//        connection = dataSource.getConnection();
//        statement = connection.createStatement();
//    }
//
//    /**
//     * 初始化H2库PreparedStatement
//     * @throws Exception
//     */
//    protected void initPreparedStatementH2() throws Exception {
//        initDB();
//        dataSource = (ZDataSource) super.applicationContext.getBean(getDataSourceBean(6));
//        connection = dataSource.getConnection();
//        preparedStatement = connection.prepareStatement("insert into test1 values(?,?,?,?)");
//    }
//
//    /**
//     * 初始化H2库connection,并建立指定的表(会先删除再建立)
//     * @throws Exception
//     */
//    protected void initConnectionH2(String createStatement) throws Exception {
//        initDB(createStatement);
//        dataSource = (ZDataSource) super.applicationContext.getBean(getDataSourceBean(6));
//        connection = dataSource.getConnection();
//    }

    protected ResultSet execAndQuary(PreparedStatement preparedStatement) throws Exception {
        preparedStatement.execute();
        preparedStatement = connection.prepareStatement(selectPreparedStatement);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet;
    }

//    /**
//     * 初始化H2库connection
//     * @throws Exception
//     */
//    protected void initConnectionH2() throws Exception {
//        initDB();
//        dataSource = (ZDataSource) super.applicationContext.getBean(getDataSourceBean(6));
//        connection = dataSource.getConnection();
//    }
//
//    protected void addDB() {
//        dBTool = new DBTool((DataSource) super.applicationContext.getBean(getDataSourceBean(6)));
//        dBTool.addDB();
//    }

    protected void clearRequestTime() {
        requestTime = 0l;
    }

    protected void incremRequestTime() {
        requestTime++;
    }

    protected long getRequestTime() {
        return requestTime;
    }

    /**
     * 
     * @param i -1-默认 1,2,3...
     * @return
     */
    protected String getDataSourceBean(int i) {
        String bean = dataSourceBean;
        if (i == 2) {
            bean = "valveDatasourceMysqlApp2";
        }
        if (i == 3) {
            bean = "valveDatasourceMysqlApp3";
        }
        if (i == 4) {
            bean = "valveDatasourceFail";
        }
        if (i == 5) {
            bean = "datasourceMysqlPara";
        }
        if (i == 6) {
            bean = "valveDatasource-h2";
        }
        if (i == 7) {
            bean = "datasource-localMysql";
        }
        return bean;
    }

    /**
     * 
     * @param i -1-默认 1,2,3...
     * @return
     */
    protected String getDsKey(int i) {
        String dsKey = dskey;
        if (i == 1) {
            dsKey = "datasourceMysqlApp2";
        }
        return dsKey;
    }

    protected void sleep(long mill) {
        try {
            long basicSec = 1000l;
            long sec = mill / basicSec;
            long mil = mill % basicSec;
            for (long i = 0; i < sec; i++) {
                Thread.sleep(basicSec);
            }
            Thread.sleep(mil);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected String[] getSQL() {
        return new String[] { "select * from `" + dbName + "`.`" + tbName + "` " };
    }

    /**
     * 初始化表名解析场景，mock解析表名的实际条件。
     * @param tbName mock数据库已有的全部表名
     * @return 数据库表名和其对应的限流规则，此处的限流规则以默认值为准，不支持限流逻辑的测试。<br>
     * 只为表名解析功能提供支持。
     * @author wei.yao
     * @since 2012-2-13
     */
    protected Map<String, ThresholdAndPeriod> initParseScenario(String[] tbName) {
        Map<String, ThresholdAndPeriod> map = new HashMap<String, ThresholdAndPeriod>();
        for (int i = 0; i < tbName.length; i++) {
            map.put(tbName[i], new ThresholdAndPeriod());
        }
        return map;
    }

    /**
     * 调用表名解析方法，执行表名解析方法的测试。
     * @param sql 被解析的sql语句
     * @param map 数据库表名和其对应的限流规则，由initParseScenario(String [] tbName)获得。
     * @return 解析后的表名
     * @author wei.yao
     * @since 2012-2-13
     */
    protected String parseSQL(String sql, @SuppressWarnings("rawtypes") Map map) {
        TableNameParserImpl advice = new TableNameParserImpl();
        Method method;
        String tbName = null;
        try {
            method = TableNameParserImpl.class.getDeclaredMethod("getTableNameFromSQL",
                String.class, Set.class);
            method.setAccessible(true);
            tbName = (String) method.invoke(advice, sql, map.keySet());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tbName;
    }

    protected void onSetUpBefore() {
        //子类覆盖
    }

    public void onTearDownBefore() {
        //子类覆盖
    }
}
