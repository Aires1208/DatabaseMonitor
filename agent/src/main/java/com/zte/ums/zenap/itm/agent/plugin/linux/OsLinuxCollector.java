package com.zte.ums.zenap.itm.agent.plugin.linux;

import com.zte.ums.zenap.itm.agent.common.util.ExtensionImpl;
import com.zte.ums.zenap.itm.agent.dataaq.common.DataAcquireException;
import com.zte.ums.zenap.itm.agent.extension.Collector;
import com.zte.ums.zenap.itm.agent.extension.ICollector;
import com.zte.ums.zenap.itm.agent.plugin.linux.util.cli.datacollector.TelnetOrSshDataCollector;
import com.zte.ums.zenap.itm.agent.plugin.linux.util.dataparser.TelnetOrSshDataParser;
import com.zte.ums.zenap.itm.agent.plugin.linux.util.monitor.TelnetOrSshDataMonitor;

import java.util.List;
import java.util.Map;
import java.util.Properties;

@ExtensionImpl(keys = {"os.linux.cpu", "os.linux.ram", "os.linux.mysqldisk", "os.linux.mysqlnetwork"}, entensionId = ICollector.EXTENSIONID)
public class OsLinuxCollector extends Collector {

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, List<String>> perform(Properties prop)
            throws DataAcquireException {
        TelnetOrSshDataMonitor monitor = TelnetOrSshDataMonitor.getIntance();
        TelnetOrSshDataCollector dataCollector = TelnetOrSshDataCollector.getIntance();
        TelnetOrSshDataParser dataParser = TelnetOrSshDataParser.getIntance();
        return monitor.perform(taskInfo, dataCollector, dataParser);
    }
}
