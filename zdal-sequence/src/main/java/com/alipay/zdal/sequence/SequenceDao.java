package com.alipay.zdal.sequence;

import com.alipay.zdal.sequence.exceptions.SequenceException;

/**
 * 序列DAO接口
 *
 * 
 * @author 伯牙
 * @version $Id: SequenceDao.java, v 0.1 2013-4-3 下午01:36:02 Exp $
 */
public interface SequenceDao {
    /**
     * 取得下一个可用的序列区间
     *
     * @param name 序列名称
     * @return 返回下一个可用的序列区间
     * @throws SequenceException
     */
    SequenceRange nextRange(String name) throws SequenceException;

}
