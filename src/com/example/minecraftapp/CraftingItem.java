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
	public String name;							// In-game name of item
	public List<Recipe> recipes;				// List of crafting recipes for this item
	public CraftingGrid grid;					// 9 item images to display on pop-up
	
	// Constructor
	public CraftingItem(String inName)
	{
		this.name = inName;
		this.recipes = new ArrayList<Recipe>();
		this.grid = new CraftingGrid();
	};
	
};
