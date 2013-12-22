package com.alipay.zdal.test.tair;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.alipay.ats.annotation.Feature;
import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.ats.junit.ATSJUnitRunner;

@RunWith(ATSJUnitRunner.class)
@Feature("tair本zone化,shard规则的验证")
public class SR958040 {

	@Subject("tair本zone双写,必写idc，必写ldc。groovy脚本为%")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958041() {

	}

	@Subject("tair本zone双写,必写idc，必写ldc。groovy脚本返回非整数")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958042() {

	}

	@Subject("tair本zone双写,必写idc，必写ldc。shard规则为空")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958043() {

	}

}
