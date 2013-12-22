package com.alipay.zdal.common.exception.checked;

public class CantLoadRowJepRuleException extends ZdalCheckedExcption{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1765363763147779906L;
	public CantLoadRowJepRuleException(String expression,String vtable,String parameter) {
		super("无法通过param:"+parameter+"|virtualTableName:"+vtable+"|expression:"+expression+"找到指定的规则判断引擎");
	}

}
