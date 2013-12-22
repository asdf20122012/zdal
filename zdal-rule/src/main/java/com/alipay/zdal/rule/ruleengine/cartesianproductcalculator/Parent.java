package com.alipay.zdal.rule.ruleengine.cartesianproductcalculator;

/**
 * 原谅我吧，实在不知道用哪个词
 * 
 * parent 进位时候的监听器
 * @author shenxun
 *
 */
public interface Parent {
	/**
	 * 询问父列有没有值
	 * 
	 * @return
	 */
	public boolean parentHasNext();

	/**
	 * 通知parent进位的方法
	 */
	public void add();
}
