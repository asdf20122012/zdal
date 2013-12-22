package com.alipay.zdal.test.tair;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.alipay.ats.annotation.Feature;
import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.ats.junit.ATSJUnitRunner;

@RunWith(ATSJUnitRunner.class)
@Feature("tair本zone化,从zdc上拉配置文件")
public class SR958060 {

	@Subject("tair本zone化，从zdc上拉配置文件，url不通")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958061() {

	}

	@Subject("tair本zone化，从zdc上拉配置文件，url连通，第一次拉下来，目录正确，启动用该文件")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958062() {

	}
	
	@Subject("tair本zone化，从zdc上拉配置文件，url连通，第二次拉下来覆盖之前文件")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958063() {

	}
	
	@Subject("tair本zone化，从zdc上拉配置文件，url连通，但在zdc上找不到配置文件")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958064() {

	}
	
	@Subject("tair本zone化，从zdc上拉配置文件，url连通，但在zdc上找不到配置文件，在user.home/config/zdal在对应配置文件")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958065() {

	}
	
	@Subject("tair本zone化，从zdc上拉配置文件，url连通，但在zdc上找不到配置文件，在user.home/config/zdal也没有对应配置文件，在localConfigurationPath有对应配置文件")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958066() {

	}
	
	@Subject("tair本zone化，从zdc上拉配置文件，url连通，但在zdc上找不到配置文件，在user.home/config/zdal也没有对应配置文件，在localConfigurationPath也没对应配置文件")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958067() {

	}
	
	
	
	

}
