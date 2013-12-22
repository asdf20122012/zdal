package com.alipay.zdal.common.monitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 为了不把日志打印到stdout里，将这几个工具类从common-monitor 的jar里迁移出来
 * @author zhaofeng.wang
 * @version $Id: Keys.java,v 0.1 2012-9-12 上午09:56:28 zhaofeng.wang Exp $
 */
public class Keys implements Serializable {

    private static final long   serialVersionUID = 3816390661745162968L;
    private static final String placeholder      = "level-";
    private List<String>        keys             = new ArrayList<String>(4);

    private String              key1;
    private String              key2;
    private String              key3;
    private String              appName;

    public Keys() {

    }

    public Keys(List<String> keys) {
        this.keys.addAll(keys);
        if (keys != null && keys.size() == 4) {
            this.appName = keys.get(1);
        }
    }

    public Keys(String key) {
        this(null, key, null, null);
    }

    public Keys(String key1, String key2) {
        this(null, key1, key2, null);

    }

    public Keys(String key1, String key2, String key3) {
        this(null, key1, key2, key3);
    }

    public Keys(String appName, String key1, String key2, String key3) {
        this.keys.add(appName);
        this.keys.add(key1);
        this.keys.add(key2);
        this.keys.add(key3);

        this.appName = appName;
        this.key1 = key1;
        this.key2 = key2;
        this.key3 = key3;

    }

    public boolean equals(Object comparedKeys) {
        if (!(comparedKeys instanceof Keys)) {
            return false;
        }
        return keys.equals(((Keys) comparedKeys).getKeys());
    }

    public int hashCode() {
        return keys.hashCode();
    }

    public String getString(String splitter) {
        StringBuilder sb = new StringBuilder();
        boolean isNotFirst = false;
        int i = 0;
        for (String key : keys) {

            if (++i == 1 && key == null && appName == null) {
                continue;
            } else if (key == null) {
                key = placeholder + (i - 1);
            }

            if (isNotFirst) {
                sb.append(splitter);
            } else {
                isNotFirst = true;
            }

            sb.append(key);
        }

        return sb.toString();
    }

    private List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public String getKey1() {
        return key1;
    }

    public void setKey1(String key1) {
        this.key1 = key1;
    }

    public String getKey2() {
        return key2;
    }

    public void setKey2(String key2) {
        this.key2 = key2;
    }

    public String getKey3() {
        return key3;
    }

    public void setKey3(String key3) {
        this.key3 = key3;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
