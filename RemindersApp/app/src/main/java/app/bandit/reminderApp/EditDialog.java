package app.bandit.reminderApp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;

import app.bandit.reminderApp.reminders.Reminder;
import app.bandit.reminderApp.reminders.ReminderArrayAdapter;

/**
 * Custom dialog that pops up for editing a reminder, similar to CreateReminderActivity
 * Created by Carlton Semple on 10/22/2015.
 */
public class EditDialog extends Dialog
                        implements View.OnClickListener, DialogInterface.OnDismissListener {
    private Context rootContext;

    private EditText titleEdit, dateText, timeText, detailsText;
    private Button submitButton, deleteButton;

    /* Database components */
    private Manager manager;
    private Database db;
    final String TAG = "EditDialog";
    private final String db_name = "reminders";

    private String docId;
    private Document document; // document to be changed
    private Reminder reminder;
    private ReminderArrayAdapter arrayAdapter;


    public EditDialog(Context context, Reminder selectedReminder, ReminderArrayAdapter adapter) {
        super(context);
        rootContext = context;
        docId = selectedReminder.getId();
        reminder = selectedReminder;
        arrayAdapter = adapter;
        setOnDismissListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_reminder_edit);
        setUpComponents();
        try {
            getDatabase(rootContext, db_name);
        } catch (Exception e) {
            setTitle("fetch error");
            return;
        }

        document = db.getDocument(docId);
        setTitle("edit reminder");

        //createReminderFromDocument(document);
        fillFields();
    }

    /*
     * Get references to the components on the page and set their onClickListeners to this class
     */
    private void setUpComponents() {
        titleEdit = (EditText) findViewById(R.id.titleEditView);
        dateText = (EditText) findViewById(R.id.dateText);
        timeText = (EditText) findViewById(R.id.timeText);
        detailsText = (EditText) findViewById(R.id.detailsText);
        submitButton = (Button) findViewById(R.id.submitButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        titleEdit.setOnClickListener(this);
        dateText.setOnClickListener(this);
        timeText.setOnClickListener(this);
        detailsText.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
    }

    private void createReminderFromDocument(Document doc) {
        int year = (int)doc.getProperty("year");
        int month = (int)doc.getProperty("month");
        int day = (int)doc.getProperty("day");
        int hour = (int)doc.getProperty("hour");
        int minute = (int)doc.getProperty("minute");
        String title = (String)doc.getProperty("title");
        String details = (String)doc.getProperty("details");
        String status = (String)doc.getProperty("status");
        reminder = new Reminder(docId, year, month, day, hour, minute, title, details, status, false);
    }

    /* Fill the fields in the dialog according to the reminder */
    private void fillFields() {
        titleEdit.setText(reminder.getTitle());
        detailsText.setText(reminder.getDetails());
    }

    /**
     * Handle button clicks for this dialog
     * @param v
     */
    @Override
    public void onClick(View v){
        switch (v.getId()){
            /* Delete Button Click */
            case R.id.deleteButton:
                try {
                    deleteDocument(db, reminder.getId());
                    arrayAdapter.remove(reminder); // remove from the visible list
                    // Go back to the Main Activity
                    dismiss();
                } catch (CouchbaseLiteException ce) {
                    ce.printStackTrace();
                    Log.e(TAG, ce.getMessage());
                }
                break;
        }
    }

    private void deleteDocument(Database database, String id) throws CouchbaseLiteException {
        database.getDocument(id).delete();
    }

    /**
     * Start the database manager, and open the database
     * @param context
     * @throws Exception
     */
    private void getDatabase(Context context, String dbName) throws Exception {
        /*
         * Start database manager
         */
        try {
            // Create manager
            manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
            Log.i(TAG, "manager created");
        } catch (IOException e) {
            Log.e(TAG, "Cannot create Manager instance", e);
        }

        /*
         * Open Database
         */
        try {
            // Creates the database if it doesn't exist
            db = manager.getDatabase(dbName);
            Log.i(TAG, "database opened");
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "error creating/opening the database");
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        closeDatabaseConnection(manager, db);
    }

    private void closeDatabaseConnection(Manager dbManager, Database database) {
        dbManager.close();
        database.close();
    }
}
