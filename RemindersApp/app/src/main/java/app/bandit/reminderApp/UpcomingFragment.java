package app.bandit.reminderApp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import app.bandit.reminderApp.reminders.Reminder;
import app.bandit.reminderApp.reminders.ReminderArrayAdapter;

/**
 * A fragment containing a list view of upcoming reminders.
 *
 * Created by Carlton Semple on 9/27/2015.
 */
public class UpcomingFragment extends android.support.v4.app.Fragment
        implements View.OnClickListener{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private Manager manager;
    private Database db;
    final String TAG = "UpcomingFragment";
    private final String db_name = "reminders";

    /** Components Inside Edit Dialog **/

    private EditText titleEdit, dateText, timeText, detailsText;
    private Button submitButton;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static UpcomingFragment newInstance(int sectionNumber) {
        UpcomingFragment fragment = new UpcomingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public UpcomingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Use this to call actions that are only available to the parent activity
        final View rootView = inflater.inflate(R.layout.fragment_upcoming, container, false);

        /* Get the database */
        try {
            getDatabase(rootView.getContext(), db_name);
        } catch (Exception e) {
            e.printStackTrace();
            return  rootView;
        }

        // create an object that contains data for a document
        Map<String, Object> docContent = new HashMap<String, Object>();
        docContent.put("year", 2015);
        docContent.put("month", 12);
        docContent.put("day", 21);
        docContent.put("hour", 12);
        docContent.put("minute", 0);
        docContent.put("title", "uua");
        docContent.put("details", "this is going to be a test");
        docContent.put("status", "upcoming");
        docContent.put("repeat", false);
        docContent.put("repeatType", "");
        docContent.put("repeatDay", "Tuesday");

        // Create document with automatically generated ID
        /*
        Document document = db.createDocument(); // Create document with custom ID : // db.getDocument("1");
        try {
            document.putProperties(docContent);
            Log.i(TAG, "document " + document.getId() + "created and written to reminders db");
            Log.i(TAG, document.toString());
            System.out.println();
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "error writing document to database");
        }*/

        final ArrayList<Reminder> reminders = new ArrayList<Reminder>();

        // Query all documents
        try {
            List views = db.getAllViews();

            com.couchbase.lite.View cview = db.getView("upcoming");
            if (cview.getMap() == null) {
                setUpcomingMappingFunction(cview);
            }
            Query query = cview.createQuery();
            query.setDescending(true);
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document rDoc = row.getDocument();
                int year = (int) rDoc.getProperty("year");
                int month = (int) rDoc.getProperty("month");
                int day = (int) rDoc.getProperty("day");
                int hour = (int) rDoc.getProperty("hour");
                int minute = (int) rDoc.getProperty("minute");
                String ti = (String) rDoc.getProperty("title");
                String details = (String) rDoc.getProperty("details");
                String status = (String) rDoc.getProperty("status");
                boolean repeat = (boolean) rDoc.getProperty("repeat");


                reminders.add(new Reminder(rDoc.getId(), year, month, day, hour, minute, ti, details, status, repeat));
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        } catch (Exception ee) {
            String he = ee.getMessage();
            int i = 0;
        }

        /* Free database resource */
        closeDatabaseConnection(manager, db);

        /* Fill Item List */
        final ReminderArrayAdapter arrayAdapter = new ReminderArrayAdapter(getActivity(), reminders);
        ListView itemslist = (ListView) rootView.findViewById(R.id.upcomingListView);
        itemslist.setAdapter(arrayAdapter);

        // item click listener
        itemslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Reminder selectedReminder = arrayAdapter.getItem(arg2); // get the item at arg2 index
                final EditDialog dialog = new EditDialog(rootView.getContext(), selectedReminder, arrayAdapter);
                //dialog.
                dialog.show();
            }
        });



        return rootView;
    }

    /**
     * Calls the parent activity's function to change the title of the navigation drawer
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    /**
     * Handle button clicks for this fragment
     * @param v
     */
    @Override
    public void onClick(View v){
        switch (v.getId()){
            /*
            case R.id.fragmentMain_mapButton: // map button
                //goToMap();
                break;
            case R.id.fragmentMain_phoneText: // phone number
                phoneCall(phoneTextView.getText().toString());
                break;
                */
        }
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

    /**
     *
     */
    private void closeDatabaseConnection(Manager dbManager, Database database) {
        dbManager.close();
        database.close();
    }

    private void setUpcomingMappingFunction(com.couchbase.lite.View cview) {
        cview.setMap(new Mapper() {
                 @Override
                 public void map(Map<String, Object> document, Emitter emitter) {
                     /* Check to see if the reminder is upcoming still or not */

                     try {
                         int year = (int)document.get("year");
                         int month = (int)document.get("month");
                         int day = (int)document.get("day");
                         int hour = (int)document.get("hour");
                         int minute = (int)document.get("minute");
                         Long dueTime = Reminder.getTimeMillisGiven(year, month, day, hour, minute);
                         java.util.Date cur = new GregorianCalendar().getTime();
                         TimeZone zone = Calendar.getInstance().getTimeZone();
                         Long current = cur.getTime();
                         /*Reminder nre = new Reminder(year, month, day, hour, minute,
                                 (String)document.get("title"), (String)document.get("details"), (String)document.get("status"), false);
                         String s = nre.getMonthDay() + " " + nre.getYear();*/
                         if (dueTime >= current) { // Still upcoming if the due time is larger than the current time
                             List<Object> key = new ArrayList<Object>();
                             key.add(document.get("year"));
                             key.add(document.get("month"));
                             key.add(document.get("day"));
                             key.add(document.get("hour"));
                             key.add(document.get("minute"));

                             // Add this document to the mapping function
                             emitter.emit(key, null);    // since it'll just be retrieving, no need to emit a value
                         }
                     } catch (ParseException pe) {
                         pe.printStackTrace();
                     }
                 }
             },
        "3"); // Version number *** MUST BE INCREMENTED EVERY TIME THE CODE CHANGES, or code that it calls changes
    }
}

