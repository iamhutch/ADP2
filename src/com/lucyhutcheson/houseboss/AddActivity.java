package com.lucyhutcheson.houseboss;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.lucyhutcheson.libs.DatePickerFragment;
import com.lucyhutcheson.libs.FileFunctions;
import com.lucyhutcheson.libs.ReminderSingleton;
import com.lucyhutcheson.libs.ScheduleClient;
import com.lucyhutcheson.libs.TimePickerFragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class AddActivity extends Activity {

	public static final String TAG = "AddActivity";
	private ScheduleClient scheduleClient;
	Context _context;
	private static int _hour;
	private static int _minute;
	private static int _day;
	private static int _month;
	private static int _year;
	Calendar _c;
	static EditText _titleField;
	static EditText _descriptionField;
	static EditText _dateField;
	static EditText _timeField;
	Spinner _category;
	private ArrayList<HashMap<String, String>> _reminders;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);

	    List<String> SpinnerArray =  new ArrayList<String>();
	    SpinnerArray.add("Interior");
	    SpinnerArray.add("Exterior");

		// CREATE ADAPTER FOR MY DROPDOWN
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, SpinnerArray);
		_category = (Spinner) findViewById(R.id.categorySpinner);
		_category.setAdapter(adapter);
		
		_category.setOnItemSelectedListener(new OnItemSelectedListener() {
			// IF A CATEGORY IS SELECTED
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
				String str = parent.getItemAtPosition(pos).toString();
				Log.i(TAG, str);
				
				// MAKE SURE THAT WE AREN'T SELECTING OUR PLACEHOLDER
				if (!str.equals("View Favorites")) {

				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		// Create a new service client and bind our activity to this service
		scheduleClient = new ScheduleClient(this);
		scheduleClient.doBindService();

		_titleField = (EditText) findViewById(R.id.titleField);
		_descriptionField = (EditText) findViewById(R.id.descriptionField);
		_dateField = (EditText) findViewById(R.id.dateField);
		_timeField = (EditText) findViewById(R.id.timeField);

	}

	/**
	 * This is the onClick called from the XML to set a new notification
	 */
	public void onSubmit() {

		_reminders = new ArrayList<HashMap<String, String>>();
		
        HashMap<String, String> _newReminder = new HashMap<String, String>();
        _newReminder.put("title", _titleField.getText().toString());
        _newReminder.put("description", _descriptionField.getText().toString());
        _newReminder.put("year", Integer.toString(_year));
        _newReminder.put("month", Integer.toString(_month));
        _newReminder.put("day", Integer.toString(_day));
        _newReminder.put("hour", Integer.toString(_hour));
        _newReminder.put("minute", Integer.toString(_minute));
        
		Log.i(TAG, _newReminder.toString());

		// Store data in singleton and file storage
		ReminderSingleton.getInstance().set_reminder(_newReminder);
		_reminders.add(_newReminder);
		FileFunctions.storeObjectFile(this, "reminders", _reminders, false);
        

		// Create a new calendar set to the date chosen
		// we set the time to midnight (i.e. the first minute of that day)
		_c = Calendar.getInstance();
		_c.set(_year, _month, _day);
		_c.set(Calendar.HOUR_OF_DAY, _hour);
		_c.set(Calendar.MINUTE ,_minute);
		_c.set(Calendar.SECOND, 0);

		
		// Ask our service to set an alarm for that date, this activity talks to
		// the client that talks to the service
		scheduleClient.setAlarmForNotification(_c);

		// Notify the user what they just did
		Toast.makeText(
				this,
				"Notification set for: " + _day + "/" + (_month + 1) + "/"
						+ _year, Toast.LENGTH_SHORT).show();
	
        // Finish
        ((AddActivity)this).finish();

	}


	public void showDatePickerDialog(View v) {
	    DialogFragment newFragment = new DatePickerFragment();
	    newFragment.show(getFragmentManager(), "datePicker");
	}
	public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    newFragment.show(getFragmentManager(), "timePicker");
	}
	public static void setDate(int year, int month, int day) {
		_day = day;
		_month = month;
		_year = year;
		_dateField.setText(Integer.toString(month+1) + "/" + Integer.toString(day) + "/" + Integer.toString(year) );
	}

	public static void setTime(int hourOfDay, int minute) {
		_hour = hourOfDay;
		_minute = minute;
		// set current time into textview
		_timeField.setText(convertTime(hourOfDay, minute));

	}
	
	@Override
	protected void onStop() {
		// When our activity is stopped ensure we also stop the connection to
		// the service
		// this stops us leaking our activity into the system 
		if (scheduleClient != null)
			scheduleClient.doUnbindService();
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_action_bar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case android.R.id.home:
			AddActivity.this.finish();
			return true;
		case R.id.action_save:
			onSubmit();
			return true;
		case R.id.action_cancel:
			onCancel();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    /**
     * On click of the Skip button.
     *
     * @param v the v
     */
    public void onCancel() {
		AddActivity.this.finish();
    }  

	
    /**
     * Used to convert 24hr format to 12hr format with AM/PM values
     *
     * @param integer mins, integer hours
     */
	private static String convertTime(int hours, int mins) {
		
		String timeSet = "";
		if (hours > 12) {
			hours -= 12;
			timeSet = "PM";
		} else if (hours == 0) {
			hours += 12;
			timeSet = "AM";
		} else if (hours == 12)
			timeSet = "PM";
		else
			timeSet = "AM";

		// Pad minute with zero
		String minutes = "";
		if (mins < 10)
			minutes = "0" + mins;
		else
			minutes = String.valueOf(mins);

		// Append in a StringBuilder
		 String timeString = new StringBuilder().append(hours).append(':')
				.append(minutes).append(" ").append(timeSet).toString();

		  return timeString;
	}


}
