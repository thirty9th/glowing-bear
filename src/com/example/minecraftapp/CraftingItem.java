/**
 * 
 * CraftingItem.java
 * Description - Abstraction for storage of item data with attributes
 * such as name, ingredients, quantity produced etc.
 * 
 */

package com.example.minecraftapp;

import java.util.ArrayList;
import java.util.List;

public class CraftingItem
{

	// Member variables
	public String name;
	public List<Recipe> recipes;
	
	// Constructor
	public CraftingItem(String inName)
	{
		this.name = inName;
		this.recipes = new ArrayList<Recipe>();
	};
	
};
