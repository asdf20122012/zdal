package com.alipay.zdal.common.exception.checked;

public class ComparativeArraysOutOfBoundsException extends ZdalCheckedExcption{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -5581961568206504845L;

	public ComparativeArraysOutOfBoundsException(String ms,Throwable e) {
		super(ms,e);
	}
}
