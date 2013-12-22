package com.alipay.zdal.common.exception.checked;

public class CantFindPositionByParamException extends ZdalCheckedExcption {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3682437768303903330L;

	public CantFindPositionByParamException(String param) {
		super("不能根据"+param+"属性找到其对应的位置，请注意分表规则不支持组合规则，请不要使用组合规则来进行分表查询");
	}
}
