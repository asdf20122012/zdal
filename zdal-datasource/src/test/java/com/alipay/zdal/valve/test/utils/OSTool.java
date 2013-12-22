package com.alipay.zdal.valve.test.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import com.alipay.zdal.valve.test.objects.DSLog;
import com.alipay.zdal.valve.test.objects.SQLLog;
import com.alipay.zdal.valve.test.objects.TXLog;

/**
 * 
 * 
 * @author liangjie.li
 * @version $Id: OSTool.java, v 0.1 2012-8-17 下午5:07:47 liangjie.li Exp $
 */
public class OSTool {
    private String logNameDS         = "valve-ds-monitor.log";
    private String logNameSQL        = "valve-sql-monitor.log";
    private String logNameTX         = "valve-tx-monitor.log";
    private String logNameTair       = "valve-tair-monitor.log";
    private String logNameTairThread = "valve-tair-thread-monitor.log";
    private String logNameDV         = "zdatavalve.log";

    public String getLogNameTairThread() {
        return logNameTairThread;
    }

    public void setLogNameTairThread(String logNameTairThread) {
        this.logNameTairThread = logNameTairThread;
    }

    public String getLogNameDS() {
        return logNameDS;
    }

    public void setLogNameDS(String logNameDS) {
        this.logNameDS = logNameDS;
    }

    public String getLogNameTair() {
        return logNameTair;
    }

    public void setLogNameTair(String logNameTair) {
        this.logNameTair = logNameTair;
    }

    public String getLogNameSQL() {
        return logNameSQL;
    }

    public void setLogNameSQL(String logNameSQL) {
        this.logNameSQL = logNameSQL;
    }

    public String getLogNameTX() {
        return logNameTX;
    }

    public void setLogNameTX(String logNameTX) {
        this.logNameTX = logNameTX;
    }

    public String getLogNameDV() {
        return logNameDV;
    }

    public void setLogNameDV(String logNameDV) {
        this.logNameDV = logNameDV;
    }

    /**
     * 用户主目录
     * @return 用户主目录
     * @author wei.yao
     */
    public String getUserHome() {
        return System.getProperty("user.home");
    }

