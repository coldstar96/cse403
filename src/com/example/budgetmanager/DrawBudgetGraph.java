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

	public void setEntryList(List<Entry> entryList) {
		this.entryList = entryList;
	}
    public DrawBudgetGraph(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
    protected void onDraw(Canvas canvas) {
		int budgetMax = 200; //TODO get budget too
		
		Paint mPaint = new Paint();
        mPaint.setColor(Color.RED);
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

        if(entryHeights[entryList.size() - 1] > budgetMax)
        	
        //Draw target line
        mPaint.setStyle(Style.STROKE);
        mPaint.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
        canvas.drawLine(0, height - 1, width - 1, 0, mPaint);
    }
}
