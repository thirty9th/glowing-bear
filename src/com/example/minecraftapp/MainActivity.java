/**
 * 
 * MainActivity.java - activity definition for main menu
 * Description - shows the user their options for knowledge browsing in the form of buttons
 * 
 */

package com.example.minecraftapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity
{
	
	// Views to be instantiated
	EditText searchBarField;
	Button searchBarSubmit;

	// Called when the activity receives a create intent
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Set layout
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Instantiate views
		searchBarField = (EditText)findViewById(R.id.text_search_bar_field);
		searchBarSubmit = (Button)findViewById(R.id.button_search_bar_submit);
	}
	
	// Called when the user hits submit button on search bar
	public void onClickSearch(View v)
	{
		Toast.makeText(getBaseContext(), "Clicked Search", Toast.LENGTH_SHORT).show();
	}

	// Called when the user hits the menu softkey
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

};
