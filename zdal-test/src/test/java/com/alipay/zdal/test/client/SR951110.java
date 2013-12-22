package com.alipay.zdal.test.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import com.alipay.ats.annotation.Feature;
import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.assertion.TestAssertion;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.ats.junit.ATSJUnitRunner;
import com.alipay.zdal.client.config.drm.ZdalSignalResource;
import com.alipay.zdal.client.jdbc.ZdalDataSource;
import com.alipay.zdal.common.DBType;
import com.alipay.zdal.common.jdbc.sorter.MySQLExceptionSorter;
import com.alipay.zdal.common.jdbc.sorter.OracleExceptionSorter;
import static com.alipay.ats.internal.domain.ATS.Step;



@RunWith(ATSJUnitRunner.class)
@Feature("修改mysql和oracle的  errorCode")
public class SR951110 {
	public TestAssertion Assert = new TestAssertion();
	private ZdalSignalResource zdalSignalResource; 
	private String resourceKey= "errorCode";
	
	@Subject("修改mysq的errorCode，修改成数值")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC951111(){
		Step("修改mysq的errorCode，修改成数值");
		ZdalDataSource zd = (ZdalDataSource) ZdalClientSuite.context
		.getBean("zdalClientPrefillshardfailoverzoneDsIsNull");
		Step("进行修改");
		updateErrorCode(zd,"123456789","mysql");
		Assert.areEqual(MySQLExceptionSorter.ERRORCODE, Integer.parseInt("123456789"),"errorCode");		
		zdalSignalResource.updateResource(resourceKey, "-1");
			
	}
	
	@Subject("修改mysql的errorCode，修改成非数值")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC951112(){
		Step("修改mysql的errorCode，修改成非数值");
		ZdalDataSource zd = (ZdalDataSource) ZdalClientSuite.context
		.getBean("zdalClientPrefillshardfailoverzoneDsIsNull");
		Step("修改值");
		int  value_before = MySQLExceptionSorter.ERRORCODE;
		updateErrorCode(zd,"a","mysql");
		Assert.areEqual(MySQLExceptionSorter.ERRORCODE, value_before,"errorCode");
	}
	
	@Subject("修改oracle的errorCode，修改成数值")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC951113(){
		Step("修改oracle的errorCode，修改成数值");
		ZdalDataSource zd = (ZdalDataSource) ZdalClientSuite.context
		.getBean("zdalClientPrefillshardOraclezoneDsIsNull");
		Step("修改值");
		updateErrorCode(zd,"123456789","oracle");
		Assert.areEqual(OracleExceptionSorter.ERRORCODE, Integer.parseInt("123456789"),"errorCode");		
		zdalSignalResource.updateResource(resourceKey, "-1");
			
	}
	
	@Subject("修改oracle的errorCode，修改成非数值")
	@Priority(PriorityLevel.HIGHEST)
	@Test
	public void TC951114(){
		ZdalDataSource zd = (ZdalDataSource) ZdalClientSuite.context
		.getBean("zdalClientPrefillshardOraclezoneDsIsNull");
		
		int  value_before = OracleExceptionSorter.ERRORCODE;
		updateErrorCode(zd,"a","oracle");
		Assert.areEqual(OracleExceptionSorter.ERRORCODE, value_before,"errorCode");				
		
	}
	
	/**
	 * 修改errorCode的公共函数
	 * @param zd
	 * @param value
	 * @param type
	 */
	public void updateErrorCode(ZdalDataSource zd,String value,String type){
		if("mysql".equalsIgnoreCase(type)){
			zdalSignalResource=new ZdalSignalResource(zd.getZdalConfigListener(), "zdal-test",DBType.MYSQL);
		}else{
			zdalSignalResource=new ZdalSignalResource(zd.getZdalConfigListener(), "zdal-test",DBType.ORACLE);
		}

		zdalSignalResource.updateResource(resourceKey, value);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	


}
