package com.alipay.zdal.client.util.condition;

import java.util.List;

import com.alipay.zdal.client.RouteCondition;



public interface DBSelectorRouteCondition extends RouteCondition{
	public String getDBSelectorID();
	public List<String> getTableList();
}
