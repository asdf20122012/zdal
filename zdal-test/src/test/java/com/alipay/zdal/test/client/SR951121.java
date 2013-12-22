package com.alipay.zdal.test.client;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.alipay.ats.annotation.Feature;
import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.ats.junit.ATSJUnitRunner;
import com.alipay.zdal.client.config.ZdalConfigAdapter;
import com.alipay.zdal.test.common.ZdalV3Utils;


@RunWith(ATSJUnitRunner.class)
@Feature("zdal配置文件转化功能")
public class SR951121 {
	
	@Subject("zdal配置文件转换,由2.0转换成3.0")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC951121(){
		ZdalV3Utils.generateV3FilesWithV2DsFile("RW952060", "dev", "gzone", "./config/client", "./config/client/v3");	
		
	}
	
	
	@Subject("有url时,配置文件的拉取")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC951122(){
		ZdalConfigAdapter.adapterConfig("gotone", "lab6", "gz00a",
	            "http://zdataconsole-1-64.test.alipay.net/index.htm", "./config/client", true);
		
	}

}
