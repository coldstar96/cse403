package com.example.budgetmanager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * This class handles preparing lists of entries for display in the Budget Log.
 * It aggregates the entries associated with one or more budgets and allows for
 * them to be displayed in a ListView. Thus, it could be considered a kind of
 * View Model.
 *
 * This class also contains Comparators that allow for sorting entries by
 * different attributes.
 *
 * @author chris brucec5
 *
 */
public class BudgetLogAdapter extends ArrayAdapter<Budget> {

	private static final String TAG = "BudgetLogAdapter";

	// The list of budgets added to the log
	private List<Budget> budgetList;

	// Store the activity context for usage when displaying rows
	private Context context;

	// resource ID for the layout to inflate into each row
	private int layoutResourceId;

	public BudgetLogAdapter(Context context, int layoutResourceId) {
		super(context, layoutResourceId);

		this.budgetList = new ArrayList<Budget>();
		this.context = context;
		this.layoutResourceId = layoutResourceId;
	}

	/**
	 * Constructs a new BudgetLog
	 * @param context the current Context
	 * @param layoutResourceId Resource ID for the row view
	 * @param budgetList List of budgets from which entries are to be grabbed
	 */
	public BudgetLogAdapter(Context context, int layoutResourceId,
			List<Budget> budgetList) {
		this(context, layoutResourceId);

		this.budgetList.addAll(budgetList);
		Log.d(TAG, "all budgets added");
	}

	/**
	 * Clears this BudgetLogAdapter of all Entries such that it
	 * no longer holds any Entries.
	 */
	@Override
	public void clear() {
		super.clear();
		budgetList.clear();
	}
	
	@Override
	public int getCount(){
		return budgetList.size();
	}

	/**
	 * Get a View that represents the <code>position</code>th row in the
	 * BudgetLogs ListView.
	 *
	 * @param position The index of the row to get
	 * @param row old row to reuse, if there was already a row created here,
	 * otherwise null.
	 * @param parent The parent view this row is attached to
	 *
	 * @return A view corresponding to the <code>position</code>th row
	 */
	@Override
	public View getView(int position, View row, ViewGroup parent) {
		Log.d(TAG, "getView: Processing row " + position);

		if (row == null) {
			// The row hasn't been loaded in yet, so inflate a new one
			Log.d(TAG, "Inflating layout for row " + position);
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			Log.d(TAG, "Got LayoutInflater");
			row = inflater.inflate(layoutResourceId, parent, false);
			Log.d(TAG, "Finished inflating layout for row " + position);
		}

		Log.d(TAG, "Getting TextViews for row " + position);
		TextView budgetNameView = (TextView) row.findViewById(R.id.budget_name);
		TextView amountView = (TextView) row.findViewById(R.id.amount_used);
		ProgressBar progressView = (ProgressBar) row.findViewById(R.id.spenditure_progress);
		TextView startCycleView = (TextView) row.findViewById(R.id.start_of_cycle);
		TextView durationView = (TextView) row.findViewById(R.id.budget_duration);
		TextView recurView = (TextView) row.findViewById(R.id.budget_recur);
		Log.d(TAG, "Finished getting Views for row " + position);

		Budget budget = budgetList.get(position);

		budgetNameView.setText(budget.getName());

		int amountSpent = budget.getAmountSpent();
		int budgetAmount = budget.getBudgetAmount();
		amountView.setText(String.format("$%.02f / $%.02f",
				amountSpent / 100.0, budgetAmount / 100.0));

		progressView.setMax(budgetAmount);
		progressView.setProgress(Math.min(amountSpent, budgetAmount));

		startCycleView.setText(budget.getStartDate(budget.getCurrentCycle()).toString());

		durationView.setText(budget.getDuration().toString());

		String recur;
		if (budget.isRecurring()) {
			recur = "true";
		} else {
			recur = "false";
		}
		recurView.setText(recur);


		Log.d(TAG, "getView: Finished processing row " + position);

		return row;
	}
}
