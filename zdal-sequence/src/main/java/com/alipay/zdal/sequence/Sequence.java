package com.alipay.zdal.sequence;

import com.alipay.zdal.sequence.exceptions.SequenceException;

/**
 * 序列接口
 *
 * 
 * @author 伯牙
 * @version $Id: Sequence.java, v 0.1 2013-4-3 下午01:35:50 Exp $
 */
public interface Sequence {
    /**
     * 取得序列下一个值
     *
     * @return 返回序列下一个值
     * @throws SequenceException
     */
    long nextValue() throws SequenceException;
}
