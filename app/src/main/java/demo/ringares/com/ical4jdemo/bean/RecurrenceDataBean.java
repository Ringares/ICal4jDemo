package demo.ringares.com.ical4jdemo.bean;

import net.fortuna.ical4j.model.Recur;
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


    public int recurrence_id;//主键 本地id
    public int recurrence_event_id;// 对应event_id
    public int recurrence_frequency_type;// 重复频率类型
    public int recurrence_interval;// 重复间隔 (未设置-1)
    public int recurrence_end_type;// 重复截止类型
    public long recurrence_end_date;// 重复截至时间戳
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

    public RecurrenceDataBean(RRule rRule, long startDate) {
        new RecurrenceDataBean(-1, rRule, startDate);
    }

    public RecurrenceDataBean(int eventId, RRule rRule, long startDate) {
        Recur recur = rRule.getRecur();

        this.recurrence_event_id = eventId;
        this.recurrence_frequency_type = getFreqTypeFromString(recur.getFrequency());
        this.recurrence_interval = recur.getInterval();
        this.recurrence_end_count = recur.getCount();

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

    public RecurrenceDataBean() {

    }
}
