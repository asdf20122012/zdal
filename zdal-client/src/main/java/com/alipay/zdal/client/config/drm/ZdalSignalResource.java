package com.alipay.zdal.client.config.drm;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alipay.drm.client.api.annotation.AfterUpdate;
import com.alipay.drm.client.api.annotation.DAttribute;
import com.alipay.drm.client.api.annotation.DResource;
import com.alipay.zdal.client.config.ZdalConfigListener;
import com.alipay.zdal.common.Constants;
import com.alipay.zdal.common.DBType;
import com.alipay.zdal.common.jdbc.sorter.MySQLExceptionSorter;
import com.alipay.zdal.common.jdbc.sorter.OracleExceptionSorter;
import com.alipay.zdal.common.lang.StringUtil;
import com.alipay.zdal.datasource.LocalTxDataSourceDO;

/**
 * zdal 信号资源类, 主要为了响应zdal管控平台管控动作.
 * 
 * @author 伯牙
 * @version $Id: ZdalSignalResource.java, v 0.1 2012-11-17 下午4:05:12 Exp $
 */
@DResource(id = "com.alipay.zdal.signal")
public class ZdalSignalResource {

    public static final String  DRM_ATTR_RESET_DATA_SOURCE    = "resetDataSource";

    private static final String PHYSICAL_DB_NAME              = "physicalDbName";

    private static final String MAX_SIZE                      = "maxSize";

    private static final String MIN_SIZE                      = "minSize";

    private static final String LOGICAL_DB                    = "logicalDb";

    private static final String PHYSICAL_DB                   = "physicalDb";

    private static final String PHYICAL_DB_SETTINGS           = "phyicalDbSettings";

    private static final String BINDINGS                      = "bindings";

    private static final String NEWSETTINGS                   = "newSettings";

    private static final String OPT_RESET_POOL_SIZE           = "resetPoolSize";

    private static final String OPT_RESET_DATA_SOURCE_BINDING = "resetDataSourceBinding";

    private static final String OPERATION                     = "operation";

    private static final String DRM_ATT_KEY_WEIGHT            = "keyWeight";

    /** 专门打印推送结果的log信息. */
    private static final Logger log                           = Logger
                                                                  .getLogger(Constants.CONFIG_LOG_NAME_LOGNAME);

    /** drm值里面推送过来的特殊字符 */
    private static final String SPECAIL_CHAR                  = "#";

    /** 替换成的值. */
    private static final String REPLACE_CHAR                  = ":";

    private String              resourceId;

    private DBType              dbType;

    /** 切换推送的权重. */
    @DAttribute
    private String              keyWeight;

    /** 应急需要提出的errorCode. */
    @DAttribute
    private int                 errorCode;

    @DAttribute
    private String              resetDataSource;

    private ZdalConfigListener  configListener;

    private Lock                lock                          = new ReentrantLock();

    public ZdalSignalResource(ZdalConfigListener configListener, String drm, DBType dbType) {
        this.resourceId = MessageFormat.format(Constants.ZDALDATASOURCE_DRM_DATAID, drm);
        this.configListener = configListener;
        this.dbType = dbType;
    }

    public ZdalSignalResource() {
    }

