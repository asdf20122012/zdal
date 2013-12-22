package com.alipay.zdal.test.ut.client;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import com.alipay.zdal.client.config.drm.ZdalDrmPushListener;
import com.alipay.zdal.client.config.drm.ZdalSignalResource;
import com.alipay.zdal.client.jdbc.ZdalDataSource;
import com.alipay.zdal.client.scalable.DataSourceBindingChangeException;
import com.alipay.zdal.common.DBType;
import com.alipay.zdal.datasource.LocalTxDataSourceDO;

public class ZdalDrmPushResetDataSourceTests {
	
	private static final String validResetOperation = "{"
		+"\"operation\":\"resetDataSourceBinding\","
		+ "\"bindings\":["
		+ "{"
		+ "\"physicalDb\": \"phyicalDB000\","
		+ "\"logicalDb\": \"ld000\""
		+ "},"
		+ "{"
		+ "\"physicalDb\": \"phyicalDB001\","
		+ "\"logicalDb\": \"ld001\","
		+ "\"phyicalDbSettings\":"
		+ "{"
		+ "\"dbType\":\"MySQL\","
		+ "\"driverClass\":\"testDrivers\","
		+ "\"jdbcUrl\":\"jdbc:mysql://mysql-1-2.bjl.alipay.net:3306/tddl_2\","
		+ "\"userName\":\"mysql\","
		+ "\"password\":\"-76079f94c1e11c89\","
		+ "\"minConn\":\"1\","
		+ "\"maxConn\":\"20\","
		+ "\"prefill\":\"true\","
		+ "\"blockingTimeoutMillis\":\"180\","
		+ "\"idleTimeoutMinutes\":\"180\","
		+ "\"preparedStatementCacheSize\":\"100\","
		+ "\"queryTimeout\":\"180\","
		+ "\"maxReadThreshold\":\"100\","
		+ "\"maxWriteThreshold\":\"100\","
		+ "\"failoverRule\":\"failover\"}}]}"
    	;

private static final String validResetPoolSizeStr = 
		//"{\"operation\":\"resetPoolSize\",\"physicalDbName\":\"phyicalDB001\",\"minSize\":1,\"maxSize\":10}";
        "{\"operation\":\"resetPoolSize\",\"newSettings\":[{\"physicalDbName\":\"phyicalDB001\",\"minSize\":1,\"maxSize\":10}]}";
private static final String inValidResetPoolSizeStr = 
		"{\"operation\":\"resetPoolSize\",\"physicalDbName\":\"\",\"minSize\":abac,\"maxSize\":10}";

private FakedZdalDataSource dataSource = null;

private ZdalSignalResource resource = null;

private DefaultZdalConfigListener configListener = null;

@Before
public void setUp() throws Exception {
	dataSource = new FakedZdalDataSource();
	configListener = new DefaultZdalConfigListener(dataSource);
	resource = new ZdalSignalResource(configListener, "trade50",
            DBType.ORACLE);
}

@After
public void tearDown() throws Exception {
	dataSource = null;
	configListener = null;
	resource = null;
}

@Test
public void testResetWithValidOperationStr() {
	try{
		resource.updateResource(ZdalSignalResource.DRM_ATTR_RESET_DATA_SOURCE, validResetOperation);
		Assert.isTrue(true);
	}catch(Exception e){
		fail();
	}
	try{
		resource.updateResource(ZdalSignalResource.DRM_ATTR_RESET_DATA_SOURCE, validResetPoolSizeStr);
		Assert.isTrue(true);
	}catch(Exception e){
		fail();
	}
}

@Test
public void testResetWithInvalidOperationStr() {
	try{
		resource.updateResource(ZdalSignalResource.DRM_ATTR_RESET_DATA_SOURCE, null);
		Assert.isTrue(true);
	}catch(Exception e){
		fail();
	}
	try{
		resource.updateResource(ZdalSignalResource.DRM_ATTR_RESET_DATA_SOURCE, null);
		Assert.isTrue(true);
	}catch(Exception e){
		fail();
	}
}

@Test
public void testResetWithInvalidResetPoolSize() {
	try{
		resource.updateResource(ZdalSignalResource.DRM_ATTR_RESET_DATA_SOURCE, inValidResetPoolSizeStr);
		
		Assert.isTrue(dataSource.getMinSize() == 0 );
		Assert.isTrue(dataSource.getMaxSize() == 0 );
		String inValidStr = 
				"{\"operation\":\"resetPoolSize\",\"physicalDbName\":\"\",\"minSize\":1,\"maxSize\":10}";
		resource.updateResource(ZdalSignalResource.DRM_ATTR_RESET_DATA_SOURCE, inValidStr);
		
		Assert.isTrue(dataSource.getMinSize() == 0 );
		Assert.isTrue(dataSource.getMaxSize() == 0 );
		
		dataSource.getDsSettingMap().put("phyicalDB001", new LocalTxDataSourceDO());
		resource.updateResource(ZdalSignalResource.DRM_ATTR_RESET_DATA_SOURCE, validResetPoolSizeStr);
		
		Assert.isTrue(dataSource.getMinSize() == 1 );
		Assert.isTrue(dataSource.getMaxSize() == 10 );
	}catch(Exception e){
	    e.printStackTrace();
		fail();
	}
}

@Test
public void testResetWithInvalidResetDataSourceBinding() {
	try{
		resource.updateResource(ZdalSignalResource.DRM_ATTR_RESET_DATA_SOURCE, null);
		
		Assert.isNull(dataSource.getDsSettingMap().get("phyicalDB001"));
		String inValidStr = 
				"";
		resource.updateResource(ZdalSignalResource.DRM_ATTR_RESET_DATA_SOURCE, inValidStr);
		
		Assert.isNull(dataSource.getDsSettingMap().get("phyicalDB001"));
		
		Assert.isNull(dataSource.getDsSettingMap().get("phyicalDB001"));
		resource.updateResource(ZdalSignalResource.DRM_ATTR_RESET_DATA_SOURCE, validResetOperation);
		
		Assert.notNull(dataSource.getDsSettingMap().get("phyicalDB001"));
		Assert.isNull(dataSource.getDsSettingMap().get("lds001"));
	}catch(Exception e){
		e.printStackTrace();
		fail();
	}
}
}

