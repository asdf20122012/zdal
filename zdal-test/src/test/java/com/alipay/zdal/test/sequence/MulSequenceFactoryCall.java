package com.alipay.zdal.test.sequence;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import com.alipay.zdal.sequence.impl.MultipleSequenceFactory;

public class MulSequenceFactoryCall implements Callable<Map<Integer, Long>> {
	private static Log logger = LogFactory.getLog("MulSequenceCall.class");
	private long endTime;
	int counter = 0;
	MultipleSequenceFactory multipleSequenceFactory;
	JdbcTemplate jdbcTemplate;
	String sequenceName;

	public MulSequenceFactoryCall(MultipleSequenceFactory multipleSequenceFactory,
			String sequenceName, long endTime) {
		this.multipleSequenceFactory = multipleSequenceFactory;
		this.endTime = endTime;
		this.sequenceName = sequenceName;
	}

	public Map<Integer, Long> call() {
		Map<Integer, Long> res = new HashMap<Integer, Long>();
		int i=0;
		try {
			while (i<endTime) {
				long num;
				num = multipleSequenceFactory.getNextValue(sequenceName);
				logger.info("nextValue:"+num);
				if (num >= 0L)
					counter++;
				res.put(counter, num);
				i++;
//				if (System.currentTimeMillis() >= endTime) {
//					break;
//				}
			}
		} catch (Exception e) {		
			e.printStackTrace();
		}
		logger.warn(Thread.currentThread().getName() + " sequence ÊýÁ¿Îª£º"
				+ counter);
		return res;
	}

}
