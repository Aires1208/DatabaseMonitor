package com.zte.ums.oespaas.mysql.hbase;

import com.zte.ums.oespaas.mysql.bean.hbase.RegisterInfo;
import com.zte.ums.oespaas.mysql.message.ProjectConfig;
import com.zte.ums.oespaas.mysql.util.NetUtils;
import net.sf.json.JSONObject;
import org.apache.hadoop.hbase.client.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 10183966 on 8/22/16.
 */
public class RegisterInfoHandler {
    private static final Logger logger = LoggerFactory.getLogger(RegisterInfoHandler.class);

    public static Map<String, String> insert(RegisterInfo registerInfo) {
        Map<String, String> result = new LinkedHashMap<String, String>();
        if (!NetUtils.isHBaseRunnig()) {
            result.put("result", "error");
            result.put("msg", "hbase is not running on " + ProjectConfig.HBASE_IP);
            return result;
        }

        if (!HBaseCheck.isChecked){
            HBaseCheck.checkHTable();
        }

        if (isRegisterExist(registerInfo)) {
            result.put("result", "error");
            result.put("msg", "the db monitor has already existed");
            return result;
        }

        checkRegisterInfo(registerInfo);
        if (registerInfo.getOsNeId() == null || registerInfo.getDbNeId() == null ||
                registerInfo.getOsNeId().isEmpty() || registerInfo.getDbNeId().isEmpty()) {
            result.put("result", "error");
            result.put("msg", "cannot connect with dbmonitor agent");
            return result;
        }

        try {
            if (HBaseOperator.hTableRegister != null) {
                byte[] rowKey = HBaseOperator.generateRowKey(registerInfo.getOsNeId(), registerInfo.getCollectTime());
                Put put = new Put(rowKey);

                byte[] registerByteValue = registerInfo.writeValue();
                put.addColumn(HBaseConstant.REGISTER_FAMILYNAME, HBaseConstant.REGISTER_INPUTPARA_COLUMNNAME, registerByteValue);

                HBaseOperator.hTableRegister.put(put);

                HBaseOperator.osNeIdList.add(registerInfo.getOsNeId());
                HBaseOperator.dbNeIdList.add(registerInfo.getDbNeId());
                HBaseOperator.dbNeIdMapOSNeId.put(registerInfo.getDbNeId(), registerInfo.getOsNeId());
                HBaseOperator.dbNeIdMapRegisterInfo.put(registerInfo.getDbNeId(), registerInfo);
                TaskService.createTask(registerInfo);

                logger.info(registerInfo.toString());

                result.put("result", "ok");
                result.put("msg", "dbNeId: " + registerInfo.getDbNeId() + "  \n  " + "osNeId: " + registerInfo.getOsNeId());

                return result;
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        result.put("result", "error");
        result.put("msg", "cannot insert into hbase");
        return result;
    }

    public static Map<String, String> delete(String dbNeId) {
        if (!HBaseCheck.isChecked){
            HBaseCheck.checkHTable();
        }

        Map<String, String> result = new LinkedHashMap<String, String>();
        if (HBaseOperator.dbNeIdList.contains(dbNeId)) {
            RegisterInfo registerInfo = HBaseOperator.dbNeIdMapRegisterInfo.get(dbNeId);
            byte[] rowkey = HBaseOperator.generateRowKey(registerInfo.getOsNeId(), registerInfo.getCollectTime());
            if (HBaseOperator.deleteRow(HBaseOperator.hTableRegister, rowkey)) {
                HBaseOperator.dbNeIdList.remove(dbNeId);
                HBaseOperator.osNeIdList.remove(registerInfo.getOsNeId());
                HBaseOperator.dbNeIdMapOSNeId.remove(dbNeId);
                HBaseOperator.dbNeIdMapRegisterInfo.remove(dbNeId);

                //MonitorInfoHandler.delete(dbNeId);
                //OSInfoHandler.delete(dbNeId);

                result.put("result", "ok");
                result.put("msg", "success to delete the db monitor");
            }
        } else {
            result.put("result", "error");
            result.put("msg", "fail to delete the db monitor");
        }

        return result;
    }

    private static boolean isRegisterExist(RegisterInfo registerInfo) {
        if (!NetUtils.isHBaseRunnig()) {
            return false;
        }

        String dbName = registerInfo.getDbName();
        String ip = registerInfo.getUrl();
        long port = registerInfo.getDbPort();

        Collection<RegisterInfo> registerInfos = HBaseOperator.dbNeIdMapRegisterInfo.values();
        for (RegisterInfo info : registerInfos){
            if (dbName.equalsIgnoreCase(info.getDbName()) && ip.equalsIgnoreCase(info.getUrl()) && port == info.getDbPort()) {
                return true;
            }
        }

        return false;
    }

    private static void checkRegisterInfo(RegisterInfo registerInfo){
        if (registerInfo.getOsNeId() == null || registerInfo.getOsNeId().isEmpty()
                || Integer.valueOf(registerInfo.getOsNeId()) < 0) {
            String osResponse = NeService.createOSNe(registerInfo);
            if (!osResponse.isEmpty()) {
                String osNeId = JSONObject.fromObject(osResponse).getString("neId");
                registerInfo.setOsNeId(osNeId);
            }
        }

        if (registerInfo.getDbNeId() == null || registerInfo.getDbNeId().isEmpty()
                || Integer.valueOf(registerInfo.getDbNeId()) < 0) {
            String databaseResponse = NeService.createDBNe(registerInfo);
            if (!databaseResponse.isEmpty()) {
                String dbNeId = JSONObject.fromObject(databaseResponse).getString("neId");
                registerInfo.setDbNeId(dbNeId);
            }
        }

        if (registerInfo.getCollectTime() == null || registerInfo.getCollectTime().isEmpty()) {
            registerInfo.setCollectTime(String.valueOf(System.currentTimeMillis()));
        }
    }

}

