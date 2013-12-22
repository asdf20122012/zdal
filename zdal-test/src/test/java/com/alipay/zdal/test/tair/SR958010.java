package com.alipay.zdal.test.tair;

import java.util.HashMap;

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
@Feature("tair本zone化,通过本地化去初始化,且为单写")
public class SR958010 {
	TestAssertion Assert = new TestAssertion();

	
	  @Subject("tair本zone单写,不写idc,写ldc的master,tairIndexs只有一个")
	  @Priority(PriorityLevel.NORMAL)
	  @Test public void TC958011() { 
	        String va = (String)
	        TestBaseOne.getInstanceWithLocalpath(
	        "singleWriteLdcInitFromLocal01", "test", "./config/tair",
	        "12345678909876543", "555555", "jerrypanwei");
	  
	        Assert.areEqual("jerrypanwei", va, "验证存储到tair的值"); 
	        }
	        
	  @Subject("tair本zone单写,不写idc,写ldc的master,tairIndexs有多个,多个逻辑指向同一集群（不同的namespace）")
	  @Priority(PriorityLevel.NORMAL)
	  @Test public void TC958012() { 
	        String va = (String)
	        TestBaseOne.getInstanceWithLocalpath(
	        "singleWriteLdcInitFromLocal02", "test", "./config/tair",
	        "12345678909876613", "test02", "test123456789-03");
	        Assert.areEqual("test123456789-03", va, "验证存储到tair的值"); 
	        }
	 

	@Subject("tair本zone单写,只写idc,不写ldc")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958013() {
		String va = (String) TestBaseOne.getInstanceWithLocalpath(
				"singleWriteLdcInitFromLocal03", "test", "./config/tair",
				"12345678909876663", "test03", "test123456789-04");
		
		Assert.areEqual("test123456789-04", va, "验证存储到tair的值");
	}
	

	
	  @Subject("tair本zone单写,不写idc,写ldc的master,tairIndexs有多个,第个逻辑tair各指向不同的集群")
	  @Priority(PriorityLevel.NORMAL)
	  @Test public void TC958014(){
	  
		  String va = (String) TestBaseOne.getInstanceWithLocalpath(
					"singleWriteLdcInitFromLocal04", "test", "./config/tair",
					"12345678909876663", "test04", "test123456789-05");
			
			Assert.areEqual("test123456789-05", va, "验证存储到tair的值");
			
	        }
	  
	  
	  @Subject("tair本zone单写,不写idc,写ldc的failover,tairIndexs有多个(全是failover为r10w10),每个逻辑tair指向不同的集群")
	  @Priority(PriorityLevel.NORMAL)
	  @Test public void TC958015(){
		  String va = (String) TestBaseOne.getInstanceWithLocalpath(
					"singleWriteLdcInitFromLocal05", "test", "./config/tair",
					"12345678909876683", "test05", "test123456789-06");
			
			Assert.areEqual("test123456789-06", va, "验证存储到tair的值");
	  
	        }
	  
	  
	  @Subject("tair本zone单写,不写idc,写ldc的failover,tairIndexs有多个(对应的index的failover为r00w00，其它index的failover为r10w10),每个逻辑tair指向不同的集群")
	  @Priority(PriorityLevel.NORMAL)
	  @Test public void TC958016(){
		  
		  String va = (String) TestBaseOne.getInstanceWithLocalpath(
					"singleWriteLdcInitFromLocal06", "test", "./config/tair",
					"12345678909876683", "test05", "test123456789-07");
			
			Assert.areEqual("test123456789-07", va, "验证存储到tair的值");
	  
	        }
	 
	
	  @Subject("tair本zone单写,不写idc,写ldc的failover,tairIndexs有多个(对应的index的failover为r10w10，其它index的failover为r00w00),每个逻辑tair指向不同的集群")
	  @Priority(PriorityLevel.NORMAL)
	  @Test public void TC958017(){
		  
		  String va = (String) TestBaseOne.getInstanceWithLocalpath(
					"singleWriteLdcInitFromLocal07", "test", "./config/tair",
					"12345678909876683", "test05", "test123456789-08");
			
			Assert.areEqual("test123456789-08", va, "验证存储到tair的值");
	  
	        }
	  
	  @Subject("tair本zone单写,只写idc,不写ldc,并且idc对应的物理tair不存在")
		@Priority(PriorityLevel.NORMAL)
		@Test
		public void TC958018() {
		  try{
			TestBaseOne.getInstanceWithLocalpathException(
					"singleWriteLdcInitFromLocal08", "test", "./config/tair",
					"12345678909876663", "test08", "test123456789-09");
		  }catch(Exception e){
			 Assert.areEqual(ZdalTairShardingViolationException.class, e.getClass(), "验证异常信息");
		  }
			
		}
	 
	  
	  @Subject("tair本zone单写,不写idc,写ldc的master,tairIndexs只有一个.写入值为hashmap")
	  @Priority(PriorityLevel.NORMAL)
	  @Test public void TC958019() { 
		  HashMap<String, String> hm=new HashMap<String, String>();
		  hm.put("meimei", "gege");
		  hm.put("gongzhou","xuexi");
		  hm.put("yule","kaixin");
		  
		  HashMap<String, String> va = (HashMap<String, String>)
	        TestBaseOne.getInstanceWithLocalpath(
	        "singleWriteLdcInitFromLocal01", "test", "./config/tair",
	        "12345678909876543", "555555", hm);

	         Assert.isTrue(va.equals(hm), "验证是否相等");
        }
	  
	  @Subject("tair本zone单写,不写idc,写ldc的failover,tairIndexs有多个(对应的index的failover为r10w10，其它index的failover为r00w00),每个逻辑tair指向不同的集群,写入HashMap对象")
	  @Priority(PriorityLevel.NORMAL)
	  @Test public void TC95801a(){
		  
		  HashMap<String, String> hm=new HashMap<String, String>();
		  hm.put("meimei", "gege");
		  hm.put("gongzhou","xuexi");
		  hm.put("yule","kaixin");
		  
		  HashMap<String, String> va = (HashMap<String, String>) TestBaseOne.getInstanceWithLocalpath(
					"singleWriteLdcInitFromLocal07", "test", "./config/tair",
					"12345678909876683", "test05", hm);
			
			Assert.isTrue(va.equals(hm), "验证是否相等");
	  
	        }

}
