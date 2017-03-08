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
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;
import com.zte.ums.zenap.itm.agent.resources.wrapper.TaskWrapper;
import com.zte.ums.zenap.itm.agent.task.TaskInfo;

/**
 * rest interface definition of task manage(create/modify/delete)
 */
@Path("/tasks")
@Api(tags = {" TaskResource "})
public class TaskResource {
    @POST
    @Path("/")
    @ApiOperation(value = "create task.")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response taskCreate(@ApiParam(value = "task", required = true) TaskInfo task) {
        return TaskWrapper.taskCreate(task);
    }

    @PUT
    @Path("/{taskid}")
    @ApiOperation(value = "modify task")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response taskGranularityModify(
            @ApiParam(value = "task id", required = true) @PathParam("taskid") int taskId,
            @ApiParam(value = "granularity", required = true) int granularity) {
        return TaskWrapper.taskGranularityModify(taskId, granularity);
    }

    @DELETE
    @Path("/{taskid}")
    @ApiOperation(value = "delete task by task id.")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response taskDelete(
            @ApiParam(value = "task id", required = true) @PathParam("taskid") int taskId) {
        return TaskWrapper.taskDelete(taskId);
    }
    
    @POST
    @Path("/acquiredata")
    @ApiOperation(value = "execute task.")
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Internal Server Error.") })
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Map<String, List<String>> taskExecute(@ApiParam(value = "task", required = true) TaskInfo task) {
        return TaskWrapper.taskExecute(task);
    }
}
