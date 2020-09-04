package com.norman.util;

import android.text.TextUtils;
import android.text.format.DateUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 日期时间工具类
 *
 * @since 2013/10/11
 */
public final class DateTimeUtil {

    /**
     * 一天毫秒数
     */
    public static final int TIME_DAY_MILLISECOND = 86400000;
    /**
     * 格式一
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    /**
     * 格式二
     */
    public static final String DATE_FORMAT_CN = "yyyy年MM月dd日";
    /**
     * 格式三
     */
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 格式四
     */
    public static final String TIME_FORMAT_CN = "yyyy年MM月dd日 HH:mm:ss";
    /**
     * 格式五
     */
    public static final String MONTH_FORMAT = "yyyy-MM";
    /**
     * 格式六
     */
    public static final String DAY_FORMAT = "yyyyMMdd";
    /**
     * 格式七
     */
    public static final String DAY_FORMAT_MONTH_CN = "MM月dd日";
    /**
     * 格式八
     */
    public static final String YEAR_FORMAT = "yyyy";
    /**
     * 下一天的模式：normal
     */
    public static final int MODE_NEXT_NORMAL_DAY = 1;
    /**
     * 下一天的模式：下一个工作日
     */
    public static final int MODE_NEXT_WORKDAY = 2;
    /**
     * 下一天的模式：下一个休息日
     */
    public static final int MODE_NEXT_WEEKEND = 3;

    /**
     * 工作日星期12345的集合 <1,2,3,4,5>
     */
    private static final Set<Integer> WORK_DAY = new HashSet<Integer>();
    /**
     * 休息日星期67的集合  <6,0>
     */
    private static final Set<Integer> WEEKEND_DAY = new HashSet<Integer>();
    /**
     * 整个星期的集合 <1,2,3,4,5,6,0>
     */
    private static final Set<Integer> NORMAL_DAY = new HashSet<Integer>();

    static {
        // 初始化工作日、休息日、每天的集合Set
        WORK_DAY.add(2);
        WORK_DAY.add(3);    // SUPPRESS CHECKSTYLE  星期2常量
        WORK_DAY.add(4);    // SUPPRESS CHECKSTYLE  星期3常量
        WORK_DAY.add(5);    // SUPPRESS CHECKSTYLE  星期4常量
        WORK_DAY.add(6);    // SUPPRESS CHECKSTYLE  星期5常量
        WEEKEND_DAY.add(7); // SUPPRESS CHECKSTYLE  星期6常量
        WEEKEND_DAY.add(1);
        NORMAL_DAY.addAll(WORK_DAY);
        NORMAL_DAY.addAll(WEEKEND_DAY);
    }

    /**
     * 私有构造方法
     */
    private DateTimeUtil() {

    }

    /**
     * 取得当前系统时间，返回java.util.Date类型
     *
     * @return java.util.Date 返回服务器当前系统时间
     *
     * @see java.util.Date
     */
    public static Date getCurrDate() {
        return new Date();
    }

