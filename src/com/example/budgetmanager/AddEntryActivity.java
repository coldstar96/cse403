package com.example.budgetmanager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class AddEntryActivity extends Activity {
    
	private Spinner budgetSpinner;
	private String currentBudget = "china";
	
	@Override
	/** Called when the activity is first created. */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // get rid of the title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // inflate view
        setContentView(R.layout.add_activity_main);
        
        // populate list items for the budget selector
        addItemsToBudgetSpinner();
    }

	private void addItemsToBudgetSpinner() {
		// TODO Auto-generated method stub
		budgetSpinner = (Spinner) findViewById(R.id.spinner_budget);
		List<String> list = new ArrayList<String>();
		list.add(currentBudget);
		list.add("poop");
		list.add("bee");
		list.add("lol");
		list.add("gaga");
		list.add("tooots");
		list.add("booots");
		list.add("boobies");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_layout, list);
		dataAdapter.setDropDownViewResource(R.layout.spinner_entry_layout);
		budgetSpinner.setAdapter(dataAdapter);
		budgetSpinner.setOnItemSelectedListener(new OnBudgetSelectedListener());
	}
}
