package com.alipay.zdal.test.sequence;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import com.alipay.zdal.sequence.impl.MultipleSequence;


/**通过mulSequenceFactory.getNextValue(sequenceName)生成sequence核心类
 * @version $Id: SequenceCall.java,v 0.1 2012-4-28 上午06:51:37 xiaoju.luo Exp $
 */

class MulSequenceCall implements Callable<Map<Integer, Long>> {
	private static Log logger = LogFactory.getLog("MulSequenceCall.class");
	private long endTime;
	int counter = 0;
	MultipleSequence multipleSequence;
	JdbcTemplate jdbcTemplate;

	public MulSequenceCall(MultipleSequence mulSequence,
			String sequenceName, long endTime) {
		this.multipleSequence = mulSequence;
		this.endTime = endTime;
	}

	public Map<Integer, Long> call() {
		Map<Integer, Long> res = new HashMap<Integer, Long>();
		int i=0;
		try {
			while (i<endTime) {
				long num;
				num = multipleSequence.nextValue();
				logger.info("nextValue:"+num);
				if (num >= 0L)
					counter++;
				res.put(counter, num);
				i++;
			}
		} catch (Exception e) {		
			e.printStackTrace();
		}
		logger.warn(Thread.currentThread().getName() + " sequence 数量为："
				+ counter);
		return res;
	}

}
