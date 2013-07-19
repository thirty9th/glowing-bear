/**
 * 
 * CraftingGrid.java
 * Description - This class provides an abstraction for the crafting grid and its
 * 9 images, representing crafting items.
 * 
 */

package com.example.minecraftapp;

public class CraftingGrid
{

	// Member variables
	public String ul, uc, ur, l, c, r, ll, lc, lr;
	
	// Constructor
	public CraftingGrid()
	{
		// Start with no ingredient in each square, fill in squares that have ingredients
		ul = new String();
		uc = new String();
		ur = new String();
		l = new String();
		c = new String();
		r = new String();
		ll = new String();
		lc = new String();
		lr = new String();
	}
	
}
