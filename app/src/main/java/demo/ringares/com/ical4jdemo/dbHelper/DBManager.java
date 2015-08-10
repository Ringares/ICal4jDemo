package demo.ringares.com.ical4jdemo.dbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import demo.ringares.com.ical4jdemo.bean.EventDataBean;
import demo.ringares.com.ical4jdemo.bean.LocationDataBean;
import demo.ringares.com.ical4jdemo.bean.PersonDataBean;
import demo.ringares.com.ical4jdemo.bean.RecurrenceDataBean;

/**
 * Created by ls
 * on 2015/8/7
 * Description
 */
public class DBManager {

    /***************************************************************
     * 表定义
     ***************************************************************/

    /**
     * 表名:connection  用户绑定第三方账号的连接信息
     */
    static class Connection {
        public static final String TableName = "connection";
        public static final String KEY_connection_id = "connection_id";//主键 本地id
        public static final String KEY_connection_sid = "connection_sid";// 服务器id
        public static final String KEY_connection_type = "connection_type";// 绑定类型,例如google、iCloud、facebook、local
        public static final String KEY_connection_status = "connection_status";// 绑定状态(0绑定失效,1绑定正常)
        public static final String KEY_connection_userinfo = "connection_userinfo";// 绑定的用户信息 JSON String
        public static final String[] columns = new String[]{
                KEY_connection_id, KEY_connection_sid, KEY_connection_type,
                KEY_connection_status, KEY_connection_userinfo};
        public static final String Create_table = "create table if not exists " +
                TableName + " (" +
                KEY_connection_id + " integer primary key autoincrement, " +
                KEY_connection_sid + " long not null, " +//todo
                KEY_connection_type + " integer not null," + //todo
                KEY_connection_status + " integer not null," +
                KEY_connection_userinfo + " text not null" +
                ");";
    }

    /**
     * 表名:calendar  对应之前数据的分组
     */
    static class Calendar {
        public static final String TableName = "calendar";
        public static final String KEY_calendar_Id = "calendar_id";//主键 本地id
        public static final String KEY_calendar_sid = "calendar_sid";// 服务器id
        public static final String KEY_calendar_connection_id = "calendar_connection_id";// 绑定连接的id,对应connection 表的id
        public static final String KEY_calendar_name = "calendar_name";// 日历名称
        public static final String KEY_calendar_color = "calendar_color";// 日历颜色
        public static final String KEY_calendar_desc = "calendar_desc";// 日历描述
        public static final String KEY_calendar_type = "calendar_type";// 日历类型：如订阅节假日、授权获取、用户添加等
        public static final String KEY_calendar_visible = "calendar_visible";// 是否显示日历数据(0不显示,1显示)
        public static final String KEY_calendar_remind = "calendar_remind";// 日历数据是否提醒(0不提醒,1提醒)
        public static final String KEY_calendar_access = "calendar_access";// 日历操作权限类型，只读、读写
        public static final String KEY_calendar_other_info = "calendar_other_info";// 扩展字段 JSON String
        public static final String[] columns = new String[]{
                KEY_calendar_Id, KEY_calendar_sid, KEY_calendar_connection_id,
                KEY_calendar_name, KEY_calendar_color, KEY_calendar_desc,
                KEY_calendar_type, KEY_calendar_visible, KEY_calendar_remind,
                KEY_calendar_access, KEY_calendar_other_info};
        public static final String Create_table = "create table if not exists " +
                TableName + " (" +
                KEY_calendar_Id + " integer primary key autoincrement, " +
                KEY_calendar_sid + " long not null, " +
                KEY_calendar_connection_id + " integer not null," + //todo
                KEY_calendar_name + " text not null," +
                KEY_calendar_color + " text not null," +
                KEY_calendar_desc + " text not null," +
                KEY_calendar_type + " long not null," +
                KEY_calendar_visible + " integer not null," +
                KEY_calendar_remind + " integer not null," +
                KEY_calendar_access + " integer not null," + //todo
                KEY_calendar_other_info + " text not null" +
                ");";
    }

