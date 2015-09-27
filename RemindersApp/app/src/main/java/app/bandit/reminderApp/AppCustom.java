package app.bandit.reminderApp;

import android.app.Application;
import android.util.Log;

/**
 * Can be used to run code on application start up
 *
 * Created by Carlton Semple on 9/27/2015.
 */
public class AppCustom extends Application {

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

        Log.i("main", "onCreate fired");
    }
}
