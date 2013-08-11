/**
 * 
 * IngredientComparator.java
 * Description - custom comparator definition for sorting individual ingredients by name
 * 
 */

package com.example.minecraftapp;

import java.util.Comparator;

public class IngredientComparator implements Comparator<Ingredient>
{

	@Override
	public int compare(Ingredient anItem, Ingredient otherItem)
	{
		return anItem.name.compareTo(otherItem.name);
	};
	
};
