package org.fibonacci.devopscenter.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * @author krame
 * @date 2019-07-09
 */
public class DateUtils {

    public static final String DATE_FORMAT_D = "d";

    public static final String DATE_FORMAT_HH_MM = "HH:mm";

    public static final String DATE_FORMAT_YYYY_MM_DD_HH_MM = "yyyy/MM/dd HH:mm";

    public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy/MM/dd";

    public static final String DATE_FORMAT_YYYY_M_D = "yyyy-M-d";

    public static final String DATE_FORMAT_M_D_E = "M月d日 EEEE";

    public static final String DATE_FORMAT_MM_DD_HH_MM = "MM月dd日 HH:mm";

    public static final String DATE_FORMATE_M_D = "M月d日";

    protected static final String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 返回日期
     *
     * @param timestamp
     * @return
     */
    public static String getD(Integer timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_D);
        Date date = new Date(timestamp.longValue() * 1000);
        return simpleDateFormat.format(date);
    }

    /**
     * 返回MM月dd日 HH:mm
     *
     * @param timestamp
     * @return
     */
    public static String getMMDDHHMM(Integer timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_MM_DD_HH_MM);
        Date date = new Date(timestamp.longValue() * 1000);
        return simpleDateFormat.format(date);
    }


    /**
     * 返回HH:mm
     *
     * @param timestamp
     * @return
     */
    public static String getHHMM(Integer timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_HH_MM);
        Date date = new Date(timestamp.longValue() * 1000);
        return simpleDateFormat.format(date);
    }

    /**
     * 返回yyyy/MM/dd HH:mm
     *
     * @param timestamp
     * @return
     */
    public static String getYYYYMMDDHHMM(Integer timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_YYYY_MM_DD_HH_MM);
        Date date = new Date(timestamp.longValue() * 1000);
        return simpleDateFormat.format(date);
    }

    /**
     * 返回yyyy/MM/dd
     *
     * @param timestamp
     * @return
     */
    public static String getYYYYMMDD(Integer timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_YYYY_MM_DD);
        Date date = new Date(timestamp.longValue() * 1000);
        return simpleDateFormat.format(date);
    }

    /**
     * 返回yyyy-M-d
     *
     * @param date
     * @return
     */
    public static String getYYYYMD(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_YYYY_M_D);
        return simpleDateFormat.format(date);
    }

    /**
     *自定义格式换回
     *
     * @param timestamp
     * @return
     */
    public static String getFormatDate(Integer timestamp, String formatStr) {
        Date date = new Date(timestamp.longValue() * 1000);
        if (formatStr == null) {
            formatStr = DATE_FORMAT_YYYY_M_D;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr);
        return simpleDateFormat.format(date);
    }

    /**
     * 返回yyyy-M-d
     *
     * @param timestamp
     * @return
     */
    public static String getYYYYMD(Integer timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_YYYY_M_D);
        Date date = new Date(timestamp.longValue() * 1000);
        return simpleDateFormat.format(date);
    }

    /**
     * 返回n天后的yyyy-M-d
     *
     * @param date
     * @param n
     * @return
     */
    public static long getTimeInMillisAfterN(Date date, int n) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DATE, c.get(Calendar.DATE) + n);
        return c.getTimeInMillis();
    }

    /**
     * M月d日 E
     *
     * @param timestamp
     * @return
     */
    public static String getMDE(Integer timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMATE_M_D);
        Date date = new Date(timestamp.longValue() * 1000);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return simpleDateFormat.format(date) + " " + weekDays[cal.get(Calendar.DAY_OF_WEEK) - 1 < 0 ? 0 : cal.get(Calendar.DAY_OF_WEEK) - 1];
    }


    /**
     * 日期比较
     *
     * @param start
     * @param end
     * @return
     */
    public static long compare(Long start, Long end) {
        return start - end;
    }

    /**
     * 日期比较
     *
     * @param start
     * @param end
     * @return
     */
    public static Boolean compareContainEqual(Long start, Long end) {
        return start <= end;
    }

    /**
     * 秒转成毫秒
     *
     * @param time
     * @return
     */
    public static Long millsecondDeal(Long time) {
        return time * 1000;
    }

    public static String formatDate(Date date, String formatStr) {
        if (date == null) {
            return null;
        }
        if (StringUtils.isBlank(formatStr)) {
            formatStr = DATE_FORMAT;
        }
        DateFormat format = new SimpleDateFormat(formatStr);
        return format.format(date);
    }

    public static Date parse(String str, String dateFormat) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        if (StringUtils.isBlank(dateFormat)) {
            dateFormat = DATE_FORMAT;
        }
        DateFormat format = new SimpleDateFormat(dateFormat);
        try {
            return format.parse(str);
        } catch (ParseException localParseException) {
        }
        return null;
    }

    /**
     * 相差多少小时
     *
     * @param preDate
     * @param date
     * @return
     */
    public static int hoursDifference(Date preDate, Date date) {
        long milliSeconds = date.getTime() - preDate.getTime();
        return (int) (milliSeconds / 1000L / 3600L);
    }

    /**
     * 相差多少天
     *
     * @param preDate
     * @param date
     * @return
     */
    public static int daysDifference(Date preDate, Date date) {
        long milliSeconds = date.getTime() - preDate.getTime();
        return (int) (milliSeconds / 1000L / 3600L / 24L);
    }

    /**
     * 相差多少秒
     *
     * @param beforedate
     * @param afterdate
     * @return
     */
    public static int secondDifference(Date beforedate, Date afterdate) {
        long timeDelta = (beforedate.getTime() - afterdate.getTime()) / 1000L;
        int secondsDelta = timeDelta > 0L ? (int) timeDelta : (int) Math.abs(timeDelta);
        return secondsDelta;
    }


}
