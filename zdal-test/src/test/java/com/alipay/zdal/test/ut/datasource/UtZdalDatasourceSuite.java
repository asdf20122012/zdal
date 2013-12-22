package com.alipay.zdal.test.ut.datasource;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ 
	DatabaseBindingChangeTest.class,MySQLExceptionSorterTest.class,OracleExceptionSorterTest.class,
	PoolMinSizeResetTests.class,ScalaDownMySQLConnectionInOutOfConnectionTests.class,
	ScalaUpAndDownMySQLConnectionsTests.class,ScalaUpConnectionsTests.class,
	ScalaUpMySQLConnectionInOutOfConnectionTests.class,SecureIdentityLoginModuleTest.class,
	TestGetPoolCondition.class,TestScalableSemaphore.class,
	ZDS951010.class,ZDS951020.class,ZDS951030.class,ZDS951040.class,ZDS951050.class,
	ZdsInit.class,ZdsTestMysql.class
	
})

public class UtZdalDatasourceSuite {

}
