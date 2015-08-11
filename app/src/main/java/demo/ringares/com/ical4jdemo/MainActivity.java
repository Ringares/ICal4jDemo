package demo.ringares.com.ical4jdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.google.ical.compat.jodatime.LocalDateIteratorFactory;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.NumberList;
import net.fortuna.ical4j.model.ParameterFactoryImpl;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.model.WeekDayList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Encoding;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Attach;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;

import org.joda.time.LocalDate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import demo.ringares.com.ical4jdemo.manager.EventManager;
import demo.ringares.com.ical4jdemo.dbHelper.DBManager;


public class MainActivity extends ActionBarActivity {

    final static String ICAL_DATA = "BEGIN:VCALENDAR\n" +
            "PRODID:-//Ben Fortuna//iCal4j 1.0//EN\n" +
            "VERSION:2.0\n" +
            "CALSCALE:GREGORIAN\n" +
            "BEGIN:VEVENT\n" +
            "DTSTAMP:20150723T092650Z\n" +
            "DTSTART:20150806T080000\n" +
            "DTEND:20150806T090000\n" +
            "SUMMARY:RRuleEvent\n" +
            "UID:20150723T092653Z-iCal4j@fe80::7651:baff:fe6f:3e83%wlan0\n" +
            "RRULE:FREQ=WEEKLY;COUNT=4;INTERVAL=2\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /**测试
         * iCal4j生成uid需要联网获取主机地址,需要开子线程
         * 可以考虑别的方式...未定*/
        new Thread() {
            @Override
            public void run() {
                try {
                    /**创建*/
                    createEvent();
                    createOneDayEvent();
                    createRDateEvent();
                    createRRuleEvent();
                    //createEventWithAttachedBinary();

                    /**解析*/
                    parseRRule();

                    /**修改*/
                    updateCalendar();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.run();
            }
        }.start();


        /**解析*/

        try {
            net.fortuna.ical4j.model.Calendar calendar = parseCalerdar(ICAL_DATA);
            //获取vEvent
            VEvent vEvent = (VEvent) calendar.getComponents().getComponent(Component.VEVENT);
            EventManager eventModel = new EventManager(vEvent, this.getApplicationContext());

            eventModel.insertEventInLocal();








        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserException e) {
            e.printStackTrace();
        }



        /**数据库测试*/
        DBManager open = DBManager.open(this);
        open.testSQL();

    }

    private void updateCalendar() throws IOException, ParserException {
        net.fortuna.ical4j.model.Calendar calendar = parseCalerdar(ICAL_DATA);
        VEvent vEvent = (VEvent) calendar.getComponents().getComponent(Component.VEVENT);
        calendar.getComponents().add(vEvent);

    }

