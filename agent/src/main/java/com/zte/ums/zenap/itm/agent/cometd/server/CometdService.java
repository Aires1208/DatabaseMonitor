/**
 * Copyright (C) 2015 ZTE, Inc. and others. All rights reserved. (ZTE)
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.zte.ums.zenap.itm.agent.cometd.server;

import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.LocalSession;
import org.cometd.bayeux.server.ServerChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zte.ums.zenap.itm.agent.cometd.CometdUtil;
import com.zte.ums.zenap.itm.agent.common.util.AgentUtil;

public class CometdService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CometdService.class);
    public static String PM_UPLOAD_CHANNEL = "/data";
    
    private BayeuxServer bayeux;
    private LocalSession session;

    private static String bayeuxChannel = "/meta/";

    private static String serviceChannel = "/service/";

    private static CometdService service = new CometdService();

    public static CometdService getInstance() {
        return service;
    }

    public void publish(String channel, Object message) throws CometdException {
        if (bayeux == null) {
            this.bayeux = CometdUtil.getBayeuxServer();
            checkBayeuxServer();
            this.session = this.bayeux.newLocalSession("itm_agent_local_session~");
            this.session.handshake();
        }
        String jsonMsg;
        jsonMsg = AgentUtil.convertBeanToJson(message);
        LOGGER.info("upload json=" + jsonMsg);

        checkAndInit(channel);
        ServerChannel serverChannel = this.bayeux.getChannel(channel);
        serverChannel.publish(this.session, jsonMsg);
    }

    private void checkAndInit(String channel) throws CometdException {
        checkBayeuxServer();
        checkSession();
        checkChannel(channel);
        bayeux.createChannelIfAbsent(channel, new ConfigurableServerChannel.Initializer() {
            public void configureChannel(ConfigurableServerChannel channel) {
                channel.setPersistent(true);
                channel.setLazy(true);
            }
        });
    }

    private void checkBayeuxServer() throws CometdException {
        if (bayeux == null) {
            throw new CometdException(CometdException.ERROR_CODE_BAYEUX, "bayeux is null.");
        }
    }

    private void checkSession() throws CometdException {
        if (session == null || !session.isConnected()) {
            throw new CometdException(CometdException.ERROR_CODE_SESSION_ERROR,
                    "session is invalid.");
        }
    }

    private void checkChannel(String channel) throws CometdException {
        if (channel == null || "".equals(channel)) {
            throw new CometdException(CometdException.ERROR_CODE_PARAM_ERROR, "channel is null.");
        }
        if (channel.startsWith(bayeuxChannel)) {
            throw new CometdException(CometdException.ERROR_CODE_PARAM_ERROR,
                    "channel [" + channel + "] is bayeuxChannel.");
        }
        if (channel.startsWith(serviceChannel)) {
            throw new CometdException(CometdException.ERROR_CODE_PARAM_ERROR,
                    "channel [" + channel + "] is serviceChannel.");
        }
    }
}
