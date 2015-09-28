package app.bandit.reminderApp;

/**
 * Created by Carlton Semple on 9/27/2015.
 */
public class Reminder {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private String title;
    private String details; // extra
    private ReminderStatus status;
    private Boolean repeat;


    public Reminder(int yp, int mp, int dp, int hp, int minp, String tp, String det, String stat, boolean reepeet) {
        year = yp;
        month = mp;
        day = hp;
        hour = hp;
        minute = minp;
        title = tp;
        details = det;
        if(stat.equals("upcoming")) {
            status = ReminderStatus.UPCOMING;
        } else if (stat.equals("past")) {
            status = ReminderStatus.PAST;
        }
        repeat = reepeet;
    }

    public String getTitle() {
        return title;
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
        switch (month) {
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
        
    }
}
