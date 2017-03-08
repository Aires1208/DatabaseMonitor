package com.zte.ums.oespaas.mysql.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by root on 9/7/16.
 */
public class TimeConvert {

    public static long getSecondFromTimeString(String time) {
        if (time.matches("\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}")) {
            int firstDash = time.indexOf('-');
            int lastDash = time.lastIndexOf('-');
            int firstSpace = time.indexOf(' ');
            int firstColon = time.indexOf(':');
            int lastColon = time.lastIndexOf(':');

            int year = Integer.valueOf(time.substring(0, firstDash));
            int month = Integer.valueOf(time.substring(firstDash + 1, lastDash));
            int day = Integer.valueOf(time.substring(lastDash + 1, firstSpace));
            int hour = Integer.valueOf(time.substring(firstSpace + 1, firstColon));
            int minute = Integer.valueOf(time.substring(firstColon + 1, lastColon));
            int second = Integer.valueOf(time.substring(lastColon + 1, time.length()));

            return year * 12 * 30 * 24 * 3600 + month * 30 * 24 * 3600 + day * 24 * 3600
                    + hour * 3600 + minute * 60 + second;
        } else if (time.matches("\\d{1,2}:\\d{1,2}:\\d{1,2}")) {
            int firstColon = time.indexOf(':');
            int lastColon = time.lastIndexOf(':');

            int hour = Integer.valueOf(time.substring(0, firstColon));
            int minute = Integer.valueOf(time.substring(firstColon + 1, lastColon));
            int second = Integer.valueOf(time.substring(lastColon + 1, time.length()));

            return hour * 3600 + minute * 60 + second;
        } else if (time.matches("\\d{1,}d\\d{1,2}h\\d{1,2}m\\d{1,2}s")) {
            int dPos = time.indexOf("d");
            int hPos = time.indexOf("h");
            int mPos = time.indexOf("m");
            int sPos = time.indexOf("s");

            int day = Integer.valueOf(time.substring(0, dPos));
            int hour = Integer.valueOf(time.substring(dPos + 1, hPos));
            int minute = Integer.valueOf(time.substring(hPos + 1, mPos));
            int second = Integer.valueOf(time.substring(mPos + 1, sPos));

            return day * 24 * 3600 + hour * 3600 + minute * 60 + second;
        } else {
            return -1L;
        }
    }

    public static String getTimeStringFromSecond(long seconds) {
        final long MAX_DAY_SECOND = 23 * 3600 + 59 * 60 + 59;
        final long MAX_MONTH_SECOND = 30 * 24 * 3600 + 23 * 3600 + 59 * 60 + 59;

        if (seconds > MAX_MONTH_SECOND) {
            long year = seconds / (12 * 30 * 24 * 3600);
            long month = seconds % (12 * 30 * 24 * 3600) / (30 * 24 * 3600);
            long day = seconds % (12 * 30 * 24 * 3600) % (30 * 24 * 3600) / (24 * 3600);
            long hour = seconds % (12 * 30 * 24 * 3600) % (30 * 24 * 3600) % (24 * 3600) / 3600;
            long minute = seconds % (12 * 30 * 24 * 3600) % (30 * 24 * 3600) % (24 * 3600) % 3600 / 60;
            long second = seconds % (12 * 30 * 24 * 3600) % (30 * 24 * 3600) % (24 * 3600) % 3600 % 60;

            return year + "-" + format(month) + "-" + format(day) + " " + format(hour)
                    + ":" + format(minute) + ":" + format(second);
        } else if (seconds <= MAX_MONTH_SECOND && seconds > MAX_DAY_SECOND) {
            long day = seconds / (24 * 3600);
            long hour = seconds % (24 * 3600) / 3600;
            long minute = seconds % (24 * 3600) % 3600 / 60;
            long second = seconds % (24 * 3600) % 3600 % 60;

            return day + "d" + hour + "h" + minute + "m" + second + "s";
        } else if (seconds <= MAX_DAY_SECOND && seconds > 0) {
            long hour = seconds / 3600;
            long minute = seconds % 3600 / 60;
            long second = seconds % 3600 % 60;

            return format(hour) + ":" + format(minute) + ":" + format(second);
        } else {
            return "0";
        }
    }

    public static String getTimeStringFromTimestamp(long timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        return simpleDateFormat.format(new Date(timestamp));
    }

    public static String getFullTimeStringFromTimestamp(long timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date(timestamp));
    }

    public static long getTimestampFromTimeString(String time) throws ParseException {
        if (time.matches("\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}")) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time));

            return calendar.getTimeInMillis();
        }

        return -1L;
    }

    private static String format(long l) {
        if (l < 10) {
            return "0" + l;
        } else {
            return String.valueOf(l);
        }
    }
}
