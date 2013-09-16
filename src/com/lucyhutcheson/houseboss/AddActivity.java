package com.lucyhutcheson.houseboss;

import java.util.Calendar;

import com.lucyhutcheson.libs.ScheduleClient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddActivity extends Activity {

	public static final String TAG = "AddActivity";
	// This is a handle so that we can call methods on our service
	private ScheduleClient scheduleClient;
	// This is the date picker used to select the date for our notification
	private DatePicker _datePicker;
	private TimePicker _timePicker;
	private int _hour;
	private int _minute;
	private int _day;
	private int _month;
	private int _year;
	Calendar _c;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);

		_c = Calendar.getInstance();
		_hour = _c.get(Calendar.HOUR_OF_DAY);
		_minute = _c.get(Calendar.MINUTE);

		// Create a new service client and bind our activity to this service
		scheduleClient = new ScheduleClient(this);
		scheduleClient.doBindService();
		// Get a reference to our date picker

		_datePicker = (DatePicker) findViewById(R.id.datePicker);
		_timePicker = (TimePicker) findViewById(R.id.timePicker);

		// set current time into timepicker
		_timePicker.setCurrentHour(_hour);
		_timePicker.setCurrentMinute(_minute);
	}

	/**
	 * This is the onClick called from the XML to set a new notification
	 */
	public void onSubmit(View v) {
		// Get the date from our datepicker
		_day = _datePicker.getDayOfMonth();
		_month = _datePicker.getMonth();
		_year = _datePicker.getYear();

		// Create a new calendar set to the date chosen
		// we set the time to midnight (i.e. the first minute of that day)
		_c.set(_year, _month, _day);
		_c.set(Calendar.HOUR_OF_DAY, 0);
		_c.set(Calendar.MINUTE, 0);
		_c.set(Calendar.SECOND, 0);

		// Ask our service to set an alarm for that date, this activity talks to
		// the client that talks to the service
		scheduleClient.setAlarmForNotification(_c);

		// Notify the user what they just did
		Toast.makeText(
				this,
				"Notification set for: " + _day + "/" + (_month + 1) + "/"
						+ _year, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onStop() {
		// When our activity is stopped ensure we also stop the connection to
		// the service
		// this stops us leaking our activity into the system *bad*
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
			// onSettingsActivity();
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


}
