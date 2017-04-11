package com.qiaoxg.leanclouddemo.utils;

import java.text.SimpleDateFormat;

/**
 * Created by admin on 2017/3/27.
 */

public class DateUtils {

    private static final String FORMAT_yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";

    public static String format(long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_yyyy_MM_dd_HH_mm_ss);
        String dateStr = sdf.format(timeStamp);
        return dateStr;
    }
}
