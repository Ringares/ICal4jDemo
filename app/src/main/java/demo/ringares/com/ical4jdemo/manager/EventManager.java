package demo.ringares.com.ical4jdemo.manager;

import android.content.Context;
import android.util.Log;

import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Summary;

import demo.ringares.com.ical4jdemo.bean.EventDataBean;
import demo.ringares.com.ical4jdemo.bean.LocationDataBean;
import demo.ringares.com.ical4jdemo.bean.PersonDataBean;
import demo.ringares.com.ical4jdemo.bean.RecurrenceDataBean;
import demo.ringares.com.ical4jdemo.dbHelper.DBManager;

/**
 * Created by ls
 * on 2015/8/10
 * Description
 */
public class EventManager {
    private Context ctx;
    /**
     * 需要处理的iCal协议定义字段
     */
    public String eventData; //vEvent原文
    public String uid; //UID
    public long createTime; //
    public long updateTime; //DTSTAMP
    public long startTime; //DTSTART
    //DTEND和DURATION 只会出现一个
    public long endTime; //DTEND
    public Dur duration; //DURATION
    public String status; //STATUS
    public String title; //SUMMARY
    public String content; //DESCRIPTION
    public Location location; //LOCATION
    public Attendee attendee; //ATTENDEE


    public RRule rrule; //RRULE
    /**
     * 自定义的所需字段
     */
    public int isSyn; //是否需要同步
    public long sid; //同步服务器id
    public long synTime; //同步时间戳
    public int eventFlag; //数据操作类型5:add,6:edit,7:delete
    public String advance; //提前x分钟提醒 eg.10,20,30
    public String url;
    public int editable;
    public int isAllDay;
    //todo 还有


    /**
     * 4个相关的DataBean
     */
    public EventDataBean eventDataBean;
    public LocationDataBean locationDataBean;
    public PersonDataBean personDataBean;
    public RecurrenceDataBean recurrenceDataBean;


    public EventManager(VEvent vEvent, Context ctx) {
        this.ctx = ctx;
        initData(vEvent);
    }

    private void initData(VEvent vEvent) {
        this.eventData = vEvent.toString();
        this.uid = vEvent.getUid().getValue();
        Created created = vEvent.getCreated();
        if (created != null) {
            this.createTime = created.getDate().getTime();
        }
        DtStamp dateStamp = vEvent.getDateStamp();
        if (dateStamp != null) {
            this.updateTime = dateStamp.getDateTime().getTime();
        }
        DtStart startDate = vEvent.getStartDate();
        if (startDate != null) {
            this.startTime = startDate.getDate().getTime();
        }
        DtEnd endDate = vEvent.getEndDate();
        if (endDate != null) {
            this.endTime = endDate.getDate().getTime();
        }
        Duration duration = vEvent.getDuration();
        if (duration != null) {
            this.duration = duration.getDuration();
        }
        Status status = vEvent.getStatus();
        if (status != null) {
            this.status = status.getValue();
        }
        Summary summary = vEvent.getSummary();
        if (summary != null) {
            this.title = summary.getValue();
        }
        Description description = vEvent.getDescription();
        if (description != null) {
            this.content = description.getValue();
        }

        this.location = vEvent.getLocation();
        this.attendee = (Attendee) vEvent.getProperty(Property.ATTENDEE);
        this.rrule = (RRule) vEvent.getProperty(Property.RRULE);

        eventDataBean = createEventDataBean();
        locationDataBean = createLocationDataBean();
        personDataBean = createPersonDataBean();
        recurrenceDataBean = createRecurrenceDataBean();
    }

    private EventDataBean createEventDataBean() {
        EventDataBean eventDataBean = new EventDataBean();
        eventDataBean.event_is_syn = isSyn;
        eventDataBean.event_flag = eventFlag;
        eventDataBean.event_ts = synTime;
        eventDataBean.event_sid = sid;
        eventDataBean.event_calendar_id = isSyn;
        eventDataBean.event_uuid = uid;
        eventDataBean.event_title = title;
        eventDataBean.event_note = content;
        eventDataBean.event_start_date = startTime;
        eventDataBean.event_end_date = endTime;
        eventDataBean.event_is_allday = isAllDay;
        eventDataBean.event_advance = advance;
        eventDataBean.event_url = url;
        eventDataBean.event_editable = editable;
        eventDataBean.event_create_ts = createTime;
        eventDataBean.event_update_ts = updateTime;
        eventDataBean.event_status = status;
        eventDataBean.event_iCal = eventData;

        return eventDataBean;
    }

    private RecurrenceDataBean createRecurrenceDataBean() {
        return new RecurrenceDataBean(rrule, startTime);
    }

    private PersonDataBean createPersonDataBean() {
        return null;
    }

    private LocationDataBean createLocationDataBean() {
        return null;
    }

    /**
     * 添加事件到本地
     *
     * @return
     */
    public boolean insertEventInLocal() {
        DBManager db = DBManager.open(ctx);
        /**事务操作*/
        db.beginTransaction();
        try {

            /**插入Event表, 返回eventId*/
            db.insertDataIntoEvent(eventDataBean);
            int eventId = db.getEventIdByUuid(eventDataBean.event_uuid);
            if (eventId==-1){
                throw new RuntimeException("插入event失败");
            }

            Log.e("-->", "插入Event表 数据:" + eventDataBean.toString());
            Log.e("-->", "插入Event表 返回eventId:" + eventId);

            /**插入Recurrence表*/
            recurrenceDataBean.recurrence_event_id = eventId;
            long pos = db.insertDataIntoRecurrence(recurrenceDataBean);

            Log.e("-->", "插入Recurrence表 数据:" + recurrenceDataBean.toString());
            Log.e("-->", "插入Recurrence表 返回位置:" + pos);

            /**插入Person表*/

            /**插入Location表*/


            db.setTransactionSuccessful();
        } catch (Exception e) {
            db.endTransaction();
            throw e;
        }
        db.endTransaction();
        db.close();


        return false;
    }

    /**
     * 修改事件在本地
     *
     * @return
     */
    public boolean updateEventInLocal() {

        return false;
    }

    /**
     * 删除事件在本地
     *
     * @return
     */
    public boolean deleteEventInLocal() {

        return false;
    }

    /**
     * 将此事件与远端同步
     *
     * @return
     */
    public boolean synEventWithRemote() {

        return false;
    }
}
