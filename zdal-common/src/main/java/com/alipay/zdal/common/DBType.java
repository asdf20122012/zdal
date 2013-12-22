package com.alipay.zdal.common;

/**
 * @author linxuan
 */
public enum DBType {
    ORACLE(0), MYSQL(1), TAIR(2), OB(3), HBase(5), Neo4J(6);
    
    private int i;

    private DBType(int i) {
        this.i = i;
    }

    public int value() {
        return this.i;
    }

    public boolean isOracle() {
        return this.equals(DBType.ORACLE);
    }

    public boolean isMysql() {
        return this.equals(DBType.MYSQL);
    }

    public boolean isTair() {
        return this.equals(DBType.TAIR);
    }

    public static DBType valueOf(int i) {
        for (DBType t : values()) {
            if (t.value() == i) {
                return t;
            }
        }
        throw new IllegalArgumentException("Invalid SqlType:" + i);
    }
    
    public static DBType convert(String strType) {
        for (DBType t : values()) {
            if (t.toString().equalsIgnoreCase(strType)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Invalid SqlType:" + strType);
    }
    
    public static void main(String[] args){
    	
    	System.out.println(DBType.valueOf("MYSQL"));
    }
}