package com.alipay.zdal.valve.test.objects;

public class DSLog {
    private String timeString;
    private String dsString;
    private String minSize;
    private String MaxSize;
    private String availableConnections;
    private String connectionCount;
    private String connectionInUseCount;
    private String maxConnectionsInUseCount;
    private String ConnectionCreatedCount;
    private String ConnectionDestroyedCount;
    private String hostName;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getTimeString() {
        return timeString;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    public String getDsString() {
        return dsString;
    }

    public void setDsString(String dsString) {
        this.dsString = dsString;
    }

    /**
     * 解析数据源日志记录<br>
     * 2012-04-17 11:28:54;时间<br>DefaultDS;数据源名称<br>5;最小连接数<br>20;最大连接数<br>
     * 20;可用连接数<br>5;总连接数<br>0;正在使用的连接数<br>1;历史连接数峰值<br>
     * 5;连接数累计值<br>0;销毁的连接数量<br>BJG-AP53289主机名称<br>
     * @param log
     */
    public DSLog(String log) {
        String[] centStrings = log.split(";");
        timeString = centStrings[0];
        dsString = centStrings[1];
        minSize = centStrings[2];
        MaxSize = centStrings[3];
        availableConnections = centStrings[4];
        connectionCount = centStrings[5];
        connectionInUseCount = centStrings[6];
        maxConnectionsInUseCount = centStrings[7];
        ConnectionCreatedCount = centStrings[8];
        ConnectionDestroyedCount = centStrings[9];
        hostName = centStrings[10];
    }

    public String getMinSize() {
        return minSize;
    }

    public void setMinSize(String minSize) {
        this.minSize = minSize;
    }

    public String getMaxSize() {
        return MaxSize;
    }

    public void setMaxSize(String maxSize) {
        MaxSize = maxSize;
    }

    /**
     * 当前可用连接数
     * @return 当前可用连接数
     */
    public String getAvailableConnections() {
        return availableConnections;
    }

    public void setAvailableConnections(String availableConnections) {
        this.availableConnections = availableConnections;
    }

    public String getConnectionCount() {
        return connectionCount;
    }

    public void setConnectionCount(String connectionCount) {
        this.connectionCount = connectionCount;
    }

    public String getConnectionInUseCount() {
        return connectionInUseCount;
    }

    public void setConnectionInUseCount(String connectionInUseCount) {
        this.connectionInUseCount = connectionInUseCount;
    }

    public String getMaxConnectionsInUseCount() {
        return maxConnectionsInUseCount;
    }

    public void setMaxConnectionsInUseCount(String maxConnectionsInUseCount) {
        this.maxConnectionsInUseCount = maxConnectionsInUseCount;
    }

    public String getConnectionCreatedCount() {
        return ConnectionCreatedCount;
    }

    public void setConnectionCreatedCount(String connectionCreatedCount) {
        ConnectionCreatedCount = connectionCreatedCount;
    }

    public String getConnectionDestroyedCount() {
        return ConnectionDestroyedCount;
    }

    public void setConnectionDestroyedCount(String connectionDestroyedCount) {
        ConnectionDestroyedCount = connectionDestroyedCount;
    }

    @Override
    public String toString() {
        return "DSLog [timeString=" + timeString + ", dsString=" + dsString + ", minSize="
               + minSize + ", MaxSize=" + MaxSize + ", availableConnections="
               + availableConnections + ", connectionCount=" + connectionCount
               + ", connectionInUseCount=" + connectionInUseCount + ", maxConnectionsInUseCount="
               + maxConnectionsInUseCount + ", ConnectionCreatedCount=" + ConnectionCreatedCount
               + ", ConnectionDestroyedCount=" + ConnectionDestroyedCount + "]";
    }

    public static void main(String[] args) {
    }
}
