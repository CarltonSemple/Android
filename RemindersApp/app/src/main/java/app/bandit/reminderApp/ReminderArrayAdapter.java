package app.bandit.reminderApp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Carlton Semple on 9/27/2015.
 */
public class ReminderArrayAdapter extends ArrayAdapter<Reminder> {

    public ReminderArrayAdapter(Context context, ArrayList<Reminder> reminders) {
        super(context, 0, reminders);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Reminder reminder = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_row_layout, parent, false);
        }
        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(R.id.row_main_text);
        TextView monthDay = (TextView) convertView.findViewById(R.id.row_month_day);
        TextView hourMinute = (TextView) convertView.findViewById(R.id.row_time);

        // Populate the data into the template view using the data object
        title.setText(reminder.getTitle());
        monthDay.setText(reminder.getMonthDay() + ",");
        hourMinute.setText(reminder.getTimeOfDay());

        // Return the completed view to render on screen
        return convertView;
    }

}