class FakedZdalDataSource extends ZdalDataSource{

private int minSize;

private int maxSize;

private Map<String, String> dsBindingMap = new HashMap<String, String>();

private Map<String, LocalTxDataSourceDO> dsSettingMap = new HashMap<String, LocalTxDataSourceDO>();

public void reBindingDataSource(String phyicalDbName, String logicalDbName,
		LocalTxDataSourceDO phyicalDbParameters)
		throws DataSourceBindingChangeException {
	if( null == logicalDbName || "".equalsIgnoreCase(logicalDbName) ){
		CONFIG_LOGGER.error("reBindingDataSource failed with an empty logicalDbName.");
		return;
	}
	if( null == phyicalDbName || "".equalsIgnoreCase(phyicalDbName) ){
		CONFIG_LOGGER.error("reBindingDataSource failed with an empty phyicalDbName.");
		return;
	}
	String existedPhyicalDbName = getDsBindingMap().get(logicalDbName);
	if( null == existedPhyicalDbName ){
		CONFIG_LOGGER.warn("reBindingDataSource failed to located physical datasource with logicalDbName " + logicalDbName);
	}
	if( null == getDsSettingMap().get(phyicalDbName) && null == phyicalDbParameters ){
		CONFIG_LOGGER.error("reBindingDataSource failed with an empty phyicalDbParameters for DB " + phyicalDbName);
		return;
	}
	if( null == phyicalDbParameters ){
		getDsBindingMap().put(logicalDbName, phyicalDbName);
	}else{
		getDsSettingMap().put(phyicalDbName, phyicalDbParameters);
		getDsBindingMap().put(logicalDbName, phyicalDbName);
	}
}

public void resetConnectionPoolSize(String physicalDbId, int poolMinSize, int poolMaxSize){
	if( null == dsSettingMap.get(physicalDbId)){
		CONFIG_LOGGER.error("The physical DB " + physicalDbId + " is not existed in current ZdalDataSource.");
		return ;
	}
	setMinSize(poolMinSize);
	setMaxSize(poolMaxSize);
}

public int getMinSize() {
	return minSize;
}

public void setMinSize(int minSize) {
	this.minSize = minSize;
}

public int getMaxSize() {
	return maxSize;
}

public void setMaxSize(int maxSize) {
	this.maxSize = maxSize;
}

public Map<String, String> getDsBindingMap() {
	return dsBindingMap;
}

public void setDsBindingMap(Map<String, String> dsBindingMap) {
	this.dsBindingMap = dsBindingMap;
}

public Map<String, LocalTxDataSourceDO> getDsSettingMap() {
	return dsSettingMap;
}

public void setDsSettingMap(Map<String, LocalTxDataSourceDO> dsSettingMap) {
	this.dsSettingMap = dsSettingMap;
}

}
