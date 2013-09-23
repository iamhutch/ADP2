/*
 * project		HouseBoss
 * 
 * package		com.lucyhutcheson.houseboss
 * 
 * @author		Lucy Hutcheson
 * 
 * date			Sep 19, 2013
 * 
 */
package com.lucyhutcheson.houseboss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.lucyhutcheson.libs.DatePickerFragment;
import com.lucyhutcheson.libs.FileFunctions;
import com.lucyhutcheson.libs.ReminderSingleton;
import com.lucyhutcheson.libs.ScheduleClient;
import com.lucyhutcheson.libs.TimePickerFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.format.Time;
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

/**
 * Add Activity class created to handle the addition of reminders and save the
 * reminders to internal storage.
 * 
 */
@SuppressLint("SimpleDateFormat")
public class AddActivity extends Activity {

	public static final String TAG = "AddActivity";
	private ScheduleClient scheduleClient;
	Context _context;
	private static int _hour;
	private static int _minute;
	private static int _day;
	private static int _month;
	private static int _year;
	private static Date _myReminderDate;
	Calendar _c;
	static EditText _titleField;
	static EditText _descriptionField;
	static EditText _dateField;
	static EditText _timeField;
	String _categorySelected;
	Spinner _category;
	private ArrayList<HashMap<String, String>> _reminders;
	public static final String REMINDER_FILENAME = "reminders";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// SETUP ACTIVITY
		setContentView(R.layout.activity_add);
		_titleField = (EditText) findViewById(R.id.titleField);
		_descriptionField = (EditText) findViewById(R.id.descriptionField);
		_dateField = (EditText) findViewById(R.id.dateField);
		_timeField = (EditText) findViewById(R.id.timeField);

		// Add today's date and time to the text fields
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		((EditText) findViewById(R.id.dateField)).setHint("Example: "
				+ today.format("%m/%d/%Y"));
		((EditText) findViewById(R.id.timeField)).setHint("Example: "
				+ today.format("%l:%M %p"));

		// SETUP CATEGORY SPINNER
		List<String> SpinnerArray = new ArrayList<String>();
		SpinnerArray.add("Interior");
		SpinnerArray.add("Exterior");

