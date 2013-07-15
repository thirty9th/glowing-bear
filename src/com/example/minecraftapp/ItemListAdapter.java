/**
 * 
 * ItemListAdapter.java
 * Description - This class provides a custom adapter for the item lists displayed in
 * worksheets.
 * 
 */

package com.example.minecraftapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
	List<Integer> indentation;
	
	// Constructor
	public ItemListAdapter(Context inContext, int inId, List<Ingredient> inData)
	{
		super(inContext, inId, inData);
		this.id = inId;
		this.context = inContext;
		this.data = inData;
		ELog = new ErrorLogManager();
		ELog.setContext(context);
		
		// Initialize the boolean array for setting the correct images and dividers
		openTabStatus = new ArrayList<Boolean>();
		for (int i = 0; i < inData.size(); i++) openTabStatus.add(false);
		bigDivider = new ArrayList<Boolean>();
		for (int i = 0; i < inData.size(); i++) bigDivider.add(false);
		indentation = new ArrayList<Integer>();
		for (int i = 0; i < inData.size(); i++) indentation.add(20);
	}
	
	// Implement the getView method from ArrayAdapter
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
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
			holder.name = (TextView)row.findViewById(R.id.text_listview_item_name);
			holder.quantity = (TextView)row.findViewById(R.id.text_listview_item_quantity);
			holder.divider = (LinearLayout)row.findViewById(R.id.listview_item_divider);
			
			row.setTag(holder);
		}
		else
		{
			holder = (IngredientHolder)row.getTag();
		}
		
		// Set up icon, item name and quantity text fields
		Ingredient ingredient = data.get(position);
		holder.name.setText(ingredient.name);
		holder.quantity.setText(ingredient.quantity);
		
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
		if (openTabStatus.get(position)) holder.icon.setImageResource(R.drawable.icon_list_open);
		else holder.icon.setImageResource(R.drawable.icon_list_closed);
		
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
			indentation.set(position, ((ingredient.level + 1) * 35));
		}
		View root = row.findViewById(R.id.listview_item_items_container);
		root.setPadding(indentation.get(position), 0, 0, 0);

		return row;
	}
	
	// Used to adapt ingredient abstraction to actual layout
	static class IngredientHolder
	{
		ImageView icon;
		TextView name;
		TextView quantity;
		LinearLayout divider;
	}
	
}