    /**
     * 表名:event  对应之前basetable
     */
    static class Event {
        public static final String TableName = "event";
        public static final String KEY_event_id = "event_id";//主键 本地id
        public static final String KEY_event_is_syn = "event_is_syn";// 是否需要同步
        public static final String KEY_event_flag = "event_flag";// 数据操作类型A:add,E:edit,D:delete
        public static final String KEY_event_ts = "event_ts";// 同步的时间戳 服务器返回的
        public static final String KEY_event_sid = "event_sid";// 服务器id
        public static final String KEY_event_calendar_id = "event_calendar_id";// calendar id,对应calenadr 表的local id
        public static final String KEY_event_uuid = "event_uuid";// 活动的唯一标识符
        public static final String KEY_event_title = "event_title";// 活动标题
        public static final String KEY_event_note = "event_note";// 活动备注
        public static final String KEY_event_start_date = "event_start_date";// 活动开始时间戳 毫秒
        public static final String KEY_event_end_date = "event_end_date";// 活动结束时间戳
        public static final String KEY_event_is_allday = "event_is_allday";// 活动是否是全天(0不是,1是)
        public static final String KEY_event_rrule = "event_rrule";// 重复方式表达式,空表示不重复，用的时候需要解析
        public static final String KEY_event_rdate = "event_rdate";// 事件的循环日期。通常可以和RRULE一起使用来定义一个重复发生的总集合。
        public static final String KEY_event_advance = "event_advance";// 提前提醒时间，单位是分钟。以,为间隔支持多个例如0,5,60,120
        public static final String KEY_event_url = "event_url";// 活动链接url
        public static final String KEY_event_editable = "event_editable";// 活动是否可以编辑，(0不可以,1可以)
        public static final String KEY_event_create_ts = "event_create_ts";// 活动创建时间戳
        public static final String KEY_event_update_ts = "event_update_ts";// 活动编辑更新时间戳
        public static final String KEY_event_status = "event_status";// 活动的状态 默认conformed
        public static final String KEY_event_iCal = "event_iCal";// ical 原文，每次修改要update对应支持修改的字段
        public static final String[] columns = new String[]{
                KEY_event_id, KEY_event_is_syn, KEY_event_flag,
                KEY_event_ts, KEY_event_sid, KEY_event_calendar_id,
                KEY_event_uuid, KEY_event_title, KEY_event_note,
                KEY_event_start_date, KEY_event_end_date, KEY_event_is_allday,
                KEY_event_rrule, KEY_event_rdate, KEY_event_advance,
                KEY_event_url, KEY_event_editable, KEY_event_create_ts,
                KEY_event_update_ts, KEY_event_status, KEY_event_iCal};
        public static final String Create_table = "create table if not exists " +
                TableName + " (" +
                KEY_event_id + " integer primary key autoincrement, " +
                KEY_event_is_syn + " integer not null, " +
                KEY_event_flag + " text not null," +
                KEY_event_ts + " long not null," +
                KEY_event_sid + " long not null," +
                KEY_event_calendar_id + " integer not null," +
                KEY_event_uuid + " text not null," +
                KEY_event_title + " text not null," +
                KEY_event_note + " text not null," +
                KEY_event_start_date + " long not null," +
                KEY_event_end_date + " long not null," +
                KEY_event_is_allday + " integer not null," +
                KEY_event_rrule + " text not null," +
                KEY_event_rdate + " text not null," +
                KEY_event_advance + " text not null," +
                KEY_event_url + " text not null," +
                KEY_event_editable + " integer not null," +
                KEY_event_create_ts + " long not null," +
                KEY_event_update_ts + " long not null," +
                KEY_event_status + " text not null," +
                KEY_event_iCal + " text not null" +
                ");";
    }

