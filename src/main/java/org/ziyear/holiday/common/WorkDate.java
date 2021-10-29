package org.ziyear.holiday.common;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.ziyear.holiday.enums.DateTypeEnum;
import org.ziyear.holiday.exception.WorkDateException;
import org.ziyear.holiday.util.DateUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 功能描述 : 日期实体
 *
 * @author zhaorui 2021-09-26 16:38
 */
public class WorkDate {
    private String dateCalUrl;
    /**
     * 获取到的节假日日历起始年
     */
    private String calStartYearStr;
    /**
     * 获取到的节假日日历终止年
     */
    private String calEndYearStr;

    private List<WorkDay> dateCalDays;
    private List<WorkDay> yearAllDays;
    private Map<String, WorkDay> yearAllDayMap;

    public WorkDate() {
        this(null);
    }

    public WorkDate(String dateCalUrl) {
        if (StringUtils.isBlank(dateCalUrl)) {
            dateCalUrl = HolidayConstant.HOLIDAY_CAL;
        }
        this.dateCalUrl = dateCalUrl;
        init();
    }

    public void init() {
        HttpResponse execute = HttpUtil.createGet(dateCalUrl).execute();
        InputStream inputStream = execute.bodyStream();
        try {
            dateCalDays = parserDateCal(inputStream);
        } catch (Exception e) {
            throw new WorkDateException(String.format("节假日日期获取处理失败：%s", e.getMessage()), e);
        }
        merge();
    }

    private void merge() {
        Map<String, WorkDay> dateMap = dateCalDays.stream().collect(Collectors.toMap(WorkDay::getDateStr, Function.identity()));
        DateTime start = DateUtils.parseYear(calStartYearStr);
        DateTime end = DateUtils.parseYear(calEndYearStr);
        java.util.Calendar startCalendar = start.toCalendar();
        List<WorkDay> all = new ArrayList<>();
        WorkDay first = null;
        WorkDay last = null;
        while (startCalendar.getTimeInMillis() <= end.toCalendar().getTimeInMillis()) {
            Date time = startCalendar.getTime();
            String dataStr = DateUtil.format(time, DatePattern.PURE_DATE_PATTERN);
            WorkDay day;
            if (dateMap.containsKey(dataStr)) {
                day = dateMap.get(dataStr);
            } else {
                day = new WorkDay();
                day.setDate(time);
                day.setDateStr(dataStr);
                day.setWeekDay(DateUtil.dayOfWeek(time));
                day.setDateType(isWorkDay(time) ? DateTypeEnum.WORKDAY : DateTypeEnum.HOLIDAY);
            }
            if (first == null) {
                first = day;
            } else {
                last.setTomorrow(day);
                day.setYesterday(last);
            }
            last = day;
            all.add(day);
            startCalendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
        }
        yearAllDays = all;
        yearAllDayMap = yearAllDays.stream().collect(Collectors.toMap(WorkDay::getDateStr, Function.identity()));
    }

    private static boolean isWorkDay(Date time) {
        int i = DateUtil.dayOfWeek(time);
        return 1 < i && i < 7;
    }

    private List<WorkDay> parserDateCal(InputStream inputStream) throws Exception {
        CalendarBuilder build = new CalendarBuilder();
        Calendar calendar = build.build(inputStream);
        List<WorkDay> myDates = new ArrayList<>();
        for (Object o : calendar.getComponents(Component.VEVENT)) {
            VEvent event = (VEvent) o;
            String dataStr = event.getStartDate().getValue();
            WorkDay myDate = new WorkDay();
            if (dataStr.contains(HolidayConstant.DATE_T)) {
                dataStr = dataStr.substring(0, dataStr.indexOf(HolidayConstant.DATE_T));
                if (dataStr.length() != HolidayConstant.DEFAULT_DATE_STR_LENGTH) {
                    throw new WorkDateException(String.format("错误的日期格式：%s", dataStr));
                }
            }
            DateTime date = DateUtil.parse(dataStr);
            myDate.setDate(date);
            myDate.setDateStr(dataStr);

            String summary = event.getSummary().getValue();
            myDate.setSummary(summary);
            if (summary.contains(HolidayConstant.HOLIDAY_CAL_WORK)) {
                myDate.setDateType(DateTypeEnum.WORKDAY);
            }
            if (summary.contains(HolidayConstant.HOLIDAY_CAL_HOLIDAY)) {
                myDate.setDateType(DateTypeEnum.HOLIDAY);
            }
            myDate.setWeekDay(DateUtil.dayOfWeek(date));
            myDates.add(myDate);
        }

        if (CollectionUtils.isEmpty(myDates)) {
            throw new WorkDateException("获取节假日信息失败！");
        }

        processCalYear(myDates);

        return myDates;
    }

    private void processCalYear(List<WorkDay> myDates) {
        WorkDay start = myDates.get(myDates.size() - 1);
        WorkDay end = myDates.get(0);
        this.setCalStartYearStr(start.getDateStr().substring(0, 4));
        this.setCalEndYearStr(end.getDateStr().substring(0, 4));
    }

    public boolean isHoliday(Date date) {
        WorkDay workDay = getWorkDay(date);
        return workDay.isHoliday();
    }

    public WorkDay getWorkDay(String dataStr) {
        DateTime date = DateUtil.parse(dataStr);
        return getWorkDay(date);
    }

    public WorkDay getToday() {
        return getWorkDay(new Date());
    }

    public WorkDay getWorkDay(Date date) {
        String tmpDataStr = DateUtil.format(date, DatePattern.PURE_DATE_PATTERN);
        WorkDay workDay = yearAllDayMap.get(tmpDataStr);
        if (workDay == null) {
            throw new WorkDateException(String.format("错误的日期：%s", tmpDataStr));
        }
        return workDay;
    }

    public boolean isHoliday(String dataStr) {
        DateTime date = DateUtil.parse(dataStr);
        return isHoliday(date);
    }


    public String getDateCalUrl() {
        return dateCalUrl;
    }

    public void setDateCalUrl(String dateCalUrl) {
        this.dateCalUrl = dateCalUrl;
    }

    public List<WorkDay> getDateCalDays() {
        return dateCalDays;
    }

    public void setDateCalDays(List<WorkDay> dateCalDays) {
        this.dateCalDays = dateCalDays;
    }

    public List<WorkDay> getYearAllDays() {
        return yearAllDays;
    }

    public void setYearAllDays(List<WorkDay> yearAllDays) {
        this.yearAllDays = yearAllDays;
    }

    public Map<String, WorkDay> getYearAllDayMap() {
        return yearAllDayMap;
    }

    public void setYearAllDayMap(Map<String, WorkDay> yearAllDayMap) {
        this.yearAllDayMap = yearAllDayMap;
    }

    public String getCalStartYearStr() {
        return calStartYearStr;
    }

    public void setCalStartYearStr(String calStartYearStr) {
        this.calStartYearStr = calStartYearStr;
    }

    public String getCalEndYearStr() {
        return calEndYearStr;
    }

    public void setCalEndYearStr(String calEndYearStr) {
        this.calEndYearStr = calEndYearStr;
    }
}
