package com.alipay.zdal.test.ut.client;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.alipay.zdal.client.rule.DocumentShardingRule;

public class TestDocumentShardingRule {
	
	  private static final String KEY        = "zhouxiaoqingqingyeqing1981";

	    private static final int    DBCOUNT    = 4;

	    private static final int    TABLECOUNT = 1024;

	    @Test
	    public void test() {
	        Map<Integer, Integer> hotspots = new HashMap<Integer, Integer>();
	        hotspots.put(211, 4);
	        int dbIndex = DocumentShardingRule.getShardingDBNumber(KEY, DBCOUNT, TABLECOUNT);
	        int tableIndex = DocumentShardingRule.getShardingTableNumber(KEY, DBCOUNT, TABLECOUNT);

	        Assert.assertEquals(3, dbIndex);
	        Assert.assertEquals(211, tableIndex);

	        DocumentShardingRule.setHotspot(hotspots);
	        dbIndex = DocumentShardingRule.getShardingDBNumber(KEY, DBCOUNT, TABLECOUNT);
	        tableIndex = DocumentShardingRule.getShardingTableNumber(KEY, DBCOUNT, TABLECOUNT);
	        Assert.assertEquals(4, dbIndex);
	        Assert.assertEquals(211, tableIndex);
	    }


}
