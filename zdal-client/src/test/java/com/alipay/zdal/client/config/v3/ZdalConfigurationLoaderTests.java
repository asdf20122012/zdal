/**
 * 
 */
package com.alipay.zdal.client.config.v3;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alipay.zdal.client.config.ZdalConfig;
import com.alipay.zdal.client.config.ZdalConfigurationLoader;

/**
 * @author <a href="mailto:xiang.yangx@alipay.com">Yang Xiang</a>
 *
 */
public class ZdalConfigurationLoaderTests {

	private String[] xmlFilePaths = {"classpath:/config/spring-zdal-shardgroup.xml",
			"classpath:/config/spring-zdal-rule.xml"};
	
	private String[] tradeFilePaths = {"classpath:/config/trade-zdal-ds.xml",
	"classpath:/config/trade-zdal-rule.xml"};
	
	private static final String IDCNAME = "gz00";
	
	private static final String DBMODE = "dev";
	
	private static final String APPNAME = "ccdc";
	
	private static final String APP_BEAN_NAME = "ccdc";
	
	private static final String ATOM_DS_NAME = "atom001";
	
	private static final String SHARDGROUP_DS_NAME = "addp_tddl_ds";
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.alipay.zdal.client.config.ZdalConfigurationLoader#loadZdalConfigurationContext(java.lang.String[], java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testLoadZdalConfigurationContext() {
		Map<String, ZdalConfig> configMap = ZdalConfigurationLoader.getInstance().getZdalConfiguration(xmlFilePaths, APPNAME, DBMODE, IDCNAME);
		assertTrue( null != configMap && configMap.size() == 2 );
		
		for( Entry<String, ZdalConfig> entry : configMap.entrySet() ){
			if( ATOM_DS_NAME.equals(entry.getKey()) ){
				assertNull(entry.getValue().getAppRootRule());
			}else if( SHARDGROUP_DS_NAME.equals(entry.getKey()) ){
				assertNotNull(entry.getValue().getAppRootRule());
			}
		}
		
	}

	/**
	 * Test method for {@link com.alipay.zdal.client.config.ZdalConfigurationLoader#populateZdalConfig(com.alipay.zdal.client.config.bean.ZdalAppBean, com.alipay.zdal.client.config.bean.AppDataSourceBean)}.
	 */
	@Test
	public void testTradeZdalConfig() {
		Map<String, ZdalConfig> configMap = ZdalConfigurationLoader.getInstance().getZdalConfiguration(tradeFilePaths, "trade", "dev", "rz00a");
		assertTrue( null != configMap );
	}

}
