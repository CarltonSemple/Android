package app.bandit.reminderApp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.Button;

import app.bandit.reminderApp.R;
import app.bandit.reminderApp.floatingactionbuttonbasic.FloatingActionButton;

/**
 *
 * Created by Carlton Semple on 9/27/2015.
 */
public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, View.OnClickListener, FloatingActionButton.OnCheckedChangeListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private ActionBarDrawerToggle mDrawerToggle = null; // Switch the drawer icon between a back arrow & hamburger (3 bars)
    private DrawerLayout mDrawerLayout;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        mTitle = getTitle();


        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.abc_ic_menu_copy_mtrl_am_alpha));
        //getSupportActionBar().setDisplayUseLogoEnabled(true);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        // Drawer State toggler for hamburger || back arrow
        mDrawerToggle = new ActionBarDrawerToggle(this, (DrawerLayout)findViewById(R.id.drawer_layout), R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            public void OnDrawerClosed(View view){
                getActionBar().setTitle("");
            }

            public void OnDrawerOpened(View view){
                getActionBar().setTitle("");
            }
        };

        //mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        /* Set up click listeners for buttons */
        //Button createButton = (Button)findViewById(R.id.createButton);
        //createButton.setOnClickListener(this);
        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
        fab1.setOnCheckedChangeListener(this);
    }

    /**
     * Change Fragments. Function called by NavigationDrawerFragment. Fills the container FrameLayout
     * defined in activity_main.xml
     * @param position
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (position)
        {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, UpcomingFragment.newInstance(position + 1))
                        .commit();
                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PastFragment.newInstance(position + 1))
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, AboutFragment.newInstance("22", "11"))
                        .commit();
                break;
        }

        if(mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /**
     * Called by Fragment's onAttach().  Update title of action bar
     * @param number
     */
    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = "About";
                break;
        }

        // Update title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mTitle);

        // Signal navigation drawer change, updating icon
        if(mDrawerToggle != null)
            mDrawerToggle.syncState();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);

        actionBar.setTitle(mTitle);

        if(mDrawerToggle != null)
            mDrawerToggle.syncState();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    /*
    @Override
    public void onBackPressed(){
        (mNavigationDrawerFragment.isDrawerOpen()) ? mNavigationDrawerFragment.

    }
    */

    /**
     * Handle button clicks for this activity
     * @param v
     */
    public void onClick(View v){
        switch (v.getId()){/*
            case R.id.createButton:
                // Start activity without passing any variables
                Intent intent = new Intent(this, CreateReminderActivity.class);
                startActivity(intent);
                break;*/
        }
    }

    /**
     * Handle when the Floating Action Button is checked.  Go to the create reminder activity
     * @param fabView   The FAB view whose state has changed.
     * @param isChecked The new checked state of buttonView.
     */
    @Override
    public void onCheckedChanged(FloatingActionButton fabView, boolean isChecked) {
        // When a FAB is toggled, log the action.
        switch (fabView.getId()){
            case R.id.fab_1:
                // Start activity without passing any variables
                Intent intent = new Intent(this, CreateReminderActivity.class);
                startActivity(intent);
            default:
                break;
        }
    }
}