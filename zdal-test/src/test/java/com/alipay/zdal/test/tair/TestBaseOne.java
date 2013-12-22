package com.alipay.zdal.test.tair;

import com.alipay.zdal.tair.impl.ZdalTairCacheManager;

public class TestBaseOne {
	

	/**
	 * 
	 * @param appTairName
	 * @param dbmode
	 * @param configPath
	 * @param uid
	 * @param originalKey
	 * @param putvalue
	 * @return
	 */
	public  static Object getInstanceWithLocalpath(String appTairName,String dbmode,String configPath,String uid,String originalKey,Object putvalue) {
		ZdalTairCacheManager zdalTairCacheManager = new ZdalTairCacheManager();
		zdalTairCacheManager.setAppTairName(appTairName);
		zdalTairCacheManager.setCurrentMode(dbmode);
		zdalTairCacheManager.setLocalConfigurationPath(configPath);
		try {
			//1.初始化
		zdalTairCacheManager.init();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		try {
			// 2.生成新的key
			// (ud为uid,ok为原始码,
			// ws为写模式[0:LEGACY_MASTER,1:LEGACY_MASTER_LDC_REDUNDANT,2:LEGACY_MASTER_LDC_MASTER],3:LDC_MASTER,4:LEGACY_DEDUNDANT_LDC_MASTER
			// ti为选择的tairlogicId,
			// mf为master和failover模式[1:master,0:failover])
			String newKey = zdalTairCacheManager.createZdalTairKey(
					uid ,originalKey);
			System.out.println("===newkey:" + newKey);
			// 3.写值
			boolean bl = zdalTairCacheManager
					.putObject(newKey, putvalue);
			// 4.读值并返回
			return zdalTairCacheManager.getObject(newKey);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return null;		
	}
	
	/**
	 * 
	 * @param appTairName
	 * @param dbmode
	 * @param configPath
	 * @param uid
	 * @param originalKey
	 * @param putvalue
	 * @return
	 * @throws Exception
	 */
	public static void getInstanceWithLocalpathException(String appTairName,String dbmode,String configPath,String uid,String originalKey,Object putvalue) throws Exception{
		ZdalTairCacheManager zdalTairCacheManager = new ZdalTairCacheManager();
		zdalTairCacheManager.setAppTairName(appTairName);
		zdalTairCacheManager.setCurrentMode(dbmode);
		zdalTairCacheManager.setLocalConfigurationPath(configPath);
		try {
			//1.初始化
		zdalTairCacheManager.init();
		}catch(Exception ex){
			ex.printStackTrace();
			throw ex;
		}
		
		try {
			String newKey = zdalTairCacheManager.createZdalTairKey(
					uid ,originalKey);
			System.out.println("===newkey:" + newKey);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
			
		}
	}

}
