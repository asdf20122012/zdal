package com.alipay.zdal.client.jdbc.test;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alipay.drm.client.DRMClient;
import com.alipay.zdal.client.jdbc.ZdalDataSource;

public class ZdalBaseTest {
    private String         appName;
    private String         appDsName       = null;
    private String         dbmode;
    private String         idcName;
    private String         serverUrl       = null;
    private String         localConfigPath = null;
    private ZdalDataSource dataSource      = new ZdalDataSource();
    private JdbcTemplate   zdalDataSourceTemplate;
    
    private ZdalDataSource dataSourceV3      = new ZdalDataSource();
    private JdbcTemplate   zdalDataSourceTemplateV3;

    public void setUp() throws Exception {
        dataSource = new ZdalDataSource();
        dataSource.setAppName(appName);
        dataSource.setAppDsName(appDsName);
        dataSource.setDbmode(dbmode);
        dataSource.setZone(idcName);
        dataSource.setZdataconsoleUrl(serverUrl);
        dataSource.setConfigPath(localConfigPath);
        dataSource.init();
        setZdalDataSourceTemplate(new JdbcTemplate(dataSource));
        
        dataSourceV3 = new ZdalDataSource();
        dataSourceV3.setAppName(appName);
        dataSourceV3.setAppDsName(appDsName);
        dataSourceV3.setDbmode(dbmode);
        dataSourceV3.setZone(idcName);
        dataSourceV3.setZdataconsoleUrl(serverUrl);
        dataSourceV3.setConfigPath(localConfigPath);
        dataSourceV3.init();
        setZdalDataSourceTemplate(new JdbcTemplate(dataSource));
    }

    public void insertByZdalDataSourceTemplate(String sql, Object[] objects) {
        zdalDataSourceTemplate.update(sql, objects);
    }

    public void updateByZdalDataSourceTemplate(String sql, Object[] objects) {
        zdalDataSourceTemplate.update(sql, objects);
    }

    public Map<?, ?> selectMapByZdalDataSourceTemplate(String sql, Object[] objects) {
        return zdalDataSourceTemplate.queryForMap(sql, objects);
    }

    public List selectListByZdalDataSourceTemplate(String sql, Object[] objects) {
        return zdalDataSourceTemplate.queryForList(sql, objects);
    }

    public void deleteByZdalDataSourceTemplate(String sql, Object[] objects) {
        zdalDataSourceTemplate.update(sql, objects);
    }

    @After
    public void tearDown() throws Throwable {
        DRMClient.getInstance().unregister("com.alipay.zdal.signal." + appName + "." + appDsName);
        dataSource.close();
    }

    public void setAppDsName(String appDsName) {
        this.appDsName = appDsName;
    }

    public String getAppDsName() {
        return appDsName;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setLocalConfigPath(String localConfigPath) {
        this.localConfigPath = localConfigPath;
    }

    public String getLocalConfigPath() {
        return localConfigPath;
    }

    public void setZdalDataSourceTemplate(JdbcTemplate zdalDataSourceTemplate) {
        this.zdalDataSourceTemplate = zdalDataSourceTemplate;
    }

    public JdbcTemplate getZdalDataSourceTemplate() {
        return zdalDataSourceTemplate;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getDbmode() {
        return dbmode;
    }

    public void setDbmode(String dbmode) {
        this.dbmode = dbmode;
    }

    public String getIdcName() {
        return idcName;
    }

    public void setIdcName(String idcName) {
        this.idcName = idcName;
    }

}
