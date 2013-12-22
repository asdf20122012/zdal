package com.alipay.zdal.test.common;

import org.junit.Test;

import com.alipay.zdal.test.common.ZdalV3Utils;

public class ZdalV2ToV3Tests {

	private static final String CONFIG_PATH = "./config/sequence";
    private static final String CONFIG_V3_PATH = "./config/sequence/v3";
   
  /**  
	@Test
	
	public void test01() {
		String[] springXmlPath = {  "./sequence/spring-sequence-ds.xml" };
		ZdalV3Utils.generateV3Configurations(springXmlPath, CONFIG_PATH, CONFIG_V3_PATH);
	}
	
	
    @Test
    
	public void test02() {
    	
    	ZdalV3Utils.generateV3FilesWithV2DsFile("zdalReadWriteRule", "dev", "gzone", "./config/rw", "./config/rw/v3");	
    	
    	
    }
  
    */
    
    @Test
    public void test03(){
    	String[] springXmlPath = {  "./sequence/spring-sequence-ds.xml" };
    	ZdalV3Utils.generateV3ConfigurationsWithoutInit(springXmlPath,CONFIG_PATH,CONFIG_V3_PATH);
    }

}
