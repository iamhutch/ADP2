package com.lucyhutcheson.houseboss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.lucyhutcheson.libs.EditDatePickerFragment;
import com.lucyhutcheson.libs.EditTimePickerFragment;
import com.lucyhutcheson.libs.FileFunctions;
import com.lucyhutcheson.libs.ReminderSingleton;
import com.lucyhutcheson.libs.ScheduleClient;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
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

@SuppressLint("SimpleDateFormat")
public class EditActivity extends Activity {

	public static final String TAG = "EditActivity";
	private ScheduleClient scheduleClient;
	Calendar _c;
	private static int _hour;
	private static int _minute;
	private static int _day;
	private static int _month;
	private static int _year;
	static EditText _titleField;
	static EditText _descriptionField;
	static EditText _dateField;
	static EditText _timeField;
	private ArrayList<HashMap<String, HashMap<String,String>>> _reminderArrayList;
	private ArrayList<HashMap<String, String>> _reminders;
	public static final String REMINDER_FILENAME = "reminders";
	private String _reminderTitle;
	private static Date _myReminderDate;
	private String _categorySelected;
	private Spinner _category;
	private	String _reminderID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// SETUP ACTIVITY		
		setContentView(R.layout.activity_edit);
		_titleField = (EditText) findViewById(R.id.titleField);
		_descriptionField = (EditText) findViewById(R.id.descriptionField);
		_dateField = (EditText) findViewById(R.id.dateField);
		_timeField = (EditText) findViewById(R.id.timeField);
		_reminderTitle = null;

		// ADD DATA FROM SINGLETON TO OUR TEXTVIEWS IN LAYOUT
		HashMap<String, String> myItem = ReminderSingleton.getInstance()
				.get_reminder();
		_reminderID = myItem.get("id");
		Log.i(TAG, myItem.get("year"));

		try {
		    _hour = Integer.parseInt(myItem.get("hour"));
		    _minute = Integer.parseInt(myItem.get("minute"));
		    _month = Integer.parseInt(myItem.get("month"));
		    _day = Integer.parseInt(myItem.get("day"));
		    _year = Integer.parseInt(myItem.get("year"));
		} catch(NumberFormatException nfe) {
		   System.out.println("Could not parse " + nfe);
		} 

		// ADD OUR EDITING VALUES TO OUR TEXT FIELDS
		_titleField.setText(myItem.get("title"));
		_descriptionField.setText(myItem.get("description"));
		_dateField.setText(myItem.get("month") + "/" + myItem.get("day") + "/" + myItem.get("year"));
		setTime(_hour, _minute);
		
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
		// SET SPINNER TO OUR VALUE
		_category.setSelection(getIndex(_category, myItem.get("category")));

	}
	
	
	private int getIndex(Spinner spinner, String choice) {
		int index = 0;
		for (int i=0; i<spinner.getCount(); i++) {
			if (spinner.getItemAtPosition(i).equals(choice)) {
				index = i;
			}
		}
		return index;
	}

	/**
	 * This is the onClick called from the XML to set a new notification.
	 */
	@SuppressLint("SimpleDateFormat")
	public void onSubmit() {

		// CHECK IF OUR TITLE, DATE, OR TIME FIELDS ARE EMPTY
		if (_titleField.getText().toString().equals("")) {
			// NOTIFY USER THAT THERE ARE EMPTY FIELDS
			Toast.makeText(this, "Please enter a title.", Toast.LENGTH_SHORT)
					.show();
		} else if (_dateField.getText().toString().equals("")) {
			// NOTIFY USER THAT THERE ARE EMPTY FIELDS
			Toast.makeText(this, "Please enter a date.", Toast.LENGTH_SHORT)
					.show();
		} else if (_timeField.getText().toString().equals("")) {
			// NOTIFY USER THAT THERE ARE EMPTY FIELDS
			Toast.makeText(this, "Please enter a time.", Toast.LENGTH_SHORT)
					.show();

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

			// SETUP OUR VARIABLES TO BE SAVED
			String convertedTime = convertTime(_hour, _minute);

			_reminderTitle = _titleField.getText().toString();
			Log.i("ADD ACTIVITY", "Reminder Title: " + _reminderTitle);
			String _reminderDate = _year + "-" + _month + "-" + _day + " "
					+ _hour + ":" + _minute;
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm");
			try {
				_myReminderDate = dateFormat.parse(_reminderDate);
			} catch (ParseException e) {
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

			HashMap<String, HashMap<String, String>> _reminderItem = new HashMap<String, HashMap<String, String>>();
			_reminderItem.put(String.valueOf(_reminderID), _newReminder);

			// STORE DATA IN SINGLETON AND FILE STORAGE
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

			// Ask our service to set an alarm for that date, this activity
			// talks to the client that talks to the service
			// ScheduleClient.setAlarmForNotification(_c, _reminderID,
			// _reminderTitle);
			Log.i(TAG, "Setalarmfornotification: " + _reminderTitle);

			// Notify the user what they just did
			Toast.makeText(
					this,
					"Notification set for: " + (_month + 1) + "/" + _day + "/"
							+ _year + " " + convertedTime, Toast.LENGTH_SHORT)
					.show();

			// FINISH
			((EditActivity) this).finish();
		}

	}
	
	public void deleteReminder() {

		/*
		 * PULL SAVED DATA FIRST AND THEN ADD OUR NEW DATA
		 */
		_reminderArrayList = MainActivity.getSavedReminderMaster();
		_reminders = new ArrayList<HashMap<String, String>>();
		Log.i(TAG, "AFTER DELETED STRING " + _reminderArrayList.toString());

		// GO THROUGH THE ARRAYLIST
		_reminderArrayList.remove(_reminderID);
		/*for (HashMap<String, HashMap<String, String>> hashMap1 : _reminderArrayList) {
			// GET THE VALUES
			for (Map.Entry<String, HashMap<String, String>> entry : hashMap1
					.entrySet()) {
				// FIND THE ENTRY THAT MATCHES OUR ID
				if (entry.getKey().equals(_reminderID)) {
					
				}
			}
		}*/

		
		_reminders.remove(_reminderID);
		Log.i(TAG, "AFTER DELETED STRING " + _reminders.toString());
		
		// INTENT TO START MAIN ACTIVITY
		Intent intent = new Intent(EditActivity.this, MainActivity.class);
		EditActivity.this.startActivity(intent);
		EditActivity.this.finish();
		
	}

	/**
	 * Show date picker dialog.
	 * 
	 * @param v
	 *            the v
	 */
	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new EditDatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
	}

	/**
	 * Show time picker dialog.
	 * 
	 * @param v
	 *            the v
	 */
	public void showTimePickerDialog(View v) {
		DialogFragment newFragment = new EditTimePickerFragment();
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
		_dateField.setText(Integer.toString(month + 1) + "/"+ Integer.toString(day) + "/" + Integer.toString(year));
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
		getMenuInflater().inflate(R.menu.edit_action_bar, menu);
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
			EditActivity.this.finish();
			return true;
		case R.id.action_delete:
			deleteReminder();
			return true;
		case R.id.action_save:
			// onSubmit();
			return true;
		case R.id.action_cancel:
			EditActivity.this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
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