    /**
     * 取得当前系统时间戳
     *
     * @return java.sql.Timestamp 系统时间戳
     *
     * @see java.sql.Timestamp
     */
    public static Timestamp getCurrTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * 根据格式得到格式化后的日期
     *
     * @param currDate 要格式化的日期
     * @param format   日期格式，如yyyy-MM-dd
     *
     * @return String 返回格式化后的日期，格式由参数<code>format</code>
     * 定义，如yyyy-MM-dd，如2013-10-11
     *
     * @see java.text.SimpleDateFormat#format(java.util.Date)
     */
    public static String getFormatDate(java.util.Date currDate, String format) {
        if (currDate == null) {
            return "";
        }
        SimpleDateFormat dtFormatdB = null;
        try {
            dtFormatdB = new SimpleDateFormat(format);
            return dtFormatdB.format(currDate);
        } catch (Exception e) {
            dtFormatdB = new SimpleDateFormat(DATE_FORMAT);
            try {
                return dtFormatdB.format(currDate);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 根据格式得到格式化后的日期
     *
     * @param currDate 要格式化的日期
     * @param format   日期格式，如yyyy-MM-dd
     *
     * @return Date 返回格式化后的日期，格式由参数<code>format</code>
     * 定义，如yyyy-MM-dd，如2013-10-11
     *
     * @see java.text.SimpleDateFormat#parse(java.lang.String)
     */
    public static Date getFormatDate(String currDate, String format) {
        if (currDate == null) {
            return null;
        }
        SimpleDateFormat dtFormatdB = null;
        try {
            dtFormatdB = new SimpleDateFormat(format);
            return dtFormatdB.parse(currDate);
        } catch (Exception e) {
            dtFormatdB = new SimpleDateFormat(DATE_FORMAT);
            try {
                return dtFormatdB.parse(currDate);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 根据format的数组格式, 逐个去解析日期字符串, 直到成功格式化或者失败返回null
     *
     * @param currDate 要格式化的日期
     * @param formatArray 可遍历的日期格式数组
     * @return Date 返回格式化后的日期 格式由参数<code>format</code>
     * 定义, 如yyyy-MM-dd, 如2013-10-11
     */
    public static Date getFormatDate(String currDate, String[] formatArray) {
        if (TextUtils.isEmpty(currDate) || null == formatArray) {
            return null;
        }

        Date formatDate = null;
        for (int i = 0; i < formatArray.length; i++) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(formatArray[i]);
                formatDate = dateFormat.parse(currDate);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (formatDate != null) {
                break;
            }
        }
        return formatDate;
    }

    /**
     * 根据格式得到格式化后的时间
     *
     * @param currDate 要格式化的时间
     * @param format   时间格式，如yyyy-MM-dd HH:mm:ss
     *
     * @return String 返回格式化后的时间，格式由参数<code>format</code>定义，如yyyy-MM-dd
     * HH:mm:ss
     *
     * @see java.text.SimpleDateFormat#format(java.util.Date)
     */
    public static String getFormatDateTime(java.util.Date currDate, String format) {
        if (currDate == null) {
            return "";
        }
        SimpleDateFormat dtFormatdB = null;
        try {
            dtFormatdB = new SimpleDateFormat(format);
            return dtFormatdB.format(currDate);
        } catch (Exception e) {
            dtFormatdB = new SimpleDateFormat(TIME_FORMAT);
            try {
                return dtFormatdB.format(currDate);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 根据格式得到格式化后的时间
     *
     * @param currDate 要格式化的时间
     * @param format   时间格式，如yyyy-MM-dd HH:mm:ss
     *
     * @return Date 返回格式化后的时间，格式由参数<code>format</code>定义，如yyyy-MM-dd
     * HH:mm:ss
     *
     * @see java.text.SimpleDateFormat#parse(java.lang.String)
     */
    public static Date getFormatDateTime(String currDate, String format) {
        if (currDate == null) {
            return null;
        }
        SimpleDateFormat dtFormatdB = null;
        try {
            dtFormatdB = new SimpleDateFormat(format);
            return dtFormatdB.parse(currDate);
        } catch (Exception e) {
            dtFormatdB = new SimpleDateFormat(TIME_FORMAT);
            try {
                return dtFormatdB.parse(currDate);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 得到当前星期
     *
     * @return 星期几
     */
    public static String getWeek() {
        SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
        return dateFm.format(new Date());
    }

    /**
     * 强制使用中文显示星期
     *
     * @return 星期几
     */
    public static String getWeekOfDate() {
        Date dt = new Date();
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    /**
     * 获取下一个指定模式的时间值
     * <pre>
     * 例如输入时间为2014年6月11日21:26:00 （星期三），mode为下一个休息日模式，则输出为： 2014年6月14日21:26:00 （星期六）
     * 输入输出均为毫秒
     * </pre>
     *
     * @param timeInMills 时间（毫秒）
     * @param mode        MODE_NORMAL_NEXT_DAY：下一天， MODE_NEXT_WORKDAY：下一个工作日， MODE_NEXT_WEEKEND：下一个休息日
     *
     * @return 下一个日期的同一时间
     */
    public static long getNextDay(long timeInMills, int mode) {
        Set<Integer> weekDaySet = null;
        switch (mode) {
            case MODE_NEXT_WORKDAY:
                weekDaySet = WORK_DAY;
                break;
            case MODE_NEXT_NORMAL_DAY:
                weekDaySet = NORMAL_DAY;
                break;
            case MODE_NEXT_WEEKEND:
                weekDaySet = WEEKEND_DAY;
                break;
            default:
                weekDaySet = NORMAL_DAY;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMills + DateUtils.DAY_IN_MILLIS);
        while (!weekDaySet.contains(cal.get(Calendar.DAY_OF_WEEK))) {
            cal.setTimeInMillis(cal.getTimeInMillis() + DateUtils.DAY_IN_MILLIS);
        }
        return cal.getTimeInMillis();
    }

    /**
     * 判断是不是同一天
     * @param firstTime first
     * @param secondTime second
     * @return 如果是返回true
     */
    public static boolean isSameDay(Long firstTime, Long secondTime) {
        return (firstTime / TIME_DAY_MILLISECOND) == (secondTime / TIME_DAY_MILLISECOND);
    }

    /**
     * 判断是不是昨天
     * @param date 要判断的日期
     * @return 如果是返回true
     */
    public static boolean isYesterday(Date date){
        if (date == null) {
            return false;
        }
        Long currDays = System.currentTimeMillis() / TIME_DAY_MILLISECOND;
        Long dateDays = date.getTime() / TIME_DAY_MILLISECOND;
        return (currDays - dateDays) == 1;

    }

    /**
     * 判断是不是今天
     * @param date 传入的日期
     * @return 如果是返回true
     */
    public static boolean isToday(Date date) {
        if (date == null) {
            return false;
        }
        Date curr = new Date();
        return (curr.getYear() == date.getYear()) &&
                (curr.getMonth() == date.getMonth()) &&
                (curr.getDate() == date.getDate());
    }

    /**
     * 将字符串的秒转成<b>分:秒</b>的格式
     *
     * @param durString 秒的字符串，如果不是数字或为空，返回00:00
     *
     * @return
     */
    public static String convertSecondToHumanView(String durString) {
        if (TextUtils.isEmpty(durString) || !TextUtils.isDigitsOnly(durString)) {
            return "00:00";
        }
        long duration = Long.parseLong(durString);

        long minutes = duration / 60;
        long seconds = duration % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

}
