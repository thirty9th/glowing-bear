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
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;

public class ItemDataManager
{
	// Member variables
	List<CraftingItem> itemList;
	ErrorLogManager ELog;
	
	// Constructor
	public ItemDataManager()
	{
		itemList = new ArrayList<CraftingItem>();
		this.ELog = new ErrorLogManager();
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
		ELog.setContext(context);
		
		// Move through the document and parse relevant info
		p.next();
		int eventType = p.getEventType();
		String attName, attValue, attQuantity, attProduced, attPosition, attItem;
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
					else if (p.getName().equalsIgnoreCase("grid") && eventType == XmlPullParser.START_TAG)	// Parse grid info
					{
						// Add the appropriate grid data to the current item named by attName
						CraftingItem currentItem = itemList.get(searchItem(attName));
						currentItem.grid = new CraftingGrid();
						
						// Go to first <box /> tag
						p.next(); eventType = p.getEventType();
						
						// Stop at </grid> end tag
						while (p.getName().equalsIgnoreCase("box"))
						{
							// See what type of tag it is and store it in the grid we just created
							// Currently at <box ... />
							attPosition = p.getAttributeValue(null, "pos");
							attItem = p.getAttributeValue(null, "item");
							if (attPosition != null && attItem != null)
							{
								if (attPosition.equalsIgnoreCase("ul")) currentItem.grid.ul = attItem;
								else if (attPosition.equalsIgnoreCase("uc")) currentItem.grid.uc = attItem;
								else if (attPosition.equalsIgnoreCase("ur")) currentItem.grid.ur = attItem;
								else if (attPosition.equalsIgnoreCase("l")) currentItem.grid.l = attItem;
								else if (attPosition.equalsIgnoreCase("c")) currentItem.grid.c = attItem;
								else if (attPosition.equalsIgnoreCase("r")) currentItem.grid.r = attItem;
								else if (attPosition.equalsIgnoreCase("ll")) currentItem.grid.ll = attItem;
								else if (attPosition.equalsIgnoreCase("lc")) currentItem.grid.lc = attItem;
								else if (attPosition.equalsIgnoreCase("lr")) currentItem.grid.lr = attItem;
							}

							// Move to next tag
							p.next(); eventType = p.getEventType();
						}
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
	
	// Used to translate an item's display name to its image file name
	// e.g. "Wood Planks" translates to "item_wood_planks"; used as R.id.item_wood_planks
	static public String getImageFilename(String inName)
	{
		String fileName = inName.replace(' ', '_').toLowerCase(Locale.ENGLISH);
		fileName = "item_" + fileName;
		fileName = fileName.replace("'", "");
		return fileName;
	}
	
};
