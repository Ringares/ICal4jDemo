package demo.ringares.com.ical4jdemo.bean;

import android.content.Context;

import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.RRule;

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
public class EventModel {
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


    public EventModel(VEvent vEvent, Context ctx) {
        this.ctx =ctx;

        this.eventData = vEvent.toString();
        this.uid = vEvent.getUid().getValue();
        this.createTime = vEvent.getCreated().getDate().getTime();
        this.updateTime = vEvent.getDateStamp().getDateTime().getTime();
        this.startTime = vEvent.getStartDate().getDate().getTime();
        this.endTime = vEvent.getEndDate().getDate().getTime();
        this.duration = vEvent.getDuration().getDuration();
        this.status = vEvent.getStatus().getValue();

        this.title = vEvent.getSummary().getValue();
        this.content =vEvent.getDescription().getValue();

        this.location = vEvent.getLocation();
        this.attendee = (Attendee)vEvent.getProperty(Property.ATTENDEE);

        this.rrule = (RRule) vEvent.getProperty(Property.RRULE);
    }

    /**
     *添加事件到本地
     * @return
     */
    public boolean insertEventInLocal(){
        DBManager open = DBManager.open(ctx);

        /**插入Event表*/
        EventDataBean dataBean = new EventDataBean();
        //dataBean.event_id
        dataBean.event_is_syn = this.isSyn;
        dataBean.event_flag = this.eventFlag;
        //todo...


        //open.insertDataIntoEvent(dataBean);

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
