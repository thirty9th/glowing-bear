/**
 * 
 * ColorQueue.java
 * Description - This class uses a continuous queue of pre-selected colors as well as simple
 * mechanisms for advancing to the next color to differentiate items in a tiered list.
 * Implemented in ItemListAdapter.
 * 
 */

package com.example.minecraftapp;

import android.graphics.Color;


public class ColorQueue
{

	// Globals
	public static int currentIndex;
	public int[] colorList;
	
	// Constructor
	ColorQueue()
	{
		// Using 3 arbitrary colors; red, green and blue
		colorList = new int[] {Color.RED, Color.GREEN, Color.BLUE};
		currentIndex = 0;
	}
	
	// Advance to next color index
	public void next()
	{
		if (currentIndex >= 2)
		{
			currentIndex = 0;
			return;
		}
		else
		{
			currentIndex++;
			return;
		}
	}
	
	// Retrieve ID of current color
	public int currentColor()
	{
		return colorList[currentIndex];
	}
	
	// Returns to starting color
	public void reset()
	{
		currentIndex = 0;
	}
	
}