    /**
     * 主机名
     * @return ""-空;主机名
     * @author wei.yao
     */
    public String getHostName() {
        String hostNameString = "";
        try {
            hostNameString = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return hostNameString;
    }

    /**
     * zdatasource日志文件名
     * @param index ds-数据源 sql-sql tx-事务 dv-zdatavalve tair-tair
     * @param date null-当天 yyyy-mm-dd-指定日期
     * @return 文件绝对路径
     * @author wei.yao
     */
    public String getLogName(String index, String date) {
        String pathString = getLogPath();
        if ("ds".equals(index)) {
            pathString += logNameDS;
        }
        if ("sql".equals(index)) {
            pathString += logNameSQL;
        }
        if ("tx".equals(index)) {
            pathString += logNameTX;
        }
        if ("dv".equals(index)) {
            pathString += logNameDV;
        }
        if ("tair".equals(index)) {
            pathString += logNameTair;
        }
        if ("tairthread".equals(index)) {
            pathString += logNameTairThread;
        }
        if (date != null) {//自定义文件名
            pathString += "." + date;
        } else {//默认文件名
            if (!new File(pathString).exists()) {//单日单文件
                pathString += "." + getDate() + "_" + getHour();//单日多文件
                if (!new File(pathString).exists()) {//文件不存在
                    pathString = "日志文件不存在";
                }
            }
        }
        return pathString;
    }

    /**
     * 日志路径
     * @return 日志路径
     * @author wei.yao
     */
    public String getLogPath() {
        return getUserHome() + "/logs/valve/";
    }

    /**
     * 文件夹下文件数量
     * @param path 文件夹
     * @return 文件数量
     * @author wei.yao
     */
    public int getFileNum(String path) {
        File dir = new File(path);
        if (!dir.exists() || dir.list() == null) {
            return 0;
        }
        return dir.list().length;
    }

    /**
     * 文件最后N行记录
     * @param file 文件绝对路径
     * @param lastline 倒数行数,如果大于总行数，则从首行开始;-1-全部记录
     * @return 最后N行记录
     * @author wei.yao
     */
    public Object[] getRecord(String file, int lastline) {
        Vector<String> record = new Vector<String>();
        RandomAccessFile raf = null;
        if (lastline == -1) {
            lastline = 2147483647;
        }
        try {
            raf = new RandomAccessFile(file, "r");
            long size = getLineNum(file);
            String contentString;
            for (long i = 1; i <= size; i++) {
                contentString = raf.readLine();
                if ("".equals(contentString)) {
                    i--;//空白行不计数
                } else {
                    if (i > (size - lastline)) {
                        record.add(contentString);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return record.toArray();
    }

    /**
     * 事务日志记录对象集合
     * @param records 日志记录
     * @return 
     * @author wei.yao
     */
    public TXLog[] getRecTXLog(Object[] records) {
        TXLog[] tXLogArray = new TXLog[records.length];
        for (int i = 0; i < records.length; i++) {
            tXLogArray[i] = new TXLog(records[i].toString());
        }
        return tXLogArray;
    }

    /**
     * sql日志记录对象集合
     * @param records 日志记录
     * @return 
     * @author wei.yao
     */
    public SQLLog[] getRecSQLLog(Object[] records) {
        SQLLog[] logArray = new SQLLog[records.length];
        for (int i = 0; i < records.length; i++) {
            logArray[i] = new SQLLog(records[i].toString());
        }
        return logArray;
    }

    /**
     * 数据源日志记录对象集合
     * @param records 日志记录
     * @return 
     * @author wei.yao
     */
    public DSLog[] getRecDSLog(Object[] records) {
        if (records.length == 0) {
            return null;
        }
        DSLog[] logArray = new DSLog[records.length];
        for (int i = 0; i < records.length; i++) {
            logArray[i] = new DSLog(records[i].toString());
        }
        return logArray;
    }

    /**
     * 文件行数
     * @param file 文件绝对路径
     * @return 文件行数
     * @author wei.yao
     */
    public long getLineNum(String file) {
        int lineNum = 0;
        RandomAccessFile raf = null;
        String cont;
        try {
            raf = new RandomAccessFile(file, "r");
            while ((cont = raf.readLine()) != null) {
                if (!"".equals(cont)) {
                    lineNum++;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lineNum;
    }

    /**
     * 一组文件全部行数
     * @param file 文件组绝对路径
     * @return 文件行数
     */
    public long getLineNum(String[] file) {
        int lineNum = 0;
        for (int i = 0; i < file.length; i++) {
            lineNum += getLineNum(file[i]);
        }
        return lineNum;
    }

    /**
     * 所有相同文件总行数 例如：a.log;a.log.2011;a.log.2012 统计所有文件
     * @param dir 路径
     * @param fileIndex 文件名子串
     * @return 文件总行数
     * @author wei.yao
     */
    public long getLineNum(String dir, String fileIndex) {
        int lineNum = 0;
        Object[] files = getFiles(dir, fileIndex);
        for (int i = 0; i < files.length; i++) {
            lineNum += getLineNum(dir + files[i]);
        }
        return lineNum;
    }

    /**
     * 文件是否存在
     * @param file 绝对路径
     * @return true-存在 false-不存在
     * @author wei.yao
     */
    public boolean fileIsExsit(String file) {
        return new File(file).exists();
    }

    /**
     * 删除文件
     * @param filePath 被删除文件绝对路径
     * @author wei.yao
     */
    public void fileDel(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            System.out.println("删除文件结果" + file.delete());
        }
    }

    /**
     * 系统日期
     * @return 系统日期
     * @author wei.yao
     */
    public String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * 系统时间
     * @return 系统时间
     * @author wei.yao
     */
    public String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * 系统时间
     * @param partString 时间格式
     * @return 系统时间
     * @author wei.yao
     */
    public String getTime(String partString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(partString);
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * 当前时间区间秒，前后1分钟
     * @return 时间区间秒 yyyy-MM-dd HH:mm:ss
     * @author wei.yao
     */
    public Object[] getTimeRegion() {
        Calendar calendar = Calendar.getInstance();
        int error = 60;
        Vector<String> timeRegion = new Vector<String>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        calendar.add(Calendar.SECOND, -error);
        for (int i = 0; i < (error * 2); i++) {
            timeRegion.add(dateFormat.format(calendar.getTime()));
            calendar.add(Calendar.SECOND, 1);
        }
        return timeRegion.toArray();
    }

    /**
     * 给定时间是否在当前时间区间内 误差1分钟
     * @param timeActual
     * @return true-是
     * @author wei.yao
     */
    public boolean timeEquals(String timeActual) {
        boolean res = false;
        Object[] timeRegion = getTimeRegion();
        for (int i = 0; i < timeRegion.length; i++) {
            //			Log.info(timeRegion[i] + "");
            if (timeRegion[i].equals(timeActual)) {
                res = true;
                break;
            }
        }
        return res;
    }

    /**
     * 系统小时
     * @return 系统小时
     * @author wei.yao
     */
    public String getHour() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH");
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * 设置系统时间
     * @param time hh:mm:ss
     * @author wei.yao
     */
    public void setHostTime(String time) {
        try {
            if (getOSName().equals("windows")) {
                Runtime.getRuntime().exec("cmd /c time " + time);
            }
            if (getOSName().equals("linux")) {
                //没实现
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 操作系统类型
     * @return windows- linux(unix)-
     * @author wei.yao
     */
    public String getOSName() {
        String os = System.getProperty("os.name").toLowerCase();
        String osActualString = "unkown";
        if (os.indexOf("win") >= 0) {
            osActualString = "windows";
        }
        if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
            osActualString = "linux";
        }
        return osActualString;
    }

    /**
     * 所有相同的文件
     * @param dir 路径
     * @param fileIndex 文件名包含的字符串
     * @return 相同的文件
     * @author wei.yao
     */
    public Object[] getFiles(String dir, String fileIndex) {
        File dirString = new File(dir);
        Vector<String> sameFileName = new Vector<String>();
        String[] listStrings = dirString.list();
        for (int i = 0; i < listStrings.length; i++) {
            if (listStrings[i].contains(fileIndex)) {
                sameFileName.add(listStrings[i]);
            }
        }
        return sameFileName.toArray();
    }

}