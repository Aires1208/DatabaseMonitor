package com.zte.ums.oespaas.mysql;

import com.zte.ums.oespaas.mysql.hbase.HBaseCheck;
import com.zte.ums.oespaas.mysql.message.MockCometdClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by 10172605 on 2016/8/8.
 */
@SpringBootApplication
@EnableAutoConfiguration
@EnableCaching
@EnableSwagger2
@EnableScheduling
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        //has probloms, need to check connect
        MockCometdClient.connect();
        HBaseCheck.checkHTable();
    }
}

