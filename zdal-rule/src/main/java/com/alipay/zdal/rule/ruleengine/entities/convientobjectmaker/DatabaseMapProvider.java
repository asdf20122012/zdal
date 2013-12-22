package com.alipay.zdal.rule.ruleengine.entities.convientobjectmaker;

import java.util.Map;

import com.alipay.zdal.rule.bean.Database;

public interface DatabaseMapProvider {
	public Map<String, Database> getDatabaseMap();
}
