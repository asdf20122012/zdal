package com.alipay.zdal.valve.test.objects;

public class SQLLog {
    private String timeString;
    private String dsString;
    private String sqlString;
    private String succeedString;
    private String execDegreeString;
    private String execTotalTime;
    private String hostNameString;

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

    public String getSqlString() {
        return sqlString;
    }

    public void setTxString(String sqlString) {
        this.sqlString = sqlString;
    }

    public String getSucceedString() {
        return succeedString;
    }

    public void setSucceedString(String succeedString) {
        this.succeedString = succeedString;
    }

    public String getExecDegreeString() {
        return execDegreeString;
    }

    public void setExecDegreeString(String execDegreeString) {
        this.execDegreeString = execDegreeString;
    }

    public int getExecTotalTime() {
        return Integer.parseInt(execTotalTime);
    }

    public void setExecTotalTime(String execTotalTime) {
        this.execTotalTime = execTotalTime;
    }

    public String getHostNameString() {
        return hostNameString;
    }

    public void setHostNameString(String hostNameString) {
        this.hostNameString = hostNameString;
    }

    public SQLLog(String log) {
        String[] centStrings = log.split(";");
        timeString = centStrings[0];
        dsString = centStrings[1];
        sqlString = centStrings[2];
        succeedString = centStrings[3];
        execDegreeString = centStrings[4];
        execTotalTime = centStrings[5];
        hostNameString = centStrings[6];
    }

    public String toString() {
        return "timeString:" + timeString + "\r" + "dsString:" + dsString + "\r" + "sqlString:"
               + sqlString + "\r" + "succeedString:" + succeedString + "\r" + "execDegreeString:"
               + execDegreeString + "\r" + "execTotalTime:" + execTotalTime + "\r"
               + "hostNameString:" + hostNameString + "\r";
    }

    public static void main(String[] args) {
    }
}
