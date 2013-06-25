/**
 * 
 * FileManager.java - class definition for file manager
 * Description - This class condenses many common file loading and writing functions for the
 * app into one place. It can be instantiated anywhere to gain access.
 * 
 */

package com.example.minecraftapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

public class FileManager
{

	// File names
	public static final String WORKSHEET_FILENAME = "worksheets";
	
	// Globals
	Context context;
	
	// Constructor
	public FileManager(Context inContext)
	{
		context = inContext;
	}
	
	// Loads a pre-formatted list from a file
	// File list should have each list item on a separate line, with lines ended with standard
	// EOL characters
	public List<String> loadFileList(String fileName)
	{
		// New, blank list to fill from file
		List<String> loadingList = new ArrayList<String>();
		
		// Load from file
		try
		{
			// Open file input and wrappers: ends up in buffered reader
			FileInputStream fis = context.openFileInput(WORKSHEET_FILENAME);
			DataInputStream dis = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(dis));
			
			// Read file by line
			String line;
			while ((line = br.readLine()) != null)
			{
				loadingList.add(line);
			}
			
			// Close reader
			dis.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			Log.e(this.toString(), "FileNotFoundException");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Log.e(this.toString(), "IOException");
		}
		
		// Return filled list
		return loadingList;
	}
	
	// Performs first-time setup of the worksheet storage file, if needed
	public void setupWorksheetFile()
	{
		// End of line character (may differ by system)
		String endl = System.getProperty("line.separator");
		BufferedWriter bw = null;
		FileInputStream fis = null;

		// Attempt to open the target file; if it doesn't exist, then create a blank instance of it
		// for future data storage
		try 
		{
			fis = context.openFileInput(WORKSHEET_FILENAME);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Log.e(this.toString(), "Could not find worksheet storage file... creating it.");

			// Create the file
			try
			{
				bw = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(WORKSHEET_FILENAME, Context.MODE_PRIVATE)));
				bw.write("Blank Worksheet" + endl);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				Log.e(this.toString(), "Couldn't use buffered writer.");
			}
			finally
			{
				if (bw != null)
				{
					try
					{
						bw.close();
					}
					catch (IOException exc)
					{
						e.printStackTrace();
						Log.e(this.toString(), "Couldn't close buffered writer.");
					}
				}
			}
		}
		finally
		{
			if (fis != null)
			{
				try 
				{
					fis.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
					Log.e(this.toString(), "Couldn't close file input stream.");
				}
			}
		}
	}
	
	// Stores worksheet names to file for persistence
	public void writeWorksheetFile(List<String> worksheetList)
	{
		// End of line character (may differ by system)
		String endl = System.getProperty("line.separator");
		BufferedWriter bw = null;

		// Attempt to write worksheet list to file
		try
		{
			bw = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(WORKSHEET_FILENAME, Context.MODE_PRIVATE)));
			int current = 0;
			while (current < worksheetList.size())
			{
				bw.write(worksheetList.get(current) + endl);
				current++;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Log.e(this.toString(), "IOException for buffered writer.");
		}
		finally
		{
			if (bw != null)
			{
				try
				{
					bw.close();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
					Log.e(this.toString(), "IOException while closing buffered writer.");
				}
			}
		}
	}
	
}
