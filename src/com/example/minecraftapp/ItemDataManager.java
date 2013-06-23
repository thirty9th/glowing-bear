/**
 * 
 * ItemDataManager.java
 * Description - class that handles retrieval of item data from XML file and storage/access to that
 * data
 * 
 */

package com.example.minecraftapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;

public class ItemDataManager
{
	// Member variables
	List<CraftingItem> itemList;
	
	// Constructor
	public ItemDataManager()
	{
		itemList = new ArrayList<CraftingItem>();
	}
	
	// Adds a new list of ingredients to the target item; also defines how many of this item
	// will be produced by each recipe
	private void addItemRecipe(String itemName, Recipe ingredientList, Context context)
	{
		// Get proper index
		int insertIndex = searchItem(itemName);
		
		// Add the new recipe to the list of recipes for the target item
		itemList.get(insertIndex).recipes.add(ingredientList);		
	}
	
	// Searches the list of crafting items for the target item, returning its index
	public int searchItem(String itemName)
	{
		String thisName;
		for (int i = 0; i < itemList.size(); i++)
		{
			thisName = itemList.get(i).name;
			if (thisName.equalsIgnoreCase(itemName))
			{
				return i;
			}
		}
		
		return -1;
	}

	// Loads item data, recipes etc. from res/xml/raw/item_data.xml
	public void loadItemsFromXml(Context context) throws XmlPullParserException, IOException
	{
		// Instantiate a new resource parser to read data from file
		XmlResourceParser p = context.getResources().getXml(R.xml.item_data);
		
		// Move through the document and parse relevant info
		p.next();
		int eventType = p.getEventType();
		String attName, attValue, attQuantity, attProduced;
		Recipe ingredientList;
		while (eventType != XmlPullParser.END_DOCUMENT)
		{
			// Found an open item tag
			if (eventType == XmlPullParser.START_TAG && p.getName().equalsIgnoreCase("item"))
			{
				attName = p.getAttributeValue(null, "name"); 	// Item name
				itemList.add(new CraftingItem(attName));		// Make a new item in the list
				Collections.sort(itemList, new CraftingItemComparator());	// Sort itemList alphabetically
				
				// Move into item recipe list
				p.next(); eventType = p.getEventType();			// <recipe>
				
				// Loop through all recipes
				while (!p.getName().equalsIgnoreCase("item"))
				{
					// Find the next open recipe tag (<recipe>, not </recipe>)
					if (p.getName().equalsIgnoreCase("recipe") && eventType == XmlPullParser.START_TAG)
					{
						// Get ingredient attributes, store them
						ingredientList = new Recipe();
						attProduced = p.getAttributeValue(null, "produced");		// Get quantity produced
						ingredientList.produced = Integer.parseInt(attProduced);	// Set the new recipe's produced value
						p.next(); eventType = p.getEventType(); 					// Go to first <ingredient />
						while (p.getName().equalsIgnoreCase("ingredient"))			// Stop at </recipe>
						{
							// Should be seeing <ingredient /> tags here
							attValue = p.getAttributeValue(null, "name");
							attQuantity = p.getAttributeValue(null, "quantity");
							if (attValue != null && attQuantity != null) ingredientList.addItem(attValue, attQuantity);
							
							// Move to next tag
							p.next(); eventType = p.getEventType();
						}
						
						// Add this newly assembled recipe to the current item's list of recipes
						addItemRecipe(attName, ingredientList, context);
					}
					
					// Move to next tag
					p.next(); eventType = p.getEventType();
				}
			}

			// Move to next tag
			// Note: We are ignoring closing tags, comments etc.
			p.next(); eventType = p.getEventType();
		}
	};
	
};
