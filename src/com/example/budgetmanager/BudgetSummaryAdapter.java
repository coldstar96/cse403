package com.example.budgetmanager;

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
public class BudgetSummaryAdapter extends ArrayAdapter<Budget> {

	private static final String TAG = "BudgetLogAdapter";

	// actual spending <= (expected spending) * WARNING_PROPORTION warns user
	private final double WARNING_PROPORTION = 1.20;

	// The list of budgets added to the log
	private final List<Budget> budgetList;

	// Store the activity context for usage when displaying rows
	private final Context context;

	// resource ID for the layout to inflate into each row
	private final int layoutResourceId;

	public BudgetSummaryAdapter(Context context, int layoutResourceId) {
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
	public BudgetSummaryAdapter(Context context, int layoutResourceId,
			List<Budget> budgetList) {
		this(context, layoutResourceId);
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
			LayoutInflater inflater = (LayoutInflater)
					context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			Log.d(TAG, "Got LayoutInflater");
			row = inflater.inflate(layoutResourceId, parent, false);
			Log.d(TAG, "Finished inflating layout for row " + position);
		}

		Log.d(TAG, "Getting TextViews for row " + position);

		// Views for the row
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

		// set budget name
		String name = Utilities.shorten(budget.getName(), 16);
		budgetNameView.setText(name);

		int currentCycle = 0;
		if (budget.isActive()) {
			currentCycle = budget.getCurrentCycle();
		}

		// start date and end date of cycle
		LocalDate startDate = budget.getStartDate(currentCycle);
		LocalDate endDate = budget.getEndDate(currentCycle);

		// set period
		budgetPeriodView.setText(startDate.toString() + " ~ " + endDate.toString());

		if (!budget.isRecurring()) {
			budgetCycleView.setVisibility(View.INVISIBLE);
		} else {
			budgetCycleView.setVisibility(View.VISIBLE);
		}

		// set duration
		int totalDays = Utilities.dateDifference(startDate, endDate);
		int currentDays = Utilities.dateDifference(startDate, LocalDate.now());

		budgetDurationView.setText(String.format("%d / %d days", currentDays, totalDays));

		// set expenditure
		int amountSpent = budget.getAmountSpent(currentCycle);
		int budgetAmount = budget.getBudgetAmount();
		int amountLeft = budgetAmount - amountSpent;

		expenditureTextView.setText(String.format("$%.02f / $%.02f ($%.02f left)",
				amountSpent / 100.0, budgetAmount / 100.0, amountLeft / 100.0));

		progressView.setMax(budgetAmount);
		progressView.setProgress(Math.min(amountSpent, budgetAmount));

		// set averages
		if (currentDays > totalDays) {
			currentDays = totalDays;
		}
		int daysLeft =  totalDays - currentDays;
		double actualAvg = amountSpent / 100.0 / currentDays;
		double expectedAvg = budgetAmount / 100.0 / totalDays;
		double suggestedAvg = expectedAvg;
		if (daysLeft > 0) {
			suggestedAvg = amountLeft / 100.0 / (totalDays - currentDays);
		}
		actualDailyAvgView.setText(String.format("$%.02f / day", actualAvg));
		suggestDailyAvgView.setText(String.format("$%.02f / day", suggestedAvg));

		// set expenditure textColor
		double spending = actualAvg / expectedAvg;
		setExpenditureTextColor(budget, spending, budgetDurationView, expenditureTextView);

		Log.d(TAG, "getView: Finished processing row " + position);

		return row;
	}

	private void setExpenditureTextColor(Budget budget, double spending,
			TextView budgetDurationView, TextView expenditureTextView) {
		// set color of text
		int color;
		if (budget.isActive()) {
			if (spending <= 1.0) {
				color = R.color.green;
			} else if (spending <= WARNING_PROPORTION) {
				color = R.color.orange;
			} else {
				color = R.color.red;
			}
			color = getContext().getResources().getColor(color);
		} else {
			color = budgetDurationView.getTextColors().getDefaultColor();
		}
		expenditureTextView.setTextColor(color);
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
	 * Comparator for comparing Budgets by their dates.
	 *
	 * @author Chi Ho coldstar96
	 *
	 */
	public static class BudgetActiveComparator implements Comparator<Budget> {

		/**
		 * Compare budgets by their activeness for use in sorting.
		 * Active budgets come before inactive budgets.
		 *
		 * @param lhs The left hand side of the Comparator
		 * @param rhs The right hand side of the Comparator
		 *
		 * @return negative if only lhs is active, positive if only rhs is active.
		 * 			If lhs and rhs both does not recur, returns negative if only
		 * 			lhs is before start, positive if only rhs is before start.
		 * 			Otherwise, compare by budget names.
		 */
		@Override
		public int compare(Budget lhs, Budget rhs) {
			boolean lhsActive = lhs.isActive();
			boolean rhsActive = rhs.isActive();

			if (lhsActive && !rhsActive) {
				return -1;
			} else if (!lhsActive && rhsActive) {
				return 1;
			} else if (!lhsActive && !rhsActive) {
				LocalDate now = LocalDate.now();
				boolean lhsBeforeStart = now.isBefore(lhs.getStartDate());
				boolean rhsBeforeStart = now.isBefore(rhs.getStartDate());
				if (lhsBeforeStart && !rhsBeforeStart) {
					return -1;
				} else if (!lhsBeforeStart && rhsBeforeStart) {
					return 1;
				}
			}
			return lhs.getName().compareTo(rhs.getName());
		}
	}
}
