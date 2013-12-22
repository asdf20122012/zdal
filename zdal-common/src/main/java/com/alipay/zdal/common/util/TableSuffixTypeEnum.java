package com.alipay.zdal.common.util;

public enum TableSuffixTypeEnum {
    twoColumnForEachDB("twoColumnForEachDB"), dbIndexForEachDB("dbIndexForEachDB"), groovyTableList(
                                                                                                    "groovyTableList"), groovyAdjustTableList(
                                                                                                                                              "groovyAdjustTableList"), groovyThroughAllDBTableList(
                                                                                                                                                                                                    "groovyThroughAllDBTableList"), throughAllDB(
                                                                                                                                                                                                                                                 "throughAllDB"), resetForEachDB(
                                                                                                                                                                                                                                                                                 "resetForEachDB");
    private String value;

    private TableSuffixTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
