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


    public String event_id;//���� ����id
    public int event_is_syn;// �Ƿ���Ҫͬ��
    public String event_flag;// ���ݲ�������A:add,E:edit,D:delete
    public long event_ts;// ͬ����ʱ��� ���������ص�
    public long event_sid;// ������id
    public int event_calendar_id;// calendar id,��Ӧcalenadr ���local id
    public String event_uuid;// ���Ψһ��ʶ��
    public String event_title;// �����
    public String event_note;// ���ע
    public long event_start_date;// ���ʼʱ��� ����
    public long event_end_date;// �����ʱ���
    public int event_is_allday;// ��Ƿ���ȫ��(0����,1��)
    public String event_rrule;// �ظ���ʽ���ʽ,�ձ�ʾ���ظ����õ�ʱ����Ҫ����
    public String event_rdate;// �¼���ѭ�����ڡ�ͨ�����Ժ�RRULEһ��ʹ��������һ���ظ��������ܼ��ϡ�
    public String event_advance;// ��ǰ����ʱ�䣬��λ�Ƿ��ӡ���,Ϊ���֧�ֶ������0,5,60,120
    public String event_url;// �����url
    public int event_editable;// ��Ƿ���Ա༭��(0������,1����)
    public long event_create_ts;// �����ʱ���
    public long event_update_ts;// ��༭����ʱ���
    public String event_status;// ���״̬ Ĭ��conformed
    public String event_iCal;// ical ԭ�ģ�ÿ���޸�Ҫupdate��Ӧ֧���޸ĵ��ֶ�

}
