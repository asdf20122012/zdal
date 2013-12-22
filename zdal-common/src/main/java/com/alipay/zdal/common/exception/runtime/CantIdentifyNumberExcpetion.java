package com.alipay.zdal.common.exception.runtime;

public class CantIdentifyNumberExcpetion extends ZdalRunTimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7861250013675710468L;
	public CantIdentifyNumberExcpetion(String input,String input1,Throwable e) {
		super("关键字："+input+"或："+input1+"不能识别为一个数，请重新设定",e);
	}
}
