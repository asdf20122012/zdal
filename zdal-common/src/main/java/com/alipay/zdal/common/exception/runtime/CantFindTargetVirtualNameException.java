package com.alipay.zdal.common.exception.runtime;

public class CantFindTargetVirtualNameException extends ZdalRunTimeException{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 5542737179921749267L;

	public CantFindTargetVirtualNameException(String virtualTabName) {
		super("can't find virtualTabName:"+virtualTabName);
	}
}
