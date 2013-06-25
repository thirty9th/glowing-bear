/**
 * 
 * WorksheetActivity.java - activity definition for worksheet browsing, editing
 * Description - shows the user the contents of a particular worksheet and allows them to add,
 * remove and view more items. This is where the app displays crafting recipes and final tallies
 * of materials. 
 * 
 */

package com.example.minecraftapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class WorksheetActivity extends Activity
{
	
	// View to be instantiated
	TextView worksheetTitle;

	// Called when the activity is created
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Resume and set layout
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);	//Remove title bar
		setContentView(R.layout.activity_worksheet);
		
		// Grab intent extras
		Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        
        // Instantiate views
        worksheetTitle = (TextView)findViewById(R.id.text_worksheet_title);
        
        // Set proper title for worksheet
        worksheetTitle.setText(name);
	}
	
}
