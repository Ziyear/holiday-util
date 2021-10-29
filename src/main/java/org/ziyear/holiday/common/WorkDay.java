package org.ziyear.holiday.common;

import org.ziyear.holiday.enums.DateTypeEnum;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 功能描述 : 日历类
 *
 * @author zhaorui 2021-09-26 16:39
 */
public class WorkDay {

    private WorkDay yesterday;
    private WorkDay tomorrow;
    /**
     * 日期类型
     */
    private DateTypeEnum dateType;
    /**
     * 日期
     */
    private Date date;
    /**
     * 日期格式化（全年唯一）
     */
    private String dateStr;

    /**
     * 概括
     */
    private String summary;
    /**
     * 当前周的第几天
     */
    private int weekDay;

    public boolean isLastOfWeek() {
        if (tomorrow == null || DateTypeEnum.HOLIDAY.equals(dateType)) {
            return false;
        }
        return tomorrow.getDateType().equals(DateTypeEnum.HOLIDAY);
    }

    public String getMonthDayStr() {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        DecimalFormat df = new DecimalFormat("00");

        int month = instance.get(Calendar.MONTH) + 1;
        int day = instance.get(Calendar.DAY_OF_MONTH);

        return df.format(month) + df.format(day);
    }

    public boolean isHoliday() {
        return DateTypeEnum.HOLIDAY.equals(dateType);
    }

    public WorkDay getYesterday() {
        return yesterday;
    }

    public void setYesterday(WorkDay yesterday) {
        this.yesterday = yesterday;
    }

    public WorkDay getTomorrow() {
        return tomorrow;
    }

    public void setTomorrow(WorkDay tomorrow) {
        this.tomorrow = tomorrow;
    }

    public DateTypeEnum getDateType() {
        return dateType;
    }

    public void setDateType(DateTypeEnum dateType) {
        this.dateType = dateType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(int weekDay) {
        this.weekDay = weekDay;
    }
}
