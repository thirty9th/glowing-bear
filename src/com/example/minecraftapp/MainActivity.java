/**
 * 
 * MainActivity.java - activity definition for main menu
 * Description - shows the user their options for knowledge browsing in the form of buttons
 * 
 */

package com.example.minecraftapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity
{
	
	// File names
	static final String FILENAME_WORKSHEETS = "worksheets";
	
	// Views to be instantiated
	ListView list;
	
	// Globals
	ItemDataManager itemManager;
	List<String> worksheetList;

	// Called when the activity receives a create intent
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Set layout
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);	//Remove title bar
		setContentView(R.layout.activity_main);
		
		// Instantiate views
		list = (ListView)findViewById(R.id.list_worksheets);
		
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
		
		// Populate the list of worksheets
		worksheetList = new ArrayList<String>();
//		loadWorksheets();
		
		// Test custom listview
		List<HashMap<String,String>> hashList = new ArrayList<HashMap<String,String>>();
		for(int i = 0; i < 10; i++)
		{
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("name", String.valueOf(i));
            hashList.add(hm);
        }
		String[] from = {"name"};
		int[] to = {R.id.text_worksheet_name};
		SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), hashList, R.layout.listview_item_worksheets, from, to);
		list.setAdapter(adapter);
		
		// Test display of all items
//		int count = 0;
//		int numRecipes;
//		String name;
//		while (count < itemManager.itemList.size())
//		{
//			CraftingItem thisItem = itemManager.itemList.get(count);
//			numRecipes = thisItem.recipes.size();
//			name = thisItem.name;
//			Toast.makeText(this, name + " has " + numRecipes + " recipes.", Toast.LENGTH_SHORT).show();
//			count++;
//		}
	}
	
	// Called when the user hits submit button on search bar
	public void onClickSearch(View v)
	{
		Toast.makeText(getBaseContext(), "Clicked Search", Toast.LENGTH_SHORT).show();
	}
	
	// Called when the user hits the add worksheet button
	public void onClickAddWorksheet(View v)
	{
		Toast.makeText(getBaseContext(), "Clicked add worksheet.", Toast.LENGTH_SHORT).show();
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
