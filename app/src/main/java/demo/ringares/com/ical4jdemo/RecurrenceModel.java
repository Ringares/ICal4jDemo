package demo.ringares.com.ical4jdemo;

import android.text.TextUtils;
import android.text.format.Time;
import android.util.TimeFormatException;

import java.util.Arrays;

/**
 * Created by ls
 * on 2015/8/5
 * Description
 */
public class RecurrenceModel {
    // Should match EventRecurrence.DAILY, etc
    static final int COUNT_DEFAULT = 5;
    private static final int INTERVAL_DEFAULT = 1;

    static final int FREQ_DAILY = 0;
    static final int FREQ_WEEKLY = 1;
    static final int FREQ_MONTHLY = 2;
    static final int FREQ_YEARLY = 3;

    static final int END_NEVER = 0;
    static final int END_BY_DATE = 1;
    static final int END_BY_COUNT = 2;

    static final int MONTHLY_BY_DATE = 0;
    static final int MONTHLY_BY_NTH_DAY_OF_WEEK = 1;

    static final int STATE_NO_RECURRENCE = 0;
    static final int STATE_RECURRENCE = 1;

    int recurrenceState;

    /**
     * FREQ: Repeat pattern
     *
     * @see FREQ_DAILY
     * @see FREQ_WEEKLY
     * @see FREQ_MONTHLY
     * @see FREQ_YEARLY
     */
    int freq = FREQ_WEEKLY;

    /**
     * INTERVAL: Every n days/weeks/months/years. n >= 1
     */
    int interval = INTERVAL_DEFAULT;

    /**
     * UNTIL and COUNT: How does the the event end?
     *
     * @see END_NEVER
     * @see END_BY_DATE
     * @see END_BY_COUNT
     * @see untilDate
     * @see untilCount
     */
    int end;

    /**
     * UNTIL: Date of the last recurrence. Used when until == END_BY_DATE
     */
    Time endDate;

    /**
     * COUNT: Times to repeat. Use when until == END_BY_COUNT
     */
    int endCount = COUNT_DEFAULT;

    /**
     * BYDAY: Days of the week to be repeated. Sun = 0, Mon = 1, etc
     */
    boolean[] weeklyByDayOfWeek = new boolean[7];

    /**
     * BYDAY AND BYMONTHDAY: How to repeat monthly events? Same date of the
     * month or Same nth day of week.
     *
     * @see MONTHLY_BY_DATE
     * @see MONTHLY_BY_NTH_DAY_OF_WEEK
     */
    int monthlyRepeat;

    /**
     * Day of the month to repeat. Used when monthlyRepeat ==
     * MONTHLY_BY_DATE
     */
    int monthlyByMonthDay;

    /**
     * Day of the week to repeat. Used when monthlyRepeat ==
     * MONTHLY_BY_NTH_DAY_OF_WEEK
     */
    int monthlyByDayOfWeek;

    /**
     * Nth day of the week to repeat. Used when monthlyRepeat ==
     * MONTHLY_BY_NTH_DAY_OF_WEEK 0=undefined, -1=Last, 1=1st, 2=2nd, ..., 5=5th
     * <p/>
     * We support 5th, just to handle backwards capabilities with old bug, but it
     * gets converted to -1 once edited.
     */
    int monthlyByNthDayOfWeek;


    public RecurrenceModel() {
    }

    @Override
    public String toString() {
        return "Model [freq=" + freq + ", interval=" + interval + ", end=" + end + ", endDate="
                + endDate + ", endCount=" + endCount + ", weeklyByDayOfWeek="
                + Arrays.toString(weeklyByDayOfWeek) + ", monthlyRepeat=" + monthlyRepeat
                + ", monthlyByMonthDay=" + monthlyByMonthDay + ", monthlyByDayOfWeek="
                + monthlyByDayOfWeek + ", monthlyByNthDayOfWeek=" + monthlyByNthDayOfWeek + "]";
    }

    // Special cases in monthlyByNthDayOfWeek
    private static final int FIFTH_WEEK_IN_A_MONTH = 5;
    private static final int LAST_NTH_DAY_OF_WEEK = -1;

