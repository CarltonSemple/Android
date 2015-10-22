package app.bandit.reminderApp.background;

import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Context;
import android.os.PersistableBundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import app.bandit.reminderApp.R;

/**
 * Service to create notifications for reminders
 *
 * Created by Carlton Semple on 10/17/2015.
 */
public class ReminderService extends JobService{
    private static final String TAG = "ReminderService";

    private Context appContext;

    public ReminderService() {}

    public ReminderService(Context context) {
        appContext = context;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        PersistableBundle bundle = params.getExtras();
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.common_signin_btn_icon_normal_dark)
                        .setContentTitle(bundle.getString("title"))
                        .setContentText(bundle.getString("details"));

        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

        appContext = getApplicationContext();
        JobScheduler scheduler = (JobScheduler) appContext.getSystemService(appContext.JOB_SCHEDULER_SERVICE);
        scheduler.cancel(params.getJobId());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        return true;
    }

    /** Send job to the JobScheduler. */
    public void scheduleJob(JobInfo t) {
        Log.d(TAG, "Scheduling job");
        try {
            JobScheduler tm = (JobScheduler) appContext.getSystemService(appContext.JOB_SCHEDULER_SERVICE);
            tm.schedule(t);
            int i = 0;
        } catch (Exception e) {
            e.printStackTrace();
            int i = 0;
        }
    }

}
