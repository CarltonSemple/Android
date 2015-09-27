package app.bandit.reminderApp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.bandit.navigationdrawertemplate.R;

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



        testList = new ArrayList<>();
        testList.add("ayy");
        testList.add("chica");
        testList.add("tonight");
        ListView itemslist = (ListView) rootView.findViewById(R.id.upcomingListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_view_row_layout, R.id.mainText, testList);
        itemslist.setAdapter(adapter);



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
}

