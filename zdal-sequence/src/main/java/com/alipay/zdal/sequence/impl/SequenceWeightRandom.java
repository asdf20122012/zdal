/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.zdal.sequence.impl;

import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.alipay.zdal.sequence.SequenceConstants;

/**
 * 生成随机数，用于选择在哪个库上获取sequence
 * @author zhaofeng.wang
 * @version $Id: SequeceWeightRandom.java, v 0.1 2011-12-16 上午09:45:25 zhaofeng.wang Exp $
 */
public class SequenceWeightRandom {
    private static final Logger logger         = Logger
                                                   .getLogger(SequenceConstants.ZDAL_SEQUENCE_LOG_NAME);
    private final int           dataSourceNum;
    private final int           weights[];

    private final Random        random         = new Random();
    private final int           DEFAULT_WEIGHT = 10;

    public SequenceWeightRandom(int dataSourceNum) {
        this.dataSourceNum = dataSourceNum;
        weights = new int[dataSourceNum];
        for (int i = 0; i < dataSourceNum; i++) {
            weights[i] = DEFAULT_WEIGHT;
        }
    }

    public int getRandomDataSourceIndex(List<Integer> excludeIndexes) {
        int[] tempWeights = weights.clone();
        for (int i = 0; i < dataSourceNum; i++) {
            if (excludeIndexes.contains(i)) {
                tempWeights[i] = 0;
            }
        }
        int[] areaEnds = getAreaEnds(tempWeights);
        return select(areaEnds);
    }

    private int[] getAreaEnds(int[] weights) {
        if (weights == null) {
            return null;
        }
        int[] areaEnds = new int[weights.length];
        int sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i];
            areaEnds[i] = sum;
        }
        return areaEnds;
    }

    private int select(int[] areaEnds) {
        int sum = areaEnds[areaEnds.length - 1];
        if (sum == 0) {
            logger.error("ERROR ## areaEnds: " + intArray2String(areaEnds));
            return -1;
        }
        int rand = random.nextInt(sum);
        for (int i = 0; i < areaEnds.length; i++) {
            if (rand < areaEnds[i]) {
                return i;
            }
        }
        return -1;
    }

    private String intArray2String(int[] inta) {
        if (inta == null) {
            return "null";
        } else if (inta.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(inta[0]);
        for (int i = 1; i < inta.length; i++) {
            sb.append(", ").append(inta[i]);
        }
        sb.append("]");
        return sb.toString();
    }
}
