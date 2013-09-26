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

import com.lucyhutcheson.houseboss.R;
import com.lucyhutcheson.houseboss.ViewActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class NotifyService extends Service {

	/**
	 * Class for clients to access
	 */
	public class ServiceBinder extends Binder {
		NotifyService getService() {
			return NotifyService.this;
		}
	}

	// Unique id to identify the notification.
	private static int NOTIFICATION_ID = 123;
	// Name of an intent extra we can use to identify if this service was
	// started to create a notification
	public static final String INTENT_NOTIFY = "com.lucyhutcheson.houseboss.INTENT_NOTIFY";
	// Name of the intent extra info ID
	public static final String INTENT_ID = "com.lucyhutcheson.houseboss.INTENT_ID";
	// Name of the intent extra info TITLE
	public static final String INTENT_TITLE = "com.lucyhutcheson.houseboss.INTENT_TITLE";
	
	// The system notification manager
	private NotificationManager mNM;

	@Override
	public void onCreate() {
		Log.i("NotifyService", "onCreate()");
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);

		// If this service was started by out AlarmTask intent then we want to
		// show our notification
		if (intent.getBooleanExtra(INTENT_NOTIFY, false))
			showNotification(intent);

		// We don't care if this service is stopped as we have already delivered
		// our notification
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients
	private final IBinder mBinder = new ServiceBinder();

	/**
	 * Creates a notification and shows it in the OS drag-down status bar
	 */
	private void showNotification(Intent intent) {
		
		Bundle extras = intent.getExtras();
		
		if (extras != null) {
			NOTIFICATION_ID =  extras.getInt(INTENT_ID);
		} else {
			NOTIFICATION_ID = 123;
		}
		Log.i("NOTIFY SERVICE INTENT", Integer.toString(extras.getInt(INTENT_ID)));
		
		// This is the 'title' of the notification
		CharSequence title = intent.getCharSequenceExtra(INTENT_TITLE);
		Log.i("NOTIFY SERVICE", "Setalarmfornotification: " + title);
		// This is the icon to use on the notification
		int icon = R.drawable.ic_launcher;
		// This is the scrolling text of the notification
		CharSequence text = "Happy Reminder from House Boss";
		// What time to show on the notification
		long time = System.currentTimeMillis();


		// The PendingIntent to launch our activity with the id if the user selects this
		// notification
		Intent _intent = new Intent(this, ViewActivity.class);
		_intent.putExtra(INTENT_ID, NOTIFICATION_ID);
		_intent.setAction("houseBoss"+NOTIFICATION_ID);
		_intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), NOTIFICATION_ID, _intent, PendingIntent.FLAG_UPDATE_CURRENT);
		_intent.setData(Uri.parse("houseBoss"+NOTIFICATION_ID));
		
		Log.i("NOTIFY SERVICE", Integer.toString(NOTIFICATION_ID));
		
		// SETUP NOTIFICATION
		Notification.Builder builder = new Notification.Builder(this);	
		builder.setContentIntent(contentIntent)
		.setSmallIcon(icon)
		.setContentTitle(title)
		.setWhen(time)
		.setAutoCancel(true)
		.setVibrate(new long[]{100, 250, 100, 500})
		.setContentText(text);
				
		Notification notification = builder.build();

		// Send the notification to the system.
		mNM.notify(NOTIFICATION_ID, notification);

		// Stop the service when we are finished
		stopSelf();
	}

}
