/**
 * 
 * ErrorLogManager.java - stores, displays error messages
 * Description - This class simplifies displaying error messages through toasts, the error
 * log and various other means with streamlined functions.
 * 
 */

package com.thirtyninthsoftware.minebutler;

import android.content.Context;
import android.widget.Toast;

public class ErrorLogManager
{

	// Globals
	Context context;
	
	// Used for display of simple key-value pair toasts
	public void toast(Context thisContext, String key, String value)
	{
		Toast.makeText(thisContext, key + ": " + value, Toast.LENGTH_SHORT).show();
	}
	
	// Used for display of simple key-value pair toasts
	// Overloaded to use base context
	public void toast(String key, Object value)
	{
		Toast.makeText(context, key + ": " + value.toString(), Toast.LENGTH_SHORT).show();
	}
	
	// Used for display of any toString-able object
	public void toast(String text)
	{
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	
	// Sets the defualt context for toasts
	public void setContext(Context newContext)
	{
		this.context = newContext;
	}
	
}
