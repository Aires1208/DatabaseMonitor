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
package com.zte.ums.zenap.itm.agent.common.bean;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.zte.ums.zenap.itm.agent.resources.wrapper.NeWrapper;

/**
 * rest interface definition of ne manage(create/modify/delete)
 */
@Path("/ne")
@Api(tags = {" NeResource "})
public class NeResource {
    @POST
    @Path("/")
    @ApiOperation(value = "create ne.")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public NeResult neCreate(@ApiParam(value = "neInfo", required = true) NeBean neInfo) {
        return NeWrapper.neCreate(neInfo);
    }

    @PUT
    @Path("/{neid}")
    @ApiOperation(value = "modify ne by neid.")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public NeResult taskModify(
            @ApiParam(value = "neId", required = true) @PathParam("neid") String neId,
            @ApiParam(value = "neInfo", required = true) NeBean neInfo) {
        return NeWrapper.neModify(neId, neInfo);
    }

    @DELETE
    @Path("/{neid}")
    @ApiOperation(value = "delete ne by neid.")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public NeResult taskDelete(
            @ApiParam(value = "neId", required = true) @PathParam("neid") String neId) {
        return NeWrapper.neDelete(neId);
    }
    
}
