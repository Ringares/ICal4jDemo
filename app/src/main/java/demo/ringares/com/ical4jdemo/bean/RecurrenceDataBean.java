package demo.ringares.com.ical4jdemo.bean;

/**
 * Created by ls
 * on 2015/8/10
 * Description
 */
public class RecurrenceDataBean {
    public String recurrence_id = "recurrence_id";//主键 本地id
    public String recurrence_event_id = "recurrence_event_id";// 对应event_id
    public String recurrence_frequency_type = "recurrence_frequency_type";// 重复频率类型
    public String recurrence_interval = "recurrence_interval";// 重复间隔
    public String recurrence_end_type = "recurrence_end_type";// 重复截止类型
    public String recurrence_end_date = "recurrence_end_date";// 重复截至时间戳
    public String recurrence_end_count = "recurrence_end_count";// 重复截至次数
    public String recurrence_by_monthday = "recurrence_by_monthday";// 一个月中的哪几天
    public String recurrence_by_month = "recurrence_by_month";// 一年中的哪几个月
    public String recurrence_by_weekno = "recurrence_by_weekno";// 一年中的哪几周
    public String recurrence_by_yearday = "recurrence_by_yearday";// 一年中的哪几天
    public String recurrence_by_day = "recurrence_by_day";// 一周中的哪几天
    public String recurrence_positions = "recurrence_positions";// 位置集合，分情况使用，是否计算，暂定
    public String recurrence_week_start = "recurrence_week_start";// 周首日 1 SUN 2Mon
    public String recurrence_start_date = "recurrence_start_date";// event 开始日期时间戳
    public String recurrence_syear = "recurrence_syear";// event 开始日期年
    public String recurrence_smonth = "recurrence_smonth";// event 开始日期月
    public String recurrence_sday = "recurrence_sday";// event 开始日期日

}
