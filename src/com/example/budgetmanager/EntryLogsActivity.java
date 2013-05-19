package com.example.budgetmanager;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays list of entries screen to the user, offering add entry
 * and add budget as well
 *
 * @author Chi Ho coldstar96
 */

public class EntryLogsActivity extends Activity {
	private final String TAG = "EntrylogsActivity";

	// UI references
	private ListView listView;
	private TextView userEmailView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry_logs);

		UBudgetApp app = (UBudgetApp) getApplication();
		EntryAdapter adapter = new EntryAdapter(this,
				R.layout.list_entry_layout, app.getEntryList());

		// set up Entry Logs screen
		listView = (ListView) findViewById(R.id.entry_list);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
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

		userEmailView = (TextView) findViewById(R.id.text_user_email);
		userEmailView.setText(app.getEmail());

		findViewById(R.id.add_entry_button).setOnClickListener(
				new View.OnClickListener() {
					@SuppressLint("ShowToast")
					@Override
					public void onClick(View view) {
						// if there is no created budget, notify user that
						// they need to create budget before they add an entry
						List<Budget> budgets = ((UBudgetApp) getApplication()).
								getBudgetList();
						if (budgets.isEmpty()) {
							Toast.makeText(EntryLogsActivity.this,
									R.string.dialog_add_budget_first,
									Toast.LENGTH_LONG).show();
						} else {
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
}

/**
 * private class that adapts the list of Entries to ListView
 *
 * @author Chi Ho coldstar96
 */

class EntryAdapter extends ArrayAdapter<Entry> {

	private Context context;
	private int layoutResourceId;
	private List<Entry> data = null;

	/**
	 * Constuctor
	 *
	 * @param context The current context
	 * @param layoutResourceId the resource ID for a layout file containing
	 *		  a layout to use when instantiating views
	 * @param data data to represent in the ListView
	 */
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
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new EntryHolder();
			holder.date = (TextView) row.findViewById(R.id.item_date);
			holder.note = (TextView) row.findViewById(R.id.item_note);
			holder.budget = (TextView) row.findViewById(R.id.item_budget);
			holder.amount = (TextView) row.findViewById(R.id.item_amount);

			row.setTag(holder);
		} else {
			holder = (EntryHolder) row.getTag();
		}

		Entry entry = data.get(position);
		holder.date.setText(entry.getDate().toString());
		holder.note.setText(entry.getNotes());
		holder.budget.setText(entry.getBudget().getName());
		holder.amount.setText(Utilities.amountToDollars(entry.getAmount()));

		return row;
	}

	static class EntryHolder {
		private TextView note;
		private TextView date;
		private TextView amount;
		private TextView budget;
	}
}