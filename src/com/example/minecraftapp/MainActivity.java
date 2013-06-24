/**
 * 
 * MainActivity.java - activity definition for main menu
 * Description - shows the user their options for knowledge browsing in the form of buttons
 * 
 */

package com.example.minecraftapp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{
	
	// File names
	static final String FILENAME_WORKSHEETS = "worksheets";
	
	// Views to be instantiated
	ListView worksheetListView;
	TextView worksheetTitleText;
	
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
		worksheetListView = (ListView)findViewById(R.id.list_worksheets);
		worksheetTitleText = (TextView)findViewById(R.id.text_main_titlebar);
		
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
		
		// Set up worksheet storage file if this is the first time entering the app
		setupWorksheetFile();
		
		// Populate the list of worksheets
		worksheetList = new ArrayList<String>();
		int worksheetCount = readWorksheetFile();
		if (worksheetCount == 0) worksheetList.add("No worksheets found");
		
		// Now add all the worksheets to the listview for display
		loadWorksheetListview();
		
		// Update count of worksheets displayed on the titlebar
		worksheetTitleText.setText("Worksheets (" + worksheetCount + ")");

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
	
	// Sets up the worksheet file for future writing and reading
	private void setupWorksheetFile()
	{
		File f = new File(FILENAME_WORKSHEETS);
		try
		{
			f.createNewFile();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Toast.makeText(this, "ERROR: IOException", Toast.LENGTH_SHORT).show();
		}
	}
	
	// Reads worksheets from stored data, if any exist
	// Returns: number of worksheets read
	private int readWorksheetFile()
	{
		int count = 0;
		try
		{
			// Open file input and wrappers: ends up in buffered reader
			FileInputStream fis = new FileInputStream(FILENAME_WORKSHEETS);
			DataInputStream dis = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(dis));
			
			// Read file by line
			String line;
			while ((line = br.readLine()) != null)
			{
				worksheetList.add(line);
				count++;
			}
			
			// Close reader
			dis.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			Toast.makeText(this, "ERROR: FileNotFoundException", Toast.LENGTH_SHORT).show();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Toast.makeText(this, "ERROR: IOException", Toast.LENGTH_SHORT).show();
		}
		
		return count;
	}
	
	// Populates the listview from the list of worksheets
	// Note: uses hashmaps for extensibility... may add custom worksheet icons later
	private void loadWorksheetListview()
	{
		List<HashMap<String, String>> hashList = new ArrayList<HashMap<String, String>>();
		for(int i = 0; i < worksheetList.size(); i++)
		{
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("name", worksheetList.get(i));
            hashList.add(hm);
        }
		String[] from = {"name"};
		int[] to = {R.id.text_worksheet_name};
		SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), hashList, R.layout.listview_item_worksheets, from, to);
		worksheetListView.setAdapter(adapter);
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
