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
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
		final View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_item, (ViewGroup)findViewById(R.id.dialog_add_item_root));
		b.setView(v);
		b.setTitle(R.string.dialog_add_item_title);
		
		// Set up auto-complete
		// TODO: Make sure the item names list is initialized before passing it to the
		// adapter and build it if it's not
		final AutoCompleteTextView itemName = (AutoCompleteTextView)v.findViewById(R.id.text_item_name);
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
	
	// Called when the user long-clicks an item for deletion
	public void onDeleteItem(final int pos)
	{
		// Set up and show a new alert dialog
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		View v = LayoutInflater.from(this).inflate(R.layout.dialog_delete_item, (ViewGroup)findViewById(R.id.dialog_delete_item_root));
		b.setView(v);
		b.setTitle(R.string.confirm_deletion);

		// Set up the right (okay/positive) button
		b.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				// Delete the item and its open children from the list and re-load the listview
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
		// Save position in the list
		Parcelable state = itemListview.onSaveInstanceState();
		
		// Populate the list, mapping the name of the item to its proper view in the
		// individual item layout (keeping hashmap -> simple adapter layout for extensibility)
		ItemListAdapter adapter = new ItemListAdapter(this, R.layout.listview_item_items, itemList);
		itemListview.setAdapter(adapter);
		
		// Scroll back to saved position
		itemListview.onRestoreInstanceState(state);
		
		// Make each item in the populated list clickable
		itemListview.setOnItemClickListener(new OnItemClickListener()
		{
			  public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id)
			  {
				  // Toggle visibility of the selected item's children
				  if (pos < itemList.size() - 1)						// Not last item in the list
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
				// Create a dialog asking the user if they want to delete the selected item
				// Note: The user should only be able to delete base items they added (level 0 items)
				//if (itemList.get(pos).level == 0) onDeleteItem(pos);
				openItemSubmenu(pos);
				
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
	
	// Opens a small dialog prompting the user to either delete the selected item or open its recipe
	// sheet
	private void openItemSubmenu(final int pos)
	{
		// Set up a new dialog
		View v = LayoutInflater.from(this).inflate(R.layout.dialog_item_submenu, (ViewGroup)findViewById(R.id.dialog_item_submenu_root));
		TextView textDeleteItem = (TextView)v.findViewById(R.id.text_delete_item);
		TextView textShowRecipe = (TextView)v.findViewById(R.id.text_show_recipe);
		final AlertDialog dialog = new AlertDialog.Builder(this).setView(v).show();
		
		// If the item is not a base item, don't display the option for deletion
		if (itemList.get(pos).level != 0)
		{
			textDeleteItem.setVisibility(View.GONE);
			LinearLayout divider = (LinearLayout)v.findViewById(R.id.dialog_items_submenu_divider);
			divider.setVisibility(View.GONE);
		}
		
		// Set click listener on delete item option
		if (itemList.get(pos).level == 0)
		{
			textDeleteItem.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View view)
				{
					dialog.dismiss();	// Dismiss the small submenu dialog
					onDeleteItem(pos);	// Show the confirmation to delete the item
				}
			});
		}
		
		// Set click listener on show recipe option
		textShowRecipe.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				dialog.dismiss();	// Dismiss the small submenu dialog
				onShowRecipe(pos);	// Show crafting grid dialog
			}
		});
	}
	
	// Shows the target recipe in the 3 x 3 crafting grid format with images
	private void onShowRecipe(final int pos)
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
		final AlertDialog dialog = b.setView(v).show();
	}
	
}
