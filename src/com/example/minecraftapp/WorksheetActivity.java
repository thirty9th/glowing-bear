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
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WorksheetActivity extends Activity
{
	// File names
	static final String WORKSHEET_DATA_DIRECTORY = "worksheet_data";
	
	// View to be instantiated
	TextView worksheetTitle;
	ListView itemList;
	
	// Globals
	FileManager fileManager;
	String name;

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
        name = intent.getStringExtra("name");
        
        // Instantiate views
        worksheetTitle = (TextView)findViewById(R.id.text_worksheet_title);
        itemList = (ListView)findViewById(R.id.list_items);
        
        // Set proper title for worksheet
        worksheetTitle.setText(name);
        
        // Grab instance of file manager, have it perform setup
        fileManager = new FileManager(this);
        fileManager.setupWorksheetDataFile(name);	// 'worksheet_data/<worksheet_name>'
	}
	
	// Called when the user clicks the add item button
	public void onClickAddItem(View view)
	{
		Toast.makeText(getApplication(), "Clicked add item", Toast.LENGTH_SHORT).show();
	}
	
}
