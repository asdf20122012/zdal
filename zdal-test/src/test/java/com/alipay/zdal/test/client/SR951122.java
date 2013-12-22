package com.alipay.zdal.test.client;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.alipay.ats.annotation.Feature;
import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.ats.junit.ATSJUnitRunner;
import com.alipay.zdal.client.config.ShardTableRule;

@RunWith(ATSJUnitRunner.class)
@Feature("zdal的规则功能")
public class SR951122 {
	
	@Subject("规则功能")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TCSR951122(){
		ShardTableRule sr=new ShardTableRule();
		sr.setLogicTableName("test001");
		sr.setDbIndex("SETBYPOOL:master_0,master_1,master_2,master_3");
		ArrayList<String> al=new ArrayList();
		al.add("Integer.valueOf((#user_id#)%5*2)");
		sr.setDbRules(al);
		ArrayList<String> al2=new ArrayList();
		al2.add("Integer.valueOf(#user_id#)%5");
		sr.setTableRules(al2);
		sr.setTableSuffix("resetForEachDB:[_0-_4]");
		
		sr.toString();
		sr.hashCode();
		sr.equals(sr);
	}
	

}
