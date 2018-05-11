package com.newland.homecontrol.utils;

import android.text.format.Time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具
 * Created by yizhong.xu on 2017/8/8.
 */

public class TimeUtils {
    /**
     * String转Date
     */
    public static final Date getDateTime(String time) {
        Date dt2 = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            dt2 = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dt2;
    }

    public static final String getStringTime(Long date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date dates = new Date(date);
        String time = sdf.format(dates);
        return time;
    }

    /**
     * 获取现在时间
     */
    public static final Long getNewTime() {
        Time times = new Time();
        times.setToNow();
        int minute = times.minute;
        int hour = times.hour;
        int sec = times.second;
        String strData = hour + ":" + minute + ":" + sec;
        return getDateTime(strData).getTime();
    }

}
