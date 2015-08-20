package demo.ringares.com.ical4jdemo.bean;

/**
 * Created by ls
 * on 2015/8/10
 * Description
 */
public class PersonDataBean {
    public int person_id;//主键 本地id
    public int person_event_id;// 对应event_id
    public int person_type;// 联系人类型，例如手机联系人 1 ，邮箱联系人 2 ，社区好友 3
    public String person_display_name;// 联系人显示名字
    public String person_first_name;// 联系人名字
    public String person_last_name;// 联系人姓氏
    public String person_Email;// 联系人邮箱
    public String person_phone;// 联系人手机
    public int person_is_self;// 联系人是否是自己 0 不是 1是
    public String person_avatar_url;// 头像url
    public int person_role;// 用户所属角色
    public int person_revp_status;// 邀请状态
    public String person_other_info;// 联系人其他信息扩展 JSON String
}
