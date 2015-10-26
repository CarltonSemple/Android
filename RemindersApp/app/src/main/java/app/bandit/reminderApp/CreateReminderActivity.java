package app.bandit.reminderApp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import app.bandit.reminderApp.background.ReminderService;
import app.bandit.reminderApp.fileIO.JobFileManager;
import app.bandit.reminderApp.reminders.Reminder;

public class CreateReminderActivity extends ActionBarActivity implements View.OnClickListener {

    private EditText titleEdit, dateText, timeText, detailsText;
    private Button submitButton, cancelButton;

    private Manager manager;
    private Database db;
    final String TAG = "CreateReminderActivity";

    /* Text Changed Listeners */
    TextWatcher titleTextWatcher, detailTextWatcher;

    private Reminder reminder;

    /* background service */

    // component to signify the service class that receives the job scheduler callbacks
    ComponentName mServiceComponent;
    public static JobFileManager jobManager;
    /** Service object to interact with scheduled jobs **/
    private ReminderService rService;

    /*
     * Set up date picker
     */
    Calendar calendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate();
        }
    };

    TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            updateTime();
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_reminder_edit);
        setTitle("Create Reminder");

        jobManager = new JobFileManager(getFilesDir());

        titleEdit = (EditText) findViewById(R.id.titleEditView);
        dateText = (EditText) findViewById(R.id.dateText);
        timeText = (EditText) findViewById(R.id.timeText);
        detailsText = (EditText) findViewById(R.id.detailsText);
        submitButton = (Button) findViewById(R.id.submitButton);
        cancelButton = (Button) findViewById(R.id.deleteButton);
        cancelButton.setText("CANCEL");
        reminder = new Reminder();

        //setOnClick();
        //setOnTextChanged();

        openDatabase();

        // Give the time and date fields initial values
        initializeTimeAndDate();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setOnClick();
        setOnTextChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();

        removeOnClick();
        titleEdit.removeTextChangedListener(titleTextWatcher);
        detailsText.removeTextChangedListener(detailTextWatcher);
    }

    /**
     * Set the onClickListeners to this class, to be handled by the onClick function
     */
    protected void setOnClick() {
        dateText.setOnClickListener(this);
        timeText.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    protected void removeOnClick() {
        dateText.setOnClickListener(null);
        timeText.setOnClickListener(null);
        submitButton.setOnClickListener(null);
        cancelButton.setOnClickListener(null);
    }

    /**
     * Prepare the title and details fields for handling user input
     */
    protected void setOnTextChanged() {
        titleTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                reminder.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        titleEdit.addTextChangedListener(titleTextWatcher);

        detailTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                reminder.setDetails(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        detailsText.addTextChangedListener(detailTextWatcher);
    }

    /**
     * Handle button clicks for this activity
     * @param v
     */
    public void onClick(View v){
        switch (v.getId()){
            case R.id.dateText:
                new DatePickerDialog(this, date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.timeText:
                new TimePickerDialog(this, time,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false).show();
                break;
            case R.id.submitButton:
                submitToDatabase();
                // Go back to the Main Activity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.deleteButton:
                // Go back to the Main Activity
                Intent intent2 = new Intent(this, MainActivity.class);
                startActivity(intent2);
                break;
        }
    }

    /**
     *
     */
    protected void openDatabase() {
        // Start database manager
        try {
            // Create manager
            manager = new Manager(new AndroidContext(getApplicationContext()), Manager.DEFAULT_OPTIONS);
            Log.i(TAG, "manager created");
        } catch (IOException e) {
            Log.e(TAG, "Cannot create Manager instance", e);
            return;
        }

        // Open Database
        try {
            // Creates the database if it doesn't exist
            db = manager.getDatabase("reminders");
            Log.i(TAG, "database opened");
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "error creating/opening the database");
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set the time and date to the current time/date
     */
    private void initializeTimeAndDate() {
        updateDate();
        updateTime();
    }

    /**
     * Update the date text view and the date in the new reminder item
     */
    protected void updateDate() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        dateText.setText(sdf.format(calendar.getTime()));

        // Update the reminder item
        reminder.setYear(calendar.get(calendar.YEAR));
        reminder.setMonth(calendar.get(calendar.MONTH));
        reminder.setDay(calendar.get(calendar.DAY_OF_MONTH));
    }

    /**
     * Update the time text view and the time in the new reminder item
     */
    protected void updateTime() {
        reminder.setHour(calendar.get(calendar.HOUR_OF_DAY));
        reminder.setMinute(calendar.get(calendar.MINUTE));

        timeText.setText(reminder.getTimeOfDay());
    }


    private void submitToDatabase() {
        // create an object that contains data for a document
        Map<String, Object> docContent = new HashMap<String, Object>();
        docContent.put("year", reminder.getYear());
        docContent.put("month", reminder.getMonth());
        docContent.put("day", reminder.getDay());
        docContent.put("hour", reminder.getHour());
        docContent.put("minute", reminder.getMinute());
        docContent.put("title", reminder.getTitle());
        docContent.put("details", reminder.getDetails());
        docContent.put("status", reminder.getStatus());
        docContent.put("repeat", reminder.getRepeat());
        docContent.put("repeatType", "");
        docContent.put("repeatDay", "Sunday");

        // Create document with automatically generated ID

        Document document = db.createDocument(); // Create document with custom ID : // db.getDocument("1");
        try {
            document.putProperties(docContent);
            Log.i(TAG, "document " + document.getId() + "created and written to reminders db");
            Log.i(TAG, document.toString());
            System.out.println();
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "error writing document to database");
        }

        // Set the job even if the database fails, so at least the notification will pop up
        try {
            scheduleReminder(document.getId(), reminder.getTitle(), reminder.getDetails(), reminder.getTimeMillis());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // Set reminder notification
    private void scheduleReminder(String id, String title, String details, Long remindTimeMillis) {
        rService = new ReminderService(getApplicationContext());
        mServiceComponent = new ComponentName(this, ReminderService.class);

        int count = jobManager.incrementCount();
        if(count >= 0) {
            JobInfo.Builder builder = new JobInfo.Builder(count, mServiceComponent);

            Long delay = calculateTimeDelay(remindTimeMillis);
            builder.setMinimumLatency(delay - 60000);
            builder.setOverrideDeadline(delay);//70000);

            // Send details about reminder to the scheduled job
            PersistableBundle params = new PersistableBundle();
            params.putString("id", id);
            params.putString("title", title);
            // limit content detail to 140 characters
            StringBuilder sb = new StringBuilder(details);
            if(sb.length() > 140) {
                sb.setLength(140);
            }
            params.putString("details", details);
            builder.setExtras(params);

            rService.scheduleJob(builder.build());
            int i = 0;
            i++;
        } else {
            // throw exception here
        }
    }

    /**
     * Get the difference between the desired time and the current time
     * @param destinationTime - long. time in millis
     * @return
     */
    private Long calculateTimeDelay(Long destinationTime) {
        java.util.Date cur = new GregorianCalendar().getTime();
        TimeZone zone = Calendar.getInstance().getTimeZone();
        Long current = cur.getTime();// + zone.getOffset(Calendar.getInstance().getTimeInMillis());
        if(destinationTime < current) {
            return -1L;
        } else {
            return destinationTime - current;
        }
    }
}