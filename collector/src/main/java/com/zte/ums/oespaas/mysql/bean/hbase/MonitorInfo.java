package com.zte.ums.oespaas.mysql.bean.hbase;

import com.zte.ums.oespaas.mysql.bean.DBInfoBean;

/**
 * Created by 10183966 on 8/23/16.
 */
public class MonitorInfo {
    //rowkey=dbNeId^collectTime

    private String dbNeId;
    private String collectTime;
    private SessionBean sessionBean;
    private SlowLogBean slowLogBean;
    private GeneralLogBean generalLogBean;
    private StatusBean statusBean;
    private DBInfoBean dbInfoBean;
    private UserBean userBean;

    public MonitorInfo() {
    }

    public String getDbNeId() {
        return dbNeId;
    }

    public void setDbNeId(String dbNeId) {
        this.dbNeId = dbNeId;
    }

    public String getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(String collectTime) {
        this.collectTime = collectTime;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public SlowLogBean getSlowLogBean() {
        return slowLogBean;
    }

    public void setSlowLogBean(SlowLogBean slowLogBean) {
        this.slowLogBean = slowLogBean;
    }

    public GeneralLogBean getGeneralLogBean() {
        return generalLogBean;
    }

    public void setGeneralLogBean(GeneralLogBean generalLogBean) {
        this.generalLogBean = generalLogBean;
    }

    public StatusBean getStatusBean() {
        return statusBean;
    }

    public void setStatusBean(StatusBean statusBean) {
        this.statusBean = statusBean;
    }

    public DBInfoBean getDbInfoBean() {
        return dbInfoBean;
    }

    public void setDbInfoBean(DBInfoBean dbInfoBean) {
        this.dbInfoBean = dbInfoBean;
    }

    @Override
    public String toString() {
        return "MonitorInfo{" +
                "dbNeId='" + dbNeId + '\'' +
                ", collectTime='" + collectTime + '\'' +
                ", sessionBean=" + sessionBean +
                ", slowLogBean=" + slowLogBean +
                ", generalLogBean=" + generalLogBean +
                ", statusBean=" + statusBean +
                ", dbInfoBean=" + dbInfoBean +
                ", userBean=" + userBean +
                '}';
    }
}
