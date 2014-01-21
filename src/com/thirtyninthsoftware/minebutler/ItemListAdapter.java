/**
 * 
 * ItemListAdapter.java
 * Description - This class provides a custom adapter for the item lists displayed in
 * worksheets.
 * 
 */

package com.thirtyninthsoftware.minebutler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.thirtyninthsoftware.minebutler.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ItemListAdapter extends ArrayAdapter<Ingredient>
{

	// Globals
	Context context;
	int id;
	List<Ingredient> data = new ArrayList<Ingredient>();
	ErrorLogManager ELog;
	List<Boolean> openTabStatus;
	List<Boolean> bigDivider;
	List<Boolean> hasRecipe;
	List<Integer> indentation;
	ItemDataManager itemManager;
	HashMap<Integer, Boolean> selected;
	
	// Constructor
	@SuppressLint("UseSparseArrays")
	public ItemListAdapter(Context inContext, int inId, List<Ingredient> inData, ItemDataManager inItemManager)
	{
		super(inContext, inId, inData);
		this.id = inId;
		this.context = inContext;
		this.data = inData;
		ELog = new ErrorLogManager();
		ELog.setContext(context);
		
		// Initialize the boolean array for setting the correct images and dividers
		openTabStatus = new ArrayList<Boolean>();
		bigDivider = new ArrayList<Boolean>();
		indentation = new ArrayList<Integer>();
		hasRecipe = new ArrayList<Boolean>();
		for (int i = 0; i < inData.size(); i++)
		{
			openTabStatus.add(false);
			bigDivider.add(false);
			indentation.add(0);
			hasRecipe.add(false);
		}
		
		// Set up hash map of currently selected items
		selected = new HashMap<Integer, Boolean>();
		
		// Set item data manager
		this.itemManager = inItemManager;
	}
	
	// Implement the getView method from ArrayAdapter
	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		IngredientHolder holder = null;
		
		// Create holder if needed
		if (row == null)
		{
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(id, parent, false);
			
			holder = new IngredientHolder();
			holder.icon = (ImageView)row.findViewById(R.id.image_tab);
			holder.item_icon = (ImageView)row.findViewById(R.id.image_item_icon);
			holder.name = (TextView)row.findViewById(R.id.text_listview_item_name);
			holder.quantity = (TextView)row.findViewById(R.id.text_listview_item_quantity);
			holder.divider = (LinearLayout)row.findViewById(R.id.listview_item_divider);
			holder.background = (RelativeLayout)row.findViewById(R.id.listview_item_items_container);
			holder.showRecipeButton = (ImageView)row.findViewById(R.id.button_show_recipe);
			holder.tabBackground = (LinearLayout)row.findViewById(R.id.layout_tab_container);
			
			row.setTag(holder);
		}
		else
		{
			holder = (IngredientHolder)row.getTag();
		}
		
		// Set up item name and quantity text fields
		Ingredient ingredient = data.get(position);
		holder.name.setText(ingredient.name);
		holder.quantity.setText(ingredient.quantity);
		
		// Set item icon
		String imageName = ItemDataManager.getImageFilename(data.get(position).name);
		holder.item_icon.setImageResource(context.getResources().getIdentifier(imageName, "drawable", context.getPackageName()));
		
		// Set proper image for the item; if it has an open child, its icon should
		// be open. If not, its icon should be closed
		if (position < data.size() - 1)	// If item is not the last item
		{
			Ingredient next = data.get(position + 1);
			if (ingredient.level < next.level)
			{
				openTabStatus.set(position, true);
			}
		}
		else openTabStatus.set(position, false);
		
		// Now set image according to the boolean array (avoids view recycling pitfall)
		String itemName = holder.name.getText().toString();
		int index = itemManager.searchItem(itemName);
		if (index != -1) 
		{
			CraftingItem targetItem = itemManager.itemList.get(index);
			Recipe targetRecipe = targetItem.recipes.get(0);
			List<Ingredient> targetIngredients = targetRecipe.ingredients;
			String targetName = null;
			Ingredient targetIngredient;
			if (targetIngredients.size() > 0)
			{
				targetIngredient = targetIngredients.get(0);
				targetName = targetIngredient.name;
			}
			if (targetName != null && targetName.equalsIgnoreCase("base_item"))
			{
				holder.icon.setImageResource(R.drawable.icon_list_no_child);
				hasRecipe.set(position, false);
			}
			else
			{
				if (openTabStatus.get(position)) holder.icon.setImageResource(R.drawable.icon_list_open);
				else holder.icon.setImageResource(R.drawable.icon_list_closed);
				hasRecipe.set(position, true);
			}
		}
		
		// Either hide the item's show recipe button or give it a click listener
		if (hasRecipe.get(position))
		{
			// Re-show the button in case it was hidden earlier
			holder.showRecipeButton.setVisibility(View.VISIBLE);
			
			// Has a recipe, show it from the worksheet activity in its dialog
			holder.showRecipeButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					((WorksheetActivity)context).onShowRecipe(position);
				}
			});
			
			// Also set the click listener to show child items
			holder.tabBackground.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// Toggle visibility of the selected item's children
					if (position < data.size() - 1)						// Not last item in the list
					{
						int currentLevel = data.get(position).level;
						if (data.get(position + 1).level > currentLevel)	// Item directly underneath is of lower precendence, hide children
						{
							while (data.get(position + 1).level > currentLevel)
							{
								data.remove(position + 1);
								if (position == data.size() - 1)
								{
									((WorksheetActivity)context).loadItemListview();
									return;
								}
							}
							((WorksheetActivity)context).loadItemListview();
						}
						else
						{
							((WorksheetActivity)context).showChildren(position);		// Item directly underneath is of equal or greater precedence, show children
						}
					}
					else								// Last item in the list
					{
						((WorksheetActivity)context).showChildren(position);
					}
				}
			});
		}
		else
		{
			holder.showRecipeButton.setVisibility(View.INVISIBLE);	// Otherwise, hide it
		}
		
		// Set the thick divider to separate distinct items and their children
		if (position < data.size() - 1)
		{
			if (data.get(position + 1).level == 0)	// If the next item is a parent item
			{
				bigDivider.set(position, true);
			}
			else bigDivider.set(position, false);
		}
		else bigDivider.set(position, false);
		
		// Now set the thick divider according to the boolean array
		if (bigDivider.get(position))
		{
			ViewGroup.LayoutParams params = holder.divider.getLayoutParams();
			params.height = 15; 				// In device pixels
			holder.divider.requestLayout();
		}
		else
		{
			ViewGroup.LayoutParams params = holder.divider.getLayoutParams();
			params.height = 2;
			holder.divider.requestLayout();
		}
		
		// Set indentation of the item's view according to its child status
		if (ingredient.level > 0)
		{
			indentation.set(position, (ingredient.level * 15));
		}
		holder.background.setPadding(indentation.get(position), 0, 0, 0);
		
		// Set background color if the current item is selected
		if (selected.get(position) != null)
		{
			holder.background.setBackgroundResource(R.color.listview_item_highlight);
		}
		else holder.background.setBackgroundResource(R.color.dark_grey);

		return row;
	}
	
	// Used to adapt ingredient abstraction to actual layout
	static class IngredientHolder
	{
		ImageView icon;
		ImageView item_icon;
		TextView name;
		TextView quantity;
		LinearLayout divider;
		RelativeLayout background;
		ImageView showRecipeButton;
		LinearLayout tabBackground;
	}
	
	// Used to add a newly checked item to the adapter
	public void setNewSelection(int pos, boolean checked)
	{
		selected.put(pos, checked);
		notifyDataSetChanged();
	}
	
	// Checks if a specific item is checked
	public boolean isItemChecked(int pos)
	{
		Boolean result = selected.get(pos);
		return result == null ? false : result;
	}
	
	// Returns a list of what items are currently checked
	public Set<Integer> getCheckedItems()
	{
		return selected.keySet();
	}
	
	// Deletes a specific item
	public void removeSelection(int pos)
	{
		selected.remove(pos);
		notifyDataSetChanged();
	}
	
	// Clears all selected items
	@SuppressLint("UseSparseArrays")
	public void clearSelection()
	{
		selected = new HashMap<Integer, Boolean>();
		notifyDataSetChanged();
	}
	
}
