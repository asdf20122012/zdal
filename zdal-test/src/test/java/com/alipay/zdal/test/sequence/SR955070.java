package com.alipay.zdal.test.sequence;

import static com.alipay.ats.internal.domain.ATS.Step;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.alipay.ats.annotation.Feature;
import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.assertion.TestAssertion;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.ats.junit.ATSJUnitRunner;
import com.alipay.zdal.sequence.impl.MultipleSequence;
import com.alipay.zdal.test.common.ConstantsTest;
import com.mysql.jdbc.PreparedStatement;

@RunWith(ATSJUnitRunner.class)
@Feature("sequence:验证数据库中的min,max,step值与配置中是否一致，并获取sequence的值")
public class SR955070 {
    public TestAssertion Assert = new TestAssertion();
    public final static Log Logger = LogFactory.getLog(SR955070.class);
    private MultipleSequence multipleSequence;
    private String sequenceName="sequence_Hello";
    private String url0 = ConstantsTest.mysql12UrlSequence0;
    private String url1 = ConstantsTest.mysql12UrlSequence1;
    private String url2 = ConstantsTest.mysql22UrlSequence2;
    
    private String user0 = ConstantsTest.mysq112User;
    private String psw0 = ConstantsTest.mysq112Psd; 
    private String user2 = ConstantsTest.mysq122User;
    private String psw2 = ConstantsTest.mysq122Psd;
    @SuppressWarnings("unused")
	private int threadNum=0;
    
	@Before
    public void beginTestCase() throws Exception {
		
		multipleSequence = (MultipleSequence)ZdalSequenceSuite.context.getBean("multipleSequence4");
		
	}
	
	@Subject("获取sequence.nextValue")
	@Priority(PriorityLevel.NORMAL)
	@Test	
	public void TC955071(){
		threadNum = 8;
		long maxValue=multipleSequence.getMaxValue();
		long minValue=multipleSequence.getMinValue();
		int step=multipleSequence.getInnerStep();
		long maxValue0,maxValue1,maxValue2,minValue0,minValue1,minValue2;
		int step0,step1,step2;
		
		Step("获取各数据源的minValue、maxValue、step");
		maxValue0 = getValueFromDB(url0, user0, psw0, sequenceName, "max");
		minValue0 = getValueFromDB(url0, user0, psw0, sequenceName, "min");
		step0 = getValueFromDB(url0, user0, psw0, sequenceName, "step");

		maxValue1 = getValueFromDB(url1, user0, psw0, sequenceName, "max");
		minValue1 = getValueFromDB(url1, user0, psw0, sequenceName, "min");
		step1 = getValueFromDB(url1, user0, psw0, sequenceName, "step");
		
		maxValue2 = getValueFromDB(url2, user2, psw2, sequenceName, "max");
		minValue2 = getValueFromDB(url2, user2, psw2, sequenceName, "min");
		step2 = getValueFromDB(url2, user2, psw2, sequenceName, "step");

		Step("验证各数据源的minValue、maxValue、step是否与配置文件中的信息一致");
		Assert.areEqual(maxValue, maxValue0, "验证最大值");
		Assert.areEqual(minValue, minValue0, "验证最小值");
		Assert.areEqual(step, step0, "验证步长");
		
		Assert.areEqual(maxValue, maxValue1, "验证最大值");
		Assert.areEqual(minValue, minValue1, "验证最小值");
		Assert.areEqual(step, step1, "验证步长");
		
		Assert.areEqual(maxValue, maxValue2, "验证最大值");
		Assert.areEqual(minValue, minValue2, "验证最小值");
		Assert.areEqual(step, step2, "验证步长");
		
		Step("获取的nextValue");
		long value = multipleSequence.nextValue();
		Assert.isTrue(value<maxValue && value>minValue, "验证nextValue");
			
	}
	
	/**
	 * 从db中获取sequence的配置信息
	 * @param url
	 * @param user
	 * @param psw
	 * @param sequenceName
	 * @param name
	 * @return
	 */
	public int getValueFromDB(String url,String user,String psw,String sequenceName,String name){
		int res = -1; 
		
		String minSql = "select min_value from multiple_sequence where name=?";
		String maxSql = "select max_value from multiple_sequence where name=?";
		String stepSql = "select step from multiple_sequence where name=?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection conLog = DriverManager.getConnection(url,
					user, psw);
			
		    if(name.equalsIgnoreCase("max")){
		    	ps = (PreparedStatement) conLog.prepareStatement(maxSql);
		    }else if(name.equalsIgnoreCase("min")){
		    	ps = (PreparedStatement) conLog.prepareStatement(minSql);
		    }else if(name.equalsIgnoreCase("step")){
		    	ps = (PreparedStatement) conLog.prepareStatement(stepSql);
		    }
			ps.setObject(1, sequenceName);
			rs = ps.executeQuery();
			rs.next();
			res = rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return res;
	}
	/**
	 * 删除某个sequence在db中的记录
	 * @param url
	 * @param user
	 * @param psw
	 * @param sequenceName
	 * @return
	 */
	public int deleteFromDB(String url,String user,String psw,String sequenceName){
		int res = 0; 
		
		String sql = "delete from multiple_sequence where name=?";
		PreparedStatement ps = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			java.sql.Connection conLog = DriverManager.getConnection(url,
					user, psw);			
		    ps = (PreparedStatement) conLog.prepareStatement(sql);
			ps.setObject(1, sequenceName);
			res = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	
		return res;
	}
}
