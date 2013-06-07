package com.example.budgetmanager;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.budgetmanager.preference.SettingsFragment;

import org.joda.time.LocalDate;

import java.util.Comparator;
import java.util.List;

/**
 * This class handles preparing lists of budgets for display in the summary
 * screen. It aggregates the budgets and allows for them to be displayed in a
 * ListView. Thus, it could be considered a kind of View Model.
 *
 * @author Chi Ho coldstar96
 *
 */
public class BudgetSummaryAdapter extends ArrayAdapter<Budget> {

	private static final String TAG = "BudgetLogAdapter";

	// actual spending <= (expected spending) * WARNING_PROPORTION warns user
	private final double WARNING_PROPORTION = 1.20;

	// Store the activity context for usage when displaying rows
	private final Context context;

	// resource ID for the layout to inflate into each row
	private final int layoutResourceId;

	public BudgetSummaryAdapter(Context context, int layoutResourceId) {
		super(context, layoutResourceId);
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
		this.addAll(budgetList);
		Log.d(TAG, "all budgets added");
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
		TextView budgetNameView =
				(TextView) row.findViewById(R.id.budget_name);
		TextView budgetCycleView =
				(TextView) row.findViewById(R.id.budget_cycle);
		TextView periodTextView =
				(TextView) row.findViewById(R.id.period_text);
		ProgressBar perProgressView =
				(ProgressBar) row.findViewById(R.id.period_progress);
		TextView expenditureTextView =
				(TextView) row.findViewById(R.id.expenditure_text);
		ProgressBar expProgressView =
				(ProgressBar) row.findViewById(R.id.expenditure_progress);
		TextView actualDailyAvgView =
				(TextView) row.findViewById(R.id.actual_daily_average);
		TextView suggestDailyAvgView =
				(TextView) row.findViewById(R.id.suggested_daily_average);

		Log.d(TAG, "Finished getting Views for row " + position);

		Budget budget = getItem(position);

		// set budget name
		budgetNameView.setText(budget.getName());


		//set background, text colors
		boolean lightTheme = PreferenceManager
				.getDefaultSharedPreferences(getContext())
				.getString(SettingsFragment.KEY_PREF_APP_THEME, "")
				.equals(SettingsFragment.APP_THEME_LIGHT);

		int currentCycle = 0;
		int bgColor;
		int txtColor;
		if (budget.isActive()) {
			currentCycle = budget.getCurrentCycle();
			if (lightTheme) {
				bgColor = R.color.background_holo_light;
				txtColor = R.color.dark_gray;
			} else {
				bgColor = R.color.background_holo_dark;
				txtColor = R.color.background_holo_light;
			}
		} else {
			if (lightTheme) {
				bgColor = R.color.dark_gray;
				txtColor = R.color.black;
			} else {
				bgColor = R.color.background_holo_dark;
				txtColor = R.color.dark_gray;
			}
		}

		bgColor = getContext().getResources().getColor(bgColor);
		txtColor = getContext().getResources().getColor(txtColor);

		row.setBackgroundColor(bgColor);

		budgetNameView.setTextColor(txtColor);
		budgetCycleView.setTextColor(txtColor);
		periodTextView.setTextColor(txtColor);
		expenditureTextView.setTextColor(txtColor);
		actualDailyAvgView.setTextColor(txtColor);
		suggestDailyAvgView.setTextColor(txtColor);
		perProgressView.getProgressDrawable()
				.setColorFilter(txtColor, Mode.SRC_IN);


		// set period views
		LocalDate startDate = budget.getStartDate(currentCycle);
		LocalDate endDate = budget.getEndDate(currentCycle);

		String startDateStr = android.text.format.DateFormat
				.getDateFormat(context).format(startDate.toDate());
		String endDateStr = android.text.format.DateFormat
				.getDateFormat(context).format(endDate.toDate());

		if (!budget.isRecurring()) {
			budgetCycleView.setVisibility(View.INVISIBLE);
		} else {
			budgetCycleView.setVisibility(View.VISIBLE);
		}

		// set period
		int totalDays = Utilities.dateDifference(startDate, endDate);
		int currentDays = Utilities.dateDifference(startDate, LocalDate.now());

		perProgressView.setMax(totalDays);
		perProgressView.setProgress(
				Math.max(0, Math.min(totalDays, currentDays)));

		String days = getContext().getResources().getString(R.string.days);
		periodTextView.setText(String.format("%d / %d %s (%s ~ %s)",
				currentDays, totalDays, days, startDateStr, endDateStr));

		// set expenditure views
		int amountSpent = budget.getAmountSpent(currentCycle);
		int budgetAmount = budget.getBudgetAmount();
		int amountLeft = budgetAmount - amountSpent;

		expProgressView.setMax(budgetAmount);
		expProgressView.setProgress(Math.min(amountSpent, budgetAmount));

		String left = getContext().getResources().getString(R.string.left);
		expenditureTextView.setText(
				String.format("$%.02f / $%.02f ($%.02f %s)",
				amountSpent / 100.0, budgetAmount / 100.0, amountLeft / 100.0, left));

		// set average views
		if (currentDays > totalDays) {
			currentDays = totalDays;
		}
		int daysLeft = totalDays - currentDays + 1;
		double actualAvg = amountSpent / 100.0 / currentDays;
		double expectedAvg = budgetAmount / 100.0 / totalDays;
		double suggestedAvg = expectedAvg;

		if (daysLeft > 0) {
			suggestedAvg = amountLeft / 100.0 / daysLeft;
		}

		String day = getContext().getResources().getString(R.string.day);
		String actual = getContext().getResources().getString(R.string.actual);
		String suggest = getContext().getResources().getString(R.string.suggest);

		actualDailyAvgView.setText(
				String.format("%s: $%.02f / %s", actual, actualAvg, day));
		suggestDailyAvgView.setText(
				String.format("%s: $%.02f / %s", suggest, suggestedAvg, day));

		// set progress color
		double spending = actualAvg / expectedAvg;
		setProgressColor(budget, spending, periodTextView, expProgressView);

		Log.d(TAG, "getView: Finished processing row " + position);

		return row;
	}

	private void setProgressColor(Budget budget, double spending,
			TextView refView, ProgressBar appliedView) {
		// set color of text
		int color;
		if (budget.isActive()) {
			if (spending <= 1.0) {
				color = R.color.green;
			} else if (spending <= WARNING_PROPORTION) {
				color = R.color.yellow;
			} else {
				color = R.color.red;
			}
			color = getContext().getResources().getColor(color);
		} else {
			color = refView.getTextColors().getDefaultColor();
		}
		appliedView.getProgressDrawable().setColorFilter(color, Mode.SRC_IN);
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
		 * @return negative if only lhs is active, positive if only rhs is
		 * 			active. If lhs and rhs both does not recur, returns negative
		 * 			if only lhs is before start, positive if only rhs is before
		 * 			start. Otherwise, compare by budget names.
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
