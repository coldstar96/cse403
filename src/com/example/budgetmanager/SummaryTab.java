package com.example.budgetmanager;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.budgetmanager.api.ApiCallback;
import com.example.budgetmanager.api.ApiInterface;

/**
 * Fragment which displays list of budgets screen to the user, offering
 * brief information about budgets and add entry and add budget as well
 *
 * @author Chi Ho coldstar96
 */

/**
 * Activity which displays list of Budgets screen to the user.
 *
 * @author Chi Ho coldstar96
 */
public class SummaryTab extends Fragment {
	private final String TAG = "SummaryTab";

	// UI reference
	private ListView listView;

	// The adapter for displaying/sorting the Budgets
	private BudgetSummaryAdapter adapter;

	// The currently selected Budget
	private Budget selectedBudget = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshList();
	}

	/* Helper method to refresh the list of ListView of Budgets. */
	private void refreshList() {
		adapter.clear();
		Log.d(TAG, String.format("Budget size: %d",
				Budget.getBudgets().size()));
		adapter.addAll(Budget.getBudgets());
		adapter.sort(new BudgetSummaryAdapter.BudgetActiveComparator());
		adapter.notifyDataSetChanged();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RelativeLayout layout =
				(RelativeLayout) inflater.inflate(R.layout.activity_summary,
						container, false);

		// set adapter
		adapter = new BudgetSummaryAdapter(getActivity(),
				R.layout.list_budget_layout, Budget.getBudgets());

		// set up Summary screen
		listView = (ListView) layout.findViewById(R.id.budget_list);
		listView.setAdapter(adapter);
		Log.d(TAG, "added the adapter!");

		// set up a context menu for the list items
		registerForContextMenu(listView);

		adapter.sort(new BudgetSummaryAdapter.BudgetActiveComparator());
		adapter.notifyDataSetChanged();

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos,
					long id) {
				// move to BudgetSummaryActivity
				Intent intent = new Intent(getActivity(),
						BudgetSummaryActivity.class);
				Budget b = (Budget) adapter.getItemAtPosition(pos);

				// get default current budget
				int cycle;
				if (b.isRecurring()) {
					cycle = b.getCurrentCycle();
				} else {
					cycle = 0;
				}

				intent.putExtra("BudgetId", b.getId());
				intent.putExtra("BudgetCycle", cycle);

				startActivity(intent);
			}
		});

		return layout;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		// Get the info on which item was selected
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

		// Retrieve the item that was clicked on
		selectedBudget = adapter.getItem(info.position);

		// inflate the context menu
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// using an if/else instead of a switch to avoid the bug of calling
		// both edit and delete at once
		if (item.getItemId() == R.id.menu_edit) {
			Log.d(TAG, "Edit called.");
			Log.d(TAG, "Budget ID: " + selectedBudget.getId());

			// tell AddBudgetActivity to start an edit budget session
			Intent intent = new Intent(getActivity(), AddBudgetActivity.class);
			intent.putExtra("Add", false);
			intent.putExtra("BudgetId", selectedBudget.getId());

			startActivity(intent);
		}	else if (item.getItemId() == R.id.menu_delete) {

			AlertDialog.Builder builder =
					new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.alert_title);
			builder.setMessage(R.string.alert_message);
			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					// back out from delete
					dialog.cancel();
				}
			});
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					Log.d(TAG, "Delete called.");

					ApiInterface.getInstance().remove(selectedBudget,
							new ApiCallback<Object>() {
						@Override
						public void onSuccess(Object result) {
							Log.d(TAG, "Delete Budget onSuccess entered.");

							// remove selected Budget from the list of Budgets
							Budget.removeBudget(selectedBudget);
							selectedBudget = null;

							// refresh the view upon change
							refreshList();
						}

						@Override
						public void onFailure(String errorMessage) {
							// if the request fails, do nothing
							Log.d(TAG, "Delete entry onFailure entered.");
						}
					});
				}
			});

			// show the alert message
			AlertDialog dialog = builder.create();
			dialog.show();
		}

		return true;
	}
}
