package com.alipay.zdal.sequence.impl;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.alipay.zdal.sequence.Sequence;
import com.alipay.zdal.sequence.SequenceDao;
import com.alipay.zdal.sequence.SequenceRange;
import com.alipay.zdal.sequence.exceptions.SequenceException;

/**
 * 序列默认实现
 *
 * @author 伯牙
 * @version $Id: DefaultSequence.java, v 0.1 2013-4-3 下午01:36:16 Exp $
 */
public class DefaultSequence implements Sequence {
    private final Lock             lock = new ReentrantLock();

    private SequenceDao            sequenceDao;

    /**
     * 序列名称
     */
    private String                 name;

    private volatile SequenceRange currentRange;

    public long nextValue() throws SequenceException {
        if (currentRange == null) {
            lock.lock();
            try {
                if (currentRange == null) {
                    currentRange = sequenceDao.nextRange(name);
                }
            } finally {
                lock.unlock();
            }
        }

        long value = currentRange.getAndIncrement();
        if (value == -1) {
            lock.lock();
            try {
                for (;;) {
                    if (currentRange.isOver()) {
                        currentRange = sequenceDao.nextRange(name);
                    }

                    value = currentRange.getAndIncrement();
                    if (value == -1) {
                        continue;
                    }

                    break;
                }
            } finally {
                lock.unlock();
            }
        }

        if (value < 0) {
            throw new SequenceException("Sequence value overflow, value = " + value);
        }

        return value;
    }

    public SequenceDao getSequenceDao() {
        return sequenceDao;
    }

    public void setSequenceDao(SequenceDao sequenceDao) {
        this.sequenceDao = sequenceDao;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
