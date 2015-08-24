package demo.ringares.com.ical4jdemo.manager;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Value;
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
import java.util.ArrayList;

import demo.ringares.com.ical4jdemo.bean.EventDataBean;
import demo.ringares.com.ical4jdemo.bean.LocationDataBean;
import demo.ringares.com.ical4jdemo.bean.PersonDataBean;
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
                String statusValue = status.getValue();
                if ("TENTATIVE".equals(statusValue)) {
                    eventDataBean.event_status = EventDataBean.EVENT_STATUS_TENTATIVE;
                } else if ("CANCELLED".equals(statusValue)) {
                    eventDataBean.event_status = EventDataBean.EVENT_STATUS_CANCELLED;
                } else {
                    eventDataBean.event_status = EventDataBean.EVENT_STATUS_CONFIRMED;
                }
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
     * 同步事件到本地
     * @param eventDataBean
     * @return
     */
    public boolean syncEventInLocal(EventDataBean eventDataBean) {
        DBManager db = DBManager.open(ctx);
        boolean result = false;

        int eventId = db.getEventIdByUuid(eventDataBean.event_uuid);
        if (eventId == -1) { //新数据
            result = insertEventInLocal(eventDataBean);
        } else { //需要更新的数据
            db.beginTransaction();
            try {
                db.updateEventData(eventDataBean);
                db.updateRecurrenceData(eventDataBean.recurrenceDataBean, eventId);
                db.updateLocationData(eventDataBean.locationDataBean, eventId);
                db.updatePersonData(eventDataBean.personDataBeans, eventId);

                db.setTransactionSuccessful();
            } catch (Exception e) {
                throw new RuntimeException("插入同步数据失败");
            } finally {
                db.endTransaction();
            }
        }

        return result;
    }

    /**
     * 添加事件到本地
     *
     * @return
     */
    public boolean insertEventInLocal(EventDataBean eventDataBean) {
        DBManager db = DBManager.open(ctx);
        boolean result = false;
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

            result = true;
            db.setTransactionSuccessful();
        } catch (Exception e) {
            result = false;
            throw new RuntimeException("插入数据失败,回滚数据");
        } finally {
            db.endTransaction();
        }
        return result;
    }

    /**
     * 修改事件在本地
     *
     * @param eventDataBean
     * @return
     */
    public boolean updateEventInLocal(EventDataBean eventDataBean) {

        return false;
    }

    /**
     * 删除事件在本地
     *
     * @param eventId
     * @return
     */
    public boolean deleteEventInLocal(int eventId) {
        DBManager db = DBManager.open(ctx);
        boolean result = false;
        db.beginTransaction();
        try {
            db.deleteEventDataByEventId(eventId);
            db.deleteRecurrenceDataByEventId(eventId);
            db.deleteLocationDataByEventId(eventId);
            db.deletePersonDataByEventId(eventId);

            result = true;
            db.setTransactionSuccessful();
        } catch (Exception e) {
            result = false;
            throw new RuntimeException("删除数据失败,回滚数据");
        } finally {
            db.endTransaction();
        }

        return result;
    }

    /**
     * 查找一月数据
     *
     * @param month
     */
    public void getEventsByMonth(String year, String month) {
        DBManager db = DBManager.open(ctx);
        Period monthPeriod = getMonthPeriod(Integer.parseInt(year), Integer.parseInt(month));

        Cursor cursor = db.getAllRuleFromRecurrenceByMonth(year, month);
        Log.e("-->", "=================getEvents " + year + ", " + month + "===================");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int eventId = cursor.getInt(0);
                long start_ts = cursor.getLong(1);
                String rule = cursor.getString(2);
                try {

                    Log.e("-eventid->", eventId + "");
                    if (!TextUtils.isEmpty(rule)) {
                        Recur recur = new Recur(rule);
                        Log.e("-rrule->", recur.toString());

                        DateList dates = recur.getDates(new Date(start_ts), monthPeriod, Value.DATE);
                        for (Object o : dates) {
                            Date date = (Date) o;
                            Log.e("-发生日期->", date.toString());
                        }

                    } else {
                        Log.e("-rrule->", "null");
                        Log.e("-发生日期->", new Date(start_ts).toString());
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            cursor.close();
        }
        db.close();
    }

    /**
     * 根据eventId获取完整EventDataBean
     *
     * @param eventId
     * @return
     */
    public EventDataBean getEventDataBeanByEventId(int eventId) {
        EventDataBean eventDataBean = new EventDataBean();
        DBManager db = DBManager.open(ctx);
        Cursor cursor = db.getEventDataByEventId(eventId);
        if (cursor != null) {
            while (cursor.moveToFirst()) {
                eventDataBean.event_id = cursor.getInt(0);
                eventDataBean.event_is_syn = cursor.getInt(1);
                eventDataBean.event_flag = cursor.getInt(2);
                eventDataBean.event_ts = cursor.getLong(3);
                eventDataBean.event_sid = cursor.getLong(4);
                eventDataBean.event_calendar_id = cursor.getInt(5);
                eventDataBean.event_uuid = cursor.getString(6);
                eventDataBean.event_title = cursor.getString(7);
                eventDataBean.event_note = cursor.getString(8);
                eventDataBean.event_start_date = cursor.getLong(9);
                eventDataBean.event_end_date = cursor.getLong(10);
                eventDataBean.event_is_allday = cursor.getInt(11);
                eventDataBean.event_advance = cursor.getString(12);
                eventDataBean.event_url = cursor.getString(13);
                eventDataBean.event_editable = cursor.getInt(14);
                eventDataBean.event_create_ts = cursor.getLong(15);
                eventDataBean.event_update_ts = cursor.getLong(16);
                eventDataBean.event_status = cursor.getInt(17);
                eventDataBean.event_iCal = cursor.getString(18);
            }

            //根据event_id查recurrence, location, person表
            Cursor cursor_currence = db.getRecurranceDataByEventId(eventId);
            if (cursor_currence != null && cursor.moveToFirst()) {
                RecurrenceDataBean recurrenceDataBean = new RecurrenceDataBean();
                recurrenceDataBean.recurrence_id = cursor_currence.getInt(0);
                recurrenceDataBean.recurrence_event_id = cursor_currence.getInt(1);
                recurrenceDataBean.recurrence_frequency_type = cursor_currence.getInt(2);
                recurrenceDataBean.recurrence_interval = cursor_currence.getInt(3);
                recurrenceDataBean.recurrence_end_type = cursor_currence.getInt(4);
                recurrenceDataBean.recurrence_end_date = cursor_currence.getLong(5);
                recurrenceDataBean.recurrence_end_count = cursor_currence.getInt(6);
                recurrenceDataBean.recurrence_by_monthday = cursor_currence.getString(7);
                recurrenceDataBean.recurrence_by_month = cursor_currence.getString(8);
                recurrenceDataBean.recurrence_by_weekno = cursor_currence.getString(9);
                recurrenceDataBean.recurrence_by_yearday = cursor_currence.getString(10);
                recurrenceDataBean.recurrence_by_day = cursor_currence.getString(11);
                recurrenceDataBean.recurrence_positions = cursor_currence.getString(12);
                recurrenceDataBean.recurrence_week_start = cursor_currence.getInt(13);
                recurrenceDataBean.recurrence_start_date = cursor_currence.getLong(14);
                recurrenceDataBean.recurrence_syear = cursor_currence.getInt(15);
                recurrenceDataBean.recurrence_smonth = cursor_currence.getInt(16);
                recurrenceDataBean.recurrence_sday = cursor_currence.getInt(17);
                recurrenceDataBean.recurrence_rule = cursor_currence.getString(18);
                eventDataBean.recurrenceDataBean = recurrenceDataBean;
            }

            Cursor cursor_location = db.getLocationDataByEventId(eventId);
            if (cursor_location != null && cursor_location.moveToFirst()) {
                LocationDataBean locationDataBean = new LocationDataBean();
                locationDataBean.location_id = cursor_location.getInt(0);
                locationDataBean.location_event_id = cursor_location.getInt(1);
                locationDataBean.location_lat = cursor_location.getFloat(2);
                locationDataBean.location_lon = cursor_location.getFloat(3);
                locationDataBean.location_city = cursor_location.getString(4);
                locationDataBean.location_country = cursor_location.getString(5);
                locationDataBean.location_desc = cursor_location.getString(6);
                locationDataBean.location_url = cursor_location.getString(7);
                eventDataBean.locationDataBean = locationDataBean;
            }

            Cursor cursor_person = db.getPersonDataByEventId(eventId);
            if (cursor != null) {
                while (cursor_person.moveToFirst()) {
                    PersonDataBean personDataBean = new PersonDataBean();
                    personDataBean.person_id = cursor_person.getInt(0);
                    personDataBean.person_event_id = cursor_person.getInt(1);
                    personDataBean.person_type = cursor_person.getInt(2);
                    personDataBean.person_display_name = cursor_person.getString(3);
                    personDataBean.person_first_name = cursor_person.getString(4);
                    personDataBean.person_last_name = cursor_person.getString(5);
                    personDataBean.person_Email = cursor_person.getString(6);
                    personDataBean.person_phone = cursor_person.getString(7);
                    personDataBean.person_is_self = cursor_person.getInt(8);
                    personDataBean.person_avatar_url = cursor_person.getString(9);
                    personDataBean.person_role = cursor_person.getInt(10);
                    personDataBean.person_revp_status = cursor_person.getInt(11);
                    personDataBean.person_other_info = cursor_person.getString(12);

                    if (eventDataBean.personDataBeans == null) {
                        eventDataBean.personDataBeans = new ArrayList<>();
                    }
                    eventDataBean.personDataBeans.add(personDataBean);
                }
            }
        }

        return eventDataBean;
    }

    private Period getMonthPeriod(int year, int month) {
        java.util.Calendar calStart = java.util.Calendar.getInstance();
        calStart.set(year, month - 1, 1);

        java.util.Calendar calEnd = java.util.Calendar.getInstance();
        calEnd.set(year, month - 1, getOneMonthDays(year, month));

        DateTime dateTimeStart = new DateTime(calStart.getTime());
        DateTime dateTimeEnd = new DateTime(calEnd.getTime());

        return new Period(dateTimeStart, dateTimeEnd);
    }

    public int getOneMonthDays(int y, int m) {

        if (m == 1 || m == 3 || m == 5 || m == 7 || m == 8 || m == 10
                || m == 12) {
            return 31;
        } else if (m == 4 || m == 6 || m == 9 || m == 11) {
            return 30;
        } else {
            if ((y % 4 == 0 && y % 100 != 0) || (y % 400 == 0)) {
                return 29;
            } else {
                return 28;
            }
        }

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
