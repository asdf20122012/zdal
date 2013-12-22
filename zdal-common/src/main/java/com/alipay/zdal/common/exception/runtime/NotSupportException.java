package com.alipay.zdal.common.exception.runtime;

public class NotSupportException extends ZdalRunTimeException{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1130122397745964828L;

	public NotSupportException(String msg) {
		super("not support yet."+msg);
		
	}
}
