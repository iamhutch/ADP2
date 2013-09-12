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

import org.json.JSONException;
import org.json.JSONObject;

import com.lucyhutcheson.libs.AppPreferences;
import com.lucyhutcheson.libs.GetDataService;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public static final String TAG = "MainActivity";
	private AppPreferences _appPrefs;
	Context _context;
	String _zipCode;

	// Handle communication between this activity and
	// GetDataService class
	@SuppressLint("HandlerLeak")
	Handler searchServiceHandler = new Handler() {

		public void handleMessage(Message mymessage) {

			Log.i(TAG, "HANDLER STARTED");

			if (mymessage.arg1 == RESULT_OK	&& mymessage.obj != null) {
		        Log.i("RESPONSE", mymessage.obj.toString());
				JSONObject json = null;
				try {
					json = new JSONObject(mymessage.obj.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Log.i("UPDATE WITH JSON", json.toString());
				updateWeather(json);
				
			} else if (mymessage.arg1 == RESULT_CANCELED && mymessage.obj != null){
				Toast.makeText(MainActivity.this,mymessage.obj.toString(), Toast.LENGTH_LONG).show();

			} else {
				Toast.makeText(MainActivity.this,"Download failed.", Toast.LENGTH_LONG).show();
			}	
		}

	};
	
	/**
	 * Updates all textviews with received JSON data.
	 * 
	 * @param data
	 *            the data
	 */
	public void updateWeather(JSONObject data) {
		Log.i("UPDATE DATA", data.toString());
		try {
			JSONObject city = data.getJSONObject("display_location");
			String forecast = data.getString("temp_f");
			Log.i(TAG, forecast);
			((TextView) findViewById(R.id.forecast)).setText(forecast +  (char) 0x00B0);
			((TextView) findViewById(R.id.city)).setText(city.getString("full"));

		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("JSON ERROR", e.toString());
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		_appPrefs = new AppPreferences(getApplicationContext());
		if (_appPrefs.getZip().length() > 0) {
			_zipCode = _appPrefs.getZip();
			Log.i(TAG, "SAVED ZIP CODE: " + _zipCode);
			
			// GET WEATHER INFORMATION BASED ON ZIP CODE IN SHARED PREFERENCES
			Messenger messenger = new Messenger(searchServiceHandler);
			Intent startServiceIntent = new Intent(getApplicationContext(), GetDataService.class);
			startServiceIntent.putExtra(GetDataService.MESSENGER_KEY,messenger);
			startServiceIntent.setData(Uri.parse("http://api.wunderground.com/api/c6dc8ff98c36bc6c/conditions/q/"+Uri.encode(_zipCode)+".json"));
			startService(startServiceIntent);
			
		} else {
    		Log.i(TAG, "Zip field is empty.");
			
    		// ALERT USER OF EMPTY FIELD
			Toast.makeText(this, "No zip code found", Toast.LENGTH_LONG).show();
		}
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
