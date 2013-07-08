/**
 * 
 * MainActivity.java - activity definition for main menu
 * Description - shows the user their options for knowledge browsing in the form of buttons
 * 
 */

package com.example.minecraftapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{

	// Views to be instantiated
	ListView worksheetListView;
	TextView worksheetTitleText;
	
	// Globals
	ItemDataManager itemManager;
	List<String> worksheetList;
	FileManager fileManager;

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
		
		// Instantiate the file manager and have it perform initial setups such as loading
		// worksheets from file
		fileManager = new FileManager(this);
		worksheetList = fileManager.listFiles(fileManager.WORKSHEET_DATA_DIRECTORY);
		loadWorksheetListview();			// Add worksheets from the above list to the actual view
		updateWorksheetCount();				// Update count of worksheets displayed on the titlebar
	}
	
	// Called when the user hits submit button on search bar
	public void onClickSearch(View v)
	{
		Toast.makeText(getBaseContext(), "Clicked Search", Toast.LENGTH_SHORT).show();
	}
	
	// Called when the user hits the add worksheet button
	public void onClickAddWorksheet(View v)
	{
		// Set up and show a new alert dialog
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		final EditText nameBox = new EditText(this);
		nameBox.setHint(R.string.dialog_create_worksheet_prompt);
		b.setView(nameBox);
		
		// Set up the right (okay/positive) button
		b.setPositiveButton(R.string.create, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				// Grab the text the user entered
				String name = nameBox.getText().toString();
				
				// Make sure it's valid input (not a duplicate, not empty)
				// TODO: Search worksheet list for duplicates, then sort it when the
				// new name is added
				if (name.length() > 0 && name.length() <= 32)
				{
					// Make a new blank file for the target worksheet
					fileManager.writeLinesToFile(new ArrayList<String>(), name, fileManager.WORKSHEET_DATA_DIRECTORY);
					worksheetList = fileManager.listFiles(fileManager.WORKSHEET_DATA_DIRECTORY);
					Collections.sort(worksheetList);
					updateWorksheetCount();
					loadWorksheetListview();
				}
				else
				{
					// User's entered name was either empty or too long
					Toast.makeText(getApplication(), "Name must be non-empty and no more than 32 characters. Please try again.", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		// Set up the left (no/negative) button
		b.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
			}
		});
		
		// Show finished result
		b.show();
	}
	
	// Called when the user confirms deletion of a worksheet after a long-click
	private void onDeleteWorksheet(final int pos)
	{
		// Set up and show a new alert dialog
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setMessage(R.string.confirm_deletion_prompt);
		b.setTitle(R.string.confirm_deletion);

		// Set up the right (okay/positive) button
		b.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				// Remove the selected worksheet
				String name = worksheetList.get(pos);
				fileManager.deleteFile(name, fileManager.WORKSHEET_DATA_DIRECTORY);
				worksheetList = fileManager.listFiles(fileManager.WORKSHEET_DATA_DIRECTORY);
				Collections.sort(worksheetList);
				loadWorksheetListview();
				updateWorksheetCount();
			}
		});

		// Set up the left (no/negative) button
		b.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
			}
		});

		// Show finished result
		b.show();
	}
	
	// Opens the worksheet activity with the requested worksheet's data
	private void openWorksheet(String name)
	{
		// Create intent, add name as extra data
		Intent intent = new Intent(this, WorksheetActivity.class);
    	intent.putExtra("name", name);
    	
    	startActivity(intent);
	}
	
	// Populates the listview from the list of worksheets
	// Note: uses hashmaps for extensibility... may add custom worksheet icons later
	private void loadWorksheetListview()
	{
		// Populate the list, mapping the name of the worksheet to its proper view in the
		// individual item layout (keeping hashmap -> simple adapter layout for extensibility)
		List<HashMap<String, String>> hashList = new ArrayList<HashMap<String, String>>();
		for(int i = 0; i < worksheetList.size(); i++)
		{
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("name", worksheetList.get(i));
            hashList.add(hm);
        }
		String[] from = {"name"};
		int[] to = {R.id.text_listview_worksheet_name};
		SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), hashList, R.layout.listview_item_worksheets, from, to);
		worksheetListView.setAdapter(adapter);
		
		// Make each item in the populated list clickable
		worksheetListView.setOnItemClickListener(new OnItemClickListener()
		{
			  public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id)
			  {
				  // Open the requested worksheet
				  openWorksheet(worksheetList.get(pos));
			  }
		});
		
		// Make each item also long-clickable for deletion
		// TODO: add multi-select functionality for faster deletion
		worksheetListView.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id)
			{
				// Create a dialog asking the user if they want to delete the selected worksheet
				onDeleteWorksheet(pos);
				
				return false;
			}
		});
	}

	// Called when the user hits the menu softkey
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// Sets the worksheet count next to the title
	private void updateWorksheetCount()
	{
		worksheetTitleText.setText("Worksheets (" + worksheetList.size() + ")");
	}

};
