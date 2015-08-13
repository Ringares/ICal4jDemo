package demo.ringares.com.ical4jdemo.manager;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Summary;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

import demo.ringares.com.ical4jdemo.bean.EventDataBean;
import demo.ringares.com.ical4jdemo.bean.RecurrenceDataBean;
import demo.ringares.com.ical4jdemo.dbHelper.DBManager;
import demo.ringares.com.ical4jdemo.synbean.SynTongBuBean;

/**
 * Created by ls
 * on 2015/8/10
 * Description
 */
public class ICalEventManager {
    private static ICalEventManager instance;
    private Context ctx;

    public static ICalEventManager getInstance(Context ctx) {
        if (instance == null) {
            instance = new ICalEventManager(ctx.getApplicationContext());
        }
        return instance;
    }

    private ICalEventManager(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * 通过同步数据bean创建EventDataBean包含vEvent存在本地的所有信息
     *
     * @param synTongBuBean
     * @return
     */
    public EventDataBean createEventDataBean(SynTongBuBean synTongBuBean) {
        String content = synTongBuBean.content;
        EventDataBean eventDataBean = null;
        try {
            Calendar iCalendar = parseCalerdar(content);
            VEvent vEvent = (VEvent) iCalendar.getComponent(Component.VEVENT);

            /*************************************
             * EventDataBean对象
             * ***********************************/
            eventDataBean = new EventDataBean();
            /**event_is_syn*/
            /**event_flag*/
            /**event_ts*/
            /**event_sid*/
            /**event_calendar_id*/
            /**event_uuid*/
            eventDataBean.event_uuid = vEvent.getUid().getValue();
            /**event_title*/
            Summary summary = vEvent.getSummary();
            if (summary != null) {
                eventDataBean.event_title = summary.getValue();
            }
            /**event_note*/
            Description description = vEvent.getDescription();
            if (description != null) {
                eventDataBean.event_note = description.getValue();
            }
            /**event_start_date*/
            DtStart startDate = vEvent.getStartDate();
            if (startDate != null) {
                eventDataBean.event_start_date = startDate.getDate().getTime();
            }
            /**event_end_date*/
            DtEnd endDate = vEvent.getEndDate();
            if (endDate != null) {
                eventDataBean.event_end_date = endDate.getDate().getTime();
            }
            /**event_is_allday*/
            /**event_advance*/
            /**event_url*/
            /**event_editable*/
            /**event_create_ts*/
            Created created = vEvent.getCreated();
            if (created != null) {
                eventDataBean.event_create_ts = created.getDate().getTime();
            }
            /**event_update_ts*/
            DtStamp dateStamp = vEvent.getDateStamp();
            if (dateStamp != null) {
                eventDataBean.event_update_ts = dateStamp.getDateTime().getTime();
            }
            /**event_status*/
            Status status = vEvent.getStatus();
            if (status != null) {
                eventDataBean.event_status = status.getValue();
            }
            /**event_iCal*/
            eventDataBean.event_iCal = vEvent.toString();

//            Duration duration = vEvent.getDuration();
//            if (duration != null) {
//                this.duration = duration.getDuration();
//            }

            /*************************************
             * RecurrenceDataBean对象
             * ***********************************/
            RRule rrule = (RRule) vEvent.getProperty(Property.RRULE);
            eventDataBean.recurrenceDataBean = new RecurrenceDataBean(rrule, eventDataBean.event_start_date);

            /*************************************
             * LocationDataBean对象
             * ***********************************/
            Location location = vEvent.getLocation();

            /*************************************
             * PersonDataBean对象
             * ***********************************/
            Attendee attendee = (Attendee) vEvent.getProperty(Property.ATTENDEE);

        } catch (IOException | ParserException e) {
            e.printStackTrace();
        }
        return eventDataBean;
    }

    /**
     * 添加事件到本地
     *
     * @return
     */
    public boolean insertEventInLocal(EventDataBean eventDataBean) {
        DBManager db = DBManager.open(ctx);
        /**事务操作*/
        db.beginTransaction();
        try {

            /**插入Event表, 返回eventId*/
            db.insertDataIntoEvent(eventDataBean);
            int eventId = db.getEventIdByUuid(eventDataBean.event_uuid);
            if (eventId == -1) {
                throw new RuntimeException("插入event失败");
            } else {
                eventDataBean.setEventId(eventId);
            }

            Log.e("-->", "插入Event表 数据:" + eventDataBean.toString());
            Log.e("-->", "插入Event表 返回eventId:" + eventId);

            /**插入Recurrence表*/
            long pos = db.insertDataIntoRecurrence(eventDataBean.recurrenceDataBean);

            Log.e("-->", "插入Recurrence表 数据:" + eventDataBean.recurrenceDataBean.toString());
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
    public boolean updateEventInLocal(EventDataBean eventDataBean) {

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
     * 查找一月数据
     *
     * @param month
     */
    public void getEventsByMonth(String year, String month) {
        DBManager db = DBManager.open(ctx);
        Cursor cursor = db.getAllRuleFromRecurrenceByMonth(year, month);
        if (cursor != null) {
            while(cursor.moveToNext()){
                int eventId = cursor.getInt(0);
                String rule = cursor.getString(1);
                try {
                    Log.e("-->","====================================");
                    Log.e("-eventid->",eventId+"");
                    if (!TextUtils.isEmpty(rule)){
                        Recur recur = new Recur(rule);
                        Log.e("-rrule->",recur.toString());
                    }else{
                        Log.e("-rrule->","null");
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            cursor.close();
        }

        db.close();
    }

    public void modifyICal(String icalData) {
        Log.e("-->", "===Original data===");
        Log.e("-->", icalData);
        Log.e("-->", "=======");

        try {
            Calendar calendar = parseCalerdar(icalData);
            VEvent vEvent = (VEvent) calendar.getComponent(Component.VEVENT);
            /**修改标题*/
            Summary summary = vEvent.getSummary();
            summary.setValue("修改标题");
            /**修改内容*/
            Description description = vEvent.getDescription();
            description.setValue("修改内容");
            /**修改rrule*/
            RRule rRule = (RRule) vEvent.getProperty(Property.RRULE);
            rRule.setValue("FREQ=WEEKLY;BYDAY=1SU,2MO,3WE");

            Log.e("-->", "===Altered data===");
            Log.e("-->", calendar.toString());
            Log.e("-->", "=======");
        } catch (IOException | ParserException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析calendar字符串->iCalenda对象
     *
     * @param calendarString
     * @return
     * @throws IOException
     * @throws ParserException
     */
    public static net.fortuna.ical4j.model.Calendar parseCalerdar(String calendarString) throws IOException, ParserException {
        StringReader stringReader = new StringReader(calendarString);
        CalendarBuilder builder = new CalendarBuilder();
        return builder.build(stringReader);

    }
}
