package com.example.budgetmanager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.SurfaceView;

import java.util.List;

/**
 * Graph class for BudgetSummaryActivity to draw a budget
 * to a surfaceView.
 *
 * @author Andrew clinger
 */
public class DrawBudgetGraph extends SurfaceView {

	//List of entries from budget from a given cycle.
	private List<Entry> entryList;

	//Budget to draw data from.
	private Budget budget;

	//cycle of budget to display.
	private int cycle;

	//Different paints used in the graph.
	private final Paint entryPaint;
	private final Paint averagePaint;

	/**
	 * Returns the end date of the given cycle.
	 *
	 * @param cycle The cycle to calculate the end time of.
	 * @param entryList A list of entries from budget to graph.
	 * @param budget A Budget.
	 * @throws IllegalArgumentException If the cycle is negative,
	 *         or entryList or budget are null
	 */
	public void setProperties(List<Entry> entryList, Budget budget, int cycle) {
		if (cycle < 0 || entryList == null || budget == null) {
			throw new IllegalArgumentException();
		}
		this.entryList = entryList;
		this.budget = budget;
		this.cycle = cycle;
	}

	/**
	 * Returns a DrawBudgetGraph
	 *
	 * @param context Context that the graph is in.
	 * @param attrs Attributes.
	 */
	public DrawBudgetGraph(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(false);

		entryPaint = new Paint();
		entryPaint.setColor(Color.GREEN);
		entryPaint.setStrokeWidth(5);
		entryPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

		averagePaint = new Paint();
		averagePaint.setColor(Color.YELLOW);
		averagePaint.setStyle(Style.STROKE);
		averagePaint.setStrokeWidth(4);
		averagePaint.setPathEffect(new DashPathEffect(new float[] {5, 5}, 3));
	}

	/** Helper method to convert from a price to a Y coordinate */
	private static long priceToY(int price, int max, int height) {
		return height - ((price * height) / (max));
	}

	/** Helper method to convert from a date to an X coordinate */
	private static long dateToX(long date, long start, long end, int width) {
		long duration = end - start;
		return ((date - start) * (width)) / duration;
	}

	/**
	 * Draws the graph.
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.GRAY);

		int budgetMax = budget.getBudgetAmount();

		int height = canvas.getHeight();
		int width = canvas.getWidth();

		//Draw entries
		int[] entryHeights = new int[entryList.size()];

		if (!entryList.isEmpty()) {
			entryHeights[0] = entryList.get(0).getAmount();
		}

		for (int i = 1; i < entryList.size(); i++) {
			entryHeights[i] = entryHeights[i - 1] + entryList.get(i).getAmount();
		}

		int max = budgetMax;
		if (entryList.size() > 0) {
			max = Math.max(budgetMax, entryHeights[entryList.size() - 1]);
		}

		long lastx = 0;
		long lasty = height;


		for (int i = 0; i < entryList.size(); i++) {
			long x = dateToX(entryList.get(i).getDate().toDateTimeAtStartOfDay().getMillis(),
					budget.getStartDate(cycle).toDateTimeAtStartOfDay().getMillis(),
					budget.getEndDate(cycle).toDateTimeAtStartOfDay().getMillis(),
					width);
			long y = priceToY(entryHeights[i], max, height);

			canvas.drawLine(lastx, lasty, x, y, entryPaint);
			lastx = x;
			lasty = y;
		}

		//Draw target line
		canvas.drawLine(0, height - 1, width - 1, priceToY(budgetMax, max, height), averagePaint);
	}
}