    /**
     * 表名:recurrence 重复方式表
     */
    static class Recurrence {
        public static final String TableName = "recurrence";
        public static final String KEY_recurrence_id = "recurrence_id";//主键 本地id
        public static final String KEY_recurrence_event_id = "recurrence_event_id";// 对应event_id
        public static final String KEY_recurrence_frequency_type = "recurrence_frequency_type";// 重复频率类型
        public static final String KEY_recurrence_interval = "recurrence_interval";// 重复间隔
        public static final String KEY_recurrence_end_type = "recurrence_end_type";// 重复截止类型
        public static final String KEY_recurrence_end_date = "recurrence_end_date";// 重复截至时间戳
        public static final String KEY_recurrence_end_count = "recurrence_end_count";// 重复截至次数
        public static final String KEY_recurrence_by_monthday = "recurrence_by_monthday";// 一个月中的哪几天
        public static final String KEY_recurrence_by_month = "recurrence_by_month";// 一年中的哪几个月
        public static final String KEY_recurrence_by_weekno = "recurrence_by_weekno";// 一年中的哪几周
        public static final String KEY_recurrence_by_yearday = "recurrence_by_yearday";// 一年中的哪几天
        public static final String KEY_recurrence_by_day = "recurrence_by_day";// 一周中的哪几天
        public static final String KEY_recurrence_positions = "recurrence_positions";// 位置集合，分情况使用，是否计算，暂定
        public static final String KEY_recurrence_week_start = "recurrence_week_start";// 周首日 1 SUN 2Mon
        public static final String KEY_recurrence_start_date = "recurrence_start_date";// event 开始日期时间戳
        public static final String KEY_recurrence_syear = "recurrence_syear";// event 开始日期年
        public static final String KEY_recurrence_smonth = "recurrence_smonth";// event 开始日期月
        public static final String KEY_recurrence_sday = "recurrence_sday";// event 开始日期日
        public static final String[] columns = new String[]{
                KEY_recurrence_id, KEY_recurrence_event_id, KEY_recurrence_frequency_type,
                KEY_recurrence_interval, KEY_recurrence_end_type, KEY_recurrence_end_date,
                KEY_recurrence_end_count, KEY_recurrence_by_monthday, KEY_recurrence_by_month,
                KEY_recurrence_by_weekno, KEY_recurrence_by_yearday, KEY_recurrence_by_day,
                KEY_recurrence_positions, KEY_recurrence_week_start, KEY_recurrence_start_date,
                KEY_recurrence_syear, KEY_recurrence_smonth, KEY_recurrence_sday};
        public static final String Create_table = "create table if not exists " +
                TableName + " (" +
                KEY_recurrence_id + " integer primary key autoincrement, " +
                KEY_recurrence_event_id + " integer not null, " +
                KEY_recurrence_frequency_type + " integer not null," +
                KEY_recurrence_interval + " integer not null," +
                KEY_recurrence_end_type + " integer not null," +
                KEY_recurrence_end_date + " long not null," +
                KEY_recurrence_end_count + " integer not null," +
                KEY_recurrence_by_monthday + " text not null," +
                KEY_recurrence_by_month + " text not null," +
                KEY_recurrence_by_weekno + " text not null," +
                KEY_recurrence_by_yearday + " text not null," +
                KEY_recurrence_by_day + " text not null," +
                KEY_recurrence_positions + " text not null," +
                KEY_recurrence_week_start + " integer not null," +
                KEY_recurrence_start_date + " long not null," +
                KEY_recurrence_syear + " integer not null," +
                KEY_recurrence_smonth + " integer not null," +
                KEY_recurrence_sday + " integer not null" +
                ");";
    }

    /**
     * 表名:location 活动地址表
     */
    static class Location {
        public static final String TableName = "location";
        public static final String KEY_location_id = "location_id";//主键 本地id
        public static final String KEY_location_event_id = "location_event_id";// 对应event_id
        public static final String KEY_location_lat = "location_lat";// 地址纬度
        public static final String KEY_location_lon = "location_lon";// 地址经度
        public static final String KEY_location_city = "location_city";// 地址城市
        public static final String KEY_location_country = "location_country";// 地址国家
        public static final String KEY_location_desc = "location_desc";// 地址整体详细描述
        public static final String[] columns = new String[]{
                KEY_location_id, KEY_location_event_id, KEY_location_lat,
                KEY_location_lon, KEY_location_city, KEY_location_country,
                KEY_location_country, KEY_location_desc};
        public static final String Create_table = "create table if not exists " +
                TableName + " (" +
                KEY_location_id + " integer primary key autoincrement, " +
                KEY_location_event_id + " integer not null, " +
                KEY_location_lat + " real not null," +
                KEY_location_lon + " real not null," +
                KEY_location_city + " text not null," +
                KEY_location_country + " text not null," +
                KEY_location_desc + " text not null" +
                ");";
    }