    @AfterUpdate
    public void updateResource(String key, Object value) {
        lock.lock();
        try {
            if (key.equals(DRM_ATT_KEY_WEIGHT)) {
                if (value == null || StringUtil.isBlank(value.toString())) {
                    log.warn("WARN ## the keyWeight is null,will ignore this drm pull");
                    return;
                }
                String receiveString = StringUtil.replace(value.toString(), SPECAIL_CHAR,
                    REPLACE_CHAR);//由于drm1.0对于：有特殊用途，所以通过#进行替换.
                Map<String, String> groupInfos = convertKeyWeights(receiveString);
                if (groupInfos == null || groupInfos.isEmpty()) {
                    log.warn("WARN ## the pull keyWeights = " + value + " is invalidate");
                    return;
                }
                configListener.resetWeight(groupInfos);
                this.keyWeight = value.toString();
            } else if (key.equals("errorCode")) {
                if (value == null || StringUtil.isBlank(value.toString())) {
                    log.warn("WARN ## the errorCode is null, will ignore this drm pull");
                    return;
                }
                try {
                    int tmpErrorCode = Integer.parseInt(value.toString());
                    if (dbType.isMysql()) {
                        MySQLExceptionSorter.ERRORCODE = tmpErrorCode;
                    } else if (dbType.isOracle()) {
                        OracleExceptionSorter.ERRORCODE = tmpErrorCode;
                    }
                } catch (Exception e) {
                    log.warn("WARN ## the errorCode is not a number", e);
                }
            } else if (key.equals(DRM_ATTR_RESET_DATA_SOURCE)) {
                //Dealing with reset data source includes reset bindings and pool sizes
                if (null == value || StringUtil.isEmpty(value.toString())) {
                    log.warn("WARN ## the pushed operation value is null, it will be ignored at this time.");
                    return;
                }
                Object obj = JSON.parse(value.toString());
                if (obj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, ?> changeSettingMap = (Map<String, ?>) obj;
                    handleResetDataSource(configListener, changeSettingMap);
                } else {
                    log.warn("WARN ## the pushed changes is not valid JSON format "
                             + value.toString());
                }
            }
        } catch (Exception e) {
            log.error("ERROR ## ", e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * <p>
     * Dealing with the latest data source settings. With DRM pushing connection pool size and 
     * data source binding can be reset dynamically.
     * </p>
     * @param configListener
     * @param changeSettingMap {@link http://doc.alipay.net/display/DFrame/Zdal+V3 } to see definition of settings
     */
    private void handleResetDataSource(ZdalConfigListener configListener,
                                       Map<String, ?> changeSettingMap) {
        if (null == changeSettingMap.get(OPERATION)) {
            log.warn("WARN ## the pushed resetDataSource changes is not valid as missed operation value. ");
            return;
        }
        //To ensure reset datasource as much as possible keep try and catch in loop
        if (changeSettingMap.get(OPERATION).equals(OPT_RESET_DATA_SOURCE_BINDING)) {
            if (null == changeSettingMap.get(BINDINGS)) {
                log.warn("WARN ## the pushed resetDataSource changes missed new bindings. ");
                return;
            }
            List<?> bindingList = (List<?>) changeSettingMap.get(BINDINGS);
            String physicalDbId, logicalDbId;
            Map<String, ?> phyicalDbSettings = null;
            
            for (Object bindingObj : bindingList) {
                try {
                    phyicalDbSettings = null;
                    Map<String, ?> binding = (Map<String, ?>) bindingObj;
                    if (!validateResetBindingMap(binding))
                        return;
                    physicalDbId = binding.get(PHYSICAL_DB).toString();
                    logicalDbId = binding.get(LOGICAL_DB).toString();
                    if (null != binding.get(PHYICAL_DB_SETTINGS))
                        phyicalDbSettings = (Map<String, ?>) binding.get(PHYICAL_DB_SETTINGS);
                    configListener.resetDataSourceBinding(physicalDbId, logicalDbId,
                        LocalTxDataSourceDO.convertFromMap(phyicalDbSettings));
                } catch (Throwable t) {
                    log.error("ERROR ## Dealing with the pushed resetDataSource changes had unexpected exception for rebinding datasources"
                              + t.getMessage());
                }
            }
        } else if (changeSettingMap.get(OPERATION).equals(OPT_RESET_POOL_SIZE)) {
            List<?> settingList = (List<?>) changeSettingMap.get(NEWSETTINGS);
            Map<String, ?> settingMap = null;
            if( null == settingList || settingList.isEmpty() ){
                return ;
            }
            for (Object setting : settingList) {
                try{
                    settingMap = (Map<String, ?>) setting;
                    if (!validateResetPoolSizeParameters(settingMap))
                        return;
                    int minSize = (Integer) settingMap.get(MIN_SIZE);
                    int maxSize = (Integer) settingMap.get(MAX_SIZE);
                    String physicalDbId = settingMap.get(PHYSICAL_DB_NAME).toString();
                    configListener.resetConnectionPoolSize(physicalDbId, minSize, maxSize);
                }catch(Throwable e){
                    log.error("ERROR ## Dealing with the pushed resetDataSource changes had unexpected exception for reset poolsize"
                            + e.getMessage());
                }
            }
        }
    }

    private boolean validateResetPoolSizeParameters(Map<String, ?> changeSettingMap) {
        boolean isValidated = true;
        if (null == changeSettingMap.get(MIN_SIZE)) {
            log.error("ERROR ## Pushed resetPoolSize operation missed minSize of parameters.");
            isValidated = false;
        }
        if (null == changeSettingMap.get(MAX_SIZE)) {
            log.error("ERROR ## Pushed resetPoolSize operation missed maxSize of parameters.");
            isValidated = false;
        }
        if (null == changeSettingMap.get(PHYSICAL_DB_NAME)) {
            log.error("ERROR ## Pushed resetPoolSize operation missed physicalDb of parameters.");
            isValidated = false;
        }
        return isValidated;
    }

    private boolean validateResetBindingMap(Map<String, ?> binding) {
        boolean isValidated = true;
        if (null == binding.get(PHYSICAL_DB)) {
            log.error("ERROR ## Pushed resetDataSourceBinding operation missed " + PHYSICAL_DB
                      + " of parameters.");
            isValidated = false;
        }
        if (null == binding.get(LOGICAL_DB)) {
            log.error("ERROR ## Pushed resetDataSourceBinding operation missed " + LOGICAL_DB
                      + " of parameters.");
            isValidated = false;
        }
        /*if( null == binding.get(PHYICAL_DB_SETTINGS)){
        	log.error("ERROR ## Pushed resetDataSourceBinding operation missed " + PHYICAL_DB_SETTINGS + " of parameters.");
        	isValidated = false;
        }*/
        return isValidated;
    }

    public String getKeyWeight() {
        return this.keyWeight;
    }

    public void setKeyWeight(String keyWeight) {
        this.keyWeight = keyWeight;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * failover:
     * group_00=10,0;group_01=10,0
     * @param keyWeight
     * @return
     */
    private Map<String, String> convertKeyWeights(String keyWeight) {
        String[] splits = keyWeight.split(";");
        Map<String, String> results = new HashMap<String, String>();
        for (int i = 0; i < splits.length; i++) {
            String tmp = splits[i];
            String[] groupInfos = tmp.split("=");
            results.put(groupInfos[0], groupInfos[1]);
        }
        return results;
    }

    public String getResourceId() {
        return resourceId;
    }

    /**
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "resourceId: " + this.resourceId;
    }

    public String getResetDataSource() {
        return resetDataSource;
    }

    public void setResetDataSource(String resetDataSource) {
        this.resetDataSource = resetDataSource;
    }

}
