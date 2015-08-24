package demo.ringares.com.ical4jdemo.bean;

import java.util.ArrayList;

/**
 * Created by ls
 * on 2015/8/10
 * Description
 */
public class EventDataBean {
    public static final int EVENT_FLAG_ADD = 5;
    public static final int EVENT_FLAG_EDIT = 6;
    public static final int EVENT_FLAG_DELETE = 7;

    public static final int EVENT_IS_ALL_DAY_FALSE = 0;
    public static final int EVENT_IS_ALL_DAY_TRUE = 1;

    public static final int EVENT_EDITABLE_FALSE = 0;
    public static final int EVENT_EDITABLE_TRUE = 1;

    public static final int EVENT_STATUS_TENTATIVE = 1;
    public static final int EVENT_STATUS_CONFIRMED = 2;
    public static final int EVENT_STATUS_CANCELLED = 3;


    public int event_id;//主键 本地id
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
    public int event_status = EVENT_STATUS_CONFIRMED;// 活动的状态 默认conformed
    public String event_iCal;// ical 原文，每次修改要update对应支持修改的字段

    public RecurrenceDataBean recurrenceDataBean;
    public LocationDataBean locationDataBean;
    public ArrayList<PersonDataBean> personDataBeans;


    @Override
    public String toString() {
        return "EventDataBean{" +
                "event_is_syn=" + event_is_syn +
                ", event_flag=" + event_flag +
                ", event_ts=" + event_ts +
                ", event_sid=" + event_sid +
                ", event_calendar_id=" + event_calendar_id +
                ", event_uuid='" + event_uuid + '\'' +
                ", event_title='" + event_title + '\'' +
                ", event_note='" + event_note + '\'' +
                ", event_start_date=" + event_start_date +
                ", event_end_date=" + event_end_date +
                ", event_is_allday=" + event_is_allday +
                ", event_advance='" + event_advance + '\'' +
                ", event_url='" + event_url + '\'' +
                ", event_editable=" + event_editable +
                ", event_create_ts=" + event_create_ts +
                ", event_update_ts=" + event_update_ts +
                ", event_status='" + event_status + '\'' +
                ", event_iCal='" + event_iCal + '\'' +
                '}';
    }

    public void setEventId(int eventId) {
        this.event_id = eventId;
        if (recurrenceDataBean != null) {
            recurrenceDataBean.recurrence_event_id = eventId;
        }
        if (locationDataBean != null) {
            locationDataBean.location_event_id = eventId;
        }
        if (personDataBeans != null) {
            for (PersonDataBean personDataBean : personDataBeans) {
                personDataBean.person_event_id = eventId;
            }
        }
    }
}
