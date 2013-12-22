package com.alipay.zdal.test.sequence;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alipay.ats.annotation.Feature;
import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.assertion.TestAssertion;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.ats.junit.ATSJUnitRunner;
import com.alipay.zdal.test.common.ConstantsTest;
import static com.alipay.ats.internal.domain.ATS.*;



@RunWith(ATSJUnitRunner.class)
@Feature("multipleSequenceFactory:初始化Sequence异常")
public class SR955030 {
    public TestAssertion Assert = new TestAssertion();
	String sequenceName="sequence_BCD1";
	String url = ConstantsTest.mysql12UrlSequence0;
	String user = ConstantsTest.mysq112User;
	String psw = ConstantsTest.mysq112Psd;
	
	@Subject("multipleSequenceFactory：sequenceDao is null，初始化异常")
	@Priority(PriorityLevel.NORMAL)
	@Test	
	public void TC955031(){		
		@SuppressWarnings("unused")
		ApplicationContext context;
		Step("错误配置");
		try{
			context = new ClassPathXmlApplicationContext(
			new String[] { "/sequence/spring-sequence-exception.xml" });
		}catch (Exception e) {
			Step("校验预期的异常");	
			String errormessage = "nested exception is java.lang.IllegalArgumentException: The sequenceDao is null!";
			Assert.isTrue(e.getMessage().contains(errormessage), "验证异常信息");
		}
		
	}
}
