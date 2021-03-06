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
package com.zte.ums.zenap.itm.agent.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;
import com.zte.ums.zenap.itm.agent.resources.wrapper.AgentWrapper;

/**
 * rest interface definition of Agent register/unregister
 */
@Path("/agent")
@Api(tags = {" AgentResource "})
public class AgentResource {
    @POST
    @Path("/")
    @ApiOperation(value = "register agent")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response registerAgent() {
        return AgentWrapper.registerAgent();
    }

    @DELETE
    @Path("/")
    @ApiOperation(value = "unregister agent")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response unregisterAgent() {
        return AgentWrapper.unregisterAgent();
    }
}
