package com.lucyhutcheson.houseboss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import com.lucyhutcheson.libs.FileFunctions;
import com.lucyhutcheson.libs.ReminderSingleton;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class EditActivity extends Activity {

	public static final String TAG = "EditActivity";
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
	private ArrayList<HashMap<String, String>> _reminders;
	public static final String REMINDER_FILENAME = "reminders";
	private String _reminderTitle;
	private static Date _myReminderDate;
	private static Date _myReminderTime;
	private String _myDate;
	private String _myTime;
	String _categorySelected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);

		// ADD DATA FROM SINGLETON TO OUR TEXTVIEWS IN LAYOUT
		HashMap<String, String> myItem = ReminderSingleton.getInstance()
				.get_reminder();

		String _reminderDate = (myItem.get("year") + "-" + myItem.get("month")
				+ "-" + myItem.get("day"));
		String _reminderTime = (myItem.get("hour") + ":" + myItem.get("minute"));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		_myDate = dateFormat.format(_reminderDate);
		_myTime = timeFormat.format(_reminderTime);

		// _selected = new HashMap<String,
		// String>(ReminderSingleton.getInstance().get_reminder());
		((TextView) findViewById(R.id.titleField)).setText(myItem.get("title"));
		((TextView) findViewById(R.id.descriptionField)).setText(myItem.get("description"));
		//((TextView) findViewById(R.id.dateField)).setText(_myDate);
		//((TextView) findViewById(R.id.timeField)).setText(_myTime);

		/*
		 * Spinner mySpinner = (Spinner) findViewById(R.id.categorySpinner);
		 * 
		 * @SuppressWarnings("unchecked") ArrayAdapter<String> adapter =
		 * (ArrayAdapter<String>) mySpinner.getAdapter(); int spinnerPosition =
		 * adapter.getPosition(myItem.get("category"));
		 * mySpinner.setSelection(spinnerPosition);
		 */

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
			Random _Id = new Random();
			int _reminderID = _Id.nextInt();
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
