package demo.ringares.com.ical4jdemo.bean;

/**
 * Created by ls
 * on 2015/8/10
 * Description
 */
public class EventDataBean {
    public static final String EVENT_FLAG_ADD = "A";
    public static final String EVENT_FLAG_EDIT = "E";
    public static final String EVENT_FLAG_DELETE = "D";


    public String event_id = "event_id";//主键 本地id
    public String event_is_syn = "event_is_syn";// 是否需要同步
    public String event_flag = "event_flag";// 数据操作类型A:add,E:edit,D:delete
    public String event_ts = "event_ts";// 同步的时间戳 服务器返回的
    public String event_sid = "event_sid";// 服务器id
    public String event_calendar_id = "event_calendar_id";// calendar id,对应calenadr 表的local id
    public String event_uuid = "event_uuid";// 活动的唯一标识符
    public String event_title = "event_title";// 活动标题
    public String event_note = "event_note";// 活动备注
    public String event_start_date = "event_start_date";// 活动开始时间戳 毫秒
    public String event_end_date = "event_end_date";// 活动结束时间戳
    public String event_is_allday = "event_is_allday";// 活动是否是全天(0不是,1是)
    public String event_rrule = "event_rrule";// 重复方式表达式,空表示不重复，用的时候需要解析
    public String event_rdate = "event_rdate";// 事件的循环日期。通常可以和RRULE一起使用来定义一个重复发生的总集合。
    public String event_advance = "event_advance";// 提前提醒时间，单位是分钟。以,为间隔支持多个例如0,5,60,120
    public String event_url = "event_url";// 活动链接url
    public String event_editable = "event_editable";// 活动是否可以编辑，(0不可以,1可以)
    public String event_create_ts = "event_create_ts";// 活动创建时间戳
    public String event_update_ts = "event_update_ts";// 活动编辑更新时间戳
    public String event_status = "event_status";// 活动的状态 默认conformed
    public String event_iCal = "event_iCal";// ical 原文，每次修改要update对应支持修改的字段

}
