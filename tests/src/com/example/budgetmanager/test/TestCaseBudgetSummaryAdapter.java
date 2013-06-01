package com.example.budgetmanager.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.widget.TextView;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.BudgetSummaryAdapter;
import com.example.budgetmanager.R;

import java.util.ArrayList;
import java.util.List;

public class TestCaseBudgetSummaryAdapter extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
		Budget.clearBudgets();
	}

	@SmallTest
	public void test_noBudgetConstructor_shouldHaveNoBudgets() {
		BudgetSummaryAdapter summary = new BudgetSummaryAdapter(getContext(),
				com.example.budgetmanager.R.layout.list_budget_layout);

		assertTrue("The Budget list wasn't empty!",
				summary.isEmpty());
	}

	@SmallTest
	public void test_listConstructor_emptyList_shouldHaveNoBudgets() {
		BudgetSummaryAdapter summary = new BudgetSummaryAdapter(getContext(),
				com.example.budgetmanager.R.layout.list_budget_layout,
				new ArrayList<Budget>());

		assertTrue("The Budget list wasn't empty!",
				summary.isEmpty());
	}

	@SmallTest
	public void test_listConstructor_nonEmptyList_shouldHaveListOfBudgets() {
		List<Budget> budgets = Factory.budgetListFactory(0, 0, 0);

		BudgetSummaryAdapter summary = new BudgetSummaryAdapter(getContext(),
				com.example.budgetmanager.R.layout.list_budget_layout, budgets);

		assertEquals("The Budget list didn't have 3 items",
				3, summary.getCount());
	}

	@SmallTest
	public void test_getView_multipleBudgets_showsRightBudget() {
		List<Budget> budgets = Factory.budgetListFactory(1, 2, 3);

		BudgetSummaryAdapter summary = new BudgetSummaryAdapter(getContext(),
				com.example.budgetmanager.R.layout.list_budget_layout, budgets);

		View row = summary.getView(1, null, null);

		TextView budgetNameView = (TextView) row.findViewById(R.id.budget_name);
		String foundName = (String) budgetNameView.getText();

		Budget expectedBudget = budgets.get(1);
		String expectedName = expectedBudget.getName();

		assertEquals("Budget name didn't match list row's name",
				expectedName, foundName);
	}
}
