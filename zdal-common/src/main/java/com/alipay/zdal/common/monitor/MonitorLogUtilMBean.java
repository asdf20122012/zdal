package com.alipay.zdal.common.monitor;

public interface MonitorLogUtilMBean {
    /**
     * <p>
     * StatLog的数据采样时间
     * </p>
     * 
     * @return
     */
    long getWaitTime();

    /**
     * <p>
     * 设置StatLog的数据采样时间
     * </p>
     * 
     * @param waitTime
     */
    void setWaitTime(long waitTime);

    /**
     * <p>
     * 获取监控jvm信息开关
     * </p>
     * 
     * @return
     */
    boolean getJVMInfoPower();

    /**
     * <p>
     * 设置监控jvm信息开关
     * </p>
     * 
     * @return
     */
    void setJVMInfoPower(boolean power);
}
