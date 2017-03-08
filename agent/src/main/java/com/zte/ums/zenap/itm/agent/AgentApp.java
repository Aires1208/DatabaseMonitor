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
package com.zte.ums.zenap.itm.agent;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.server.SimpleServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zte.ums.zenap.itm.agent.common.util.AgentUtil;
import com.zte.ums.zenap.itm.agent.common.util.ExtensionUtil;
import com.zte.ums.zenap.itm.agent.common.util.filescan.FastFileSystem;
import com.zte.ums.zenap.itm.agent.metric.MetricInit;
import com.zte.ums.zenap.itm.agent.task.TaskInit;

/**
 * agent start class
 */
public class AgentApp extends Application<AgentAppConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentApp.class);

    public static void main(String[] args) throws Exception {
        new AgentApp().run(args);
    }

    @Override
    public void initialize(Bootstrap<AgentAppConfig> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/api-doc", "/api-doc", "index.html", "api-doc"));
    }

    @Override
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
        ExtensionUtil.init(packageUrls);
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
