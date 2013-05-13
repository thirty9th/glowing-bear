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
import java.util.HashMap;
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
	private void addItemRecipe(String itemName, List<HashMap<String, String>> ingredientList, String quantity)
	{
		// Get proper index
		int insertIndex = searchItem(itemName);
		
		// Set the item's number of items produced via the above recipe
		// Note: we are settings the first value of the list of ingredients equal to this produced
		// number and pushing all other elements one forwards
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("produced", quantity);
		ingredientList.(hm);
		
		// Add the list of ingredients at that index as a new list of hashmaps
		itemList.get(insertIndex).recipes.add((ArrayList<HashMap<String, String>>) ingredientList);
	}
	
	// Searches the list of crafting items for the target item, returning its index
	private int searchItem(String itemName)
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
		String attValue, attQuantity, attProduced;
		List<HashMap<String, String>> ingredientList;
		while (eventType != XmlPullParser.END_DOCUMENT)
		{
			// Found an open item tag
			if (eventType == XmlPullParser.START_TAG && p.getName().equalsIgnoreCase("item"))
			{
				attValue = p.getAttributeValue(null, "name"); 	// Item name
				itemList.add(new CraftingItem(attValue));		// Make a new item in the list
				Collections.sort(itemList, new CraftingItemComparator());	// Sort itemList alphabetically
				
				// Move into item recipe list
				p.next(); eventType = p.getEventType();			// <recipe>
				p.next(); eventType = p.getEventType();			// <ingredient ... />
				
				// Loop through all recipes
				while (!p.getName().equalsIgnoreCase("item"))
				{
					// Get ingredient attributes, store them
					ingredientList = new ArrayList<HashMap<String, String>>();
					while (p.getName().equalsIgnoreCase("ingredient"))
					{
						attValue = p.getAttributeValue(null, "name");		// Get name of ingredient
						attQuantity = p.getAttributeValue(null, "quantity");// How much of ingredient
						HashMap<String, String> hm = new HashMap<String, String>();
						hm.put(attValue, attQuantity);
						ingredientList.add(hm);								// Add this ingredient to list
						
						// Advance to next ingredient tag
						p.next(); eventType = p.getEventType();
					}
					
					// Get how many items this recipe produces (e.g. 2 wood produces 4 sticks)
					// <produced ... />
					attProduced = p.getAttributeValue(null, "quantity");
					
					// Add this recipe to this item's recipe list
					addItemRecipe(attValue, ingredientList, attProduced);
					
					// Move to next tag (either </recipe> or </item>)
					p.next(); eventType = p.getEventType();
				}
			}

			// Move to next tag
			// Note: We are ignoring closing tags (e.g. </item>), comments etc.
			p.next(); eventType = p.getEventType();
		}
	};
	
};
