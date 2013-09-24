package com.lucyhutcheson.houseboss;

import com.lucyhutcheson.libs.AppPreferences;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	public static final String TAG = "SetupActivity";
	private AppPreferences _appPrefs;
	EditText _zipText;

	@Override
	public void onStart() {
		super.onStart();
		String _savedZip = _appPrefs.getZip();

		// CHECK IF WE HAVE A SAVED ZIP CODE
		if (_savedZip.length() > 0) {
			// ADD ZIP CODE TO EDITTEXT
			_zipText.setText(_savedZip);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		_appPrefs = new AppPreferences(getApplicationContext());
		_zipText = (EditText) findViewById(R.id.zipText);
	}

	public void onSubmit() {

		// CHECK IF WE HAVE ANY TEXT ENTERED IN THE FIELD
		if (_zipText.getText().length() > 0) {
			Log.i(TAG, _zipText.getText().toString());
			_appPrefs.saveZip(_zipText.getText().toString());

			// INTENT TO START MAIN ACTIVITY
			Intent intent = new Intent(SettingsActivity.this,
					MainActivity.class);
			SettingsActivity.this.startActivity(intent);
			SettingsActivity.this.finish();
		} else {
			Log.i(TAG, "Zip field is empty.");

			// ALERT USER OF EMPTY FIELD
			Toast.makeText(this, "Please enter a zip code or press Skip",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
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
			SettingsActivity.this.finish();
			return true;
		case R.id.action_save:
			onSubmit();
			return true;
		case R.id.action_cancel:
			SettingsActivity.this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
