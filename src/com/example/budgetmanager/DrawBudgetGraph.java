package com.example.budgetmanager;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

public class DrawBudgetGraph extends SurfaceView {
	private List<Entry> entryList;
	private Budget budget;
	private int cycle;
	
	public void setEntryList(List<Entry> entryList, Budget budget, int cycle) {
		this.entryList = entryList;
		this.budget = budget;
		this.cycle = cycle;
	}

    public DrawBudgetGraph(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setWillNotDraw(false);
	}

    private long priceToY(int price, int max, int height) {
    	return height - ((price * height) / (max));
    }
    
    private long dateToX(long date, long start, long end, int width) {
    	long duration = (long) (end - start);
    	Log.v("surface", Long.toString(end - start));
    	Log.v("surface", Long.toString(date - start));
    	Log.v("surface", Long.toString((date - start) * (width)));

    	return ((date - start) * (width)) / duration;
    }
    
	@Override
    protected void onDraw(Canvas canvas) {
		Log.v("surface","Drew to the canvas");
		int budgetMax = budget.getBudgetAmount(); //TODO get budget too
		
		Paint mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(5);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        int height = canvas.getHeight();
        int width = canvas.getWidth();
        
        //Draw entries
        //TODO sort entries by date in activity prior to this stage
        int[] entryHeights = new int[entryList.size()];
        
        if(!entryList.isEmpty())
        	entryHeights[0] = entryList.get(0).getAmount();

        for(int i = 1; i < entryList.size(); i++)
        	entryHeights[i] = entryHeights[i - 1] + entryList.get(i).getAmount();
 
		Log.v("surface","Created list of additive sizes. with size of " + entryList.size());
        
        int max = budgetMax;
        if(entryList.size() > 0)
        	max = Math.max(budgetMax, entryHeights[entryList.size() - 1]);
        
        long lastx = 0;
        long lasty = height;
        
        
        for(int i = 0; i < entryList.size(); i++) {
        	
        	//Temp testing method.  Need to change before code checkin.
        	long x = dateToX(entryList.get(i).getDate().toDate().getTime(),budget.getStartDate(cycle).toDate().getTime(),budget.getEndDate(cycle).toDate().getTime(),width);
        	long y = priceToY(entryHeights[i], max, height);
        	
			Log.v("mytag", budget.getStartDate(cycle).toString());
			Log.v("mytag", budget.getEndDate(cycle).toString());
			//Log.v("mytag", entryList.get(i).getCreatedAt().toDate().toString());
			
        	Log.v("surface", lastx + "lx, " + lasty + "ly, " + x + "x, " + y +"y");
        	
        	canvas.drawLine(lastx, lasty, x, y, mPaint);
            lastx = x;
        	lasty = y;
        }
        
        Log.v("surface", width + " " + height);
		Log.v("surface","Drew entry lines");

        
        //Draw target line
        mPaint.setColor(Color.YELLOW);
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeWidth(4);
        mPaint.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
        canvas.drawLine(0, height - 1, width - 1, priceToY(budgetMax, max, height), mPaint);
		Log.v("surface","Drew dotted line");
        Log.v("surface","Finished drawing to the canvas");
    }
}
