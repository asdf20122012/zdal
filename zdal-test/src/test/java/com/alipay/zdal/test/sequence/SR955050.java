package com.alipay.zdal.test.sequence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.alipay.ats.annotation.Feature;
import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.assertion.TestAssertion;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.ats.junit.ATSJUnitRunner;
import static com.alipay.ats.internal.domain.ATS.*;
import org.junit.Before;
import com.alipay.zdal.sequence.impl.MultipleSequence;
import com.alipay.zdal.sequence.impl.MultipleSequenceDao;
import com.alipay.zdal.sequence.impl.SequenceDataSourceHolder;

@RunWith(ATSJUnitRunner.class)
@Feature("sequence:特殊场景，自动调整值adjust=true")
public class SR955050{
    public TestAssertion Assert = new TestAssertion();
    public final static Log Logger = LogFactory.getLog(SR955050.class);
    private MultipleSequence multipleSequence;
    private String sequenceName;
	
	@Before
    public void beginTestCase() throws Exception {
		multipleSequence = (MultipleSequence) ZdalSequenceSuite.context.getBean("multipleSequence2");		
	}
	
	@Subject("nextValue不符合value%outStep== index* innerStep，需要手动先设置数据库里值")
	@Priority(PriorityLevel.NORMAL)
	@Test	
	public void TC955051(){
		int turn =200;
		sequenceName = multipleSequence.getSequenceName();
		MultipleSequenceDao sequenceDao = multipleSequence.getSequenceDao();
		
		Step("验证adjust=true");
		Assert.areEqual(true, sequenceDao.getAdjust(), "验证adjust = true");
		
		Step("插入特殊数据");
		int row = 0;
		row = updateValueInDB(sequenceDao,sequenceName);
		Assert.areEqual(1, row, "验证插入特殊数据");
		
		Step("单线程获取的nextValue");
		ExecutorService exec = Executors.newCachedThreadPool();
		ArrayList<Future<Map<Integer, Long>>> results = new ArrayList<Future<Map<Integer, Long>>>();			
		results.add(exec.submit(new MulSequenceCall(multipleSequence,
					sequenceName, turn)));
		
		Step("统计获取的nextValue的个数");
		Set<Long> result = new HashSet<Long>();
		int count = 0;
		for (Future<Map<Integer, Long>> fs : results) {
			try {
				count += fs.get().size();
				for (int i = 1; i <= fs.get().size(); i++) {
					if (!result.add(fs.get().get(i))) {
						Logger.info(""+i);
						Logger.info(""+fs.get().get(i));
					}
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} finally {
				exec.shutdown();
			}
		}
		
		Step("验证获取的nextValue的个数");
		Assert.areEqual(turn, count, "验证获取的nextValue的个数");
	}
	
	/**
	 * 修改数据库里的sequence的value
	 * @param sequenceDao
	 * @param name
	 * @return
	 */
	public int updateValueInDB(MultipleSequenceDao sequenceDao,String name){
		int res = -1; 
		
		String sql = "update "+sequenceDao.getTableName()+" set value=? ,gmt_modified=? where name=?";
		PreparedStatement ps = null;
		Connection connection = null;
		List<SequenceDataSourceHolder> dbList = sequenceDao.getDataSourceList();
		
		try {			
			connection = dbList.get(0).getDs().getConnection();
			ps = connection.prepareStatement(sql);
			ps.setLong(1, 175);
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setString(3, name);
			res = ps.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
		}
		
		return res;
	}
}
