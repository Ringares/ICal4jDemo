package demo.ringares.com.ical4jdemo.bean;

/**
 * Created by ls
 * on 2015/8/10
 * Description
 */
public class EventDataBean {
    public static final String EVENT_FLAG_ADD = "A";
    public static final String EVENT_FLAG_EDIT = "E";
    public static final String EVENT_FLAG_DELETE = "D";


    public String event_id = "event_id";//���� ����id
    public String event_is_syn = "event_is_syn";// �Ƿ���Ҫͬ��
    public String event_flag = "event_flag";// ���ݲ�������A:add,E:edit,D:delete
    public String event_ts = "event_ts";// ͬ����ʱ��� ���������ص�
    public String event_sid = "event_sid";// ������id
    public String event_calendar_id = "event_calendar_id";// calendar id,��Ӧcalenadr ���local id
    public String event_uuid = "event_uuid";// ���Ψһ��ʶ��
    public String event_title = "event_title";// �����
    public String event_note = "event_note";// ���ע
    public String event_start_date = "event_start_date";// ���ʼʱ��� ����
    public String event_end_date = "event_end_date";// �����ʱ���
    public String event_is_allday = "event_is_allday";// ��Ƿ���ȫ��(0����,1��)
    public String event_rrule = "event_rrule";// �ظ���ʽ���ʽ,�ձ�ʾ���ظ����õ�ʱ����Ҫ����
    public String event_rdate = "event_rdate";// �¼���ѭ�����ڡ�ͨ�����Ժ�RRULEһ��ʹ��������һ���ظ��������ܼ��ϡ�
    public String event_advance = "event_advance";// ��ǰ����ʱ�䣬��λ�Ƿ��ӡ���,Ϊ���֧�ֶ������0,5,60,120
    public String event_url = "event_url";// �����url
    public String event_editable = "event_editable";// ��Ƿ���Ա༭��(0������,1����)
    public String event_create_ts = "event_create_ts";// �����ʱ���
    public String event_update_ts = "event_update_ts";// ��༭����ʱ���
    public String event_status = "event_status";// ���״̬ Ĭ��conformed
    public String event_iCal = "event_iCal";// ical ԭ�ģ�ÿ���޸�Ҫupdate��Ӧ֧���޸ĵ��ֶ�

}
