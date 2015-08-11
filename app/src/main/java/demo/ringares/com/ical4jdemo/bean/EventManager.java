package demo.ringares.com.ical4jdemo.bean;

import android.content.Context;

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

import demo.ringares.com.ical4jdemo.dbHelper.DBManager;

/**
 * Created by ls
 * on 2015/8/10
 * Description
 *
 *
 "BEGIN:VEVENT\n" +
 "DTSTAMP:20150723T092650Z\n" +
 "DTSTART:20150806T080000\n" +
 "DTEND:20150806T090000\n" +
 "SUMMARY:RRuleEvent\n" +
 "UID:20150723T092653Z-iCal4j@fe80::7651:baff:fe6f:3e83%wlan0\n" +
 "RRULE:FREQ=WEEKLY;COUNT=4;INTERVAL=2\n" +
 "END:VEVENT\n"

 */
public class EventManager {
    private Context ctx;
    /**需要处理的iCal协议定义字段*/
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
    /**自定义的所需字段*/
    public int isSyn; //是否需要同步
    public long sid; //同步服务器id
    public long synTime; //同步时间戳
    public String eventFlag; //数据操作类型A:add,E:edit,D:delete
    public String advance; //提前x分钟提醒 eg.10,20,30
    //todo 还有


    /**4个相关的DataBean*/
    private final EventDataBean eventDataBean;
    private final LocationDataBean locationDataBean;
    private final PersonDataBean personDataBean;
    private final RecurrenceDataBean recurrenceDataBean;


    public EventManager(VEvent vEvent, Context ctx) {
        this.ctx =ctx;

        this.eventData = vEvent.toString();
        this.uid = vEvent.getUid().getValue();
        Created created = vEvent.getCreated();
        if (created!=null){
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
        this.attendee = (Attendee)vEvent.getProperty(Property.ATTENDEE);
        this.rrule = (RRule) vEvent.getProperty(Property.RRULE);

        eventDataBean = createEventDataBean();
        locationDataBean = createLocationDataBean();
        personDataBean = createPersonDataBean();
        recurrenceDataBean = createRecurrenceDataBean();
    }

    private RecurrenceDataBean createRecurrenceDataBean() {
        RecurrenceDataBean dataBean = new RecurrenceDataBean(rrule, startTime);
        return dataBean;
    }

    private PersonDataBean createPersonDataBean() {
        return null;
    }

    private LocationDataBean createLocationDataBean() {
        return null;
    }

    private EventDataBean createEventDataBean() {
        return null;
    }

    /**
     *添加事件到本地
     * @return
     */
    public boolean insertEventInLocal(){
        DBManager open = DBManager.open(ctx);
        /*todo 考虑加上事物操作**/

        /**插入Event表*/

        /**插入Recurrence表*/

        /**插入Person表*/

        /**插入Location表*/

        return false;
    }

    /**
     * 修改事件在本地
     * @return
     */
    public boolean updateEventInLocal(){

        return false;
    }

    /**
     * 删除事件在本地
     * @return
     */
    public boolean deleteEventInLocal(){

        return false;
    }

    /**
     * 将此事件与远端同步
     * @return
     */
    public boolean synEventWithRemote(){

        return false;
    }
}
