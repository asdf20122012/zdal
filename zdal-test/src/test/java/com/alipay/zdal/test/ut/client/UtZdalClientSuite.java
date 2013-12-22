package com.alipay.zdal.test.ut.client;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ 
	DbmodeUtilsTest.class,GroovyTest.class,Snippet.class,TestDocumentShardingRule.class,
	ZdalDrmPushResetDataSourceTests.class,ZdalLdcSignalResourceTest.class,
	ZdalSignalResourceTest.class
})

public class UtZdalClientSuite {

}
