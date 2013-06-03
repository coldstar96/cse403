package com.example.budgetmanager;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * This class handles preparing lists of entries for display in the Entry Log.
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
public class EntryLogAdapter extends ArrayAdapter<Entry> {

	private static final String TAG = "EntryLogAdapter";

	// Store the activity context for usage when displaying rows
	private final Context context;

	// resource ID for the layout to inflate into each row
	private final int layoutResourceId;

	/**
	 * Constructs a new EntryLog
	 * @param context the current Context
	 * @param layoutResourceId Resource ID for the row view
	 */
	public EntryLogAdapter(Context context, int layoutResourceId) {
		super(context, layoutResourceId);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
	}

	/**
	 * Get a View that represents the <code>position</code>th row in the
	 * EntryLogs ListView.
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
		TextView dateView = (TextView) row.findViewById(R.id.item_date);
		TextView amountView = (TextView) row.findViewById(R.id.item_amount);
		TextView budgetNameView = (TextView) row.findViewById(R.id.item_budget);
		TextView notesView = (TextView) row.findViewById(R.id.item_note);
		Log.d(TAG, "Finished getting TextViews for row " + position);

		Entry entry = getItem(position);

		dateView.setText(entry.getDate().toString());
		amountView.setText(Utilities.amountToDollars(entry.getAmount()));
		budgetNameView.setText(entry.getBudget().getName());
		notesView.setText(entry.getNotes());

		Log.d(TAG, "getView: Finished processing row " + position);

		return row;
	}

	public List<Entry> getEntryList(){
		List<Entry> entries = new ArrayList<Entry>();
		for (int i = 0; i < getCount(); i++) {
			entries.add(getItem(i));
		}
		return entries;
	}

	/**
	 * Comparator for comparing Entries by their dates.
	 *
	 * @author chris brucec5
	 *
	 */
	public static class EntryDateComparator implements Comparator<Entry> {

		/**
		 * Compare Entries by their dates for use in sorting.
		 * More recent Entries come before older Entries in sorting.
		 *
		 * @param lhs The left hand side of the Comparator
		 * @param rhs The right hand side of the Comparator
		 *
		 * @return negative if lhs is more recent than rhs, zero if lhs has
		 * the same date as rhs, and positive if lhs is older than rhs.
		 */
		@Override
		public int compare(Entry lhs, Entry rhs) {
			LocalDate lhsDate = lhs.getDate();
			LocalDate rhsDate = rhs.getDate();

			// We want later dates to appear first in the sort, so we
			// reverse the ordering of the comparison.
			return rhsDate.compareTo(lhsDate);
		}

	}

	/**
	 * Comparator for comparing entries by their amounts.
	 *
	 * @author chris brucec5
	 *
	 */
	public static class EntryAmountComparator implements Comparator<Entry> {

		/**
		 * Compare Entries by their amounts for use in sorting.
		 * More expensive Entries come before less expensive ones.
		 *
		 * @param lhs The left hand side of the Comparator
		 * @param rhs the right hand side of the Comparator
		 *
		 * @return negative if the lhs is more expensive than rhs, zero if lhs
		 * is as expensive as rhs, and positive if lhs is less expensive than
		 * rhs.
		 */
		@Override
		public int compare(Entry lhs, Entry rhs) {
			int lhsAmount = lhs.getAmount();
			int rhsAmount = rhs.getAmount();
			Log.d(TAG, "Comparing " + lhsAmount + " with " + rhsAmount);

			// We want bigger amounts first in the sort, so we reverse the
			// normal ordering of the subtraction for the comparison.
			return rhsAmount - lhsAmount;
		}

	}

	/**
	 * Comparator for comparing entries by their real creation times.
	 *
	 * @author chris brucec5
	 *
	 */
	public static class EntryCreationTimeComparator
	implements Comparator<Entry> {

		/**
		 * Compare Entries by their real creation times for use in sorting.
		 * More recent Entries come before older Entries in sorting.
		 *
		 * @param lhs The left hand side of the Comparator
		 * @param rhs the right hand side of the Comparator
		 *
		 * @return negative if lhs is more recent than rhs, zero if lhs has
		 * the same date as rhs, and positive if lhs is older than rhs.
		 */
		@Override
		public int compare(Entry lhs, Entry rhs) {
			LocalDateTime lhsTime = lhs.getCreatedAt();
			LocalDateTime rhsTime = rhs.getCreatedAt();

			// We want later creation times to appear first in the sort,
			// so we reverse the order of the comparison.
			return rhsTime.compareTo(lhsTime);
		}

	}

	/**
	 * Comparator for comparing entries by their real update times.
	 *
	 * @author chris brucec5
	 *
	 */
	public static class EntryUpdateTimeComparator
	implements Comparator<Entry> {

		/**
		 * Compare Entries by their real last update times for use in sorting.
		 * More recent Entries come before older Entries in sorting.
		 *
		 * @param lhs The left hand side of the Comparator
		 * @param rhs the right hand side of the Comparator
		 *
		 * @return negative if lhs is more recent than rhs, zero if lhs has
		 * the same date as rhs, and positive if lhs is older than rhs.
		 */
		@Override
		public int compare(Entry lhs, Entry rhs) {
			LocalDateTime lhsTime = lhs.getUpdatedAt();
			LocalDateTime rhsTime = rhs.getUpdatedAt();

			// We want later update times to appear first in the sort,
			// so we reverse the order of the comparison.
			return rhsTime.compareTo(lhsTime);
		}

	}

	/**
	 * Comparator for comparing entries by their Budget's names
	 *
	 * @author chris brucec5
	 *
	 */
	public static class EntryBudgetComparator implements Comparator<Entry> {

		/**
		 * Compare Entries lexicographically by Budget names for use in
		 * sorting.
		 *
		 * @param lhs The left hand side of the Comparator
		 * @param rhs the right hand side of the Comparator
		 *
		 * @return negative if lhs comes before rhs, zero if lhs equals rhs,
		 * and positive if lhs comes after rhs.
		 */
		@Override
		public int compare(Entry lhs, Entry rhs) {
			Locale loc = Locale.getDefault();

			Budget lhsBudget = lhs.getBudget();
			Budget rhsBudget = rhs.getBudget();

			String lhsBudgetName = lhsBudget.getName().toLowerCase(loc);
			String rhsBudgetName = rhsBudget.getName().toLowerCase(loc);

			// Since we want budget names that lexicographically appear first
			// to appear first in the sort, we don't reverse the order of the
			// comparator for this one, which is different than the others.
			return lhsBudgetName.compareTo(rhsBudgetName);
		}
	}
}
