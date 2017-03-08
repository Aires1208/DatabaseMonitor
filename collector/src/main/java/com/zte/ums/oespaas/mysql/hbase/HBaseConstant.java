package com.zte.ums.oespaas.mysql.hbase;

import org.apache.hadoop.hbase.util.Bytes;

public class HBaseConstant {
    public static byte[] MONITOR_TABLENAME = Bytes.toBytes("MonitorInfo");
    public static byte[] MONITOR_FAMILYNAME = Bytes.toBytes("mysql");
    public static byte[] MONITOR_SLOWLOG_COLUMNNAME = Bytes.toBytes("slowlog");
    public static byte[] MONITOR_GENERALLOG_COLUMNNAME = Bytes.toBytes("generallog");
    public static byte[] MONITOR_DBINFO_COLUMNNAME = Bytes.toBytes("dbinfo");
    public static byte[] MONITOR_STATUS_COLUMNNAME = Bytes.toBytes("status");
    public static byte[] MONITOR_USERS_COLUMNNAME = Bytes.toBytes("users");
    public static byte[] MONITOR_SESSION_COLUMNNAME = Bytes.toBytes("session");

    public static byte[] OS_TABLENAME = Bytes.toBytes("OSInfo");
    public static byte[] OS_FAMILYNAME = Bytes.toBytes("os");
    public static byte[] OS_CPURATIO_COLUMNNAME = Bytes.toBytes("cpuratio");
    public static byte[] OS_MOMERYRATIO_COLUMNNAME = Bytes.toBytes("momeryratio");
    public static byte[] OS_NETWORKIO_COLUMNNAME = Bytes.toBytes("networkio");
    public static byte[] OS_DISKIO_COLUMNNAME = Bytes.toBytes("diskio");


    public static byte[] REGISTER_TABLENAME = Bytes.toBytes("RegisterInfo");
    public static byte[] REGISTER_FAMILYNAME = Bytes.toBytes("inputInfo");
    public static byte[] REGISTER_INPUTPARA_COLUMNNAME = Bytes.toBytes("inputpara");
}
