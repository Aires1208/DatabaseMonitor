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

import javax.validation.Valid;

import io.dropwizard.Configuration;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zte.ums.zenap.itm.agent.cometd.server.CometdServletInfo;

public class AgentAppConfig extends Configuration {
    @NotEmpty
    private String template;

    @NotEmpty
    private String defaultName = "ITM Agent";

    @Valid
    private CometdServletInfo cometdServletInfo;
    
    @JsonProperty
    public String getTemplate() {
        return template;
    }

    @JsonProperty
    public void setTemplate(String template) {
        this.template = template;
    }

    @JsonProperty
    public String getDefaultName() {
        return defaultName;
    }

    @JsonProperty
    public void setDefaultName(String name) {
        this.defaultName = name;
    }

    @JsonProperty
	public CometdServletInfo getCometdServletInfo() {
		return cometdServletInfo;
	}

    @JsonProperty
	public void setCometdServletInfo(CometdServletInfo cometdServletInfo) {
		this.cometdServletInfo = cometdServletInfo;
	}
    
    
}
