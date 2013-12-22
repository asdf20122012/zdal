package com.alipay.zdal.test.client;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


@RunWith(Suite.class)
@Suite.SuiteClasses({ 
//client
	SR951100.class,
	SR951120.class,
	SR951090.class,
	SR951080.class,
	SR951060.class,
	SR951070.class,
	SR951110.class,
	SR951030.class,
	SR951040.class,
	SR951050.class,
	SR951010.class,
	SR951121.class,
	SR951122.class

}
)

public class ZdalClientSuite {
	
	public static ApplicationContext context;

	@BeforeClass
	public static void beforeTestClass() {
		String[] springXmlPath = { "./client/spring-client-ds.xml" };
		context = new ClassPathXmlApplicationContext(springXmlPath);

	}

}
