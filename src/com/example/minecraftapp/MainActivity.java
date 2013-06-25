/**
 * 
 * MainActivity.java - activity definition for main menu
 * Description - shows the user their options for knowledge browsing in the form of buttons
 * 
 */

package com.example.minecraftapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
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
		readWorksheetFile();
		if (worksheetList.size() == 0) worksheetList.add("No worksheets found");
		
		// Now add all the worksheets to the listview for display
		loadWorksheetListview();
		
		// Update count of worksheets displayed on the titlebar
		updateWorksheetCount();

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
	
	// Called when app is stopped
	@Override
	protected void onStop()
	{
		super.onStop();
		writeWorksheetFile();
	}
	
	// Called when app is paused
	@Override
	protected void onPause()
	{
		super.onPause();
		writeWorksheetFile();
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
				if (name.length() > 0)
				{
					if (worksheetList.size() == 1 && worksheetList.get(0) == "No worksheets found") worksheetList.clear();
					worksheetList.add(name);
					updateWorksheetCount();
					loadWorksheetListview();
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
	
	// Sets up the worksheet file for future writing and reading
	private void setupWorksheetFile()
	{
		// End of line character (may differ by system)
		String endl = System.getProperty("line.separator");
		BufferedWriter bw = null;
		FileInputStream fis = null;
		
		// Attempt to open the target file; if it doesn't exist, then create a blank instance of it
		// for future data storage
		try 
		{
			fis = openFileInput(FILENAME_WORKSHEETS);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Log.e(this.toString(), "Could not find worksheet storage file... creating it.");
			
			// Create the file
			try
			{
				bw = new BufferedWriter(new OutputStreamWriter(openFileOutput(FILENAME_WORKSHEETS, MODE_PRIVATE)));
				bw.write("Blank Worksheet" + endl);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				Log.e(this.toString(), "Couldn't use buffered writer.");
			}
			finally
			{
				if (bw != null)
				{
					try
					{
						bw.close();
					}
					catch (IOException exc)
					{
						e.printStackTrace();
						Log.e(this.toString(), "Couldn't close buffered writer.");
					}
				}
			}
		}
		finally
		{
			if (fis != null)
			{
				try 
				{
					fis.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
					Log.e(this.toString(), "Couldn't close file input stream.");
				}
			}
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
			FileInputStream fis = openFileInput(FILENAME_WORKSHEETS);
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
			Log.e(this.toString(), "FileNotFoundException");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Log.e(this.toString(), "IOException");
		}
		
		return count;
	}
	
	private void writeWorksheetFile()
	{
		// End of line character (may differ by system)
		String endl = System.getProperty("line.separator");
		BufferedWriter bw = null;
		
		// Attempt to write worksheet list to file
		try
		{
			bw = new BufferedWriter(new OutputStreamWriter(openFileOutput(FILENAME_WORKSHEETS, MODE_PRIVATE)));
			int current = 0;
			while (current < worksheetList.size())
			{
				bw.write(worksheetList.get(current) + endl);
				current++;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Log.e(this.toString(), "IOException for buffered writer.");
		}
		finally
		{
			if (bw != null)
			{
				try
				{
					bw.close();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
					Log.e(this.toString(), "IOException while closing buffered writer.");
				}
			}
		}
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
		int[] to = {R.id.text_worksheet_name};
		SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), hashList, R.layout.listview_item_worksheets, from, to);
		worksheetListView.setAdapter(adapter);
		
		// Make each item in the populated list clickable
		worksheetListView.setOnItemClickListener(new OnItemClickListener()
		{
			  public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
			  {
				  // Test
				  // TODO: Add functionality to open worksheets individually
				  Toast.makeText(getBaseContext(), "Clicked: " + worksheetList.get(position), Toast.LENGTH_LONG).show();
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
