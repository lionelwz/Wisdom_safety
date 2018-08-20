package cn.uetec.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期管理工具类
 * Created by Micheal Wang on 2015/8/10.
 */
public class CalendarManager {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_WITHOUT_SECONDS = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_WITHOUT_TIME = "yyyy-MM-dd";
    public static final String DATE_FORMAT_CN = "MM月dd日 HH:mm";
    public static final String DATE_FORMAT_HH_MM = "HH:mm:ss";
    public static final long MILLIS_OF_24= 24 * 60 * 60 *1000;

    public static String calendarToString(Calendar calendar, String pattern) {
        if (null == calendar) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date date = calendar.getTime();
        return format.format(date);
    }

    public static String millisecondToString(long times, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        if(0 != times) {
            Date date = new Date(times);
            return format.format(date);
        }
        return "";
    }

    public static String millisecondToString(long times) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        if(0 != times) {
            Date date = new Date(times);
            return format.format(date);
        }
        return "";
    }

    public static Calendar String2Calendar(String date, String pattern) {
        if (null == date) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            Date time = format.parse(date);
            calendar.setTime(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return calendar;
    }
}
