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
package com.zte.ums.zenap.itm.agent.plugin.linux.util.dataparser;

import com.zte.ums.zenap.itm.agent.plugin.linux.util.Calculator;
import com.zte.ums.zenap.itm.agent.plugin.linux.util.Const;
import com.zte.ums.zenap.itm.agent.plugin.linux.util.MonitorException;
import com.zte.ums.zenap.itm.agent.plugin.linux.util.Util;
import com.zte.ums.zenap.itm.agent.plugin.linux.util.conf.DaParserInfo;
import com.zte.ums.zenap.itm.agent.plugin.linux.util.conf.DaPerfCounterInfo;
import com.zte.ums.zenap.itm.agent.common.AgentConst;
import com.zte.ums.zenap.itm.agent.dataaq.common.DataAcquireException;
import com.zte.ums.zenap.itm.agent.dataaq.monitor.bean.common.MonitorTaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TelnetOrSshDataParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(TelnetOrSshDataParser.class);
	private static TelnetOrSshDataParser dataParser;

	public static synchronized TelnetOrSshDataParser getIntance() {
		if (dataParser == null) {
			dataParser = new TelnetOrSshDataParser();
		}
		return dataParser;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object parse(ArrayList dataCollected, DaPerfCounterInfo perfCounterInfo) throws MonitorException {
		List<String> result = new ArrayList<String>();

		if (dataCollected.size() == 0) {
			return result;
		}

		String[] valueStr = (String[]) dataCollected.toArray(new String[dataCollected.size()]);
		List parserContent = perfCounterInfo.getCounterParsers();
		String formular = perfCounterInfo.getValue();

		Map<String, List<String>> valueParsed = new HashMap<String, List<String>>();
		for (Object aParserContent : parserContent) {
			DaParserInfo parseInfo = (DaParserInfo) aParserContent;
			int line = parseInfo.getLine();
			String name = parseInfo.getName();
			int token = parseInfo.getToken();
			boolean iftokenall = parseInfo.iftokenall();
			List<String> value = Util.getInfo(line, token, valueStr, perfCounterInfo.iflist(), iftokenall, perfCounterInfo.getSpecialProcess());

			String unit = parseInfo.getUnit();
			if (unit != null) {
				value = Util.delUnit(value, unit);
			}
			valueParsed.put(name, value);
		}

		if (perfCounterInfo.ifstring()) {
			List<String> list = valueParsed.get(formular);
			if (list == null) {
				LOGGER.error("There's no such list as " + formular);
				return new ArrayList();
			}
			return list;
		}

		try {
			result = new Calculator().calculate(valueParsed, formular);
		} catch (MonitorException e) {
			Set<String> keySet = valueParsed.keySet();
			for (String key : keySet) {
				Object obj = valueParsed.get(key);
				LOGGER.info(" newKEY: " + key);
				if (obj != null) {
					List list = (ArrayList) obj;
					for (int j = 0, size = list.size(); (j < size) && (j <= 15); j++) {
						String neirong = list.get(j).toString();
						LOGGER.info("[" + j + "]: " + neirong);
					}
					if (list.size() > 15) {
						LOGGER.info("Total size: " + list.size());
					}
				} else {
					LOGGER.info(obj != null ? obj.toString() : null);
				}
			}
			LOGGER.error(formular, e);
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	private static final ConcurrentHashMap telnetNetworkInterfaceCache = new ConcurrentHashMap();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map cacheAndCalculateLinuxTelnetNetworkInterface(MonitorTaskInfo mtaskInfo, Map result) throws DataAcquireException {
		String ip = mtaskInfo.getMonitorProperty().getProperty(Const.IPADDRESS);

		long collectInterval = mtaskInfo.getGranularity();
		String sTaskId = String.valueOf(mtaskInfo.getJobId());
		Map map1 = (Map) telnetNetworkInterfaceCache.get(sTaskId);

		List vecCollectTime = new ArrayList();
		vecCollectTime.add(String.valueOf(System.currentTimeMillis() / 1000));
		result.put(AgentConst.m_COLLECTEDTIME, vecCollectTime);
		telnetNetworkInterfaceCache.put(sTaskId, result);

		if (sTaskId.equals("0")) {
			return result;
		}

		if (map1 == null) {
			throw new DataAcquireException("Taskid: " + sTaskId + " The first data acquisition from " + ip + ", not reporting data.");
		}

		List telnetInPacket = (ArrayList) result.get("TELNETINPACKET");
		List telnetOutPacket = (ArrayList) result.get("TELNETOUTPACKET");
		List telnetInError = (ArrayList) result.get("TELNETINERROR");
		List telnetOutError = (ArrayList) result.get("TELNETOUTERROR");
		List telnetInByte = (ArrayList) result.get("TELNETINBYTE");
		List telnetOutByte = (ArrayList) result.get("TELNETOUTBYTE");
		List telnetSpeed = (ArrayList) result.get("TELNETSPEED");

		if (telnetInPacket == null || telnetOutPacket == null || telnetInError == null || telnetOutError == null || telnetInByte == null || telnetOutByte == null) {
			return null;
		}

		int size = telnetInPacket.size();

		long cdTimeLast = Long.parseLong(((ArrayList) map1.get(AgentConst.m_COLLECTEDTIME)).get(0).toString());
		long cdTimeThis = System.currentTimeMillis() / 1000;

		List lastTelnetInPacket = (ArrayList) map1.get("TELNETINPACKET");
		if (lastTelnetInPacket.size() != size) {
			LOGGER.info("TelnetNetworkInterface: lastTelnetInPacket's size is not equal to this one.");
			throw new DataAcquireException("Taskid: " + sTaskId + " Collect the networkInterface data acquisition from " + ip + ", lastTelnetInPacket's size is not equal to this one."
					+ "; cdTimeLast = " + cdTimeLast + "; cdTimeThis = " + cdTimeThis + "; collectInterval = " + collectInterval);
		}
		List lastTelnetOutPacket = (ArrayList) map1.get("TELNETOUTPACKET");
		if (lastTelnetOutPacket.size() != size) {
			LOGGER.info("TelnetNetworkInterface: lastTelnetOutPacket's size is not equal to this one.");
			return result;
		}
		List lastTelnetInError = (ArrayList) map1.get("TELNETINERROR");
		if (lastTelnetInError.size() != size) {
			LOGGER.info("TelnetNetworkInterface: lastTelnetInError's size is not equal to this one.");
			return result;
		}
		List lastTelnetOutError = (ArrayList) map1.get("TELNETOUTERROR");
		if (lastTelnetOutError.size() != size) {
			LOGGER.info("TelnetNetworkInterface: lastTelnetOutError's size is not equal to this one.");
			return result;
		}
		List lastTelnetInByte = (ArrayList) map1.get("TELNETINBYTE");
		if (lastTelnetInByte.size() != size) {
			LOGGER.info("TelnetNetworkInterface: lastTelnetInByte's size is not equal to this one.");
			return result;
		}
		List lastTelnetOutByte = (ArrayList) map1.get("TELNETOUTBYTE");
		if (lastTelnetOutByte.size() != size) {
			LOGGER.info("TelnetNetworkInterface: lastTelnetOutByte's size is not equal to this one.");
			return result;
		}

		List packetRecVec = new ArrayList();
		List packetSentVec = new ArrayList();
		List packetRecErrorVec = new ArrayList();
		List packetSentErrorVec = new ArrayList();
		List packetRecErrorRatioVec = new ArrayList();
		List packetSentErrorRatioVec = new ArrayList();
		List byteRecRatioVec = new ArrayList();
		List byteSentRatioVec = new ArrayList();
		List periodTransInRatio = new ArrayList();
		List periodTransOutRatio = new ArrayList();
		for (int i = 0; i < size; i++) {
			long periodOutErrors;
			long periodInErrors;
			long periodOutPkts;
			long periodInPkts;
			double periodRecErrorRatio;
			double periodSentErrorRatio;
			double periodInBytes;
			double periodOutBytes;
			double periodRecRatio;
			double periodSentRatio;
			long periodTime;
			int speed;

			if ((cdTimeThis - cdTimeLast) > (collectInterval + 180)) {
				throw new DataAcquireException("Taskid: " + sTaskId + " Recovery of the data acquisition from " + ip + ", not reporting first data." + "; cdTimeLast = " + cdTimeLast
						+ "; cdTimeThis = " + cdTimeThis + "; collectInterval = " + collectInterval);
			} else {
				periodTime = cdTimeThis - cdTimeLast;
				speed = Integer.parseInt((String) telnetSpeed.get(i));
				long lastValue = Long.parseLong((String) lastTelnetOutError.get(i));
				long thisValue = Long.parseLong((String) telnetOutError.get(i));
				periodOutErrors = thisValue < lastValue ? thisValue : (thisValue - lastValue);

				lastValue = Long.parseLong((String) lastTelnetInError.get(i));
				thisValue = Long.parseLong((String) telnetInError.get(i));
				periodInErrors = thisValue < lastValue ? thisValue : (thisValue - lastValue);

				lastValue = Long.parseLong((String) lastTelnetOutPacket.get(i));
				thisValue = Long.parseLong((String) telnetOutPacket.get(i));
				periodOutPkts = thisValue < lastValue ? thisValue : (thisValue - lastValue);

				lastValue = Long.parseLong((String) lastTelnetInPacket.get(i));
				thisValue = Long.parseLong((String) telnetInPacket.get(i));
				periodInPkts = thisValue < lastValue ? thisValue : (thisValue - lastValue);

				periodRecErrorRatio = periodInPkts == 0 ? 0 : periodInErrors * 100 / periodInPkts;
				periodSentErrorRatio = periodOutPkts == 0 ? 0 : periodOutErrors * 100 / periodOutPkts;

				lastValue = Long.parseLong((String) lastTelnetInByte.get(i));
				thisValue = Long.parseLong((String) telnetInByte.get(i));
				periodInBytes = thisValue < lastValue ? thisValue : (thisValue - lastValue);

				lastValue = Long.parseLong((String) lastTelnetOutByte.get(i));
				thisValue = Long.parseLong((String) telnetOutByte.get(i));
				periodOutBytes = thisValue < lastValue ? thisValue : (thisValue - lastValue);

				periodRecRatio = speed == 0 ? 0 : periodInBytes * 8 * 100 / (speed * 1024 * 1024 * periodTime);
				periodSentRatio = speed == 0 ? 0 : periodOutBytes * 8 * 100 / (speed * 1024 * 1024 * periodTime);
			}
			packetSentErrorVec.add(String.valueOf(periodOutErrors));
			packetRecErrorVec.add(String.valueOf(periodInErrors));
			packetSentVec.add(String.valueOf(periodOutPkts));
			packetRecVec.add(String.valueOf(periodInPkts));
			packetRecErrorRatioVec.add(String.valueOf(periodRecErrorRatio));
			packetSentErrorRatioVec.add(String.valueOf(periodSentErrorRatio));
			byteRecRatioVec.add(String.valueOf(periodRecRatio));
			byteSentRatioVec.add(String.valueOf(periodSentRatio));
			periodTransInRatio.add(String.valueOf(periodInBytes * 8 / (1024 * periodTime)));// kbps
			periodTransOutRatio.add(String.valueOf(periodOutBytes * 8 / (1024 * periodTime)));
		}

		packetRecErrorRatioVec = Util.listSetScale(packetRecErrorRatioVec, 3);
		packetSentErrorRatioVec = Util.listSetScale(packetSentErrorRatioVec, 3);
		byteRecRatioVec = Util.listSetScale(byteRecRatioVec, 3);
		byteSentRatioVec = Util.listSetScale(byteSentRatioVec, 3);
		periodTransInRatio = Util.listSetScale(periodTransInRatio, 3);
		periodTransOutRatio = Util.listSetScale(periodTransOutRatio, 3);

		Map resultMap = new HashMap();
		resultMap.putAll(result);
		resultMap.put("SENTERRORS", packetSentErrorVec);
		resultMap.put("RECEIVEDERRORS", packetRecErrorVec);
		resultMap.put("PACKETSENT", packetSentVec);
		resultMap.put("PACKETRECEIVED", packetRecVec);
		resultMap.put("RECEIVEDERRORRATIO", packetRecErrorRatioVec);
		resultMap.put("SENTERRORRATIO", packetSentErrorRatioVec);
		resultMap.put("BYTESINRATIO", byteRecRatioVec);
		resultMap.put("BYTESOUTRATIO", byteSentRatioVec);
		resultMap.put("PERIODINTRANSRATE", periodTransInRatio);
		resultMap.put("PERIODOUTTRANSRATE", periodTransOutRatio);

		return resultMap;
	}

	/**
	 *
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map addDiskBusyRatio(Map result) {
		List readBytes = (ArrayList) result.get("DISKREADBYTES");
		List busyRatio = (ArrayList) result.get("DISKBUSYRATIO");

		if (readBytes == null) {
			return null;
		}

		if (busyRatio != null) {
			return result;
		}

		int size = readBytes.size();
		List vec = new ArrayList();
		for (int i = 0; i < size; i++) {
			vec.add("0");
		}
		result.put("DISKBUSYRATIO", vec);
		return result;
	}

	@SuppressWarnings("rawtypes")
	private static final ConcurrentHashMap mysqlNetworkCache = new ConcurrentHashMap();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map calculateMysqlNetworkIO(MonitorTaskInfo mtaskInfo, Map result) throws DataAcquireException {
		Map resultMap = new HashMap();
		String sTaskId = String.valueOf(mtaskInfo.getJobId());
		if (sTaskId.equals("0")) {
			return result;
		}
		
		Map map1 = (Map) mysqlNetworkCache.get(sTaskId);

		List vecCollectTime = new ArrayList();
		vecCollectTime.add(String.valueOf(System.currentTimeMillis() / 1000));
		result.put(AgentConst.m_COLLECTEDTIME, vecCollectTime);
		mysqlNetworkCache.put(sTaskId, result);

		List readBytesList = (ArrayList) result.get("MYSQLINTRANSRATE");
		List writeBytesList = (ArrayList) result.get("MYSQLOUTTRANSRATE");
		int size = readBytesList.size();
		if (null == map1 || map1.size() == 0) {
			return initializeMysqlMap(size);
		}else{
			List readVec = new ArrayList();
			List writeVec = new ArrayList();
			List lastReadBytesList = (ArrayList) map1.get("MYSQLINTRANSRATE");
			List lastWriteBytesList = (ArrayList) map1.get("MYSQLOUTTRANSRATE");
			if(lastReadBytesList.size() != size){
				LOGGER.info("ReadBytes: lastReadBytes's size is not equal to this one.");
				return initializeMysqlMap(size);
			}
			if(lastWriteBytesList.size() !=size){
				LOGGER.info("WriteBytes: lastWriteBytes's size is not equal to this one.");
				return initializeMysqlMap(size);
			}
			long cdTimeLast = Long.parseLong(((ArrayList) map1.get(AgentConst.m_COLLECTEDTIME)).get(0).toString());
			long cdTimeThis = System.currentTimeMillis() / 1000;
			long periodSecend = cdTimeThis - cdTimeLast;
			for (int i = 0; i < size; i++) {
				double readBytes;
				double writeBytes;					
				double periodReadBytes;
				double periodWriteRatio;
				
				double lastValue = Double.parseDouble((String) lastReadBytesList.get(i));
				double thisValue = Double.parseDouble((String) readBytesList.get(i));
				readBytes = thisValue < lastValue ? thisValue : (thisValue - lastValue);
				
				lastValue = Long.parseLong((String) lastWriteBytesList.get(i));
				thisValue = Long.parseLong((String) writeBytesList.get(i));
				writeBytes = thisValue < lastValue ? thisValue : (thisValue - lastValue);
				
				periodReadBytes = readBytes/(1024*periodSecend);
				periodWriteRatio = writeBytes/(1024*periodSecend);
				readVec.add(String.valueOf(periodReadBytes));
				writeVec.add(String.valueOf(periodWriteRatio));
			}
			
			readVec = Util.listSetScale(readVec, 3);
			writeVec = Util.listSetScale(writeVec, 3);
			resultMap.put("MYSQLINTRANSRATE", readVec);
			resultMap.put("MYSQLOUTTRANSRATE", writeVec);
			
		}
		return resultMap;

	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map initializeMysqlMap(int size){
		Map result = new HashMap();
		List vec = new ArrayList();
		for (int i = 0; i < size; i++) {
			vec.add("0");
		}
		result.put("MYSQLINTRANSRATE", vec);
		result.put("MYSQLOUTTRANSRATE", vec);
		
		return result;
		
	}

}
