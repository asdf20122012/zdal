package com.alipay.zdal.common.exception.checked;

public class ZdalCheckedExcption extends Exception{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -1186363001286203116L;
	public ZdalCheckedExcption() {
		super();
	}
	public ZdalCheckedExcption(Throwable throwable){
		super(throwable);
	}
    public ZdalCheckedExcption(String message, Throwable cause) {
        super(message, cause);
    }
	public ZdalCheckedExcption(String arg) {
		super(arg);
	}
}
