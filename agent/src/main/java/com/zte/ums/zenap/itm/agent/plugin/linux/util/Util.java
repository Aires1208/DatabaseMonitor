package com.zte.ums.zenap.itm.agent.plugin.linux.util;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class Util {
    protected static ConcurrentHashMap<String, HashMap<String, String>> remoteHostMap =
            new ConcurrentHashMap<String, HashMap<String, String>>();
	public static List<String> getInfo(int line, int tokenIndex,
			String[] valueStr, boolean iflist, boolean iftokenall, String specialProcess) {
		List<String> result = new ArrayList<String>();
		if (specialProcess != null)
		{
			valueStr = specialProcess(valueStr, specialProcess);
		}
		if (!iflist) {
			String lineStr = valueStr[line - 1];
			StringTokenizer toks = new StringTokenizer(lineStr);
			int num = toks.countTokens();
			String[] tokens = new String[num];
			if (tokenIndex > num) {
				tokenIndex = num;
			}
			for (int i = 0; i < tokens.length; i++) {
				tokens[i] = toks.nextToken();
			}
			String resultItem = "";
			if (iftokenall) {
				for (int i = tokenIndex - 1; i < num; i++) {
					resultItem = resultItem + tokens[i] + " ";
				}
				result.add(resultItem);
			} else {
				result.add(tokens[tokenIndex - 1]);
			}
		} else {
			for (int j = (line - 1); j < valueStr.length; j++) {
				String lineStr = valueStr[j];
				StringTokenizer toks = new StringTokenizer(lineStr);
				int num = toks.countTokens();
				String[] tokens = new String[num];
				if (tokenIndex > num) {
					tokenIndex = num;
				}
				for (int i = 0; i < tokens.length; i++) {
					tokens[i] = toks.nextToken();
				}
				String resultItem = "";
				if (iftokenall) {
					for (int i = tokenIndex - 1; i < num; i++) {
						resultItem = resultItem + tokens[i] + " ";
					}
					result.add(resultItem.trim());
				} else {
					result.add(tokens[tokenIndex - 1]);
				}
			}
		}
		return result;
	}
	
    public static List<String> delUnit(List initial, String unit) {
        List<String> result = new ArrayList<String>();
        for (Object anInitial : initial) {
            String value = (String) anInitial;
            int unitIndex = value.indexOf(unit);
            if (unitIndex != -1) {
                value = value.substring(0, unitIndex);
            }
            result.add(value);
        }
        return result;
    }
    
    public static String changeSHFilePath(String shFileName, String userName) {
        if (userName.equals("root")) {
            return shFileName;
        }

        StringTokenizer tokens = new StringTokenizer(shFileName, "/");
        int size = tokens.countTokens();
        String[] secs = new String[size];
        for (int i = 0; i < size; i++) {
            secs[i] = tokens.nextToken();
        }
        return "/home/" + userName + "/" + secs[size - 1];
    }
    
    public static Map<String, HashMap<String, String>> getRemoteHostMap() {
        return remoteHostMap;
    }
    
    public static List<String> listSetScale(List<String> list0, int scale) {
        if (list0 == null) {
            return null;
        }
        int size = list0.size();
        BigDecimal bdcl;
        for (int i = 0; i < size; i++) {
            bdcl = new BigDecimal((list0.get(i)));
            list0.set(i, bdcl.setScale(scale, BigDecimal.ROUND_HALF_UP).toString());
        }
        return list0;
    }
    
	public static String[] specialProcess(String[] valueString, String specialProcess)
	{

		if (specialProcess.equals("LinuxFilterDisk"))
		{
			Vector<String> vecResult = new Vector<String>();
			for (int i = 0; i < valueString.length; i++)
			{
				StringTokenizer tokens = new StringTokenizer(valueString[i]);
				if (tokens.countTokens() < 12)
					continue;
				String firstToken = tokens.nextToken();
				// if (endsWithNumber(firstToken)) {
				// continue;
				// }
				if (!firstToken.startsWith("/dev"))
				{
					String tmp = "/dev/" + valueString[i];
					valueString[i] = tmp;
				}
				vecResult.add(valueString[i]);
			}
			return vecResult.toArray(new String[0]);
		}

		return valueString;
	}
}
