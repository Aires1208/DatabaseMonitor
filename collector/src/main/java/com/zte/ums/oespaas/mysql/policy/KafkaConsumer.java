package com.zte.ums.oespaas.mysql.policy;

import com.zte.ums.oespaas.mysql.util.NetUtils;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 10183966 on 9/6/16.
 */

//@Component
public class KafkaConsumer {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private ConsumerConnector consumerConnector = getConsumer();

    //@Scheduled(cron = "* 0/5 * * * *")
    public void consumeMessage() {
        if (consumerConnector == null) {
            return;
        }

        // create 4 partitions of the stream for topic “test-topic”, to allow 4 threads to consume
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put(KafkaProperties.topic, 4);
        Map<String, List<KafkaStream<byte[], byte[]>>> topicMessageStreams =
                consumerConnector.createMessageStreams(map);
        List<KafkaStream<byte[], byte[]>> streams = topicMessageStreams.get(KafkaProperties.topic);

        if (streams != null) {
            ExecutorService executor = Executors.newFixedThreadPool(4);
            for (final KafkaStream<byte[], byte[]> stream : streams) {
                executor.submit(new Runnable() {
                    public void run() {
                        ConsumerIterator<byte[], byte[]> it = stream.iterator();
                        while (it.hasNext()) {
                            logger.info("Receive: " + new String(it.next().message()));
                        }
                    }
                });
            }
        }
    }

    private ConsumerConnector getConsumer() {
        Properties props = new Properties();
        props.put("zookeeper.connect", KafkaProperties.zk_ip + ":" + KafkaProperties.zk_port);
        //props.put("zookeeper.connectiontimeout.ms", "1000000");
        props.put("group.id", KafkaProperties.groupid);

        return Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
    }

}