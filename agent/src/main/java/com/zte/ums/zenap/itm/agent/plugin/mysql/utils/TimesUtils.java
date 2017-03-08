package com.zte.ums.zenap.itm.agent.plugin.mysql.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimesUtils {

	private static String TIMEFORMAT = "yyyy-MM-dd HH:mm:ss"; 
	public static long getCurrentTime(long time, int granutity) {
		Calendar c = Calendar.getInstance();
		Date date = new Date(time);
		c.setTime(date);
		int seconds = c.get(Calendar.MINUTE);				
		c.setTime(date);
		c.set(Calendar.MINUTE, seconds - granutity);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
	}
	
//	public static void  main(String args[]){
//		long times = System.currentTimeMillis();
//		long covertime = TimesUtils.getCurrentTime(times,1);
//		String formattime = TimesUtils.convertTime(covertime);
//        System.out.println(formattime);
//	}
	
	
	public static String convertTime(long formatTime){
        Date newDate = new Date(Long.valueOf(formatTime));
        SimpleDateFormat sdf = new SimpleDateFormat(TIMEFORMAT);
        return String.valueOf(sdf.format(newDate));
	}
	
}
