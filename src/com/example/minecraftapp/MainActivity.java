/**
 * 
 * MainActivity.java - activity definition for main menu
 * Description - shows the user their options for knowledge browsing in the form of buttons
 * 
 */

package com.example.minecraftapp;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity
{
	
	// Views to be instantiated
	EditText searchBarField;
	Button searchBarSubmit;
	ListView list;
	
	// Globals
	ItemDataManager itemManager;

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
		list = (ListView)findViewById(R.id.list_view_test);
		
		// Create the item data manager and pull items from XML file
		itemManager = new ItemDataManager();
		try
		{
			itemManager.loadItemsFromXml(this);
		}
		catch (XmlPullParserException e)
		{
			e.printStackTrace();
			Toast.makeText(this, "ERROR: XmlPullParserException", Toast.LENGTH_SHORT).show();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Toast.makeText(this, "ERROR: IOException", Toast.LENGTH_SHORT).show();
		}
		
		// Test display of all items
		int count = 0;
		int numRecipes;
		String name;
		while (count < itemManager.itemList.size())
		{
			CraftingItem thisItem = itemManager.itemList.get(count);
			numRecipes = thisItem.recipes.size();
			name = thisItem.name;
			Toast.makeText(this, name + " has " + numRecipes + " recipes.", Toast.LENGTH_SHORT).show();
			count++;
		}
		
		// Test display of recipes
		//int[] data = {1, 2, 3};
		//ArrayAdapter adapter;
		//adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, data);
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
