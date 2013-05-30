package com.example.budgetmanager;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class DrawBudgetGraph extends SurfaceView {
	private List<Entry> entryList;
	private Budget budget;

	public void setEntryList(List<Entry> entryList, Budget budget) {
		this.entryList = entryList;
		this.budget = budget;
	}

    public DrawBudgetGraph(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

    private long priceToY(int price, int max, int height) {
    	return height - ( price * height) / (max * -1);
    }
    
    private long dateToX(long date, long start, long end, int width) {
    	int duration = (int) (end - start);
    	return ((date - start) * width) / duration;
    }
    
	@Override
    protected void onDraw(Canvas canvas) {
		int budgetMax = 200; //TODO get budget too
		
		Paint mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        canvas.drawCircle(20, 20, 5, mPaint);

        int height = canvas.getHeight();
        int width = canvas.getWidth();
        
        //Draw entries
        //TODO sort entries by date in activity prior to this stage
        int[] entryHeights = new int[entryList.size()];
        
        if(!entryList.isEmpty())
        	entryHeights[0] = entryList.get(0).getAmount();

        for(int i = 1; i < entryList.size(); i++)
        	entryHeights[i] = entryHeights[i - 1] + entryList.get(i).getAmount();
 
        int max = Math.max(budgetMax, entryHeights[entryList.size() - 1]);
        
        
        long lastx = 0;
        long lasty = 0;
        
        for(int i = 1; i < entryList.size(); i++) {
        	int cycle = budget.getCurrentCycle();
        	
        	//Temp testing method.  Need to change before code checkin.
        	long x = dateToX(entryList.get(i).getCreatedAt().toDate().getTime(),budget.getStartDate(cycle).toDate().getTime(),budget.getEndDate(cycle).toDate().getTime(),width);
        	long y = priceToY(entryHeights[i], max, height);
        	
        	canvas.drawLine(lastx, lasty, x, y, mPaint);
            lastx = x;
        	lasty = y;
        }
        
        //Draw target line
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Style.STROKE);
        mPaint.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
        canvas.drawLine(0, height - 1, width - 1, 0, mPaint);
    }
}
