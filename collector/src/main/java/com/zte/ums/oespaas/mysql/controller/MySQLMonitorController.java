package com.zte.ums.oespaas.mysql.controller;

import com.zte.ums.oespaas.mysql.bean.*;
import com.zte.ums.oespaas.mysql.bean.hbase.Range;
import com.zte.ums.oespaas.mysql.bean.hbase.RegisterInfo;
import com.zte.ums.oespaas.mysql.hbase.HBaseCheck;
import com.zte.ums.oespaas.mysql.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Created by 10183966 on 8/24/16.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Api("MySQL Monitor Api")
@RequestMapping("/mysql_monitor")
public class MySQLMonitorController {
    @Autowired
    private XAddMonitorService xAddMonitorService;

    @Autowired
    private XDBsService xdBsService;

    @Autowired
    private XQueriesService xQueriesService;

    @Autowired
    private XClientsService xClientsService;

    @Autowired
    private XSessionsService xSessionsService;

    @Autowired
    private XDBInfoObjectsService xdbInfoObjectsService;

    @Autowired
    private XDBInfoLiveService xdbInfoLiveService;

    @Autowired
    private XDBInfoDashboardService xdbInfoDashboardService;

    @Autowired
    private XReportsService xReportsService;

    @ApiOperation("MySQL Monitor")
    @ApiResponses(
            @ApiResponse(code = 400, message = "params error")
    )
    @RequestMapping(value = "/addMonitor", method = RequestMethod.POST, headers = "content-type=application/json")
    public Map<String, String> addDBMonitor(@RequestBody RegisterInfo registerInfo) {
        return xAddMonitorService.addDBMonitor(registerInfo);
    }

    @RequestMapping(value = "/delMonitor", method = RequestMethod.DELETE)
    public Map<String, String> delDBMonitor(@RequestParam(value = "neId") String dbNeId) {
        return xAddMonitorService.delDBMonitor(dbNeId);
    }

    @RequestMapping(value = "/dbs", method = RequestMethod.GET)
    public Map<String, Object> getDbs(@RequestParam(value = "from") long from, @RequestParam(value = "to") long to) {
        Range range = new Range(from, to);
        return xdBsService.getDbs(range);
    }

    @RequestMapping(value = "/queries", method = RequestMethod.GET)
    public Queries getQueries(@RequestParam("neId") String dbNeId, @RequestParam(value = "from") long from, @RequestParam(value = "to") long to, @RequestParam(value = "topN") int topN) {
        Range range = new Range(from, to);
        return xQueriesService.getTopNQueries(dbNeId, range, topN);
    }

    @RequestMapping(value = "/clients", method = RequestMethod.GET)
    public Clients getClients(@RequestParam("neId") String dbNeId, @RequestParam(value = "from") long from, @RequestParam(value = "to") long to, @RequestParam(value = "topN") int topN) {
        Range range = new Range(from, to);
        return xClientsService.getTopNClients(dbNeId, range, topN);
    }

    @RequestMapping(value = "/sessions", method = RequestMethod.GET)
    public Sessions getSessions(@RequestParam("neId") String dbNeId, @RequestParam(value = "from") long from, @RequestParam(value = "to") long to, @RequestParam(value = "topN") int topN) {
        Range range = new Range(from, to);
        return xSessionsService.getTopNSessions(dbNeId, range, topN);
    }

    @RequestMapping(value = "/dbInfoDashboard", method = RequestMethod.GET)
    public DBInfoDashboard getDBInfoDashboard(@RequestParam("neId") String dbNeId, @RequestParam(value = "from") long from, @RequestParam(value = "to") long to) throws ParseException {
        Range range = new Range(from, to);
        return xdbInfoDashboardService.getDBInfoDashboard(dbNeId, range);
    }

    @RequestMapping(value = "/dbInfoLive", method = RequestMethod.GET)
    public DBInfoLive getDBInfoLive(@RequestParam("neId") String dbNeId, @RequestParam(value = "from") long from, @RequestParam(value = "to") long to) {
        Range range = new Range(from, to);
        return xdbInfoLiveService.getDBInfoLive(dbNeId, range);
    }

    @RequestMapping(value = "/dbInfoObjects", method = RequestMethod.GET)
    public DBInfoObjects getDBInfoObjects(@RequestParam("neId") String dbNeId, @RequestParam(value = "from") long from, @RequestParam(value = "to") long to) {
        Range range = new Range(from, to);
        return xdbInfoObjectsService.getDBInfoObjects(dbNeId, range);
    }

    @RequestMapping(value = "/reports", method = RequestMethod.GET)
    public Reports getReports(@RequestParam("neId") String dbNeId, @RequestParam(value = "from") long from, @RequestParam(value = "to") long to) {
        Range range = new Range(from, to);
        return xReportsService.getReports(dbNeId, range);
    }

    @RequestMapping(value = "/dblist", method = RequestMethod.GET)
    public List<Map<String, String>> getDBList() {
        return xdBsService.getDBList();
    }
}