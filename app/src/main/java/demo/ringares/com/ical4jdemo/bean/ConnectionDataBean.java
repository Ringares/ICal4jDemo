package demo.ringares.com.ical4jdemo.bean;

/**
 * Created by ls
 * on 2015/8/10
 * Description
 */
public class ConnectionDataBean {
    public int connection_id ;//主键 本地id
    public long connection_sid ;// 服务器id
    public int connection_type ;// 绑定类型,例如google、iCloud、facebook、local
    public int connection_status ;// 绑定状态(0绑定失效,1绑定正常)
    public String connection_userinfo ;// 绑定的用户信息 JSON String

}