		// CREATE ADAPTER FOR MY DROPDOWN
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, SpinnerArray);
		_category = (Spinner) findViewById(R.id.categorySpinner);
		_category.setAdapter(adapter);
		_category.setOnItemSelectedListener(new OnItemSelectedListener() {
			// IF A CATEGORY IS SELECTED
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int pos,
					long id) {
				_categorySelected = parent.getItemAtPosition(pos).toString();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		// CREATE A NEW SERVICE CLIENT AND BIND OUR ACTIVITY TO THIS SERVICE
		scheduleClient = new ScheduleClient(this);
		scheduleClient.doBindService();

	}

	/**
	 * This is the onClick called from the XML to set a new notification.
	 */
	public void onSubmit() {

		// CHECK IF OUR TITLE, DATE, OR TIME FIELDS ARE EMPTY
		if (_titleField.getText().toString().equals("")) {
			// NOTIFY USER THAT THERE ARE EMPTY FIELDS
			Toast.makeText(this, "Please enter a title.", Toast.LENGTH_SHORT).show();
		} else if (_dateField.getText().toString().equals("")) {
			// NOTIFY USER THAT THERE ARE EMPTY FIELDS
			Toast.makeText(this, "Please enter a date.", Toast.LENGTH_SHORT).show();
		} else if (_timeField.getText().toString().equals("")) {
			// NOTIFY USER THAT THERE ARE EMPTY FIELDS
			Toast.makeText(this, "Please enter a time.", Toast.LENGTH_SHORT).show();
			
		// ALL NECESSARY DATA HAS BEEN ENTERED, PROCEED WITH SAVING
		} else {
			_reminders = new ArrayList<HashMap<String, String>>();

			/*
			 * PULL SAVED DATA FIRST AND THEN ADD OUR NEW DATA
			 */
			try {
				_reminders.addAll(MainActivity.getSavedReminders());
				Log.i(TAG, "SAVED STRING " + _reminders.toString());
			} catch (Exception e) {
				Log.e("JSINTERFACE", "No saved data found.");
				e.printStackTrace();
			}

			String convertedTime = convertTime(_hour, _minute);
			Random _Id = new Random();
			int _reminderID = _Id.nextInt();
			String _reminderDate = _year + "-" + _month + "-" + _day + " " + _hour + ":" + _minute;
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			try {
				_myReminderDate = dateFormat.parse(_reminderDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			// ADD OUR DATA TO A HASHMAP
			HashMap<String, String> _newReminder = new HashMap<String, String>();
			_newReminder.put("id", String.valueOf(_reminderID));
			_newReminder.put("title", _titleField.getText().toString());
			_newReminder.put("description", _descriptionField.getText()
					.toString());
			_newReminder.put("category", _categorySelected);
			_newReminder.put("year", Integer.toString(_year));
			_newReminder.put("month", Integer.toString(_month + 1));
			_newReminder.put("day", Integer.toString(_day));
			_newReminder.put("hour", Integer.toString(_hour));
			_newReminder.put("minute", Integer.toString(_minute));
			_newReminder.put("time", convertedTime);
			_newReminder.put("fulldate", _myReminderDate.toString());

			// STORE DATA IN SINGLETON AND FILE STORAGE
			ReminderSingleton.getInstance().set_reminder(_newReminder);
			_reminders.add(_newReminder);
			FileFunctions.storeObjectFile(getApplicationContext(),
					REMINDER_FILENAME, _reminders, false);

			// Create a new calendar set to the date chosen
			// we set the time to midnight (i.e. the first minute of that day)
			_c = Calendar.getInstance();
			_c.set(_year, _month, _day);
			_c.set(Calendar.HOUR_OF_DAY, _hour);
			_c.set(Calendar.MINUTE, _minute);
			_c.set(Calendar.SECOND, 0);
			
			String _reminderTitle = _titleField.getText().toString();

			// Ask our service to set an alarm for that date, this activity
			// talks to the client that talks to the service
			scheduleClient.setAlarmForNotification(_c, _reminderID, _reminderTitle);

			// Notify the user what they just did
			Toast.makeText(
					this,
					"Notification set for: " + (_month + 1) + "/" + _day + "/"
							+ _year + " " + convertedTime, Toast.LENGTH_SHORT)
					.show();

			// FINISH
			((AddActivity) this).finish();
		}

	}

	/**
	 * Show date picker dialog.
	 * 
	 * @param v
	 *            the v
	 */
	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
	}

	/**
	 * Show time picker dialog.
	 * 
	 * @param v
	 *            the v
	 */
	public void showTimePickerDialog(View v) {
		DialogFragment newFragment = new TimePickerFragment();
		newFragment.show(getFragmentManager(), "timePicker");
	}

	/**
	 * Sets the date.
	 * 
	 * @param year
	 *            the year
	 * @param month
	 *            the month
	 * @param day
	 *            the day
	 */
	public static void setDate(int year, int month, int day) {
		_day = day;
		_month = month;
		_year = year;
		_dateField.setText(Integer.toString(month + 1) + "/"
				+ Integer.toString(day) + "/" + Integer.toString(year));
	}

	/**
	 * Sets the time.
	 * 
	 * @param hourOfDay
	 *            the hour of day
	 * @param minute
	 *            the minute
	 */
	public static void setTime(int hourOfDay, int minute) {
		_hour = hourOfDay;
		_minute = minute;
		// set current time into textview
		_timeField.setText(convertTime(hourOfDay, minute));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// When our activity is stopped ensure we also stop the connection to
		// the service
		// this stops us leaking our activity into the system
		if (scheduleClient != null)
			scheduleClient.doUnbindService();
		super.onStop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_action_bar, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
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
	 */
	public void onCancel() {
		AddActivity.this.finish();
	}

	/**
	 * Used to convert 24hr format to 12hr format with AM/PM values.
	 * 
	 * @param hours
	 *            the hours
	 * @param mins
	 *            the mins
	 * @return the string
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
