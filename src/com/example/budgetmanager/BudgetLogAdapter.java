package com.example.budgetmanager;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class handles preparing lists of budgets for display in the summary screen.
 * It aggregates the budgets and allows for them to be displayed in a ListView.
 * Thus, it could be considered a kind of View Model.
 *
 * @author Chi Ho coldstar96
 *
 */
public class BudgetLogAdapter extends ArrayAdapter<Budget> {

	private static final String TAG = "BudgetLogAdapter";
	private final double WARNING_PROPORTION = 1.20;

	// The list of budgets added to the log
	private final List<Budget> budgetList;

	// Store the activity context for usage when displaying rows
	private final Context context;

	// resource ID for the layout to inflate into each row
	private final int layoutResourceId;

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
	 * @param budgetList List of budgets
	 */
	public BudgetLogAdapter(Context context, int layoutResourceId,
			List<Budget> budgetList) {
		this(context, layoutResourceId);
		this.budgetList.clear();
		this.budgetList.addAll(budgetList);
		Log.d(TAG, "all budgets added");
	}

	/**
	 * Clears this BudgetLogAdapter of all budgets such that it
	 * no longer holds any budgets.
	 */
	@Override
	public void clear() {
		super.clear();
		budgetList.clear();
	}

	@Override
	public int getCount() {
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
		TextView budgetCycleView = (TextView) row.findViewById(R.id.budget_cycle);
		TextView budgetDurationView = (TextView) row.findViewById(R.id.budget_duration);
		TextView expenditureTextView = (TextView) row.findViewById(R.id.expenditure_text);
		ProgressBar progressView = (ProgressBar) row.findViewById(R.id.expenditure_progress);
		TextView actualDailyAvgView = (TextView) row.findViewById(R.id.actual_daily_average);
		TextView suggestDailyAvgView = (TextView) row.findViewById(R.id.suggested_daily_average);
		TextView budgetPeriodView = (TextView) row.findViewById(R.id.budget_period);


		Log.d(TAG, "Finished getting Views for row " + position);

		Budget budget = budgetList.get(position);

		budgetNameView.setText(budget.getName());

		int currentCycle = 0;
		if (budget.isRecurring()) {
			currentCycle = budget.getCurrentCycle();
		}

		LocalDate startDate = budget.getStartDate(currentCycle);
		LocalDate endDate = budget.getEndDate(currentCycle);

		budgetPeriodView.setText(startDate.toString() + " ~ " + endDate.toString());

		if (!budget.isRecurring()) {
			budgetCycleView.setVisibility(View.INVISIBLE);
		} else {
			budgetCycleView.setVisibility(View.VISIBLE);
		}

		int totalDays = Utilities.dateDifference(startDate, endDate);
		int currentDays = Math.min(totalDays, Utilities.dateDifference(startDate, LocalDate.now()));

		budgetDurationView.setText(String.format("%d / %d days", currentDays, totalDays));

		int amountSpent = budget.getAmountSpent();
		int budgetAmount = budget.getBudgetAmount();
		int amountLeft = budgetAmount - amountSpent;

		expenditureTextView.setText(String.format("$%.02f / $%.02f ($%.02f left)",
				amountSpent / 100.0, budgetAmount / 100.0, amountLeft / 100.0));

		progressView.setMax(budgetAmount);
		progressView.setProgress(Math.min(amountSpent, budgetAmount));

		double actualAvg = amountSpent / 100.0 / currentDays;

		int daysLeft =  totalDays - currentDays;
		double expectedAvg = budgetAmount / 100.0 / totalDays;
		double suggestedAvg = expectedAvg;

		if (daysLeft > 0) {
			suggestedAvg = amountLeft / 100.0 / (totalDays - currentDays);
		}

		actualDailyAvgView.setText(String.format("$%.02f / day", actualAvg));
		suggestDailyAvgView.setText(String.format("$%.02f / day", suggestedAvg));

		double spending = actualAvg / expectedAvg;


		if (budget.isActive()) {
			if (spending <= 1.0) {
				expenditureTextView.setTextColor(getContext().getResources().getColor(R.color.green));
			} else if (spending <= WARNING_PROPORTION) {
				expenditureTextView.setTextColor(getContext().getResources().getColor(R.color.orange));
			} else {
				expenditureTextView.setTextColor(getContext().getResources().getColor(R.color.red));
			}
		} else {
			expenditureTextView.setTextColor(getContext().getResources().getColor(R.color.black));
		}

		Log.d(TAG, "getView: Finished processing row " + position);

		return row;
	}


	/**
	 * Sorts this BudgetLogAdapter by the given comparator.
	 * Does not notify observers of changes.
	 */
	@Override
	public void sort(Comparator<? super Budget> comp) {
		super.sort(comp);
		Collections.sort(budgetList, comp);
	}


	/**
	 * Comparator for comparing Entries by their dates.
	 *
	 * @author chris brucec5
	 *
	 */
	public static class BudgetActiveComparator implements Comparator<Budget> {

		/**
		 * Compare Entries by their dates for use in sorting.
		 * More recent Entries come before older Entries in sorting.
		 *
		 * @param lhs The left hand side of the Comparator
		 * @param rhs The right hand side of the Comparator
		 *
		 * @return positive if only lhs is active, negative if only rhs is active, zero otherwise
		 */
		@Override
		public int compare(Budget lhs, Budget rhs) {
			boolean lhsRecur = lhs.isActive();
			boolean rhsRecur = rhs.isActive();

			if (lhsRecur && !rhsRecur) {
				return -1;
			} else if (!lhsRecur && rhsRecur) {
				return 1;
			}
			return 0;
		}

	}
}
