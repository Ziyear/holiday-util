package org.ziyear.holiday.util;

import cn.hutool.core.date.DateTime;
import org.ziyear.holiday.common.WorkDate;
import org.ziyear.holiday.common.WorkDay;
import org.ziyear.holiday.exception.WorkDateException;

import java.util.Date;

import static cn.hutool.core.date.DatePattern.PURE_DATE_FORMAT;

/**
 * 功能描述 : 节假日服务
 *
 * @author zhaorui 2021-09-28 09:27
 */
public class HolidayUtil {
    /**
     * 日历
     */
    static WorkDate workDate;

    static {
        workDate = new WorkDate();
        workDate.init();
    }

    public static boolean isHoliday() {
        String today = PURE_DATE_FORMAT.format(DateUtils.date());
        return isHoliday(today);
    }

    public static boolean isHoliday(Date date) {
        String today = PURE_DATE_FORMAT.format(date);
        return isHoliday(today);
    }

    public static boolean isHoliday(String dateStr) {
        DateTime parse = DateUtils.parse(dateStr);
        String format = PURE_DATE_FORMAT.format(parse);
        verifyDate(format);
        WorkDay workDay = workDate.getYearAllDayMap().get(format);
        return workDay.isHoliday();
    }

    private static void verifyDate(String dateStr) {
        String startYear = workDate.getCalStartYearStr();
        String endYear = workDate.getCalEndYearStr();
        try {
            int start = Integer.parseInt(startYear);
            int end = Integer.parseInt(endYear);
            int year = Integer.parseInt(dateStr.substring(0, 4));
            if (year > end || year < start) {
                throw new WorkDateException(String.format("日期解析错误，输入日期请在%d年-%d年之间！", start, end));
            }
        } catch (Exception e) {
            throw new WorkDateException("日期校验失败！", e);
        }
    }
}
