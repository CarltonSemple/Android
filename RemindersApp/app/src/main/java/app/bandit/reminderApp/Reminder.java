package app.bandit.reminderApp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Carlton Semple on 9/27/2015.
 */
public class Reminder {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private StringBuilder title;
    private StringBuilder details; // extra
    private ReminderStatus status;
    private Boolean repeat;

    public Reminder() {
        year = 0;
        month = 0;
        day = 0;
        hour = 0;
        minute = 0;
        title = new StringBuilder();
        details = new StringBuilder();
        status = ReminderStatus.UPCOMING;
        repeat = false;
    }

    public Reminder(int yp, int mp, int dp, int hp, int minp, String tp, String det, String stat, boolean reepeet) {
        year = yp;
        month = mp;
        day = hp;
        hour = hp;
        minute = minp;
        title = new StringBuilder(tp);
        details = new StringBuilder(det);
        if(stat.equals("upcoming")) {
            status = ReminderStatus.UPCOMING;
        } else if (stat.equals("past")) {
            status = ReminderStatus.PAST;
        }
        repeat = reepeet;
    }

    public String getTitle() {
        return title.toString();
    }

    public String getDetails() {
        return details.toString();
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public Boolean getRepeat() {
        return repeat;
    }

    public void setYear(int y) {
        this.year = y;
    }

    public void setMonth(int m) {
        this.month = m;
    }

    public void setDay(int d) {
        this.day = d;
    }

    public void setHour(int h) {
        this.hour = h;
    }

    public void setMinute(int min) {
        this.minute = min;
    }

    public void setTitle(String titl) {
        this.title = new StringBuilder(titl);
    }

    public void setDetails(String detai) {
        this.details = new StringBuilder(detai);
    }

    public String getStatus() {
        switch (status){
            case UPCOMING: return "upcoming";
            case PAST: return "upcoming";
        }
        return "invalid";
    }

    public String getMonthDay() {
        StringBuilder sb = new StringBuilder();
        sb.append(getMonthString());
        sb.append(' ');
        sb.append(day);
        int x = day % 10;
        if(x == 1) {
            sb.append("st");
        } else if (x == 2) {
            if(day / 10 == 1) {
                sb.append("th");
            } else {
                sb.append("nd");
            }
        } else if (x == 3) {
            sb.append("rd");
        } else {
            sb.append("th");
        }
        return sb.toString();
    }

    public String getMonthString() {
        switch (month + 1) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
        }
        return "invalid month";
    }

    public String getTimeOfDay() {
        StringBuilder sb = new StringBuilder();

        if(hour > 12) {
            sb.append(hour - 12);
        } else {
            sb.append(hour);
        }

        sb.append(':');
        if(minute < 10) {
            sb.append('0');
            sb.append(minute);
        } else {
            sb.append(minute);
        }

        if(hour < 13) {
            sb.append(" AM");
        } else {
            sb.append(" PM");
        }

        return sb.toString();
    }

    public Long getTimeMillis() throws ParseException {
        return getTimeMillisGiven(year, month, day, hour, minute);
    }

    public static Long getTimeMillisGiven(Integer year,
                                          Integer month,
                                          Integer day,
                                          Integer hour,
                                          Integer minute) throws ParseException {
        StringBuilder sb = new StringBuilder();
        if (day < 10) {
            sb.append('0');
        }
        sb.append(day);
        sb.append('/');
        if (month + 1 < 10) {
            sb.append('0');
        }
        sb.append(month + 1);
        sb.append('/');
        sb.append(year);
        sb.append(' ');
        if (hour < 10) {
            sb.append('0');
        }
        sb.append(hour);
        sb.append(':');
        if (minute < 10) {
            sb.append('0');
        }
        sb.append(minute);
        sb.append(':');
        sb.append("00");

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        Date dueDate = format.parse(sb.toString());

        return dueDate.getTime();
    }
}
