package com.mmall.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


import java.util.Date;

/**
 * Created by ztian on 2017/6/7.
 */
public class DateTimeUtil {
    public static final String STANDARD_FORMAT="yyyy-MM-dd HH:mm:ss";
    public static Date strToDate(String dateString, String formatStr)
    {
        if(dateString==null||formatStr==null) {
            return null;
        }
        DateTimeFormatter format= DateTimeFormat.forPattern(formatStr);
        DateTime dateTime=format.parseDateTime(dateString);
        return dateTime.toDate();
    }

    public static String dateToStr(Date date, String formatStr)
    {
        if(date==null||formatStr==null) {
            return null;
        }
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(formatStr);
    }

    public static Date strToDate(String dateString)
    {
        if(dateString==null) {
            return null;
        }
        DateTimeFormatter format= DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime=format.parseDateTime(dateString);
        return dateTime.toDate();
    }

    public static String dateToStr(Date date)
    {
        if(date==null) {
            return null;
        }
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }
}
