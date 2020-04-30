package tech.fanpu.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author porridge
 * @version V1.0
 * @Title: DateUtil.java
 * @Package tech.fanpu.common.util
 * @Description: TODO 时间
 * @date 2016年12月6日 下午6:14:31
 */
public class DateUtil {

    static final Logger logger = LoggerFactory.getLogger(DateUtil.class);
    public static DateFormat yyyyMMddHHmm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static DateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static DateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
    public static DateFormat yyyyMM = new SimpleDateFormat("yyyy-MM");
    public static DateFormat yyMMddHHIDFormat = new SimpleDateFormat("yyMMdd");
    public static DateFormat HHmmss = new SimpleDateFormat("HH:mm:ss");
    public static DateFormat yyyy = new SimpleDateFormat("yyyy");
    public static DateFormat MM = new SimpleDateFormat("MM");
    public static DateFormat dd = new SimpleDateFormat("dd");
    public static DateFormat HH = new SimpleDateFormat("HH");
    public static DateFormat mm = new SimpleDateFormat("mm");
    public static DateFormat ss = new SimpleDateFormat("ss");

    public static String getTimeDesc(Date date) {
        if (date == null) {
            return "";
        }
        Date now = new Date();
        long diff = (now.getTime() - date.getTime()) / 1000;
        if (diff > 6 * 30 * 24 * 60 * 60) {
            return yyyyMMdd.format(date);
        }
        if (diff > 30 * 24 * 60 * 60) {
            return diff / (30 * 24 * 60 * 60) + "月前";
        }
        if (diff > 24 * 60 * 60) {
            return diff / (24 * 60 * 60) + "天前";
        }
        if (diff > 60 * 60) {
            return diff / (60 * 60) + "小时前";
        }
        if (diff > 60) {
            return diff / 60 + "分钟前";
        }
        return "刚刚";
    }

    public static String yyyyMM(Date date) {
        return getFormatString(date, yyyyMM);
    }

    public static String yyyyMMdd(Date date) {
        return getFormatString(date, yyyyMMdd);
    }

    public static String yyyyMMddHHmmss(Date date) {
        return getFormatString(date, yyyyMMddHHmmss);
    }

    public static String yyyyMMddHHmm(Date date) {
        return getFormatString(date, yyyyMMddHHmm);
    }

    public static Date yyyyMMddHHmm(String dateStr) {
        try {
            return yyyyMMddHHmm.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String yyMMddHHIDFormatIdString(Date date) {
        return getFormatString(date, yyMMddHHIDFormat);
    }

    public static String getFormatString(Date date, DateFormat format) {
        if (date == null) {
            return "";
        }
        return format.format(date);
    }

    public static Date getBeginDate(Date date, Integer day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.DATE, day);
        return cal.getTime();
    }

    /**
     * 获得这个月的第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.DATE, 1);
        return cal.getTime();
    }

    public static Date getEndDate(Date date, Integer day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, day);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.MILLISECOND, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    /**
     * 获得当前时间到今天晚上的秒数
     */
    public static long getToDayEndSeconds() {
        Date date = new Date();
        return (DateUtil.getEndDate(date, 0).getTime() - date.getTime()) / 1000;
    }
    //	SECONDS

    public static Date getAddDayDate(Date date, Integer day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, day);
        return cal.getTime();
    }

    public static Date getAddMonthDate(Date date, Integer month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, month);
        return cal.getTime();
    }

    public static Date getAddMinuteDate(Date date, Integer month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, month);
        return cal.getTime();
    }

    /**
     * 检查时间是否在当前范围
     *
     * @param begin hh:mm
     * @param end   hh:mm
     */
    public static boolean checkTimeRange(String begin, String end) {
        String dateStr = yyyyMMdd(new Date());
        Long currentDate = System.currentTimeMillis();
        try {
            return yyyyMMddHHmm.parse(dateStr + " " + begin).getTime() < currentDate && yyyyMMddHHmm.parse(dateStr + " " + end).getTime() > currentDate;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("格式时间出现问题, e", e);
            return false;
        }
    }

    public static void main(String[] a) {

        logger.info("=====" + checkTimeRange("13:00", "17:00"));
    }


}
