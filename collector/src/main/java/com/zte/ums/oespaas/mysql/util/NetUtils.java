/*
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zte.ums.oespaas.mysql.util;

import com.zte.ums.oespaas.mysql.message.ProjectConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

public final class NetUtils {
    private static final Logger logger = LoggerFactory.getLogger(NetUtils.class);

    public static String getLocalAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    String address = inetAddress.getHostAddress();
                    if (!address.equals("127.0.0.1") && address.matches("\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}")) {
                        return address;
                    }
                }
            }

        } catch (SocketException e) {
            logger.error(e.getMessage());
        }

        return "127.0.0.1";
    }

    public static boolean checkPortIsUsed(String host, String port) {
        boolean isUsed = true;

        Socket socket = null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, Integer.valueOf(port)), 200);
        } catch (IOException e) {
            isUsed = false;
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }

        return isUsed;
    }

    public static boolean isHBaseRunnig() {
        if (checkPortIsUsed(ProjectConfig.HBASE_IP, ProjectConfig.HBASE_PORT)) {
            return true;
        } else {
            logger.error("hbase is not running on " + ProjectConfig.HBASE_IP);
            return false;
        }
    }

}
