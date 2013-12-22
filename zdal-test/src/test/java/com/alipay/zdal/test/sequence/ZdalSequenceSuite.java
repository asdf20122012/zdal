package com.alipay.zdal.test.sequence;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;




@RunWith(Suite.class)
@Suite.SuiteClasses({ 
//sequence
	SR955010.class,
	SR955020.class,
	SR955030.class,
	SR955050.class,
	SR955040.class,
	SR955060.class,
	SR955070.class
	
		
}

)

public class ZdalSequenceSuite {
	
	public static ApplicationContext context;

	@BeforeClass
	public static void beforeTestClass() {
		String[] springXmlPath = { 
				"./sequence/spring-sequence-ds.xml" };
		context = new ClassPathXmlApplicationContext(springXmlPath);
}

}
