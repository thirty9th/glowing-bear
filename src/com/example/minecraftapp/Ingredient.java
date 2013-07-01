/**
 * 
 * Ingredient.java
 * Description - Abstraction for a crafting ingredient that includes the number required and the name
 * of the ingredient
 * 
 */

package com.example.minecraftapp;

public class Ingredient
{

	// Member variables
	public String quantity;
	public String name;
	public int level;
	
	// Constructor
	Ingredient(String inQuantity, String inName)
	{
		this.quantity = inQuantity;
		this.name = inName;
		this.level = 0;
	}
	
	// Overloaded constructor for setting precedence level in worksheet list
	Ingredient(String inQuantity, String inName, int inLevel)
	{
		this.quantity = inQuantity;
		this.name = inName;
		this.level = inLevel;
	}
	
}