    /**
     * 解析iCal字符串
     *
     * @throws IOException
     * @throws ParserException
     */
    private void parseRRule() throws IOException, ParserException {

        net.fortuna.ical4j.model.Calendar calendar = parseCalerdar(ICAL_DATA);
        //获取vEvent
        VEvent vEvent = (VEvent) calendar.getComponents().getComponent(Component.VEVENT);
        String uid = vEvent.getUid().getValue();

        //
        DtStamp dateStamp = vEvent.getDateStamp();
        DtStart startDate = vEvent.getStartDate();
        DtEnd endDate = vEvent.getEndDate();

        //
        Location location = vEvent.getLocation();

        //RRule
        RRule rRule = (RRule) vEvent.getProperty(Property.RRULE);
        Recur recur = rRule.getRecur();
        String freq = recur.getFrequency();
        String wkst = recur.getWeekStartDay();
        int count = recur.getCount();
        int interval = recur.getInterval();
        NumberList monthList = recur.getMonthList();
        WeekDayList dayList = recur.getDayList();
        NumberList monthDayList = recur.getMonthDayList();
        NumberList yearDayList = recur.getYearDayList();
        NumberList posList = recur.getSetPosList();
        Date untilDate = recur.getUntil();

        Log.e("-->", "===Parse Data===");
        Log.e("--uid-->", uid);
        Log.e("--rRule-->", rRule.getValue());
        Log.e("--FREQ-->", recur.getFrequency());
        Log.e("-->", "=======");


        /**开始解析RRule*/
        String rrule = rRule.getValue();
        EventRecurrence eventRecurrence = new EventRecurrence();
        eventRecurrence.parse(rrule);
        RecurrenceModel recurrenceModel = RecurrenceModel.copyEventRecurrenceToModel(eventRecurrence);
        System.out.println("--###--RRule : " + rrule);
        System.out.println("--###--Rmodel : " + recurrenceModel);


        /** ***********************************************************************************
         * iCal4j 过滤event
         * 返回List<vEvent>
         * ************************************************************************************/
        Calendar today = Calendar.getInstance();
        today.set(Calendar.DAY_OF_MONTH,7);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.clear(java.util.Calendar.MINUTE);
        today.clear(java.util.Calendar.SECOND);

        // create a period starting now with a duration of one (1) day..
        Period period = new Period(new DateTime(today.getTime()), new Dur(100, 0, 0, 0));
        Filter filter = new Filter(new PeriodRule(period));
        List eventsToday = (List) filter.filter(calendar.getComponents(Component.VEVENT));

        System.out.println("=================\n打印从" + today.getTime() + "开始" + period.toString() + "中过滤出来的event");
        for (Object o : eventsToday) {
            if (o instanceof VEvent) {
                VEvent ve = (VEvent) o;
                System.out.println(ve.toString());
            }
        }
        System.out.println("=================");

        ComponentList components = calendar.getComponents(Component.VEVENT);
        for (Object component : components) {
            if (component instanceof VEvent) {
                RecurrenceId recurrenceId = ((VEvent) component).getRecurrenceId();
                PeriodList periodList = ((VEvent) component).calculateRecurrenceSet(period);
                System.out.println("========打印RecurrenceSet=========");
                for (Object o : periodList) {
                    System.out.println(o.toString());
                }

            }
        }


        /** ************************************************************************************
         * 用rfc2445包来解析的方法
         * Print out each date in the series.
         * *************************************************************************************/
        LocalDate localDate = LocalDate.now();
        String ical = "RRULE:FREQ=MONTHLY"
                + ";BYDAY=FR"  // every Friday
                + ";BYMONTHDAY=13"  // that occurs on the 13th of the month
                + ";COUNT=13";  // stop after 13 occurences
        try {
            for (LocalDate date :
                    LocalDateIteratorFactory.createLocalDateIterable(ical, localDate, true)) {
                System.out.println(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    /**
     * 添加二进制数据
     *
     * @throws ParseException
     * @throws SocketException
     * @throws ValidationException
     */
    private void createEventWithAttachedBinary() throws ParseException, SocketException, ValidationException {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        byte[] bytes = bitmap2Bytes(bitmap);

        net.fortuna.ical4j.model.Calendar calendar = new net.fortuna.ical4j.model.Calendar();
        calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        //周期列表,用于添加多个时间段
        PeriodList periodList = new PeriodList();

        DateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        DateTime startDate1 = new DateTime(format.parse(("11/09/2009 08:00")));
        DateTime startDate2 = new DateTime(format.parse(("11/10/2009 09:00")));
        DateTime endDate1 = new DateTime(format.parse(("11/09/2009 09:00")));
        DateTime endDate2 = new DateTime(format.parse(("11/10/2009 11:00")));
        periodList.add(new Period(startDate1, endDate1));
        periodList.add(new Period(startDate2, endDate2));

        VEvent event = new VEvent(startDate1, endDate1, "RDateEvent");
        event.getProperties().add(new UidGenerator("iCal4j").generateUid());

        //添加二进制数据
        ParameterList params = new ParameterList();
        params.add(Value.BINARY);
        params.add(Encoding.BASE64);
        Attach attach = new Attach(params, bytes);

        event.getProperties().add(attach);
        calendar.getComponents().add(event);

        // 验证
        calendar.validate();
        Log.e("-->", "===EventWithAttachedBinary===");
        Log.e("-->", calendar.toString());
        Log.e("-->", "=======");


    }

    private void createRDateEvent() throws ParseException, SocketException, URISyntaxException, ValidationException {
        net.fortuna.ical4j.model.Calendar calendar = new net.fortuna.ical4j.model.Calendar();
        calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        //周期列表,用于添加多个时间段
        PeriodList periodList = new PeriodList();

        DateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        DateTime startDate1 = new DateTime(format.parse(("11/09/2009 08:00")));
        DateTime startDate2 = new DateTime(format.parse(("11/10/2009 09:00")));
        DateTime endDate1 = new DateTime(format.parse(("11/09/2009 09:00")));
        DateTime endDate2 = new DateTime(format.parse(("11/10/2009 11:00")));
        periodList.add(new Period(startDate1, endDate1));
        periodList.add(new Period(startDate2, endDate2));

        VEvent event = new VEvent(startDate1, endDate1, "RDateEvent");
        event.getProperties().add(new UidGenerator("iCal4j").generateUid());

        //添加RDate
        ParameterList paraList = new ParameterList();
        paraList.add(ParameterFactoryImpl.getInstance().createParameter(
                Value.PERIOD.getName(), Value.PERIOD.getValue()));
        RDate rdate = new RDate(paraList, periodList);
        event.getProperties().add(rdate);
        calendar.getComponents().add(event);

        // 验证
        calendar.validate();
        Log.e("-->", "===RDateEvent===");
        Log.e("-->", calendar.toString());
        Log.e("-->", "=======");
    }

    private void createRRuleEvent() throws ParseException, SocketException, ValidationException {
        net.fortuna.ical4j.model.Calendar calendar = new net.fortuna.ical4j.model.Calendar();
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        DateTime start = new DateTime(format.parse("11/09/2009 08:00").getTime());
        DateTime end = new DateTime(format.parse("11/09/2009 09:00").getTime());
        calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        VEvent event = new VEvent(start, end, "RRuleEvent");
        event.getProperties().add(new UidGenerator("iCal4j").generateUid());

        //添加RRule
        //间隔一周,持续4次
        Recur recur = new Recur(Recur.WEEKLY, 4);
        recur.setInterval(2);
        WeekDayList dayList = recur.getDayList();
        dayList.add(new WeekDay("FR"));
        dayList.add(new WeekDay("1SU"));
        RRule rule = new RRule(recur);
        event.getProperties().add(rule);

        calendar.getComponents().add(event);

        // 验证
        calendar.validate();
        Log.e("-->", "===RRuleEvent===");
        Log.e("-->", calendar.toString());
        Log.e("-->", "=======");
    }

    /**
     * 创建一般Event
     * BEGIN:VCALENDAR
     * PRODID:-//Events Calendar//iCal4j 1.0//EN
     * CALSCALE:GREGORIAN
     * BEGIN:VEVENT
     * DTSTAMP:20150805T063154Z
     * DTSTART:20150723T070000
     * DTEND:20150724T070000
     * SUMMARY:Test Event
     * DESCRIPTION:event description.
     * LOCATION:Beijing
     * TZID:Europe/London
     * UID:20150805T063154Z-iCal4j@fe80::7651:baff:fe6f:3e83%wlan0
     * ATTENDEE;ROLE=REQ-PARTICIPANT;CN=Developer 1:mailto:dev1@mycompany.com
     * ATTENDEE;ROLE=OPT-PARTICIPANT;CN=Developer 2:mailto:dev2@mycompany.com
     * END:VEVENT
     * END:VCALENDAR
     */
    private void createEvent() {
        //创建时区
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        TimeZone timeZone = registry.getTimeZone("Etc/GMT");
        VTimeZone vTimeZone = timeZone.getVTimeZone();

        //设置起始时间
        Calendar startDate = new GregorianCalendar();
        startDate.setTimeZone(timeZone);
        //Sets the year, month, day of the month, hour of day, minute, and second fields
        startDate.set(2015, 6, 23, 0, 0, 0);//2015-07-23 00:00:00 //月份0-11

        //设置结束时间
        Calendar endDate = new GregorianCalendar();
        endDate.setTimeZone(timeZone);
        endDate.set(2015, 6, 24, 0, 0, 0);

        //创建事件
        String eventName = "Test Event";
        DateTime start = new DateTime(startDate.getTime());
        DateTime end = new DateTime(endDate.getTime());
        VEvent event = new VEvent(start, end, eventName);

        //事件描述
        event.getProperties().add(new Description("event description."));

        //事件地址
        event.getProperties().add(new Location("Beijing"));

        //添加时区信息
        event.getProperties().add(vTimeZone.getTimeZoneId());

        //生成唯一标示符UID
        try {
            UidGenerator generator = new UidGenerator("iCal4j");
            Uid uid = generator.generateUid();
            event.getProperties().add(uid);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        //添加参加者
        Attendee dev1 = new Attendee(URI.create("mailto:dev1@mycompany.com"));
        dev1.getParameters().add(Role.REQ_PARTICIPANT);
        dev1.getParameters().add(new Cn("Developer 1"));
        event.getProperties().add(dev1);

        Attendee dev2 = new Attendee(URI.create("mailto:dev2@mycompany.com"));
        dev2.getParameters().add(Role.OPT_PARTICIPANT);
        dev2.getParameters().add(new Cn("Developer 2"));
        event.getProperties().add(dev2);


        //创建日历
        net.fortuna.ical4j.model.Calendar calendar = new net.fortuna.ical4j.model.Calendar();
        calendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
        calendar.getProperties().add(CalScale.GREGORIAN);

        //添加事件
        calendar.getComponents().add(event);
        Log.e("-->", "===Event===");
        Log.e("-->", calendar.toString());
        Log.e("-->", "=======");
    }

    /**
     * 创建全天Event
     * BEGIN:VCALENDAR
     * PRODID:-//Events Calendar//iCal4j 1.0//EN
     * CALSCALE:GREGORIAN
     * BEGIN:VEVENT
     * DTSTAMP:20150723T071227Z
     * DTSTART;VALUE=DATE:20150722
     * SUMMARY:Test Event
     * TZID:Europe/London
     * UID:20150723T071228Z-iCal4j@fe80::7651:baff:fe6f:3e83%wlan0
     * END:VEVENT
     * END:VCALENDAR
     */
    private void createOneDayEvent() {
        //创建时区
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        TimeZone timeZone = registry.getTimeZone("Etc/GMT");
        VTimeZone vTimeZone = timeZone.getVTimeZone();

        //设置一天时间
        Calendar startDate = new GregorianCalendar();
        startDate.setTimeZone(timeZone);
        //Sets the year, month, day of the month, hour of day, minute, and second fields
        startDate.set(2015, 6, 23, 0, 0, 0);//2015-07-23 00:00:00 //月份0-11

        //创建事件
        String eventName = "Test Event";
        Date start = new Date(startDate.getTime());
        VEvent event = new VEvent(start, eventName);

        //添加时区信息
        event.getProperties().add(vTimeZone.getTimeZoneId());

        //生成唯一标示符UID
        try {
            UidGenerator generator = new UidGenerator("iCal4j");
            Uid uid = generator.generateUid();
            event.getProperties().add(uid);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        //添加参加者

        //创建日历
        net.fortuna.ical4j.model.Calendar calendar = new net.fortuna.ical4j.model.Calendar();
        calendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
        calendar.getProperties().add(CalScale.GREGORIAN);

        //添加事件
        calendar.getComponents().add(event);
        Log.e("-->", "===OneDayEvent===");
        Log.e("-->", calendar.toString());
        Log.e("-->", "=======");
    }

    /**
     * 解析calendar字符串->iCalenda对象
     *
     * @param calendarString
     * @return
     * @throws IOException
     * @throws ParserException
     */
    private net.fortuna.ical4j.model.Calendar parseCalerdar(String calendarString) throws IOException, ParserException {
        Log.i("-->", calendarString);
        StringReader stringReader = new StringReader(calendarString);
        CalendarBuilder builder = new CalendarBuilder();
        return builder.build(stringReader);

    }

    private byte[] bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
