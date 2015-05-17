package app.bandit.navigationdrawertemplate;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends android.support.v4.app.Fragment
        implements View.OnClickListener{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /** Button for navigating to the map page */
    private Button mapButton;
    private TextView phoneTextView;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MainFragment newInstance(int sectionNumber) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Show hamburger via Parent Activity
        //getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        // set up map button
        mapButton = (Button) rootView.findViewById(R.id.fragmentMain_mapButton);
        mapButton.setOnClickListener(this);

        // add click to phone number
        phoneTextView = (TextView) rootView.findViewById(R.id.fragmentMain_phoneText);
        phoneTextView.setOnClickListener(this);

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
            case R.id.fragmentMain_mapButton: // map button
                goToMap();
                break;
            case R.id.fragmentMain_phoneText: // phone number
                phoneCall(phoneTextView.getText().toString());
                break;
        }
    }

    /** Navigate to the map page */
    public void goToMap(){
        Intent intent = new Intent(getActivity(), MapsActivity.class); // prepare to navigate

        // no extra variables, so no need to use intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    /**
     * Call the 10 digit phone number
     */
    public void phoneCall(String numText){
        Intent call = new Intent(Intent.ACTION_CALL);
        call.setData(Uri.parse("tel:+1" + numText.trim()));
        startActivity(call);
    }
}

