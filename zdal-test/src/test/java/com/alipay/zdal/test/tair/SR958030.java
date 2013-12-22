package com.alipay.zdal.test.tair;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.alipay.ats.annotation.Feature;
import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.ats.junit.ATSJUnitRunner;


@RunWith(ATSJUnitRunner.class)
@Feature("tair本zone化,tairIndexs的验证")
public class SR958030 {
	
	@Subject("tair本zone双写,必写idc，非必写ldc。shard规则后超出tairIndexs[index从0开始]")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958031(){
		
	}
	
	@Subject("tair本zone双写,必写idc，非必写ldc。shard规则后的index为0")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958032(){
		
	}
	
	@Subject("tair本zone双写,必写idc，非必写ldc。tairIndexs中的值少于tairMappingMap中的key值")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958033(){
		
	}
	
	@Subject("tair本zone双写,必写idc，非必写ldc。tairIndexs中的值多于tairMappingMap中的key值，在shard之后的index为多出来的tairIndexs")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958034(){
		
	}
	
	@Subject("tair本zone双写,必写idc，非必写ldc。tairIndexs中的值为空")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958035(){
		
	}

}
