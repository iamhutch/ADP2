/*
 * project		HouseBoss
 * 
 * package		com.lucyhutcheson.libs
 * 
 * @author		Lucy Hutcheson
 * 
 * date			Sep 19, 2013
 * 
 */
package com.lucyhutcheson.libs;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmTask implements Runnable {
	// The date selected for the alarm
	private final Calendar date;
	// The android system alarm manager
	private final AlarmManager am;
	// Your context to retrieve the alarm manager from
	private final Context context;
	// My extra info of ID
	private final int _id;
	// My extra info of Title
	private final String _title;

	public AlarmTask(Context context, Calendar date, int id, String title) {
		this.context = context;
		this.am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		this.date = date;
		this._id = id;
		this._title = title;
		Log.i("ALARM TASK", "ID: " + Integer.toString(id) + " to ID: " + Integer.toString(_id));
		Log.i("ALARM TASK", "FROM TITLE: " + title + "TO " + _title);
	

	}

	@Override
	public void run() {
		// Request to start our service when the alarm date is here
		// We don't start an activity as we just want to pop up a notification
		// into the system bar not a full activity
		Intent intent = new Intent(context, NotifyService.class);
		intent.putExtra(NotifyService.INTENT_NOTIFY, true);
		intent.putExtra(NotifyService.INTENT_ID, _id);
		intent.putExtra(NotifyService.INTENT_TITLE, _title);
		PendingIntent pendingIntent = PendingIntent.getService(context, (int) System.currentTimeMillis(), intent, 0);

		// Sets an alarm - note this alarm will be lost if the phone is turned
		// off and on again
		am.set(AlarmManager.RTC_WAKEUP, date.getTimeInMillis(), pendingIntent);
	}

}
