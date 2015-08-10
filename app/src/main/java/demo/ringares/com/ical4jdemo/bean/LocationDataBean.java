package demo.ringares.com.ical4jdemo.bean;

/**
 * Created by ls
 * on 2015/8/10
 * Description
 */
public class LocationDataBean {
    public int location_id;//主键 本地id
    public int location_event_id;// 对应event_id
    public double location_lat;// 地址纬度
    public double location_lon;// 地址经度
    public String location_city;// 地址城市
    public String location_country;// 地址国家
    public String location_desc;// 地址整体详细描述

}
