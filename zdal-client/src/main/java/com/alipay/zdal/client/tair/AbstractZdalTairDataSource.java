/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.alipay.zdal.client.tair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alipay.zdal.client.config.TairDataSourceParameter;
import com.alipay.zdal.client.config.ZdalConfig;
import com.alipay.zdal.client.config.ZdalDataSourceConfig;
import com.alipay.zdal.client.tair.exception.ZdalTairDataSourceException;
import com.alipay.zdal.common.Closable;
import com.alipay.zdal.common.lang.StringUtil;
import com.taobao.tair.CallMode;
import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;
import com.taobao.tair.TairCallback;
import com.taobao.tair.TairManager;
import com.taobao.tair.etc.CounterPack;
import com.taobao.tair.etc.KeyValuePack;
import com.taobao.tair.impl.DefaultTairManager;

/**
 * 
 * @author 伯牙
 * @version $Id: AbstractZdalTairDataSource.java, v 0.1 2013-1-30 上午10:01:07 Exp $
 */
public abstract class AbstractZdalTairDataSource extends ZdalDataSourceConfig implements Closable,
                                                                             TairManager {
    /** tair的数据源. */
    private DefaultTairManager tairManager = null;

    /** 
     * @see com.alipay.zdal.client.config.ZdalDataSourceConfig#initDataSources(com.alipay.zdal.client.config.ZdalConfig)
     */
    public final void initDataSources(ZdalConfig zdalConfig) {
        if (zdalConfig.getTairDataSourceParameters() == null
            || zdalConfig.getTairDataSourceParameters().isEmpty()) {
            throw new ZdalTairDataSourceException(
                "ERROR ## the Tair datasource's parameter is null,the appName = "
                        + zdalConfig.getAppName() + ",the appDsName = " + zdalConfig.getAppDsName()
                        + ",the version = " + zdalConfig.getVersion());
        }
        TairDataSourceParameter parameter = null;
        for (Entry<String, TairDataSourceParameter> entry : zdalConfig
            .getTairDataSourceParameters().entrySet()) {//目前在tair数据源中只支持单个.
            parameter = entry.getValue();
            break;
        }
        String masterUrl = parameter.getMasterUrl();
        if (StringUtil.isBlank(masterUrl)) {
            throw new ZdalTairDataSourceException("ERROR ## the masterUrl is null");
        }
        String slaveUrl = parameter.getSlaveUrl();
        if (StringUtil.isBlank(slaveUrl)) {
            throw new ZdalTairDataSourceException("ERROR ## the slaveUrl is null");
        }
        String group = parameter.getGroupName();
        if (StringUtil.isBlank(group)) {
            throw new ZdalTairDataSourceException("ERROR ## the groupName is null ");
        }
        List<String> configList = new ArrayList<String>();
        configList.add(masterUrl);
        configList.add(slaveUrl);
        this.tairManager = new DefaultTairManager();
        this.tairManager.setConfigServerList(configList);
        this.tairManager.setGroupName(group);
        this.tairManager.init();
    }

    /** 
     * @see com.alipay.zdal.common.Closable#close()
     */
    public final void close() throws Throwable {
        if (this.tairManager != null) {
            this.tairManager = null;
        }
    }

    /** 
     * @see com.alipay.zdal.client.config.ZdalDataSourceConfig#initDrmListener()
     */
    public final void initDrmListener() {
    }

    /** 
     * @see com.taobao.tair.TairManager#addItems(int, java.io.Serializable, java.util.List, int, int, int)
     */
    public ResultCode addItems(int namespace, Serializable key, List<? extends Object> items,
                               int maxCount, int version, int expireTime) {

        return tairManager.addItems(namespace, key, items, maxCount, version, expireTime);
    }

    /** 
     * @see com.taobao.tair.TairManager#decr(int, java.io.Serializable, int, int, int)
     */
    public Result<Integer> decr(int namespace, Serializable key, int value, int defaultValue,
                                int expireTime) {

        return tairManager.decr(namespace, key, value, defaultValue, expireTime);
    }

    /** 
     * @see com.taobao.tair.TairManager#delete(int, java.io.Serializable)
     */
    public ResultCode delete(int namespace, Serializable key) {

        return tairManager.delete(namespace, key);
    }

    /** 
     * @see com.taobao.tair.TairManager#get(int, java.io.Serializable)
     */
    public Result<DataEntry> get(int namespace, Serializable key) {

        return tairManager.get(namespace, key);
    }

    /** 
     * @see com.taobao.tair.TairManager#getAndRemove(int, java.io.Serializable, int, int)
     */
    public Result<DataEntry> getAndRemove(int namespace, Serializable key, int offset, int count) {

        return tairManager.getAndRemove(namespace, key, offset, count);
    }

    /** 
     * @see com.taobao.tair.TairManager#getGroupName()
     */
    public String getGroupName() {

        return tairManager.getGroupName();
    }

    /** 
     * @see com.taobao.tair.TairManager#getItemCount(int, java.io.Serializable)
     */
    public Result<Integer> getItemCount(int namespace, Serializable key) {

        return tairManager.getItemCount(namespace, key);
    }

    /** 
     * @see com.taobao.tair.TairManager#getItems(int, java.io.Serializable, int, int)
     */
    public Result<DataEntry> getItems(int namespace, Serializable key, int offset, int count) {

        return tairManager.getItems(namespace, key, offset, count);
    }

    /** 
     * @see com.taobao.tair.TairManager#getStat(int, java.lang.String, long)
     */
    public Map<String, String> getStat(int qtype, String groupName, long serverId) {

        return tairManager.getStat(qtype, groupName, serverId);
    }

    /** 
     * @see com.taobao.tair.TairManager#getVersion()
     */
    public String getVersion() {

        return tairManager.getVersion();
    }

    /** 
     * @see com.taobao.tair.TairManager#incr(int, java.io.Serializable, int, int, int)
     */
    public Result<Integer> incr(int namespace, Serializable key, int value, int defaultValue,
                                int expireTime) {

        return tairManager.incr(namespace, key, value, defaultValue, expireTime);
    }

    /** 
     * @see com.taobao.tair.TairManager#invalid(int, java.io.Serializable)
     */
    public ResultCode invalid(int namespace, Serializable key) {

        return tairManager.invalid(namespace, key);
    }

    /** 
     * @see com.taobao.tair.TairManager#mdelete(int, java.util.List)
     */
    public ResultCode mdelete(int namespace, List<? extends Object> keys) {

        return tairManager.mdelete(namespace, keys);
    }

    /** 
     * @see com.taobao.tair.TairManager#mget(int, java.util.List)
     */
    public Result<List<DataEntry>> mget(int namespace, List<? extends Object> keys) {

        return tairManager.mget(namespace, keys);
    }

    /** 
     * @see com.taobao.tair.TairManager#minvalid(int, java.util.List)
     */
    public ResultCode minvalid(int namespace, List<? extends Object> keys) {

        return tairManager.minvalid(namespace, keys);
    }

    /** 
     * @see com.taobao.tair.TairManager#put(int, java.io.Serializable, java.io.Serializable)
     */
    public ResultCode put(int namespace, Serializable key, Serializable value) {

        return tairManager.put(namespace, key, value);
    }

    /** 
     * @see com.taobao.tair.TairManager#put(int, java.io.Serializable, java.io.Serializable, int)
     */
    public ResultCode put(int namespace, Serializable key, Serializable value, int version) {

        return tairManager.put(namespace, key, value, version);
    }

    /** 
     * @see com.taobao.tair.TairManager#put(int, java.io.Serializable, java.io.Serializable, int, int)
     */
    public ResultCode put(int namespace, Serializable key, Serializable value, int version,
                          int expireTime) {

        return tairManager.put(namespace, key, value, version, expireTime);
    }

    /** 
     * @see com.taobao.tair.TairManager#removeItems(int, java.io.Serializable, int, int)
     */
    public ResultCode removeItems(int namespace, Serializable key, int offset, int count) {

        return tairManager.removeItems(namespace, key, offset, count);
    }

    @Override
    public Result<DataEntry> getHidden(int arg0, Serializable arg1) {
        return tairManager.getHidden(arg0, arg1);
    }

    @Override
    public int getMaxFailCount() {
        return tairManager.getMaxFailCount();
    }

    @Override
    public Result<List<DataEntry>> getRange(int arg0, Serializable arg1, Serializable arg2,
                                            Serializable arg3, int arg4, int arg5) {
        return tairManager.getRange(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public Result<List<DataEntry>> getRangeOnlyKey(int arg0, Serializable arg1, Serializable arg2,
                                                   Serializable arg3, int arg4, int arg5) {
        return tairManager.getRangeOnlyKey(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public Result<List<DataEntry>> getRangeOnlyValue(int arg0, Serializable arg1,
                                                     Serializable arg2, Serializable arg3,
                                                     int arg4, int arg5) {
        return tairManager.getRangeOnlyValue(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public ResultCode hide(int arg0, Serializable arg1) {
        return tairManager.hide(arg0, arg1);
    }

    @Override
    public ResultCode hideByProxy(int arg0, Serializable arg1) {
        return tairManager.hideByProxy(arg0, arg1);
    }

    @Override
    public ResultCode hideByProxy(int arg0, Serializable arg1, CallMode arg2) {
        return tairManager.hideByProxy(arg0, arg1, arg2);
    }

    @Override
    public ResultCode invalid(int arg0, Serializable arg1, CallMode arg2) {
        return tairManager.invalid(arg0, arg1, arg2);
    }

    @Override
    public ResultCode lock(int arg0, Serializable arg1) {
        return tairManager.lock(arg0, arg1);
    }

    @Override
    public Result<List<Object>> mlock(int arg0, List<? extends Object> arg1) {
        return tairManager.mlock(arg0, arg1);
    }

    @Override
    public Result<List<Object>> mlock(int arg0, List<? extends Object> arg1,
                                      Map<Object, ResultCode> arg2) {
        return tairManager.mlock(arg0, arg1, arg2);
    }

    @Override
    public Result<Map<Object, Map<Object, Result<DataEntry>>>> mprefixGetHiddens(
                                                                                 int arg0,
                                                                                 Map<? extends Serializable, ? extends List<? extends Serializable>> arg1) {
        return tairManager.mprefixGetHiddens(arg0, arg1);
    }

    @Override
    public Result<List<Object>> munlock(int arg0, List<? extends Object> arg1) {
        return tairManager.munlock(arg0, arg1);
    }

    @Override
    public Result<List<Object>> munlock(int arg0, List<? extends Object> arg1,
                                        Map<Object, ResultCode> arg2) {
        return tairManager.munlock(arg0, arg1, arg2);
    }

    @Override
    public Result<Integer> prefixDecr(int arg0, Serializable arg1, Serializable arg2, int arg3,
                                      int arg4, int arg5) {
        return tairManager.prefixDecr(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public Result<Map<Object, Result<Integer>>> prefixDecrs(int arg0, Serializable arg1,
                                                            List<CounterPack> arg2) {
        return tairManager.prefixDecrs(arg0, arg1, arg2);
    }

    @Override
    public ResultCode prefixDelete(int arg0, Serializable arg1, Serializable arg2) {
        return tairManager.prefixDelete(arg0, arg1, arg2);
    }

    @Override
    public Result<Map<Object, ResultCode>> prefixDeletes(int arg0, Serializable arg1,
                                                         List<? extends Serializable> arg2) {
        return tairManager.prefixDeletes(arg0, arg1, arg2);
    }

    @Override
    public Result<DataEntry> prefixGet(int arg0, Serializable arg1, Serializable arg2) {
        return tairManager.prefixGet(arg0, arg1, arg2);
    }

    @Override
    public Result<DataEntry> prefixGetHidden(int arg0, Serializable arg1, Serializable arg2) {
        return tairManager.prefixGetHidden(arg0, arg1, arg2);
    }

    @Override
    public Result<Map<Object, Result<DataEntry>>> prefixGetHiddens(int arg0, Serializable arg1,
                                                                   List<? extends Serializable> arg2) {
        return tairManager.prefixGetHiddens(arg0, arg1, arg2);
    }

    @Override
    public Result<Map<Object, Result<DataEntry>>> prefixGets(int arg0, Serializable arg1,
                                                             List<? extends Serializable> arg2) {
        return tairManager.prefixGets(arg0, arg1, arg2);
    }

    @Override
    public ResultCode prefixHide(int arg0, Serializable arg1, Serializable arg2) {
        return tairManager.prefixHide(arg0, arg1, arg2);
    }

    @Override
    public ResultCode prefixHideByProxy(int arg0, Serializable arg1, Serializable arg2,
                                        CallMode arg3) {
        return tairManager.prefixHideByProxy(arg0, arg1, arg2, arg3);
    }

    @Override
    public Result<Map<Object, ResultCode>> prefixHides(int arg0, Serializable arg1,
                                                       List<? extends Serializable> arg2) {
        return tairManager.prefixHides(arg0, arg1, arg2);
    }

    @Override
    public Result<Map<Object, ResultCode>> prefixHidesByProxy(int arg0, Serializable arg1,
                                                              List<? extends Serializable> arg2,
                                                              CallMode arg3) {
        return tairManager.prefixHidesByProxy(arg0, arg1, arg2, arg3);
    }

    @Override
    public Result<Integer> prefixIncr(int arg0, Serializable arg1, Serializable arg2, int arg3,
                                      int arg4, int arg5) {
        return tairManager.prefixIncr(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public Result<Map<Object, Result<Integer>>> prefixIncrs(int arg0, Serializable arg1,
                                                            List<CounterPack> arg2) {
        return tairManager.prefixIncrs(arg0, arg1, arg2);
    }

    @Override
    public ResultCode prefixInvalid(int arg0, Serializable arg1, Serializable arg2, CallMode arg3) {
        return tairManager.prefixInvalid(arg0, arg1, arg2, arg3);
    }

    @Override
    public Result<Map<Object, ResultCode>> prefixInvalids(int arg0, Serializable arg1,
                                                          List<? extends Serializable> arg2,
                                                          CallMode arg3) {
        return tairManager.prefixInvalids(arg0, arg1, arg2, arg3);
    }

    @Override
    public ResultCode prefixPut(int arg0, Serializable arg1, Serializable arg2, Serializable arg3) {
        return tairManager.prefixPut(arg0, arg1, arg2, arg3);
    }

    @Override
    public ResultCode prefixPut(int arg0, Serializable arg1, Serializable arg2, Serializable arg3,
                                int arg4) {
        return tairManager.prefixPut(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public ResultCode prefixPut(int arg0, Serializable arg1, Serializable arg2, Serializable arg3,
                                int arg4, int arg5) {
        return tairManager.prefixPut(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public Result<Map<Object, ResultCode>> prefixPuts(int arg0, Serializable arg1,
                                                      List<KeyValuePack> arg2) {
        return tairManager.prefixPuts(arg0, arg1, arg2);
    }

    @Override
    public ResultCode prefixSetCount(int arg0, Serializable arg1, Serializable arg2, int arg3) {
        return tairManager.prefixSetCount(arg0, arg1, arg2, arg3);
    }

    @Override
    public ResultCode prefixSetCount(int arg0, Serializable arg1, Serializable arg2, int arg3,
                                     int arg4, int arg5) {
        return tairManager.prefixSetCount(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public ResultCode put(int arg0, Serializable arg1, Serializable arg2, int arg3, int arg4,
                          boolean arg5) {
        return tairManager.put(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public ResultCode putAsync(int arg0, Serializable arg1, Serializable arg2, int arg3, int arg4,
                               boolean arg5, TairCallback arg6) {
        return tairManager.putAsync(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public ResultCode setCount(int arg0, Serializable arg1, int arg2) {
        return tairManager.setCount(arg0, arg1, arg2);
    }

    @Override
    public ResultCode setCount(int arg0, Serializable arg1, int arg2, int arg3, int arg4) {
        return tairManager.setCount(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public void setMaxFailCount(int arg0) {
        tairManager.setMaxFailCount(arg0);
    }

    @Override
    public ResultCode unlock(int arg0, Serializable arg1) {
        return tairManager.unlock(arg0, arg1);
    }

}
