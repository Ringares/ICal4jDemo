package demo.ringares.com.ical4jdemo.synbean;

/**
 * Created by ls
 * on 2015/8/12
 * Description
 */
public abstract class BaseBean {

	/* 实现类非必须实现初始化initKey这个动作 */

    public String getCacheKey() {
        // 这里的key为接口id+interfaceType�?
        return "-1";
    };
}