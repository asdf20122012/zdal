package com.alipay.zdal.valve.test.objects;

public class TXLog {
    private String timeString;
    private String dsString;
    private String txString;
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

    public String getTxString() {
        return txString;
    }

    public void setTxString(String txString) {
        this.txString = txString;
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

    public String getExecTotalTime() {
        return execTotalTime;
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

    public TXLog(String log) {
        String[] centStrings = log.split(";");
        timeString = centStrings[0];
        dsString = centStrings[1];
        txString = centStrings[2];
        succeedString = centStrings[3];
        execDegreeString = centStrings[4];
        execTotalTime = centStrings[5];
        hostNameString = centStrings[6];
    }

    public String toString() {
        return "timeString:" + timeString + "\r" + "dsString:" + dsString + "\r" + "txString:"
               + txString + "\r" + "succeedString:" + succeedString + "\r" + "execDegreeString:"
               + execDegreeString + "\r" + "execTotalTime:" + execTotalTime + "\r"
               + "hostNameString:" + hostNameString + "\r";
    }

    public static void main(String[] args) {
    }
}
