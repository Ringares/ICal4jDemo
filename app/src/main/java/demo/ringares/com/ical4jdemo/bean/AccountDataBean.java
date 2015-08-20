package demo.ringares.com.ical4jdemo.bean;

/**
 * Created by ls
 * on 2015/8/10
 * Description
 */
public class AccountDataBean {
    public int KEY_account_id ;//主键 本地id
    public long KEY_account_sid ;// 服务器id
    public String KEY_account_type ;// 绑定类型,例如google、iCloud、facebook、local
    public String KEY_account_host_md5 ;// 绑定账号的host eg:www.google.com
    public String KEY_account_user_name ;// 绑定的用户名 eg:xxx@gamil.com
    public String KEY_account_service_provider ;// 绑定账号的服务提供者 eg:Google
    public long KEY_account_user_3sync_id ;// 绑定账号对应的服务器同步id，服务器返回，客户端不做修改
    public int KEY_account_is_sync ;
    // 标志账户是否同步处理过，同步的时候，把数据库中的该字段都设置为0，服务器返回的account 更新到数据库中，更新的数据该字段置为1，最后删除字段为0的数据，
    // 参考分组表的同步处理

}
