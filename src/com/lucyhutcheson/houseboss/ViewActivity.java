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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.lucyhutcheson.libs.NotifyService;
import com.lucyhutcheson.libs.ReminderSingleton;
import com.socialize.ActionBarUtils;
import com.socialize.Socialize;
import com.socialize.entity.Entity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * View Activity will show the selected reminder that was saved in the Reminder
 * Singleton. This activity will allow the user to share the reminder or get to
 * the edit screen.
 */
public class ViewActivity extends Activity {

	public final static String TAG = "VIEWACTIVITY";
	public static final String INTENT_VIEW = "com.lucyhutcheson.houseboss.INTENT_VIEW";
	private ArrayList<HashMap<String, HashMap<String, String>>> _reminderArrayList;
	private ArrayList<HashMap<String, String>> _reminderList;
	private String _id;

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.i(TAG, "ON NEW INTENT");

		setIntent(intent);
	}

	@Override
	protected void onResume() {

		super.onResume();
		// Call Socialize in onResume
		Socialize.onResume(this);

		
		HashMap<String, String> _selected = new HashMap<String, String>();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			// CHECK IF WE ARE COMING FROM THE MAIN ACTIVITY
			if (extras.getBoolean(INTENT_VIEW)) {
				// ADD DATA FROM SINGLETON TO OUR TEXTVIEWS IN LAYOUT
				_selected = ReminderSingleton.getInstance().get_reminder();
				((TextView) findViewById(R.id.itemTitle)).setText(_selected
						.get("title"));
				((TextView) findViewById(R.id.itemDescription))
						.setText(_selected.get("description"));
				((TextView) findViewById(R.id.reminderMonth)).setText(_selected
						.get("month"));
				((TextView) findViewById(R.id.reminderDay)).setText(_selected
						.get("day"));
				((TextView) findViewById(R.id.reminderYear)).setText(_selected
						.get("year"));
				((TextView) findViewById(R.id.reminderTime)).setText(_selected
						.get("time"));
				((TextView) findViewById(R.id.itemCategory)).setText(_selected
						.get("category"));
				_id = _selected.get("id");

				getIntent().removeExtra(NotifyService.INTENT_ID);
			}
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
		// Log.i(TAG, "STARTING ACTIVITY");

		// Call Socialize in onCreate
		Socialize.onCreate(this, savedInstanceState);

		// Your entity key. May be passed as a Bundle parameter to your activity
		String entityKey = "http://www.lucyhutcheson.com";

		// Create an entity object including a name
		// The Entity object is Serializable, so you could also store the whole
		// object in the Intent
		Entity entity = Entity.newInstance(entityKey, "Socialize");

		// Wrap your existing view with the action bar.
		// your_layout refers to the resource ID of your current layout.
		View actionBarWrapped = ActionBarUtils.showActionBar(this,
				R.layout.activity_view, entity);

		// Now set the view for your activity to be the wrapped view.
		setContentView(actionBarWrapped);

		// setContentView(R.layout.activity_view);
		HashMap<String, String> _selected = new HashMap<String, String>();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			// CHECK IF WE ARE COMING FROM THE MAIN ACTIVITY
			if (extras.getBoolean(INTENT_VIEW)) {
				// ADD DATA FROM SINGLETON TO OUR TEXTVIEWS IN LAYOUT
				_selected = ReminderSingleton.getInstance().get_reminder();
				((TextView) findViewById(R.id.itemTitle)).setText(_selected
						.get("title"));
				((TextView) findViewById(R.id.itemDescription))
						.setText(_selected.get("description"));
				((TextView) findViewById(R.id.reminderMonth)).setText(_selected
						.get("month"));
				((TextView) findViewById(R.id.reminderDay)).setText(_selected
						.get("day"));
				((TextView) findViewById(R.id.reminderYear)).setText(_selected
						.get("year"));
				((TextView) findViewById(R.id.reminderTime)).setText(_selected
						.get("time"));
				TextView category = (TextView) findViewById(R.id.itemCategory);
				category.setText(_selected.get("category"));
				Log.i(TAG, _selected.get("category"));
				_id = _selected.get("id");

				getIntent().removeExtra(NotifyService.INTENT_ID);

				// OTHERWISE, WE ARE HERE BECAUSE OF A NOTIFICATION
			} else {
				_id = Integer.toString(extras.getInt(NotifyService.INTENT_ID));
				Log.i(TAG, _id);
				try {
					// GET OUR SAVED REMINDERS
					_reminderArrayList = MainActivity.getSavedReminderMaster();
					_reminderList = new ArrayList<HashMap<String, String>>();

					// GO THROUGH THE ARRAYLIST
					for (HashMap<String, HashMap<String, String>> hashMap1 : _reminderArrayList) {
						// GET THE VALUES
						for (Map.Entry<String, HashMap<String, String>> entry : hashMap1
								.entrySet()) {
							// FIND THE ENTRY THAT MATCHES OUR ID
							if (entry.getKey().equals(_id)) {
								// ADD THE MATCHING ITEM TO OUR ARRAYLIST
								_reminderList.add(entry.getValue());
								_selected.putAll(entry.getValue());
								ReminderSingleton.getInstance().set_reminder(
										_selected);
								Log.i(TAG, _selected.toString());
							}
						}
					}

					if (_reminderList != null) {
						((TextView) findViewById(R.id.itemTitle))
								.setText(_reminderList.get(0).get("title"));
						((TextView) findViewById(R.id.itemDescription))
								.setText(_reminderList.get(0)
										.get("description"));
						((TextView) findViewById(R.id.reminderMonth))
								.setText(_reminderList.get(0).get("month"));
						((TextView) findViewById(R.id.reminderDay))
								.setText(_reminderList.get(0).get("day"));
						((TextView) findViewById(R.id.reminderYear))
								.setText(_reminderList.get(0).get("year"));
						((TextView) findViewById(R.id.reminderTime))
								.setText(_reminderList.get(0).get("time"));
					} else {
						Toast.makeText(this,
								"There was an error retrieving our item.",
								Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					Log.e(TAG, "Error getting reminders");
					e.printStackTrace();
				}

			}
			getIntent().removeExtra(NotifyService.INTENT_ID);

		} else {
			Toast.makeText(this, "Extras is null.", Toast.LENGTH_SHORT).show();
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
		getMenuInflater().inflate(R.menu.view_action_bar, menu);
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
			ViewActivity.this.finish();
			return true;
		case R.id.action_edit:
			onEdit();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * IF USER CLICKS SHARE ICON FROM MAIN ACTION BAR
	 */
	public void onShare() {
		Toast.makeText(this, "Share function not yet available.",
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * IF USER CLICKS EDIT ICON FROM MAIN ACTION BAR
	 */
	public void onEdit() {
		// INTENT TO START EDIT ACTIVITY
		Intent intent = new Intent(ViewActivity.this, EditActivity.class);
		intent.putExtra(NotifyService.INTENT_ID, _id);
		ViewActivity.this.startActivity(intent);
	}

	public static Object getKeyFromValue(
			HashMap<String, HashMap<String, String>> hm, Object value) {
		for (Object o : hm.keySet()) {
			if (hm.get(o).equals(value)) {
				return o;
			}
		}
		return null;
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Call Socialize in onPause
		Socialize.onPause(this);
	}


	@Override
	protected void onDestroy() {
		// Call Socialize in onDestroy before the activity is destroyed
		Socialize.onDestroy(this);

		super.onDestroy();
	}

}
