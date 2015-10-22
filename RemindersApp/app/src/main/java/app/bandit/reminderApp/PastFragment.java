package app.bandit.reminderApp;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


public class PastFragment extends android.support.v4.app.Fragment
        implements View.OnClickListener{

    private static final String ARG_SECTION_NUMBER = "section_number";

    private Manager manager;
    private Database db;
    final String TAG = "PastFragment";
    private final String db_name = "reminders";


    public static PastFragment newInstance(int sectionNumber) {
        PastFragment fragment = new PastFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PastFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Use this to call actions that are only available to the parent activity
        final View rootView = inflater.inflate(R.layout.fragment_past, container, false);

        /* Get the database */
        try {
            getDatabase(rootView, db_name);
        } catch (Exception e) {
            e.printStackTrace();
            return  rootView;
        }

        /* Get the reminders from the database */
        final ArrayList<Reminder> reminders = new ArrayList<Reminder>();
        try {
            com.couchbase.lite.View cview = db.getView("past");
            if (cview.getMap() == null) {
                setPastMappingFunction(cview);
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
                String status = (String) rDoc.getProperty("status");
                boolean repeat = (boolean) rDoc.getProperty("repeat");

                reminders.add(new Reminder(year, month, day, hour, minute, ti, "", status, repeat));
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        } catch (Exception ee) {
            String he = ee.getMessage();
            int i = 0;
        }

        /* Fill Item List */
        ReminderArrayAdapter arrayAdapter = new ReminderArrayAdapter(getActivity(), reminders);
        ListView pastItemsList = (ListView) rootView.findViewById(R.id.pastListView);
        try {
            pastItemsList.setAdapter(arrayAdapter);
        } catch (Exception ue) {
            ue.printStackTrace();
            ue.getMessage();
        }

        // item click listener
        pastItemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                final Dialog dialog = new Dialog(rootView.getContext());
                dialog.setContentView(R.layout.popup_reminder_edit);
                dialog.setTitle("edit reminder");
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

        }
    }

    /**
     * Start the database manager, and open the database
     * @param rootView
     * @throws Exception
     */
    private void getDatabase(View rootView, String dbName) throws Exception {
        /*
         * Start database manager
         */
        try {
            // Create manager
            manager = new Manager(new AndroidContext(rootView.getContext()), Manager.DEFAULT_OPTIONS);
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

    private void setPastMappingFunction(com.couchbase.lite.View cview) {
        cview.setMap(new Mapper() {
                 @Override
                 public void map(Map<String, Object> document, Emitter emitter) {
                    /* Check to see if the reminder is upcoming still or not */

                     try {
                         Long dueTime = Reminder.getTimeMillisGiven((int) document.get("year"), (int) document.get("month") - 1, (int) document.get("day"), (int) document.get("hour"), (int) document.get("minute"));
                         java.util.Date cur = new GregorianCalendar().getTime();
                         TimeZone zone = Calendar.getInstance().getTimeZone();
                         Long current = cur.getTime();
                         if (dueTime < current) { // Still upcoming if the due time is larger than the current time
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
