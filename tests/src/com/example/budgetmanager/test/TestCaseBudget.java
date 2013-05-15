package com.example.budgetmanager.test;

import java.util.List;

import org.joda.time.LocalDate;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Budget.Duration;
import com.example.budgetmanager.Entry;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

public class TestCaseBudget extends AndroidTestCase {

	/**
	 * Creates a new budget with the given duration d.
	 * The rest of the parameters are as follows:
	 *
	 * amount = 500, currentAmount = 0, recur = true, startDate = 2013-05-05
	 * @param d
	 * @return
	 */
	private Budget buildBasicBudget(Duration d) {
		LocalDate date = new LocalDate(2013, 05, 05);
		return new Budget("Test Budget", 500, true, date, d);
	}

	/**
	 * Runs the startDate tests with the given parameters.
	 *
	 * @param dur The {@link com.example.budgetmanager.Budget.Duration} that
	 * this test is to operate over
	 * @param initialStartDate The start date of the very first cycle
	 * @param startDate The expected start date of the <code>cycle</code>th
	 * cycle
	 * @param cycle Which cycle <code>startDate</code> is being tested on,
	 * where <code>0</code> is the cycle starting on
	 * <code>initialStartDate</code>
	 */
	private void runStartDateTest(Duration dur, LocalDate initialStartDate,
			LocalDate startDate, int cycle) {
		Budget budget = new Budget("test", 1000, true,
				initialStartDate, dur);

		assertEquals(startDate, budget.getStartDate(cycle));
	}

	/**
	 * Runs the endDate tests with the given parameters.
	 *
	 * @param dur The {@link com.example.budgetmanager.Budget.Duration} that
	 * this test is to operate over
	 * @param startDate The start date of the very first cycle
	 * @param startDate The expected end date of the <code>cycle</code>th
	 * cycle
	 * @param cycle Which cycle <code>startDate</code> is being tested on,
	 * where <code>0</code> is the cycle starting on
	 * <code>startDate</code>
	 */
	private void runEndDateTest(Duration dur, LocalDate startDate,
			LocalDate endDate, int cycle) {
		Budget budget = new Budget("test", 1000, true,
				startDate, dur);

		assertEquals(endDate, budget.getEndDate(cycle));
	}

