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
import android.widget.TextView;

public class ItemListAdapter extends ArrayAdapter<Ingredient>
{

	// Globals
	Context context;
	int id;
	List<Ingredient> data = new ArrayList<Ingredient>();
	ColorQueue colorQ;
	
	
	// Constructor
	public ItemListAdapter(Context inContext, int inId, List<Ingredient> inData)
	{
		super(inContext, inId, inData);
		this.id = inId;
		this.context = inContext;
		this.data = inData;
		colorQ = new ColorQueue();
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
				holder.icon.setImageResource(R.drawable.icon_list_open);
			}
		}
		else holder.icon.setImageResource(R.drawable.icon_list_closed);
		
		// Set indentation of the item's view according to its child status
		View root = row.findViewById(R.id.listview_item_items_container);
		root.setPadding((40 * ingredient.level), 0, 0, 0);
		
		// Set proper color of the color tag; advance to next color if this is a level 0 item
		if (ingredient.level == 0) colorQ.next();
		if (position == 0) colorQ.reset();
		View colorTag = row.findViewById(R.id.listview_item_color_tag);
		colorTag.setBackgroundColor(colorQ.currentColor());
		
		return row;
	}
	
	// Used to adapt ingredient abstraction to actual layout
	static class IngredientHolder
	{
		ImageView icon;
		TextView name;
		TextView quantity;
	}
	
}
