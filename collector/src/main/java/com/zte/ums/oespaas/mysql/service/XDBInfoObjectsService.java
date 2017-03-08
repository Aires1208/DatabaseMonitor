package com.zte.ums.oespaas.mysql.service;

import com.zte.ums.oespaas.mysql.bean.DBInfoBean;
import com.zte.ums.oespaas.mysql.bean.DBInfoObjects;
import com.zte.ums.oespaas.mysql.bean.hbase.MonitorInfo;
import com.zte.ums.oespaas.mysql.bean.hbase.Range;
import com.zte.ums.oespaas.mysql.bean.hbase.StatusBean;
import com.zte.ums.oespaas.mysql.bean.hbase.UserBean;
import com.zte.ums.oespaas.mysql.hbase.HBaseCheck;
import com.zte.ums.oespaas.mysql.hbase.HBaseOperator;
import com.zte.ums.oespaas.mysql.hbase.MonitorInfoService;
import com.zte.ums.oespaas.mysql.util.TimeConvert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 9/1/16.
 */

@Service
public class XDBInfoObjectsService {
    private static final Logger logger = LoggerFactory.getLogger(XDBInfoObjectsService.class);

    private List<DBInfoObjects.Variable> variableList;
    private DBInfoObjects.Log log;
    private DBInfoObjects.Database database;

    public DBInfoObjects getDBInfoObjects(String dbNeId, Range range) {
        if (!HBaseCheck.isChecked){
            HBaseCheck.checkHTable();
        }

        if (! HBaseOperator.dbNeIdList.contains(dbNeId)){
            return null;
        }

        List<MonitorInfo> monitorInfoList = MonitorInfoService.getMonitorInfoList(dbNeId, range);
        List<DBInfoObjects.User> userList = getUserList(monitorInfoList);
        List<DBInfoObjects.Storage> storages = getStorageList(monitorInfoList);

        setVariableListAndLogAndDatabase(monitorInfoList);

        return new DBInfoObjects(userList, variableList, storages, log, database);
    }

    private List<DBInfoObjects.User> getUserList(List<MonitorInfo> monitorInfoList) {
        List<DBInfoObjects.User> userList = new ArrayList<DBInfoObjects.User>();

        for (MonitorInfo monitorInfo : monitorInfoList) {
            try {
                UserBean userBean = monitorInfo.getUserBean();
                if (userBean != null) {
                    String userName = userBean.getUserName();
                    String hostName = userBean.getHostName();
                    if (userName != null && !userName.isEmpty() &&
                            hostName != null && !hostName.isEmpty() && !isExcludedName(hostName)) {
                        DBInfoObjects.User user = new DBInfoObjects.User(userName, hostName);
                        if (!userList.contains(user)) {
                            userList.add(user);
                        }
                    }
                }
            } catch (Exception e){
                logger.error(e.getMessage());
            }
        }

        return userList;
    }

    private void setVariableListAndLogAndDatabase(List<MonitorInfo> monitorInfoList) {
        variableList = new ArrayList<DBInfoObjects.Variable>();
        log = new DBInfoObjects.Log("");
        database = new DBInfoObjects.Database("0", "0");

        for (MonitorInfo monitorInfo : monitorInfoList) {
            try {
                StatusBean statusBean = monitorInfo.getStatusBean();
                if (statusBean != null && !statusBean.getVars().isEmpty()) {
                    String uptime = "0";
                    if (statusBean.getUp_time() != null && !statusBean.getUp_time().isEmpty()) {
                        uptime = TimeConvert.getTimeStringFromTimestamp(
                                Long.valueOf(statusBean.getUp_time()));
                    }

                    if (statusBean.getVars() != null && statusBean.getVars().length() > 2) {
                        String vars = statusBean.getVars().substring(1, statusBean.getVars().length() - 1);
                        if (!vars.isEmpty() && vars.contains(", ")) {
                            String[] keyValues = vars.split(", ");
                            for (String keyValue : keyValues) {
                                String[] keyWithValue = keyValue.split("=");
                                if (keyWithValue.length == 2) {
                                    DBInfoObjects.Variable variable = new DBInfoObjects.Variable(keyWithValue[0].trim(), keyWithValue[1].trim());
                                    if (!variableList.contains(variable)) {
                                        variableList.add(variable);
                                    }

                                    if (keyWithValue[0].equals("log_error")) {
                                        String errorLogPath = keyWithValue[1].substring(0, keyWithValue[1].lastIndexOf("/"));
                                        log = new DBInfoObjects.Log(errorLogPath);
                                    }

                                    if (keyWithValue[0].equals("version")) {
                                        database = new DBInfoObjects.Database(uptime, keyWithValue[1]);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    private List<DBInfoObjects.Storage> getStorageList(List<MonitorInfo> monitorInfoList) {
        List<DBInfoObjects.Storage> storageList = new ArrayList<DBInfoObjects.Storage>();

        for (MonitorInfo monitorInfo : monitorInfoList) {
            try {
                DBInfoBean dbInfoBean = monitorInfo.getDbInfoBean();
                if (dbInfoBean != null) {
                    String dbName = dbInfoBean.getDbName();
                    String indexLength = dbInfoBean.getIndexLength();
                    String dataLength = dbInfoBean.getDataLength();
                    if (dbName != null && !dbName.isEmpty()
                            && indexLength != null && !indexLength.isEmpty()
                            && dataLength != null && !dataLength.isEmpty()) {
                        DBInfoObjects.Storage storage = new DBInfoObjects.Storage(dbName, indexLength, dataLength);
                        if (!storageList.contains(storage)) {
                            storageList.add(storage);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        return storageList;
    }

    private boolean isExcludedName(String name) {
        return name.equals("::1") || name.equals("%");
    }
}
