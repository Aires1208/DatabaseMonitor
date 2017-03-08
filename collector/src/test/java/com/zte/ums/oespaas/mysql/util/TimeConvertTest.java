package com.zte.ums.oespaas.mysql.util;

import org.junit.Test;

/**
 * Created by root on 9/7/16.
 */
public class TimeConvertTest {
    @Test
    public void testTimeConvert() {
        TimeConvert timeConvert = new TimeConvert();
        long second = timeConvert.getSecondFromTimeString("01:25:47");
        String secondTime = timeConvert.getTimeStringFromSecond(second);
        String time = timeConvert.getTimeStringFromTimestamp(System.currentTimeMillis());

        System.out.println("end");
    }
}