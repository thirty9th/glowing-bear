/**
 * 
 * WorksheetActivity.java - activity definition for worksheet browsing, editing
 * Description - shows the user the contents of a particular worksheet and allows them to add,
 * remove and view more items. This is where the app displays crafting recipes and final tallies
 * of materials. 
 * 
 */

package com.example.minecraftapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WorksheetActivity extends Activity
{
	// File names
	static final String WORKSHEET_DATA_DIRECTORY = "worksheet_data";
	
	// View to be instantiated
	TextView worksheetTitle;
	ListView itemListview;
	
	// Globals
	FileManager fileManager;
	ErrorLogManager ELog;
	String name;
	ItemDataManager itemManager;
	List<Ingredient> itemList;

	// Called when the activity is created
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Resume and set layout
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);	//Remove title bar
		setContentView(R.layout.activity_worksheet);
		
		// Grab instancse of error log manager
		ELog = new ErrorLogManager();
		ELog.setContext(this);
		
		// Grab intent extras
		Intent intent = getIntent();
        name = intent.getStringExtra("name");
        
        // Instantiate views
        worksheetTitle = (TextView)findViewById(R.id.text_worksheet_title);
        itemListview = (ListView)findViewById(R.id.list_items);
        
        // Set proper title for worksheet
        worksheetTitle.setText(name);
        
        // Instantiate the item lists and item manager
        itemList = new ArrayList<Ingredient>();
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
        
        // Grab instance of file manager, have it perform setup
        fileManager = new FileManager(this);
        fileManager.setupFile(name, fileManager.WORKSHEET_DATA_DIRECTORY);
        
        // Parse out the needed data from rawFileData and put it into the baseItems list
        loadItemList();
        loadItemListview();
	}
	
	// Called when app is stopped
	@Override
	protected void onStop()
	{
		super.onStop();
		
		// Build a list of lines to write to file
		// A line is formatted as: <name of item> <quantity of item>
		// File manager takes care of end-of-line characters
		List <String> linesToWrite = new ArrayList<String>();
		for (int i = 0; i < itemList.size(); i++)
		{
			Ingredient thisIngredient = itemList.get(i);
			String thisName = thisIngredient.name;
			String thisQuantity = thisIngredient.quantity;
			int thisLevel = thisIngredient.level;
			linesToWrite.add(thisName + "-" + thisQuantity + "-" + thisLevel);
		}
		
		fileManager.writeLinesToFile(linesToWrite, name, fileManager.WORKSHEET_DATA_DIRECTORY);
	}
	
	// Called when app is paused
	@Override
	protected void onPause()
	{
		super.onPause();
		
		// Build a list of lines to write to file
		// A line is formatted as: <name of item> <quantity of item>
		// File manager takes care of end-of-line characters
		List <String> linesToWrite = new ArrayList<String>();
		for (int i = 0; i < itemList.size(); i++)
		{
			Ingredient thisIngredient = itemList.get(i);
			String thisName = thisIngredient.name;
			String thisQuantity = thisIngredient.quantity;
			int thisLevel = thisIngredient.level;
			linesToWrite.add(thisName + "-" + thisQuantity + "-" + thisLevel);
		}
		
		fileManager.writeLinesToFile(linesToWrite, name, fileManager.WORKSHEET_DATA_DIRECTORY);
	}
	
	// Called when app is resumed
	@Override
	protected void onResume()
	{
		super.onResume();
		loadItemList();
		loadItemListview();
	}
	
	// Used to load item list from file
	private void loadItemList()
	{
		// First clear the item list
		itemList.clear();
		
		// Load the item list from data stored in file
        List<String> rawFileData = new ArrayList<String>();
        rawFileData = fileManager.readLinesFromFile(name, fileManager.WORKSHEET_DATA_DIRECTORY);
        
        // Parse out the data
        String thisLine, thisQuantity, thisName;
        int thisLevel = 0;
        for (int i = 0; i < rawFileData.size(); i++)
        {
        	thisLine = rawFileData.get(i);
        	String[] tokens = thisLine.split("-");
        	thisName = tokens[0];
        	thisQuantity = tokens[1];
        	thisLevel = Integer.parseInt(tokens[2]);
        	itemList.add(new Ingredient(thisQuantity, thisName, thisLevel));
        }
	}
	
	// Called when the user clicks the add item button
	public void onClickAddItem(View view)
	{
		// Set up and show a new alert dialog
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_item, (ViewGroup)findViewById(R.id.dialog_add_item_root));
		b.setView(v);
		
		// Set up the right (okay/positive) button
		b.setPositiveButton(R.string.add, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				// Instantiate our views and grab info from them
				EditText itemName = (EditText)((AlertDialog)dialog).findViewById(R.id.text_item_name);
				EditText itemQuantity = (EditText)((AlertDialog)dialog).findViewById(R.id.text_item_quantity);
				String name = itemName.getText().toString();
				String quantity = itemQuantity.getText().toString();

				// TODO: Make sure it's valid input (non-empty, number limit etc.)
				// TODO: Search item database for items, load them as quick-search results
				// NOTE: Third parameter indicates the item's hierarchy level; a level of 0 is a parent item added by the user
				// Items with levels of 1 indicate children of level 0 items and so forth
				itemList.add(new Ingredient(quantity, name, 0));
				loadItemListview();
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
	
	// Populates the listview from the list of items
	// Note: uses hashmaps for extensibility... may add custom item icons later
	private void loadItemListview()
	{
		// Populate the list, mapping the name of the item to its proper view in the
		// individual item layout (keeping hashmap -> simple adapter layout for extensibility)
		ItemListAdapter adapter = new ItemListAdapter(this, R.layout.listview_item_items, itemList);
		itemListview.setAdapter(adapter);
		
		// Make each item in the populated list clickable
		itemListview.setOnItemClickListener(new OnItemClickListener()
		{
			  public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id)
			  {
				  // Toggle visibility of the selected item's children
				  if (pos < itemList.size() - 1)	// Not last item in the list
				  {
					  int currentLevel = itemList.get(pos).level;
					  if (itemList.get(pos + 1).level > currentLevel)	// Item directly underneath is of lower precendence, hide children
					  {
						  while (itemList.get(pos + 1).level > currentLevel)
						  {
							  itemList.remove(pos + 1);
							  if (pos == itemList.size() - 1)
							  {
								  loadItemListview();
								  return;
							  }
						  }
						  loadItemListview();
					  }
					  else
					  {
						  showChildren(pos);		// Item directly underneath is of equal or greater precedence, show children
					  }
				  }
				  else								// Last item in the list
				  {
					  showChildren(pos);
				  }
			  }
		});
		
		// Make each item also long-clickable for deletion
		// TODO: add multi-select functionality for faster deletion
		itemListview.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id)
			{
				// Create a dialog asking the user if they want to delete the selected worksheet
				//onDeleteWorksheet(pos);
				
				return false;
			}
		});
	}
	
	// Adds the children of the target item to the item list and updates it (recursive)
	private void showChildren(int targetIndex)
	{
		// Grab desired ingredient and its recipe from item manager
		Ingredient i = itemList.get(targetIndex);
		int parentLevel = i.level;
		int index = itemManager.searchItem(i.name);
		if (index != -1)
		{
			// Grab the recipe items
			CraftingItem item = itemManager.itemList.get(index);
			int produced = item.recipes.get(0).produced;
			if (produced == 0) return;
			double required = Double.parseDouble(i.quantity) / (double)produced;
			int scaleFactor = (int)Math.ceil(required);
			
			// Update each item in tempList to its proper quantity defined by scaleFactor * ingredientQuantityinRecipe
			// Set their levels, then add each ingredient to the item list for display
			for (Ingredient ingredient : item.recipes.get(0).ingredients)
			{
				int before = Integer.parseInt(ingredient.quantity);
				String neededQuantity = String.valueOf(before * scaleFactor);
				itemList.add(targetIndex + 1, new Ingredient(neededQuantity, ingredient.name, parentLevel + 1));
			}
			
			// Tell the list view to update to reflect the changes to the list of items
			loadItemListview();
		}
	}
	
}
