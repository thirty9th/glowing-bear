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
	
	// Constructor
	Ingredient(String inQuantity, String inName)
	{
		this.quantity = inQuantity;
		this.name = inName;
	}
	
}
