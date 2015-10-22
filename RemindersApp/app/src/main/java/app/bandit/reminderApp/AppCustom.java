package app.bandit.reminderApp;

import android.app.Application;
import android.app.job.JobInfo;
import android.content.ComponentName;
import android.os.PersistableBundle;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.bandit.reminderApp.background.ReminderService;
import app.bandit.reminderApp.fileIO.JobFileManager;

/**
 * Can be used to run code on application start up
 *
 * Created by Carlton Semple on 9/27/2015.
 */
public class AppCustom extends Application {

    private Manager manager;
    private Database db;
    private final String TAG = "AppCustom";

    /* background service */

    // component to signify the service class that receives the job scheduler callbacks
    ComponentName mServiceComponent;

    public static JobFileManager jobManager;
    /** Service object to interact with scheduled jobs **/
    ReminderService rService;



    /*
     * Called only once.
     * getApplicationContext doesn't work here
     */
    public AppCustom(){

    }

    /*
     * Called once. getApplicationContext works here
     */
    @Override
    public void onCreate() {
        super.onCreate();

        setupDatabaseViews();

        Log.i(TAG, "onCreate fired");

        jobManager = new JobFileManager(getFilesDir());
        //createBackgroundService();
    }

    public void createBackgroundService() {
        rService = new ReminderService(getApplicationContext());
        mServiceComponent = new ComponentName(this, ReminderService.class);

        int count = jobManager.incrementCount();
        if(count >= 0) {
            JobInfo.Builder builder = new JobInfo.Builder(count, mServiceComponent);
            builder.setMinimumLatency(10000);
            builder.setOverrideDeadline(70000);

            PersistableBundle params = new PersistableBundle();

            rService.scheduleJob(builder.build());
            int i = 0;
        } else {
            // throw exception here
        }
    }

    /*
     * The Views' mapping functions for the database are function pointers and need to be set up
     * during application startup
     */
    private void setupDatabaseViews() {
         /*
         * Access database
         */
        try {
            // Create manager
            manager = new Manager(new AndroidContext(getApplicationContext()), Manager.DEFAULT_OPTIONS);
            Log.i(TAG, "manager created");
        } catch (IOException e) {
            Log.e(TAG, "Cannot create Manager instance", e);
            return;
        }

        try {
            // open the database
            // Creates the database if it doesn't exist
            db = manager.getDatabase("reminders");
            Log.i(TAG, "database opened");
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "error creating/opening the database");
            return;
        }

        // Create a view and register its map function:
        View upcomingView = db.getView("upcoming");
        upcomingView.setMap(new Mapper() {
                        @Override
                        public void map(Map<String, Object> document, Emitter emitter) {
                            String status = (String) document.get("status");
                            if(status.equals("upcoming")){
                                List<Object> key = new ArrayList<Object>();
                                key.add(document.get("year"));
                                key.add(document.get("month"));
                                key.add(document.get("day"));
                                key.add(document.get("hour"));
                                key.add(document.get("minute"));
                                emitter.emit(key, null);    // since it'll just be retrieving, no need to emit a value
                            }
                        }
                    },
        "1"); // Version number *** MUST BE INCREMENTED EVERY TIME THE CODE CHANGES, or code that it calls changes

        // Create a view and register its map function:
        View pastView = db.getView("past");
        pastView.setMap(new Mapper() {
                    @Override
                    public void map(Map<String, Object> document, Emitter emitter) {
                        String status = (String) document.get("status");
                        if(status.equals("past")){
                            List<Object> key = new ArrayList<Object>();
                            key.add(document.get("year"));
                            key.add(document.get("month"));
                            key.add(document.get("day"));
                            key.add(document.get("hour"));
                            key.add(document.get("minute"));
                            emitter.emit(key, null);    // since it'll just be retrieving, no need to emit a value
                        }
                    }
                },
        "1"); // Version number *** MUST BE INCREMENTED EVERY TIME THE CODE CHANGES, or code that it calls changes
    }
}
