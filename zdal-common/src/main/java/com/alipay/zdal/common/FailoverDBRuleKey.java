package com.alipay.zdal.common;

public enum FailoverDBRuleKey {
    MASTER_KEY("master_"), FAILOVER_KEY("failover_");
    private String value;

    private FailoverDBRuleKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
