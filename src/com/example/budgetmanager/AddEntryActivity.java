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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_activity_main);
        
        addItemsToBudgetSpinner();
    }

	private void addItemsToBudgetSpinner() {
		// TODO Auto-generated method stub
		budgetSpinner = (Spinner) findViewById(R.id.budget_spinner);
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
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		budgetSpinner.setAdapter(dataAdapter);
		budgetSpinner.setOnItemSelectedListener(new OnBudgetSelectedListener());
	}
}
