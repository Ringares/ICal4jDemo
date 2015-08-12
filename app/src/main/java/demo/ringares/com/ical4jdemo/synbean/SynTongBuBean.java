package demo.ringares.com.ical4jdemo.synbean;

/**
 * Created by ls
 * on 2015/8/12
 * Description
 */
public class SynTongBuBean extends BaseBean{
    public String status = "";
    /** 数据类型，NOTE记事，ALERT提醒 */
    public String type = "";
    public String id = "";
    public String catId = "";
    public long tx;
    /** 操作，A添加，E修改，D删除 */
    public String act = "";
    public String actTime = "";
    /** 数据内容，格式自定义 */
    public String content = "";
    /** 自定义标示 */
    public String uuid = "";
    public String extendJson = "";
    public long lastUpdateTime;
    public long createTime;

    public String sub_catId = "";
    public int formatVersionCode = 1;

    public String toString() {
        return "type=" + type + "\nid=" + id + "\ncatid=" + catId + "\ntx="
                + tx + "\nact=" + act + "\nactTime=" + actTime + "\ncontent="
                + content +"\nextend="+extendJson+ "\nuuid=" + uuid + "\nsub_catId=" + sub_catId + "\nformatVersionCode="+formatVersionCode+
                "\n------------------";
    }
}
