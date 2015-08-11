package demo.ringares.com.ical4jdemo.bean;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.NumberList;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.WeekDayList;
import net.fortuna.ical4j.model.property.RRule;

/**
 * Created by ls
 * on 2015/8/10
 * Description
 */
public class RecurrenceDataBean {

    /**
     * FREQ
     */
    static final int FREQ_NONE = -1;
    static final int FREQ_DAILY = 0;
    static final int FREQ_WEEKLY = 1;
    static final int FREQ_MONTHLY = 2;
    static final int FREQ_YEARLY = 3;
    /**
     * ENDTYPE
     */
    static final int END_TYPE_NONE = 0;
    static final int END_TYPE_COUNT = 1;
    static final int END_TYPE_UNTIL = 2;
    /**
     * WEEKSTARTDAY
     */
    static final int WKST_NONE =0;
    static final int WKST_SUN =1;
    static final int WKST_MON =2;
    static final int WKST_TUE =3;
    static final int WKST_WED =4;
    static final int WKST_THU =5;
    static final int WKST_FRI =6;
    static final int WKST_SAT =7;


    private Recur recur;
    public int recurrence_id;//主键 本地id
    public int recurrence_event_id;// 对应event_id
    public int recurrence_frequency_type;// 重复频率类型
    public int recurrence_interval;// 重复间隔 (未设置-1)
    public int recurrence_end_type;// 重复截止类型
    public long recurrence_end_date;// 重复截至时间戳,until
    public int recurrence_end_count;// 重复截至次数 (未设置-1)
    public String recurrence_by_monthday;// 一个月中的哪几天
    public String recurrence_by_month;// 一年中的哪几个月
    public String recurrence_by_weekno;// 一年中的哪几周
    public String recurrence_by_yearday;// 一年中的哪几天
    public String recurrence_by_day;// 一周中的哪几天
    public String recurrence_positions;// 位置集合，分情况使用，是否计算，暂定
    public int recurrence_week_start;// 周首日 1 SUN 2Mon
    public long recurrence_start_date;// event 开始日期时间戳
    public int recurrence_syear;// event 开始日期年
    public int recurrence_smonth;// event 开始日期月

    public int recurrence_sday;// event 开始日期日


    public RecurrenceDataBean() {
    }

    public RecurrenceDataBean(RRule rRule, long startDate) {
        new RecurrenceDataBean(-1, rRule, startDate);
    }

    public RecurrenceDataBean(int eventId, RRule rRule, long startDate) {
        recur = rRule.getRecur();
        this.recurrence_event_id = eventId;
        this.recurrence_frequency_type = getFreqTypeFromString(recur.getFrequency());
        this.recurrence_interval = recur.getInterval();
        Date until = recur.getUntil();
        if (until != null) {
            this.recurrence_end_date = until.getTime();
        } else {
            this.recurrence_end_date = -1;
        }
        this.recurrence_end_count = recur.getCount();
        this.recurrence_end_type = getEndType();
        this.recurrence_by_monthday = getByMonthDay();
        this.recurrence_by_month = getByMonth();
        this.recurrence_by_weekno = getByWeekNo();
        this.recurrence_by_yearday = getByYearDay();
        this.recurrence_by_day = getByDay();
        this.recurrence_positions = getPosList();
        this.recurrence_week_start = getWeekStartDay();
        this.recurrence_start_date = startDate;
        java.util.Date date = new java.util.Date(startDate);
        this.recurrence_syear = date.getYear();
        this.recurrence_smonth = date.getMonth();
        this.recurrence_sday = date.getDay();


    }

    private int getFreqTypeFromString(String frequency) {
        int type;
        if ("YEARLY".equals(frequency)) {
            type = FREQ_YEARLY;
        } else if ("MONTHLY".equals(frequency)) {
            type = FREQ_MONTHLY;
        } else if ("WEEKLY".equals(frequency)) {
            type = FREQ_WEEKLY;
        } else if ("DAILY".equals(frequency)) {
            type = FREQ_DAILY;
        } else {
            type = FREQ_NONE;
        }
        return type;
    }


    private int getEndType() {
        if (recurrence_end_date == -1 && recurrence_end_count == -1) {
            return END_TYPE_NONE;
        } else if (recurrence_end_date != -1 && recurrence_end_count != -1) {
            throw new IllegalArgumentException("count和until字段同时存在");
        } else if (recurrence_end_date != -1) {
            return END_TYPE_UNTIL;
        } else {
            return END_TYPE_COUNT;
        }

    }

    private String getByMonthDay() {
        NumberList monthDayList = recur.getMonthDayList();
        if (monthDayList != null && monthDayList.size() > 0) {
            return monthDayList.toString();
        } else {
            return null;
        }

    }

    private String getByMonth() {
        NumberList monthList = recur.getMonthList();
        if (monthList != null && monthList.size() > 0) {
            return monthList.toString();
        } else {
            return null;
        }
    }

    private String getByWeekNo() {
        NumberList weekNoList = recur.getWeekNoList();
        if (weekNoList != null && weekNoList.size() > 0) {
            return weekNoList.toString();
        } else {
            return null;
        }
    }

    private String getByYearDay() {
        NumberList yearDayList = recur.getYearDayList();
        if (yearDayList != null && yearDayList.size() > 0) {
            return yearDayList.toString();
        } else {
            return null;
        }
    }

    private String getByDay() {
        WeekDayList dayList = recur.getDayList();
        if (dayList != null && dayList.size() > 0) {
            return dayList.toString();
        } else {
            return null;
        }
    }

    private String getPosList() {
        NumberList posList = recur.getSetPosList();
        if (posList!=null && posList.size()>0){
            return posList.toString();
        }else{
            return null;
        }
    }

    private int getWeekStartDay() {
        String weekStartDay = recur.getWeekStartDay();
        if ("SU".equals(weekStartDay)){
            return WKST_SUN;
        }else if ("MO".equals(weekStartDay)){
            return WKST_MON;
        }else if ("TU".equals(weekStartDay)){
            return WKST_TUE;
        }else if ("WE".equals(weekStartDay)){
            return WKST_WED;
        }else if ("TH".equals(weekStartDay)){
            return WKST_THU;
        }else if ("FR".equals(weekStartDay)){
            return WKST_FRI;
        }else if ("SA".equals(weekStartDay)){
            return WKST_SAT;
        }else{
            return WKST_NONE;
        }
    }

}
