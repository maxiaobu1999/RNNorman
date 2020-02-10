import React, {Component} from 'react';

import moment from 'moment';
import 'moment/locale/zh-cn'

export function conversionTime(millis) {
    let ONE_MINUTE_MILLIONS = 60 * 1000;
    let ONE_HOUR_MILLIONS = 60 * ONE_MINUTE_MILLIONS;
    let ONE_DAY_MILLIONS = 24 * ONE_HOUR_MILLIONS;
    let date = new Date(millis);
    let curDate = new Date();

    var str = '';
    let durTime = curDate.getTime() - date.getTime();

    let dayStatus = calculateDayStatus(date, new Date());

    if (durTime <= 10 * ONE_MINUTE_MILLIONS) {
        str = '刚刚';
    } else if (durTime < ONE_HOUR_MILLIONS) {
        str = durTime / ONE_MINUTE_MILLIONS + '分钟前';
    } else if (dayStatus == 0) {
        str = durTime / ONE_HOUR_MILLIONS + '小时前';
    } else if (dayStatus == -1) {
        str = '昨天' + moment(millis).locale('zh-cn').format('HH:mm');
    } else if (isSameYear(date, curDate) && dayStatus < -1) {
        str =  moment(millis).locale('zh-cn').format('MM-dd').toString();
    } else {
        str =  moment(millis).locale('zh-cn').format('YYYY-MMM').toString();
    }
    return str;
}


/**
 * 判断是否处于今天还是昨天，0表示今天，-1表示昨天，小于-1则是昨天以前
 * @param targetTime
 * @param compareTime
 * @return
 */
export function calculateDayStatus(targetTime, compareTime) {
    // Calendar tarCalendar = Calendar.getInstance();
    // tarCalendar.setTime(targetTime);
    // int tarDayOfYear = tarCalendar.get(Calendar.DAY_OF_YEAR);
    //
    // Calendar compareCalendar = Calendar.getInstance();
    // compareCalendar.setTime(compareTime);
    // int comDayOfYear = compareCalendar.get(Calendar.DAY_OF_YEAR);

    return -2;
}

/**
 * 判断是否是同一年
 * @param targetTime
 * @param compareTime
 * @return
 */
export function  isSameYear( targetTime,  compareTime) {
    // Calendar tarCalendar = Calendar.getInstance();
    // tarCalendar.setTime(targetTime);
    // int tarYear = tarCalendar.get(Calendar.YEAR);
    //
    // Calendar compareCalendar = Calendar.getInstance();
    // compareCalendar.setTime(compareTime);
    // int comYear = compareCalendar.get(Calendar.YEAR);
    // return tarYear == comYear;

    return false;
}
