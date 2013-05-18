package com.example.budgetmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

	// The list of budgets added to the log
	private List<Budget> budgetList;

	// List of entries added by budgets in the budgetList
	private List<Entry> entryList;

	// Store the activity context for usage when displaying rows
	private Context context;

	// resource ID for the layout to inflate into each row
	int layoutResourceId;

	public EntryLogAdapter(Context context, int layoutResourceId) {
		super(context, layoutResourceId);

		this.budgetList = new ArrayList<Budget>();
		this.entryList = new ArrayList<Entry>();
		this.context = context;
		this.layoutResourceId = layoutResourceId;
	}

	/**
	 * Constructs a new EntryLog
	 * @param context the current Context
	 * @param layoutResourceId Resource ID for the row view
	 * @param budgetList List of budgets from which entries are to be grabbed
	 */
	public EntryLogAdapter(Context context, int layoutResourceId,
			List<Budget> budgetList) {
		this(context, layoutResourceId);

		for (Budget b : budgetList) {
			addEntriesFromBudget(b);
		}
	}

	/**
	 * Constructs a new EntryLog
	 * @param context the current Context
	 * @param layoutResourceId Resource ID for the row view
	 * @param budget Budget from which entries are to be grabbed
	 */
	public EntryLogAdapter(Context context, int layoutResourceId, Budget budget) {
		this(context, layoutResourceId);

		addEntriesFromBudget(budget);
	}

	/**
	 * Attempts to add the given budget's entries to this EntryLog. If the
	 * given Budget is already added to this EntryLog, it will not add again.
	 * Entries will be added at the end of the list.
	 */
	public void addEntriesFromBudget(Budget budget) {
		if (!budgetList.contains(budget)) {
			budgetList.add(budget);

			List<Entry> budgetEntries = budget.getEntries();
			addAll(budgetEntries);
			entryList.addAll(budgetEntries);
			Log.d(TAG, "Now have " + entryList.size() + " Entries");
		}
	}

	/**
	 * Gets a list of Entries that have been added to this EntryLog
	 *
	 * @return An unmodifiable list of entries for this EntryLog
	 */
	public List<Entry> getEntryList() {
		return Collections.unmodifiableList(entryList);
	}

	/**
	 * Clears this EntryLogAdapter of all Entries such that it
	 * no longer holds any Entries.
	 */
	@Override
	public void clear() {
		super.clear();
		budgetList.clear();
		entryList.clear();
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
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			Log.d(TAG, "Got LayoutInflater");
			row = inflater.inflate(layoutResourceId, parent, false);
			Log.d(TAG, "Finished inflating layout for row " + position);
		}

		Log.d(TAG, "Getting TextViews for row " + position);
		TextView dateView = (TextView)row.findViewById(R.id.item_date);
		TextView amountView = (TextView)row.findViewById(R.id.item_amount);
		TextView budgetNameView = (TextView)row.findViewById(R.id.item_budget);
		TextView notesView = (TextView)row.findViewById(R.id.item_note);
		Log.d(TAG, "Finished getting TextViews for row " + position);

		Entry entry = entryList.get(position);

		dateView.setText(entry.getDate().toString());
		amountView.setText(Utilities.amountToDollars(entry.getAmount()));
		budgetNameView.setText(entry.getBudget().getName());
		notesView.setText(entry.getNotes());

		Log.d(TAG, "getView: Finished processing row " + position);

		return row;
	}

	/**
	 * Sorts this EntryLogAdapter by the given comparator.
	 * Does not notify observers of changes.
	 */
	@Override
	public void sort(Comparator<? super Entry> comp) {
		super.sort(comp);
		Collections.sort(entryList, comp);
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

			return lhsBudgetName.compareTo(rhsBudgetName);
		}

	}
}