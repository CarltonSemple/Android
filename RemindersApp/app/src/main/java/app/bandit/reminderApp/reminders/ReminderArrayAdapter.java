package app.bandit.reminderApp.reminders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import app.bandit.reminderApp.R;
import app.bandit.reminderApp.reminders.Reminder;

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
        TextView monthDayTime = (TextView) convertView.findViewById(R.id.row_month_day);
        TextView details = (TextView) convertView.findViewById(R.id.row_detail_text);

        // Populate the data into the template view using the data object
        title.setText(reminder.getTitle());
        monthDayTime.setText(reminder.getMonthDayPlain() + ", " + reminder.getTimeOfDay());
        details.setText(reminder.getDetails());

        // Return the completed view to render on screen
        return convertView;
    }

}
