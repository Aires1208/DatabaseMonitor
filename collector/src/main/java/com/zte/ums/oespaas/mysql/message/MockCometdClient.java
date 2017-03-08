package com.zte.ums.oespaas.mysql.message;

/**
 * Created by 10183966 on 8/26/16.
 */

import org.cometd.bayeux.Channel;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.LongPollingTransport;
import org.eclipse.jetty.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MockCometdClient {
    private static final Logger logger = LoggerFactory.getLogger(MockCometdClient.class);

    private static String topic = "/data";
    private static String serverURL = "http://" + ProjectConfig.DBMONITOR_AGENT_IP + ":" + ProjectConfig.DBMONITOR_AGENT_PORT
            + "/api/itmagentnotification/v1";

    public static void connect() {
        try {
            HttpClient httpClient = new HttpClient();
            httpClient.start();

            final BayeuxClient client = new BayeuxClient(serverURL, new LongPollingTransport(null, httpClient));
            //client.handshake(1000);//set timeover
//            client.handshake(new ClientSessionChannel.MessageListener() {
//                @Override
//                public void onMessage(ClientSessionChannel clientSessionChannel, Message message) {
//                    if (message.isSuccessful()) {
//                        logger.info("Success to open connection to agent server");
//                        client.getChannel(topic).subscribe(new MsgListener(), new SubscribeCallback());
//                    } else {
//                        logger.info("Error: fail to open connection to agent server");
//                    }
//                }
//            });

            client.handshake(1000);
            boolean isConnected = client.waitFor(2000, BayeuxClient.State.CONNECTED);
            if (isConnected) {
                logger.info("Success to open connection to agent server");
                client.batch(new Runnable() {
                    @Override
                    public void run() {
                        client.getChannel(topic).subscribe(new MsgListener(), new SubscribeCallback());
                    }
                });
            } else {
                logger.info("Error: fail to open connection to agent server");
            }
        } catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    private static class MsgListener implements ClientSessionChannel.MessageListener {
        public void onMessage(ClientSessionChannel channel, Message message) {
            //do not use message.isSucessful() to check
            MessageHandler.messageProess(message.getData().toString());
        }
    }

    private static class SubscribeCallback implements ClientSessionChannel.MessageListener {
        public void onMessage(ClientSessionChannel channel, Message message) {
            if (message.isSuccessful()) {
                logger.info("Success to subscribe");
            } else {
                logger.error("Error: fail to subscribe");
            }
        }
    }
}
