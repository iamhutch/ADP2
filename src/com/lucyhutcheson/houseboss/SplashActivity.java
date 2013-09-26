/*
 * project		HouseBoss
 * 
 * package		com.lucyhutcheson.houseboss
 * 
 * @author		Lucy Hutcheson
 * 
 * date			Sep 11, 2013
 * 
 */
package com.lucyhutcheson.houseboss;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
 *  SplashActivity Class created to show a splash screen for 2 seconds 
 *  and then redirect user to main activity
 */
public class SplashActivity extends Activity {

	private static String TAG = SplashActivity.class.getName();
	private static long SLEEP_TIME = 2; // Sleep for some time

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // Removes title bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); // Removes
																// notification
																// bar

		setContentView(R.layout.activity_splash);

		// Start timer and launch main activity
		IntentLauncher launcher = new IntentLauncher();
		launcher.start();
	}

	/**
	 * The Class IntentLauncher.
	 */
	private class IntentLauncher extends Thread {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			try {
				// Sleeping
				Thread.sleep(SLEEP_TIME * 2000);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}

			// INTENT TO START MAIN ACTIVITY
			Intent intent = new Intent(SplashActivity.this, SetupActivity.class);
			SplashActivity.this.startActivity(intent);
			SplashActivity.this.finish();
		}
	}
	
}
