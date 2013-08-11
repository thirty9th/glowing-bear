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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
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
	ItemListAdapter adapter;
	List<Ingredient> itemList;

	// Called when the activity is created
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Resume and set layout
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);	//Remove title bar
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
        
        // Set up action bar icon to take user home when clicked
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
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
	public void onClickAddItem()
	{
		// Set up and show a new alert dialog
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		final View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_item, (ViewGroup)findViewById(R.id.dialog_add_item_root));
		b.setView(v);
		b.setTitle(R.string.dialog_add_item_title);
		
		// Set up auto-complete
		// TODO: Make sure the item names list is initialized before passing it to the
		// adapter and build it if it's not
		final AutoCompleteTextView itemName = (AutoCompleteTextView)v.findViewById(R.id.text_item_name);
		final TextView itemQuantity = (TextView)v.findViewById(R.id.text_item_quantity);
		itemQuantity.setText("1");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, MainActivity.itemNamesList);
		itemName.setAdapter(adapter);
		
		// Set up the right (okay/positive) button
		b.setPositiveButton(R.string.add, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				// Instantiate our views and grab info from them
				EditText itemQuantity = (EditText)((AlertDialog)dialog).findViewById(R.id.text_item_quantity);
				String name = itemName.getText().toString();
				String quantity = itemQuantity.getText().toString();

				// NOTE: Third parameter indicates the item's hierarchy level; a level of 0 is a parent item added by the user
				// Items with levels of 1 indicate children of level 0 items and so forth
				// Check for errors in input before attempting to add the item
				if (quantity.length() <= 0)
				{
					ELog.toast("Error", "Quantity field left blank");
					return;
				}
				if (Integer.parseInt(quantity) < 10000 && Integer.parseInt(quantity) > 0)
				{
					if (itemManager.searchItem(name) != -1)
					{
						itemList.add(new Ingredient(quantity, name, 0));
						loadItemListview();
					}
					else ELog.toast("Error", "Requested item not found");
				}
				else ELog.toast("Error", "Requested quantity must be between 1 and 10,000");
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
	public void loadItemListview()
	{
		// Save position in the list
		Parcelable state = itemListview.onSaveInstanceState();
		
		// Populate the list, mapping the name of the item to its proper view in the
		// individual item layout (keeping hashmap -> simple adapter layout for extensibility)
		adapter = new ItemListAdapter(this, R.layout.listview_item_items, itemList, itemManager);
		itemListview.setAdapter(adapter);
		
		// Scroll back to saved position
		itemListview.onRestoreInstanceState(state);
		
		// Set up multi-selection and context menu interface for items
		itemListview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		itemListview.setMultiChoiceModeListener(new MultiChoiceModeListener()
		{
			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int pos, long id, boolean checked)
			{
				ELog.toast("Item #" + Integer.toString(pos) + " is set to " + Boolean.toString(checked));
				// Handle selecting an item
				if (checked)
				{
					adapter.setNewSelection(pos, checked);				}
				else
				{
					adapter.removeSelection(pos);
				}
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item)
			{
				switch(item.getItemId())
				{
				case R.id.menu_delete_selected_items:
					deleteSelectedItems();
					mode.finish();
					break;
				}
				return false;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode)
			{
				// Called when the CAB is removed
				adapter.clearSelection();
				itemListview.setAdapter(adapter);
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu)
			{
				return false;
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu)
			{
				// Inflate menu
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.menu_items_cab, menu);
				return true;
			}
		});
	}
	
	// Removes the selected items when the user hits delete
	private void deleteSelectedItems()
	{
		Set<Integer> toDelete = adapter.getCheckedItems();
		List<Integer> toDeleteList = new ArrayList<Integer>(toDelete);
		Collections.sort(toDeleteList, Collections.reverseOrder());
		
		// Delete selected items and their open children
		for (int i = 0; i < toDeleteList.size(); i++)
		{
			int pos = toDeleteList.get(i);
			int originalLevel = itemList.get(pos).level;
			itemList.remove(pos);
			if (itemList.size() > 0 && pos < itemList.size())
			{
				int currentLevel = itemList.get(pos).level;
				while (pos < itemList.size() && currentLevel > originalLevel)
				{
					itemList.remove(pos);
					if (pos < itemList.size()) currentLevel = itemList.get(pos).level;
				}
			}
		}
		loadItemListview();
	}
	
	// Adds the children of the target item to the item list and updates it (recursive)
	public void showChildren(int targetIndex)
	{
		// First check for a special case; possible infinite loop between gold ingots and gold nuggets
		if (itemList.get(targetIndex).name.equalsIgnoreCase("Gold Ingot") && targetIndex > 0)
		{
			if (itemList.get(targetIndex - 1).name.equalsIgnoreCase("Gold Nugget")) return;
		}
		else if (itemList.get(targetIndex).name.equalsIgnoreCase("Gold Nugget") && targetIndex > 0)
		{
			if (itemList.get(targetIndex - 1).name.equalsIgnoreCase("Gold Ingot")) return;
		}
		
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
	
	// Shows the target recipe in the 3 x 3 crafting grid format with images
	public void onShowRecipe(final int pos)
	{
		// Set up a new dialog
		View v = LayoutInflater.from(this).inflate(R.layout.dialog_show_recipe, (ViewGroup)findViewById(R.id.dialog_show_recipe_root));
		ImageView imageUL = (ImageView)v.findViewById(R.id.layout_grid_row1_image1);
		ImageView imageUC = (ImageView)v.findViewById(R.id.layout_grid_row1_image2);
		ImageView imageUR = (ImageView)v.findViewById(R.id.layout_grid_row1_image3);
		ImageView imageL = (ImageView)v.findViewById(R.id.layout_grid_row2_image1);
		ImageView imageC = (ImageView)v.findViewById(R.id.layout_grid_row2_image2);
		ImageView imageR = (ImageView)v.findViewById(R.id.layout_grid_row2_image3);
		ImageView imageLL = (ImageView)v.findViewById(R.id.layout_grid_row3_image1);
		ImageView imageLC = (ImageView)v.findViewById(R.id.layout_grid_row3_image2);
		ImageView imageLR = (ImageView)v.findViewById(R.id.layout_grid_row3_image3);
		
		// Load proper images into the grid squares
		int index = itemManager.searchItem(itemList.get(pos).name);
		CraftingItem thisItem = itemManager.itemList.get(index);
		CraftingGrid grid = thisItem.grid;
		if (grid.ul != null)	// Here we are parsing the item name (e.g. "Wood Planks") into the qualified image name (e.g. "wood_planks")
		{
			String imageName = ItemDataManager.getImageFilename(grid.ul);
			imageUL.setImageResource(getResources().getIdentifier(imageName, "drawable", "com.example.minecraftapp"));
		}
		if (grid.uc != null)
		{
			String imageName = ItemDataManager.getImageFilename(grid.uc);
			imageUC.setImageResource(getResources().getIdentifier(imageName, "drawable", "com.example.minecraftapp"));
		}
		if (grid.ur != null)
		{
			String imageName = ItemDataManager.getImageFilename(grid.ur);
			imageUR.setImageResource(getResources().getIdentifier(imageName, "drawable", "com.example.minecraftapp"));
		}
		if (grid.l != null)
		{
			String imageName = ItemDataManager.getImageFilename(grid.l);
			imageL.setImageResource(getResources().getIdentifier(imageName, "drawable", "com.example.minecraftapp"));
		}
		if (grid.c != null)
		{
			String imageName = ItemDataManager.getImageFilename(grid.c);
			imageC.setImageResource(getResources().getIdentifier(imageName, "drawable", "com.example.minecraftapp"));
		}
		if (grid.r != null)
		{
			String imageName = ItemDataManager.getImageFilename(grid.r);
			imageR.setImageResource(getResources().getIdentifier(imageName, "drawable", "com.example.minecraftapp"));
		}
		if (grid.ll != null)
		{
			String imageName = ItemDataManager.getImageFilename(grid.ll);
			imageLL.setImageResource(getResources().getIdentifier(imageName, "drawable", "com.example.minecraftapp"));
		}
		if (grid.lc != null)
		{
			String imageName = ItemDataManager.getImageFilename(grid.lc);
			imageLC.setImageResource(getResources().getIdentifier(imageName, "drawable", "com.example.minecraftapp"));
		}
		if (grid.lr != null)
		{
			String imageName = ItemDataManager.getImageFilename(grid.lr);
			imageLR.setImageResource(getResources().getIdentifier(imageName, "drawable", "com.example.minecraftapp"));
		}
		
		// Set up the back button
		Builder b = new AlertDialog.Builder(this);
		b.setNegativeButton(R.string.back, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		
		// Show the resulting dialog
		@SuppressWarnings("unused")
		final AlertDialog dialog = b.setView(v).show();
	}
	
	// Called when the user hits the menu softkey
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_items, menu);
		return true;
	}

	// Handle menu items being clicked
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.menu_add_item:
			onClickAddItem();
			return true;
		case R.id.menu_sort_items:
			for (int i = 0; i < itemList.size(); i++)
			{
				while (itemList.get(i).level != 0)
				{
					itemList.remove(i);
					if (i >= itemList.size()) break;
				}
			}
			Collections.sort(itemList, new IngredientComparator());
			loadItemListview();
			ELog.toast("Sorted items alphabetically.");
			return true;
		case android.R.id.home:
			finish();
			return true;

		default:
			return super.onOptionsItemSelected(item); 
		}
	}
	
}
