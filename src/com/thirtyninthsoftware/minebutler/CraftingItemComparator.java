/**
 * 
 * CraftingItemComparator.java
 * Description - custom comparator definition for sorting crafting items by name
 * 
 */

package com.thirtyninthsoftware.minebutler;

import java.util.Comparator;

public class CraftingItemComparator implements Comparator<CraftingItem>
{

	@Override
	public int compare(CraftingItem anItem, CraftingItem otherItem)
	{
		return anItem.name.compareTo(otherItem.name);
	};
	
};
