package com.alipay.zdal.sequence.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.alipay.zdal.sequence.SequenceConstants;

/**
 * 
 * 
 * @author yaojun.yj
 * @version $Id: ZdalJdbcUtil.java, v 0.1 2013-11-1 ÏÂÎç2:36:39 yaojun.yj Exp $
 */
public class ZdalJdbcUtil {

    private static final Logger logger = Logger.getLogger(SequenceConstants.ZDAL_SEQUENCE_LOG_NAME);
    
    
    

    public static void close(Object ...objects ){
        if(null != objects){
            for(Object o : objects){
                
                if(o instanceof ResultSet){
                    try {
                        ((ResultSet) o).close();
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                }
                else if(o instanceof Statement){
                    try {
                        ((Statement) o).close();
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                }
                else if(o instanceof Connection){
                    try {
                        ((Connection) o).close();
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                }
                
            }
        }
    }
}
