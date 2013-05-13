/**
 * 
 * CraftingItem.java
 * Description - Abstraction for storage of item data with attributes
 * such as name, ingredients, quantity produced etc.
 * 
 */

package com.example.minecraftapp;

import java.util.ArrayList;
import java.util.HashMap;

public class CraftingItem
{

	// Member variables
	protected String name;
	protected ArrayList<ArrayList<HashMap<String, String>>> recipes;
	
	// Constructor
	public CraftingItem(String inName)
	{
		this.name = inName;
	};
	
};
