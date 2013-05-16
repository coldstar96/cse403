package com.example.budgetmanager;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetmanager.api.ApiCallback;
import com.example.budgetmanager.api.ApiInterface;

public class EntryLogsActivity extends Activity {
	public final String TAG = "EntrylogsActivity";

	ListView listView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry_logs);

		UBudgetApp app = (UBudgetApp)getApplication();
		
		// fetch data from server
		fetchBudgets();
		for(Budget b: app.getBudgetList()){
			fetchEntries(b);
		}
		

		EntryAdapter adapter = new EntryAdapter(this, R.layout.list_entry_layout, app.getEntryList());

		listView = (ListView) findViewById(R.id.entry_list);

		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int pos, long id) {
				Toast.makeText(EntryLogsActivity.this, "not implemented yet", Toast.LENGTH_LONG).show();
			}
		});
		
		findViewById(R.id.add_entry_button).setOnClickListener(
				new View.OnClickListener() {
					@SuppressLint("ShowToast")
					@Override
					public void onClick(View view) {
						// if there is no created budget, notify user that they need
						// to create budget before they add an entry
						List<Budget> budgets = ((UBudgetApp) getApplication()).getBudgetList();
						if(budgets.isEmpty()){
							Toast.makeText(EntryLogsActivity.this, R.string.dialog_add_budget_first, Toast.LENGTH_LONG).show();
						}else{
							Intent intent = new Intent(EntryLogsActivity.this, AddEntryActivity.class);
							startActivity(intent);
						}
					}
				});


		findViewById(R.id.add_budget_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(EntryLogsActivity.this, AddBudgetActivity.class);
						startActivity(intent);
					}
				});

	}

	public void fetchBudgets(){
		// fetch budgets
		ApiInterface.getInstance().fetchBudgets(new ApiCallback<List<Budget>>() {
			@Override
			public void onSuccess(List<Budget> result) {

				Log.d(TAG, "Success on fetchBudgets callback");

				UBudgetApp app = (UBudgetApp)getApplication();

				// Add these budgets to the application state
				List<Budget> budgetList = app.getBudgetList();
				budgetList.clear();
				budgetList.addAll(result);

				for (Budget b : budgetList) {
					Log.d(TAG, b.getName());
				}
			}

			@Override
			public void onFailure(String errorMessage) {
				Log.d(TAG, "fail on log in callback");
				Toast.makeText(getBaseContext(), "Couldn't get a list of budgets", Toast.LENGTH_LONG).show();
			}

		});
	}

	public void fetchEntries(Budget b){
		// fetch budgets
		ApiInterface.getInstance().fetchEntries(b, new ApiCallback<List<Entry>>() {
			@Override
			public void onSuccess(List<Entry> result) {

				Log.d(TAG, "Success on fetchEntries callback");

				UBudgetApp app = (UBudgetApp)getApplication();

				// Add these budgets to the application state
				List<Entry> entryList = app.getEntryList();
				entryList.clear();
				entryList.addAll(result);

				for (Entry e : entryList) {
					Log.d(TAG, e.toString());
				}
			}

			@Override
			public void onFailure(String errorMessage) {
				Log.d(TAG, "fail on log in callback");
				Toast.makeText(getBaseContext(), "Couldn't get a list of entries", Toast.LENGTH_LONG).show();
			}

		});
	}
}

class EntryAdapter extends ArrayAdapter<Entry>{

	Context context; 
	int layoutResourceId;    
	List<Entry> data = null;

	public EntryAdapter(Context context, int layoutResourceId, List<Entry> data) {
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
		holder.date.setText(entry.getDate());
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