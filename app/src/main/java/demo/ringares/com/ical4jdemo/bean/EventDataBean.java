package demo.ringares.com.ical4jdemo.bean;

/**
 * Created by ls
 * on 2015/8/10
 * Description
 */
public class EventDataBean {
    public static final int EVENT_FLAG_ADD = 5;
    public static final int EVENT_FLAG_EDIT = 6;
    public static final int EVENT_FLAG_DELETE = 7;


    public String event_id;//主键 本地id
    public int event_is_syn;// 是否需要同步
    public int event_flag;// 数据操作类型 5:add,6:edit,7:delete
    public long event_ts;// 同步的时间戳 服务器返回的
    public long event_sid;// 服务器id
    public int event_calendar_id;// calendar id,对应calenadr 表的local id
    public String event_uuid;// 活动的唯一标识符
    public String event_title;// 活动标题
    public String event_note;// 活动备注
    public long event_start_date;// 活动开始时间戳 毫秒
    public long event_end_date;// 活动结束时间戳
    public int event_is_allday;// 活动是否是全天(0不是,1是)
    public String event_advance;// 提前提醒时间，单位是分钟。以,为间隔支持多个例如0,5,60,120
    public String event_url;// 活动链接url
    public int event_editable;// 活动是否可以编辑，(0不可以,1可以)
    public long event_create_ts;// 活动创建时间戳
    public long event_update_ts;// 活动编辑更新时间戳
    public String event_status;// 活动的状态 默认conformed
    public String event_iCal;// ical 原文，每次修改要update对应支持修改的字段

}
