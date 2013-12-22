package com.alipay.zdal.valve.test.utils;

import java.sql.SQLException;
import java.sql.Statement;


import com.alipay.zdal.valve.util.OutstripValveException;

/**
 * 
 * 
 * @author liangjie.li
 * @version $Id: ValveClient.java, v 0.1 2012-8-17 下午5:06:42 liangjie.li Exp $
 */
public class ValveClient implements Runnable {
    private Statement statement;
    private String    state;
    private int       unRequestNum = 0;   //剩余未请求次数
    private int       qeuuestNum   = 0;   //请求计数器
    private boolean   flag         = true;
    private int       exceptionNum = 0;   //出现限流异常时，请求发起的次数
    private int       stopNum      = 0;   //启动的线程数量
    private int       UNLIMIT      = -10;

    public ValveClient() {
    }

    /**
     * 构造一个valve的客户端
     * @param statement 当前线程执行sql的statement
     * @param state 当前线程执行的sql语句
     * @param type 操作类型 1-简单sql操作 2-tair操作
     * @param unRequestNum 当前线程执行sql的次数 -10-表示不限制执行次数
     */
    public ValveClient(Object object, String state, int unRequestNum) {
        this.statement = (Statement) object;
        this.state = state;
        this.unRequestNum = unRequestNum;
    }

    public void execute(Statement statement, String state) throws SQLException {
        statement.execute(state);
    }

    public int getQeuuestNum() {
        return qeuuestNum;
    }

    public int getExceptionNum() {
        return exceptionNum;
    }

    /**
     * 得到已经启动的线程总数量
     * @return
     */
    public int getStopNum() {
        return stopNum;
    }

    public void run() {

        while (true) {
            if (unRequestNum <= 0 && unRequestNum != UNLIMIT) {
                break;
            }
            try {
                qeuuestNum++;
                execute(statement, state);
            } catch (OutstripValveException e) {
                if (flag) {
                    exceptionNum = qeuuestNum;
                    flag = false;
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (unRequestNum != UNLIMIT) {
                unRequestNum--;
            }
        }
        stopNum++;
    }

}
