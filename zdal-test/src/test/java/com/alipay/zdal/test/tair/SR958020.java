package com.alipay.zdal.test.tair;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.alipay.ats.annotation.Feature;
import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.assertion.TestAssertion;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.ats.junit.ATSJUnitRunner;
import com.alipay.zdal.tair.shard.impl.ZdalTairShardingViolationException;


@RunWith(ATSJUnitRunner.class)
@Feature("tair本zone化,通过本地化去初始化,且为双写")
public class SR958020 {
	TestAssertion Assert = new TestAssertion();
	
	/**
	@Subject("tair本zone双写,必写idc和ldc,两个都成功")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958021(){
		String va = (String)
        TestBaseOne.getInstanceWithLocalpath(
        "doubleWriteLdcInitFromLocal01", "test", "./config/tair",
        "12345678909876553", "555555", "testdouble01");
		
		Assert.areEqual("testdouble01", va, "验证存储到tair的值");
		
	}
	

	@Subject("tair本zone双写,必写idc和ldc,两个都失败")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958022(){
		
        try {
			TestBaseOne.getInstanceWithLocalpathException(
			"doubleWriteLdcInitFromLocal02", "test", "./config/tair",
			"12345678909876553", "555555", "testdouble02");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.areEqual(ZdalTairShardingViolationException.class, e.getClass(), "异常");
		}	
		
	}
	
	
	@Subject("tair本zone双写,必写idc和ldc,其中一个成功，另一个失败(一个连接不上,另一个可以连接上)")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958023(){
		try {
			TestBaseOne.getInstanceWithLocalpathException(
			"doubleWriteLdcInitFromLocal03", "test", "./config/tair",
			"12345678909876553", "555555", "testdouble03");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.areEqual(ZdalTairShardingViolationException.class, e.getClass(), "异常");
		}	
	}
	
	@Subject("tair本zone双写,必写idc和ldc,其中一个成功，另一个失败(一个写失败namespace过大,别一个写成功)")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958024(){
		try {
			TestBaseOne.getInstanceWithLocalpath(
			"doubleWriteLdcInitFromLocal04", "test", "./config/tair",
			"12345678909876553", "555555", "testdouble03");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.areEqual(ZdalTairShardingViolationException.class, e.getClass(), "异常");
		}	
	}
	*/
	
	@Subject("tair本zone双写,必写idc和ldc,其中一个成功，另一个失败(一个写失败namespace为99999,别一个写成功)")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958025(){
		try {
			TestBaseOne.getInstanceWithLocalpath(
			"doubleWriteLdcInitFromLocal05", "test", "./config/tair",
			"12345678909876553", "555555", "testdouble03");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.areEqual(ZdalTairShardingViolationException.class, e.getClass(), "异常");
		}	
	}
	
	
	
	
	@Subject("tair本zone双写,必写idc非必写ldc。写idc成功，写ldc成功")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC95802j(){
		
	}
	
	@Subject("tair本zone双写,必写idc非必写ldc。写idc成功，但写ldc失败")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC95802i(){
		
	}
	
	@Subject("tair本zone双写,必写idc非必写ldc。写idc失败，但写ldc成功")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958026(){
		
	}
	
	@Subject("tair本zone双写,必写idc非必写ldc。写idc失败，写ldc失败")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958027(){
		
	}
	
	@Subject("tair本zone双写,非必写idc但必写ldc，写idc成功，写ldc成功")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958028(){
		
	}
	
	@Subject("tair本zone双写,非必写idc但必写ldc，写idc成功，写ldc失败")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958029(){
		
	}
	
	@Subject("tair本zone双写,非必写idc但必写ldc，写idc失败，写ldc失败")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC95802a(){
		
	}
	
	@Subject("tair本zone双写,非必写idc但必写ldc，写idc失败，写ldc成功")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC95802b(){
		
	}

}
