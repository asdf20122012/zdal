package com.alipay.zdal.rule.bean;

import java.util.List;

import com.alipay.zdal.rule.ruleengine.entities.retvalue.TargetDB;

/**
 * 匹配的结果
 * 
 * @author shenxun
 *
 */
public interface CalculationResult {
	/**
	 * 根据当前规则，返回一个TargetDB的列表
	 * @return
	 */
	public List<TargetDB> getTargetDBList();
	
}
