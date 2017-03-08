package com.zte.ums.zenap.itm.agent.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zte.ums.zenap.itm.agent.AgentApp;
import com.zte.ums.zenap.itm.agent.AgentAppConfig;
import com.zte.ums.zenap.itm.agent.common.bean.NeBean;
import com.zte.ums.zenap.itm.agent.common.bean.NeResult;
import com.zte.ums.zenap.itm.agent.common.util.AgentUtil;
import com.zte.ums.zenap.itm.agent.common.util.ExtensionUtil;
import com.zte.ums.zenap.itm.agent.common.util.filescan.FastFileSystem;
import com.zte.ums.zenap.itm.agent.metric.MetricInit;
import com.zte.ums.zenap.itm.agent.resources.wrapper.NeWrapper;
import com.zte.ums.zenap.itm.agent.task.TaskInit;
import io.dropwizard.server.SimpleServerFactory;
import io.dropwizard.setup.Environment;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;

import javax.servlet.DispatcherType;
import java.util.EnumSet;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Created by 10203846 on 11/11/16.
 */
public class NeResourceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(NeResourceTest.class);

//    public static final DropwizardTestSupport<TestConfiguration> SUPPORT =
//            new DropwizardTestSupport<TestConfiguration>(AgentApp.class, ResourceHelpers.resourceFilePath("my-app-config.yaml"));
//

    @org.junit.Test
    public void testNeCreate() throws Exception {
        NeBean neBean = new NeBean();
        neBean.setNeTypeId("os.linux");

        Properties properties = new Properties();
        properties.setProperty("ip", "10.62.100.137");
        properties.setProperty("port", "22");
        properties.setProperty("protocol", "SSH");
        properties.setProperty("username", "root");
        properties.setProperty("password", "root123");
        neBean.setProperties(properties);

        Properties tags = new Properties();
        tags.setProperty("dahu", "www");
        neBean.setTags(tags);

        NeResult result = NeWrapper.neCreate(neBean);
        System.out.println("end");
    }

    @org.junit.Test
    public void testNeCreate2() throws Exception {
        NeBean neBean = new NeBean();
        neBean.setNeTypeId("database.mysql");

        Properties properties = new Properties();
        properties.setProperty("ip", "10.62.100.137");
        properties.setProperty("port", "3306");
        properties.setProperty("username", "root");
        properties.setProperty("password", "root123");
        neBean.setProperties(properties);

        Properties tags = new Properties();
        tags.setProperty("aaa", "aaa");
        neBean.setTags(tags);

        NeResult result = NeWrapper.neCreate(neBean);
        System.out.println("end");
    }

    public void run(AgentAppConfig appConfig, Environment environment) throws Exception {
        LOGGER.info("Start to initialize Agent.");
        environment.jersey().packages("com.zte.ums.zenap.itm.agent.resources");// register rest-api interface

        initSwaggerConfig(environment, appConfig);
        FastFileSystem.init();
        initAgent();
        initCometd(environment, appConfig);
        LOGGER.info("Initialize Agent finished.");
    }

    /**
     * initialize swagger configuration
     *
     * @param environment   environment information
     * @param configuration DAC configuration
     */
    private void initSwaggerConfig(Environment environment, AgentAppConfig configuration) {
        environment.jersey().register(new ApiListingResource());
        environment.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

        BeanConfig config = new BeanConfig();
        config.setTitle("API Description");
        config.setVersion("1.0.0");
        config.setResourcePackage("com.zte.ums.zenap.itm.agent.resources");

        SimpleServerFactory simpleServerFactory =
                (SimpleServerFactory) configuration.getServerFactory();
        String basePath = simpleServerFactory.getApplicationContextPath();
        String rootPath = simpleServerFactory.getJerseyRootPath();
        rootPath = rootPath.substring(0, rootPath.indexOf("/*"));

        basePath = basePath.equals("/")
                ? rootPath
                : (new StringBuilder()).append(basePath).append(rootPath).toString();
        config.setBasePath(basePath);
        config.setScan(true);
    }

    /**
     * initialize Agent
     */
    private void initAgent() {
        String[] packageUrls =
                new String[] {"com.zte.ums"};
//        ExtensionUtil.init(packageUrls);
        AgentUtil.initQueue();
        MetricInit.init();
        TaskInit.init();
    }

    /**
     * initialize cometD server
     *
     * @param environment environment information
     */
    private void initCometd(Environment environment, AgentAppConfig appConfig) {
        String allPath = appConfig.getCometdServletInfo().getServletPath() + "/*";
        environment.getApplicationContext().addFilter(CrossOriginFilter.class, allPath,
                EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR));// add filter
        environment.getApplicationContext()
                .addServlet("org.cometd.server.CometDServlet", allPath)
                .setInitOrder(1);// add servlet
        environment.getApplicationContext()
                .addServlet(appConfig.getCometdServletInfo().getServletClass(),
                        appConfig.getCometdServletInfo().getServletPath())
                .setInitOrder(2);// add servlet
    }
}