	@SmallTest
	public void test_addEntry_nullEntry_throwsIllegalArgumentException() {
		Budget budget = buildBasicBudget(Duration.WEEK);
		try {
			budget.addEntry(null);
			fail("Adding a null entry should throw an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertNotNull(e.getMessage());
		}
	}

	@SmallTest
	public void test_addEntry_duplicateEntry_throwsIllegalArgumentException() {
		Budget budget = buildBasicBudget(Duration.WEEK);
		Entry entry = new Entry(100, budget, "", "2013-05-05");
		budget.addEntry(entry);
		try {
			budget.addEntry(entry);
			fail("Adding a duplicate entry should throw an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertNotNull(e.getMessage());
		}
	}

	@SmallTest
	public void test_addEntry_newEntry_shouldHaveTheEntry() {
		Budget budget = buildBasicBudget(Duration.WEEK);
		Entry entry = new Entry(100, budget, "", "2013-05-05");
		budget.addEntry(entry);
		List<Entry> entries = budget.getEntries();
		assertEquals(1, entries.size());
		assertEquals(entry, entries.get(0));
	}

	@SmallTest
	public void test_isRecurring_isRecurring_shouldBeRecurring() {
		Budget budget = new Budget("", 0, true, LocalDate.now(), Duration.DAY);
		assertTrue(budget.isRecurring());
	}

	@SmallTest
	public void test_isRecurring_isNotRecurring_shouldNotBeRecurring() {
		Budget budget = new Budget("", 0, false, LocalDate.now(), Duration.DAY);
		assertFalse(budget.isRecurring());
	}

	@SmallTest
	public void test_removeEntry_removeNull_throwsIllegalArgumentException() {
		Budget budget = buildBasicBudget(Duration.WEEK);
		try {
			budget.removeEntry(null);
			fail("Removing a null entry should throw an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertNotNull(e.getMessage());
		}
	}

	@SmallTest
	public void test_removeEntry_existingEntry_shouldNotHaveTheEntry() {
		Budget budget = buildBasicBudget(Duration.WEEK);
		Entry entry1 = new Entry(100, budget, "entry1", "2013-05-05");
		Entry entry2 = new Entry(1000, budget, "entry2", "2013-05-06");
		budget.addEntry(entry1);
		budget.addEntry(entry2);
		budget.removeEntry(entry1);
		List<Entry> entries = budget.getEntries();
		assertEquals(1, entries.size());
		assertFalse(entries.contains(entry1));
		assertTrue(entries.contains(entry2));
	}

	@SmallTest
	public void test_getCurrentCycle_isBeforeStart_shoudBeNegative1() {
		// Create a budget that starts tomorrow, so that it's always
		// after today
		Budget budget = new Budget("test", 1000, true,
				LocalDate.now().plusDays(1), Duration.WEEK);
		assertEquals(-1, budget.getCurrentCycle());
	}

	@SmallTest
	public void test_getCurrentCycle_isOnStart_shouldBe1() {
		Budget budget = new Budget("test", 1000, true,
				LocalDate.now(), Duration.WEEK);
		assertEquals(0, budget.getCurrentCycle());
	}

	@SmallTest
	public void test_getCurrentCycle_isAfterStart_shouldBeGreaterThan1() {
		Budget budget = new Budget("test", 1000, true,
				LocalDate.now().minusWeeks(1), Duration.WEEK);
		assertEquals(1, budget.getCurrentCycle());
	}

	@SmallTest
	public void test_getStartDate_negativeCycle_shouldThrowIllegalArgumentException() {
		Budget budget = buildBasicBudget(Duration.WEEK);

		try {
			budget.getStartDate(-1);
			fail("Should have thrown an IllegalArgumentException with negative cycle");
		} catch (IllegalArgumentException e) {
			assertNotNull(e.getMessage());
		}
	}

	@SmallTest
	public void test_getStartDate_zeroCycleWeekly_shouldBeInitialStartDate() {
		LocalDate initialStartDate = new LocalDate(2012, 05, 05);

		runStartDateTest(Duration.WEEK, initialStartDate, initialStartDate, 0);
	}

	@SmallTest
	public void test_getStartDate_firstCycleWeekly_shouldBeOneWeekLater() {
		LocalDate initialStartDate = new LocalDate(2012, 05, 05);
		LocalDate startDate = new LocalDate(2012, 05, 12);

		runStartDateTest(Duration.WEEK, initialStartDate, startDate, 1);
	}

	@SmallTest
	public void test_getStartDate_firstCycleFortnightly_shouldBeOneFortnightLater() {
		LocalDate initialStartDate = new LocalDate(2012, 05, 05);
		LocalDate startDate = new LocalDate(2012, 05, 19);

		runStartDateTest(Duration.FORTNIGHT, initialStartDate, startDate, 1);
	}

	@SmallTest
	public void test_getStartDate_firstCycleDaily_shouldBeOneDayLater() {
		LocalDate initialStartDate = new LocalDate(2012, 05, 05);
		LocalDate startDate = new LocalDate(2012, 05, 06);

		runStartDateTest(Duration.DAY, initialStartDate, startDate, 1);
	}

	@SmallTest
	public void test_getEndDate_negativeCycle_shouldThrowIllegalArgumentException() {
		Budget budget = buildBasicBudget(Duration.WEEK);

		try {
			budget.getEndDate(-1);
			fail("Should have thrown an IllegalArgumentException with negative cycle");
		} catch (IllegalArgumentException e) {
			assertNotNull(e.getMessage());
		}
	}

	@SmallTest
	public void test_getEndDate_zeroCycleWeekly_shouldBeInSixDays() {
		LocalDate startDate = new LocalDate(2012, 05, 05);
		LocalDate endDate = new LocalDate(2012, 05, 11);

		runEndDateTest(Duration.WEEK, startDate, endDate, 0);
	}

	@SmallTest
	public void test_getEndDate_zeroCycleDaily_shouldBeToday() {
		LocalDate startDate = new LocalDate(2012, 05, 05);
		LocalDate endDate = new LocalDate(2012, 05, 05);

		runEndDateTest(Duration.DAY, startDate, endDate, 0);
	}

	@SmallTest
	public void test_getEndDate_zeroCycleMonthly_shouldBeNextMonthFromYesterday() {
		LocalDate startDate = new LocalDate(2012, 05, 05);
		LocalDate endDate = new LocalDate(2012, 06, 04);

		runEndDateTest(Duration.MONTH, startDate, endDate, 0);
	}

	@SmallTest
	public void test_getEndDate_firstCycleWeekly_shouldBeInThirteenDays() {
		LocalDate startDate = new LocalDate(2012, 05, 05);
		LocalDate endDate = new LocalDate(2012, 05, 18);

		runEndDateTest(Duration.WEEK, startDate, endDate, 1);
	}

	@SmallTest
	public void test_getEndDate_firstCycleDaily_shouldBeTomorrow() {
		LocalDate startDate = new LocalDate(2012, 05, 05);
		LocalDate endDate = new LocalDate(2012, 05, 06);

		runEndDateTest(Duration.DAY, startDate, endDate, 1);
	}

	@SmallTest
	public void test_getEndDate_firstCycleMonthly_shouldBeTwoMonthsFromYesterday() {
		LocalDate startDate = new LocalDate(2012, 05, 05);
		LocalDate endDate = new LocalDate(2012, 07, 04);

		runEndDateTest(Duration.MONTH, startDate, endDate, 1);
	}

}