    /**
     * person 邀请联系人表
     */
    static class Person {
        public static final String TableName = "person";
        public static final String KEY_person_id = "person_id";//主键 本地id
        public static final String KEY_person_event_id = "person_event_id";// 对应event_id
        public static final String KEY_person_type = "person_type";// 联系人类型，例如手机联系人，邮箱联系人，社区好友//todo
        public static final String KEY_person_display_name = "person_display_name";// 联系人显示名字
        public static final String KEY_person_first_name = "person_first_name";// 联系人名字
        public static final String KEY_person_last_name = "person_last_name";// 联系人姓氏
        public static final String KEY_person_Email = "person_Email";// 联系人邮箱
        public static final String KEY_person_phone = "person_phone";// 联系人手机
        public static final String KEY_person_is_self = "person_is_self";// 联系人是否是自己 0 不是 1是
        public static final String KEY_person_avatar_url = "person_avatar_url";// 头像url
        public static final String KEY_person_other_info = "person_other_info";// 联系人其他信息扩展 JSON String
        public static final String[] columns = new String[]{
                KEY_person_id, KEY_person_event_id, KEY_person_type,
                KEY_person_display_name, KEY_person_first_name, KEY_person_last_name,
                KEY_person_Email, KEY_person_phone, KEY_person_is_self,
                KEY_person_is_self, KEY_person_avatar_url, KEY_person_other_info};
        public static final String Create_table = "create table if not exists " +
                TableName + " (" +
                KEY_person_id + " integer primary key autoincrement, " +
                KEY_person_event_id + " integer not null, " +
                KEY_person_type + " integer not null," + //todo
                KEY_person_display_name + " text not null," +
                KEY_person_first_name + " text not null," +
                KEY_person_last_name + " text not null," +
                KEY_person_Email + " text not null," +
                KEY_person_phone + " text not null," +
                KEY_person_is_self + " integer not null," +
                KEY_person_avatar_url + " text not null," +
                KEY_person_other_info + " text not null" +
                ");";
    }


    /**
     * ************************************************************
     * 数据库操作
     * *************************************************************
     */

    private static DatabaseHelper mDbHelper = null;
    private static SQLiteDatabase mDb = null;
    private static DBManager dBManagerInstance = null;
    private static final String DATABASE_NAME = "etouch.db";
    private static final int DATABASE_VERSION = 1;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {

        private Context mDBContext;

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mDBContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Calendar.Create_table);
            db.execSQL(Event.Create_table);
            db.execSQL(Recurrence.Create_table);
            db.execSQL(Location.Create_table);
            db.execSQL(Person.Create_table);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    private DBManager(Context ctx) {
        this.mCtx = ctx;
        mDbHelper = new DatabaseHelper(mCtx);
    }

    public static DBManager open(Context ctx) throws SQLException {
        if (dBManagerInstance != null) {
            if (mDb == null) {
                mDb = mDbHelper.getWritableDatabase();
            }
        } else {
            dBManagerInstance = new DBManager(ctx.getApplicationContext());
            mDb = mDbHelper.getWritableDatabase();
        }
        return dBManagerInstance;
    }

    public void testSQL() {
        Log.e("==>", Calendar.Create_table);
        Log.e("==>", Connection.Create_table);
        Log.e("==>", Event.Create_table);
        Log.e("==>", Recurrence.Create_table);
        Log.e("==>", Location.Create_table);
        Log.e("==>", Person.Create_table);
    }

