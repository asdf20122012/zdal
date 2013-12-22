package com.alipay.zdal.test.tair;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.alipay.ats.annotation.Feature;
import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.ats.junit.ATSJUnitRunner;

@RunWith(ATSJUnitRunner.class)
@Feature("tair本zone化,跨zone访问")
public class SR958050 {

	@Subject("tair本zone化，应用为非gzone，跨zone访问,跨zone访问模式为log。")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958051() {

	}

	@Subject("tair本zone化，应用为非gzone,跨zone访问,跨zone访问模式为exception。")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958052() {

	}
	
	@Subject("tair本zone化，应用为gzone,跨zone访问,跨zone访问模式为exception。")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958053() {

	}
	
	@Subject("tair本zone化，应用为gzone,跨zone访问,跨zone访问模式为log。")
	@Priority(PriorityLevel.NORMAL)
	@Test
	public void TC958054() {

	}

}
