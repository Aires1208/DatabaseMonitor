package com.zte.ums.oespaas.mysql.util;

import org.junit.Test;

/**
 * Created by 10203846 on 11/7/16.
 */
public class NetUtilsTest {

    @Test
    public void testGetLocalAddress() throws Exception {
        String localhost = NetUtils.getLocalAddress();

        System.out.println("end");
    }

    @Test
    public void testCheckPortIsUsed() throws Exception {
        String host = "127.0.0.1";
        String port = "2181";
        boolean ret = NetUtils.checkPortIsUsed(host, port);

        System.out.println("end");
    }
}