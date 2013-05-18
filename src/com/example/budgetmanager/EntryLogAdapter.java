package com.example.budgetmanager;

import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

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

	/**
	 * Constructs a new EntryLog
	 * @param context the current Context
	 * @param layoutResourceId Resource ID for the row view
	 * @param budgetList List of budgets from which entries are to be grabbed
	 */
	public EntryLogAdapter(Context context, int layoutResourceId,
			List<Budget> budgetList) {
		super(context, layoutResourceId);
	}

	/**
	 * Constructs a new EntryLog
	 * @param context the current Context
	 * @param layoutResourceId Resource ID for the row view
	 * @param budget Budget from which entries are to be grabbed
	 */
	public EntryLogAdapter(Context context, int layoutResourceId, Budget budget) {
		super(context, layoutResourceId);
	}

	/**
	 * Attempts to add the given budget's entries to this EntryLog. If the
	 * given Budget is already added to this EntryLog, it will not add again.
	 * Entries will be added at the end of the list.
	 *
	 * @return true if the budget has not yet been added to this EntryLog,
	 * false if the budget has already been added to this EntryLog.
	 */
	public void addEntriesFromBudget(Budget budget) {

	}

	/**
	 * Gets a list of Entries that have been added to this EntryLog
	 *
	 * @return An unmodifiable list of entries for this EntryLog
	 */
	public List<Entry> getEntryList() {
		return null;
	}

	/**
	 * Get a View that represents the <code>position</code>th row in the
	 * EntryLogs ListView.
	 *
	 * @param position The index of the row to get
	 * @param row The old row to reuse, if there was already a row created here
	 * @param parent The parent view this row is attached to
	 *
	 * @return A view corresponding to the <code>position</code>th row
	 */
	@Override
	public View getView(int position, View row, ViewGroup parent) {
		return null;
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
			return 0;
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
			return 0;
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
			return 0;
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
			return 0;
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
			return 0;
		}

	}
}