    static public boolean isSupportedMonthlyByNthDayOfWeek(int num) {
        // We only support monthlyByNthDayOfWeek when it is greater then 0 but less then 5.
        // Or if -1 when it is the last monthly day of the week.
        return (num > 0 && num <= FIFTH_WEEK_IN_A_MONTH) || num == LAST_NTH_DAY_OF_WEEK;
    }

    static public RecurrenceModel copyEventRecurrenceToModel(final EventRecurrence er) {

        RecurrenceModel model = new RecurrenceModel();
        // Freq:
        switch (er.freq) {
            case EventRecurrence.DAILY:
                model.freq = RecurrenceModel.FREQ_DAILY;
                break;
            case EventRecurrence.MONTHLY:
                model.freq = RecurrenceModel.FREQ_MONTHLY;
                break;
            case EventRecurrence.YEARLY:
                model.freq = RecurrenceModel.FREQ_YEARLY;
                break;
            case EventRecurrence.WEEKLY:
                model.freq = RecurrenceModel.FREQ_WEEKLY;
                break;
            default:
                throw new IllegalStateException("freq=" + er.freq);
        }

        // Interval:
        if (er.interval > 0) {
            model.interval = er.interval;
        }

        // End:
        // End by count:
        model.endCount = er.count;
        if (model.endCount > 0) {
            model.end = RecurrenceModel.END_BY_COUNT;
        }

        // End by date:
        if (!TextUtils.isEmpty(er.until)) {
            if (model.endDate == null) {
                model.endDate = new Time();
            }

            try {
                model.endDate.parse(er.until);
            } catch (TimeFormatException e) {
                model.endDate = null;
            }

            // LIMITATION: The UI can only handle END_BY_DATE or END_BY_COUNT
            if (model.end == RecurrenceModel.END_BY_COUNT && model.endDate != null) {
                throw new IllegalStateException("freq=" + er.freq);
            }

            model.end = RecurrenceModel.END_BY_DATE;
        }

        // Weekly: repeat by day of week or Monthly: repeat by nth day of week
        // in the month
        Arrays.fill(model.weeklyByDayOfWeek, false);
        if (er.bydayCount > 0) {
            int count = 0;
            for (int i = 0; i < er.bydayCount; i++) {
                int dayOfWeek = EventRecurrence.day2TimeDay(er.byday[i]);
                model.weeklyByDayOfWeek[dayOfWeek] = true;

                if (model.freq == RecurrenceModel.FREQ_MONTHLY &&
                        isSupportedMonthlyByNthDayOfWeek(er.bydayNum[i])) {
                    // LIMITATION: Can handle only (one) weekDayNum in nth or last and only
                    // when
                    // monthly
                    model.monthlyByDayOfWeek = dayOfWeek;
                    model.monthlyByNthDayOfWeek = er.bydayNum[i];
                    model.monthlyRepeat = RecurrenceModel.MONTHLY_BY_NTH_DAY_OF_WEEK;
                    count++;
                }
            }

            if (model.freq == RecurrenceModel.FREQ_MONTHLY) {
                if (er.bydayCount != 1) {
                    // Can't handle 1st Monday and 2nd Wed
                    throw new IllegalStateException("Can handle only 1 byDayOfWeek in monthly");
                }
                if (count != 1) {
                    throw new IllegalStateException(
                            "Didn't specify which nth day of week to repeat for a monthly");
                }
            }
        }

        // Monthly by day of month
        if (model.freq == RecurrenceModel.FREQ_MONTHLY) {
            if (er.bymonthdayCount == 1) {
                if (model.monthlyRepeat == RecurrenceModel.MONTHLY_BY_NTH_DAY_OF_WEEK) {
                    throw new IllegalStateException(
                            "Can handle only by monthday or by nth day of week, not both");
                }
                model.monthlyByMonthDay = er.bymonthday[0];
                model.monthlyRepeat = RecurrenceModel.MONTHLY_BY_DATE;
            } else if (er.bymonthCount > 1) {
                // LIMITATION: Can handle only one month day
                throw new IllegalStateException("Can handle only one bymonthday");
            }
        }
        return model;
    }
}
