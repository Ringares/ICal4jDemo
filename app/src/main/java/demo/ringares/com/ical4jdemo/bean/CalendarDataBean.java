package demo.ringares.com.ical4jdemo.bean;

/**
 * Created by ls
 * on 2015/8/10
 * Description
 */
public class CalendarDataBean {
    public String calendar_Id = "calendar_id";//主键 本地id
    public String calendar_sid = "calendar_sid";// 服务器id
    public String calendar_connection_id = "calendar_connection_id";// 绑定连接的id,对应connection 表的id
    public String calendar_name = "calendar_name";// 日历名称
    public String calendar_color = "calendar_color";// 日历颜色
    public String calendar_desc = "calendar_desc";// 日历描述
    public String calendar_type = "calendar_type";// 日历类型：如订阅节假日、授权获取、用户添加等
    public String calendar_visible = "calendar_visible";// 是否显示日历数据(0不显示,1显示)
    public String calendar_remind = "calendar_remind";// 日历数据是否提醒(0不提醒,1提醒)
    public String calendar_access = "calendar_access";// 日历操作权限类型，只读、读写
    public String calendar_other_info = "calendar_other_info";// 扩展字段 JSON String

}
