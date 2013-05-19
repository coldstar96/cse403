package com.example.budgetmanager;

import java.util.List;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetmanager.preference.SettingsFragment;
import com.example.budgetmanager.preference.SettingsActivity;

/**
 * Activity which displays list of entries screen to the user, offering add entry
 * and add budget as well
 * 
 * @author Chi Ho coldstar96
 */
public class EntryLogsActivity extends Activity {
	public final String TAG = "EntrylogsActivity";

	// UI references
	private ListView listView;
	// private TextView userEmailView; TODO Decide whether to get rid of this

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// set default values for settings (if never done before)
		PreferenceManager.setDefaultValues(this, R.xml.fragment_settings, false);
		
		// check the preference to see which theme to set
		String startingScreen = PreferenceManager.
				getDefaultSharedPreferences(this).getString(SettingsFragment
				.KEY_PREF_APP_THEME, "");

		if (startingScreen.equals(SettingsFragment
				.APP_THEME_LIGHT)) {
			setTheme(android.R.style.Theme_Holo_Light);
		} else {
			setTheme(android.R.style.Theme_Holo);
		}
		
		super.onCreate(savedInstanceState);

		// inflate view
		setContentView(R.layout.activity_entry_logs);

		// retrieve the application data
		UBudgetApp app = (UBudgetApp)getApplication();

		EntryAdapter adapter = new EntryAdapter(this, 
				R.layout.list_entry_layout, app.getEntryList());

		// set up Entry Logs screen
		listView = (ListView) findViewById(R.id.entry_list);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, 
					int pos, long id) {
				Toast.makeText(EntryLogsActivity.this, 
						"click not implemented yet", Toast.LENGTH_LONG).show();
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Toast.makeText(EntryLogsActivity.this, 
						"Long click not implemented yet", 
						Toast.LENGTH_LONG).show();
				return false;
			}
		});

		// userEmailView = (TextView) findViewById(R.id.text_user_email); TODO Decide whether to get rid of this
		// userEmailView.setText(app.getEmail()); TODO Decide whether to get rid of this

		findViewById(R.id.add_entry_button).setOnClickListener(
				new View.OnClickListener() {
					@SuppressLint("ShowToast")
					@Override
					public void onClick(View view) {
						// if there is no created budget, notify user that 
						// they need to create budget before they add an entry
						List<Budget> budgets = ((UBudgetApp) getApplication()).
								getBudgetList();
						if(budgets.isEmpty()){
							Toast.makeText(EntryLogsActivity.this, 
									R.string.dialog_add_budget_first, 
									Toast.LENGTH_LONG).show();
						}else{
							Intent intent = new Intent(EntryLogsActivity.this, 
									AddEntryActivity.class);
							startActivity(intent);
						}
					}
				});


		findViewById(R.id.add_budget_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(EntryLogsActivity.this, 
								AddBudgetActivity.class);
						startActivity(intent);
					}
				});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem buttonSettings = menu.add(R.string.title_settings);
		// this forces it to go in the overflow menu, which is preferred.
		buttonSettings.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		buttonSettings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			/** Take the users to the Settings activity upon clicking the button. */
			public boolean onMenuItemClick(MenuItem item) {
				Intent settingsIntent = new Intent(EntryLogsActivity.this, SettingsActivity.class);
				settingsIntent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsFragment.class.getName());
				settingsIntent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);	
				EntryLogsActivity.this.startActivity(settingsIntent);

				return false;
			}
		});

		MenuItem buttonSignout = menu.add(R.string.title_signout);
		buttonSignout.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		buttonSignout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			/** Sign out the user upon clicking the button. */
			public boolean onMenuItemClick(MenuItem item) {
				Toast.makeText(EntryLogsActivity.this, "Successfully handled Sign out selection"
						, Toast.LENGTH_LONG).show();
				return false;
			}
		});
		return true;
	}
}

/**
 * private class that adapts the list of Entries to ListView
 * 
 * @author Chi Ho coldstar96
 */

class EntryAdapter extends ArrayAdapter<Entry>{

	Context context; 
	int layoutResourceId;    
	List<Entry> data = null;

	public EntryAdapter(Context context, int layoutResourceId, 
			List<Entry> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		EntryHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new EntryHolder();
			holder.date = (TextView)row.findViewById(R.id.item_date);
			holder.note = (TextView)row.findViewById(R.id.item_note);
			holder.budget = (TextView)row.findViewById(R.id.item_budget);
			holder.amount = (TextView)row.findViewById(R.id.item_amount);

			row.setTag(holder);
		} else {
			holder = (EntryHolder)row.getTag();
		}

		Entry entry = data.get(position);
		holder.date.setText(entry.getDate().toString());
		holder.note.setText(entry.getNotes());
		holder.budget.setText(entry.getBudget().getName());
		holder.amount.setText(Utilities.amountToDollars(entry.getAmount()));

		return row;
	}

	static class EntryHolder {
		TextView note;
		TextView date;
		TextView amount;
		TextView budget;
	}
}
