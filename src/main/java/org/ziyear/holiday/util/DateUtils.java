package org.ziyear.holiday.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

/**
 * 功能描述 : 日期工具类
 *
 * @author zhaorui 2021-10-29 15:06
 */
public class DateUtils extends DateUtil {
    private static final String YEAR_FORMAT = "yyyy";

    public static DateTime parseYear(String yearStr) {
        return parse(yearStr, YEAR_FORMAT);
    }

    public static DateTime endOfYear(String yearsStr) {
        DateTime dateTime = parseYear(yearsStr);
        return endOfYear(dateTime);
    }
}
