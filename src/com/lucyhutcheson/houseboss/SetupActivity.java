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

import com.lucyhutcheson.libs.AppPreferences;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Class created to show a setup screen to the user on first launch of the application.
 * User will be able to save their zip code or skip the setup.
 */
public class SetupActivity extends Activity {
	
	public static final String TAG = "SetupActivity";
	private AppPreferences _appPrefs;

	@Override
	public void onStart() {
		super.onStart();
		String setupSet = _appPrefs.getSetup();
		
		// CHECK IF WE HAVE SEEN THIS SCREEN ALREADY
		if (setupSet.length() > 0) {
			
			// INTENT TO START MAIN ACTIVITY
			Intent intent = new Intent(SetupActivity.this, MainActivity.class);
			SetupActivity.this.startActivity(intent);
			SetupActivity.this.finish();
		}
	}

	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup);
		_appPrefs = new AppPreferences(getApplicationContext());
		
	}
	
    /**
     * On click of the Skip button.
     *
     * @param v the v
     */
    public void onSkip(View v) {
    	
    	// SAVE FIRST VIEW OF APP SO WE DON'T SEE THIS SCREEN AGAIN
    	_appPrefs.saveSetup("yes");

		// INTENT TO START MAIN ACTIVITY
		Intent intent = new Intent(SetupActivity.this, MainActivity.class);
		SetupActivity.this.startActivity(intent);
		SetupActivity.this.finish();
    }  

    
    /**
     * On submit.
     *
     * @param v the v
     */
    public void onSubmit(View v) {
    	
    	EditText zipText = (EditText) findViewById(R.id.zipText);
    	
    	// CHECK IF WE HAVE ANY TEXT ENTERED IN THE FIELD
    	if (zipText.getText().length() > 0) {
    		Log.i(TAG, zipText.getText().toString());
        	_appPrefs.saveZip(zipText.getText().toString());
        	
        	// SAVE FIRST VIEW OF APP SO WE DON'T SEE THIS SCREEN AGAIN
        	_appPrefs.saveSetup("yes");
        	
    		// INTENT TO START MAIN ACTIVITY
    		Intent intent = new Intent(SetupActivity.this, MainActivity.class);
    		SetupActivity.this.startActivity(intent);
    		SetupActivity.this.finish();
    	} else {
    		Log.i(TAG, "Zip field is empty.");
			
    		// ALERT USER OF EMPTY FIELD
			Toast.makeText(this,
					"Please enter a zip code or press Skip",
					Toast.LENGTH_LONG).show();
    	}
    }  


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
