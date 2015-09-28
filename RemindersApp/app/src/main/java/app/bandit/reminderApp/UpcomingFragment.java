package app.bandit.reminderApp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import app.bandit.reminderApp.R;

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
    private List<String> testList;

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
        View rootView = inflater.inflate(R.layout.fragment_upcoming, container, false);

        // Start database manager
        try {
            // Create manager
            manager = new Manager(new AndroidContext(rootView.getContext()), Manager.DEFAULT_OPTIONS);
            Log.i(TAG, "manager created");
        } catch (IOException e) {
            Log.e(TAG, "Cannot create Manager instance", e);
            return rootView;
        }

        // Open Database
        try {
            // Creates the database if it doesn't exist
            db = manager.getDatabase("reminders");
            Log.i(TAG, "database opened");
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "error creating/opening the database");
            return rootView;
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

        testList = new ArrayList<>();

        ArrayList<Reminder> reminders = new ArrayList<Reminder>();

        // Query all documents
        try {
            List views = db.getAllViews();

            com.couchbase.lite.View cview = db.getView("upcoming");
            if (cview.getMap() == null) {
                setMappingFunction(cview);
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
                testList.add(ti);
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        } catch (Exception ee) {
            String he = ee.getMessage();
            int i = 0;
        }

        ReminderArrayAdapter arrayAdapter = new ReminderArrayAdapter(getActivity(), reminders);

        testList.add("ayy");
        testList.add("chica");
        testList.add("tonight");
        ListView itemslist = (ListView) rootView.findViewById(R.id.upcomingListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_view_row_layout, R.id.row_main_text, testList);
        //itemslist.setAdapter(adapter);
        itemslist.setAdapter(arrayAdapter);



        return rootView;
    }

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

    private void populateReminderList(View rootView) {
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
            db = manager.getDatabase("reminders");
            Log.i(TAG, "database opened");
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "error creating/opening the database");
        }
    }

    private void setMappingFunction(com.couchbase.lite.View cview) {
        cview.setMap(new Mapper() {
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
    }
}

