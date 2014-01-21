/**
 * 
 * Recipe.java
 * Description - abstraction for a crafting recipe that stores pairs of items along with the required
 * quantity. Also stores how much this recipe produces.
 * 
 */

package com.thirtyninthsoftware.minebutler;

import java.util.ArrayList;
import java.util.List;

public class Recipe
{

	// Member variables
	public int produced;
	public List<Ingredient> ingredients;
	
	// Constructor
	public Recipe()
	{
		ingredients = new ArrayList<Ingredient>();
	}
	
	// Overloaded constructor with produced parameter set
	public Recipe(int inProduced)
	{
		this.produced = inProduced;
		ingredients = new ArrayList<Ingredient>();
	}
	
	// Add an item to the ingredients list
	public void addItem(String name, String quantity)
	{
		ingredients.add(new Ingredient(quantity, name));
	}
	
};
