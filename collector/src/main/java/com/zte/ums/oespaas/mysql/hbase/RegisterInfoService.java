package com.zte.ums.oespaas.mysql.hbase;

/**
 * Created by 10183966 on 8/22/16.
 */

import com.zte.ums.oespaas.mysql.bean.hbase.RegisterInfo;
import com.zte.ums.oespaas.mysql.util.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class RegisterInfoService {
    private static final Logger logger = LoggerFactory.getLogger(RegisterInfoService.class);

    public static Set<RegisterInfo> getRegisterInfoList() {
        if (!NetUtils.isHBaseRunnig()) {
            return new HashSet<RegisterInfo>();
        }

        if (!HBaseCheck.isChecked){
            HBaseCheck.checkHTable();
        }

        return new HashSet<RegisterInfo>(HBaseOperator.dbNeIdMapRegisterInfo.values());
    }
}