    public long insertDataIntoEvent(EventDataBean bean) {
        ContentValues cv = new ContentValues();
        cv.put(Event.KEY_event_is_syn, bean.event_is_syn);
        cv.put(Event.KEY_event_flag, bean.event_flag);
        cv.put(Event.KEY_event_ts, bean.event_ts);
        cv.put(Event.KEY_event_sid, bean.event_sid);
        cv.put(Event.KEY_event_calendar_id, bean.event_calendar_id);
        cv.put(Event.KEY_event_uuid, bean.event_uuid);
        cv.put(Event.KEY_event_title, bean.event_title);
        cv.put(Event.KEY_event_note, bean.event_note);
        cv.put(Event.KEY_event_start_date, bean.event_start_date);
        cv.put(Event.KEY_event_end_date, bean.event_end_date);
        cv.put(Event.KEY_event_is_allday, bean.event_is_allday);
        cv.put(Event.KEY_event_rrule, bean.event_rrule);
        cv.put(Event.KEY_event_rdate, bean.event_rdate);
        cv.put(Event.KEY_event_advance, bean.event_advance);
        cv.put(Event.KEY_event_url, bean.event_url);
        cv.put(Event.KEY_event_editable, bean.event_editable);
        cv.put(Event.KEY_event_create_ts, bean.event_create_ts);
        cv.put(Event.KEY_event_update_ts, bean.event_update_ts);
        cv.put(Event.KEY_event_status, bean.event_status);
        cv.put(Event.KEY_event_iCal, bean.event_iCal);

        return mDb.insert(Event.TableName, null, cv);
    }

    public long insertDataIntoRecurrence(RecurrenceDataBean bean) {
        ContentValues cv = new ContentValues();
        cv.put(Recurrence.KEY_recurrence_event_id, bean.recurrence_event_id);
        cv.put(Recurrence.KEY_recurrence_frequency_type, bean.recurrence_frequency_type);
        cv.put(Recurrence.KEY_recurrence_interval, bean.recurrence_interval);
        cv.put(Recurrence.KEY_recurrence_end_type, bean.recurrence_end_type);
        cv.put(Recurrence.KEY_recurrence_end_date, bean.recurrence_end_date);
        cv.put(Recurrence.KEY_recurrence_end_count, bean.recurrence_end_count);
        cv.put(Recurrence.KEY_recurrence_by_monthday, bean.recurrence_by_monthday);
        cv.put(Recurrence.KEY_recurrence_by_month, bean.recurrence_by_month);
        cv.put(Recurrence.KEY_recurrence_by_weekno, bean.recurrence_by_weekno);
        cv.put(Recurrence.KEY_recurrence_by_yearday, bean.recurrence_by_yearday);
        cv.put(Recurrence.KEY_recurrence_by_day, bean.recurrence_by_day);
        cv.put(Recurrence.KEY_recurrence_positions, bean.recurrence_positions);
        cv.put(Recurrence.KEY_recurrence_week_start, bean.recurrence_week_start);
        cv.put(Recurrence.KEY_recurrence_start_date, bean.recurrence_start_date);
        cv.put(Recurrence.KEY_recurrence_syear, bean.recurrence_syear);
        cv.put(Recurrence.KEY_recurrence_smonth, bean.recurrence_smonth);
        cv.put(Recurrence.KEY_recurrence_sday, bean.recurrence_start_date);

        return mDb.insert(Recurrence.TableName, null, cv);
    }

    public long insertDataIntoLocation(LocationDataBean bean) {
        ContentValues cv = new ContentValues();
        cv.put(Location.KEY_location_event_id, bean.location_event_id);
        cv.put(Location.KEY_location_lat, bean.location_lat);
        cv.put(Location.KEY_location_city, bean.location_city);
        cv.put(Location.KEY_location_country, bean.location_country);
        cv.put(Location.KEY_location_desc, bean.location_desc);

        return mDb.insert(Location.TableName, null, cv);
    }

    public long insertDataIntoPerson(PersonDataBean bean) {
        ContentValues cv = new ContentValues();
        cv.put(Person.KEY_person_event_id, bean.person_event_id);
        cv.put(Person.KEY_person_type, bean.person_type);
        cv.put(Person.KEY_person_display_name, bean.person_display_name);
        cv.put(Person.KEY_person_first_name, bean.person_first_name);
        cv.put(Person.KEY_person_last_name, bean.person_last_name);
        cv.put(Person.KEY_person_Email, bean.person_Email);
        cv.put(Person.KEY_person_phone, bean.person_phone);
        cv.put(Person.KEY_person_is_self, bean.person_is_self);
        cv.put(Person.KEY_person_avatar_url, bean.person_avatar_url);
        cv.put(Person.KEY_person_other_info, bean.person_other_info);

        return mDb.insert(Person.TableName, null, cv);
    }


}
