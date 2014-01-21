/**
 * 
 * FileManager.java - class definition for file manager
 * Description - This class condenses many common file loading and writing functions for the
 * app into one place. It can be instantiated anywhere to gain access.
 * 
 */

package com.thirtyninthsoftware.minebutler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

public class FileManager
{

	// File names
	public final String WORKSHEET_FILENAME = "worksheets";
	public final String WORKSHEET_DATA_DIRECTORY = "worksheet_data";
	
	// Globals
	Context context;
	ErrorLogManager ELog;
	
	// Constructor
	public FileManager(Context inContext)
	{
		context = inContext;
		ELog = new ErrorLogManager();
		ELog.setContext(context);
	}
	
	// Loads a pre-formatted list from a file
	// File list should have each list item on a separate line, with lines ended with standard
	// EOL characters
	public List<String> readLinesFromFile(String fileName, String targetDirectory)
	{
		// New, blank list to fill from file
		List<String> loadingList = new ArrayList<String>();
		BufferedReader br = null;
		
		// Load lines from file
		try
		{
			// Open directory
			ContextWrapper cw = new ContextWrapper(context);
			File directory = cw.getDir(targetDirectory, Context.MODE_PRIVATE);
			
			// Make sure it exists, exit otherwise
			if (!directory.exists()) return loadingList;
			
			// If it does, set up the desired file for reading
			File file =  new File(directory + File.separator + fileName);
			br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null)
			{
				loadingList.add(line);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.e(this.toString(), "Error reading from file.");
		}
		finally
		{
			try
			{
				br.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				Log.e(this.toString(), "Error closing buffered reader.");
			}
		}
		
		// Return filled list
		return loadingList;
	}
	
	// Performs first-time setup of a storage file, if needed
	// Returns true of the file was setup and false otherwise
	public boolean setupFile(String fileName, String targetDirectory)
	{
		// Check if the target file exists
		try
		{
			ContextWrapper cw = new ContextWrapper(context);
			File directory = cw.getDir(targetDirectory, Context.MODE_PRIVATE);
			if (!directory.exists())
			{
				// Create new directory
				directory.createNewFile();
				directory.mkdir();
				
				// Create new blank file
				File file = new File(directory + File.separator + fileName);
				file.createNewFile();
				return true;
			}
			else
			{
				// Check if the file already exists, DON'T over-write if it does
				File file = new File(directory + File.separator + fileName);
				if (!file.exists())
				{
					file.createNewFile();
					return true;
				}
				else return false;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.e(this.toString(), "Error while creating new file.");
			return false;
		}
	}
	
	// Stores worksheet names to file for persistence
	public void writeLinesToFile(List<String> linesToWrite, String fileName, String targetDirectory)
	{
		// End of line character (may differ by system)
		String endl = System.getProperty("line.separator");
		BufferedWriter bw = null;

		// Write desired lines to file
		try
		{
			// Open the proper directory, creating it if needed
			ContextWrapper cw = new ContextWrapper(context);
			File directory = cw.getDir(targetDirectory, Context.MODE_PRIVATE);
			if (!directory.exists())
			{
				directory.createNewFile();
				directory.mkdir();
			}
			
			// Now create the desired file
			File file = new File(directory + File.separator + fileName);
			file.createNewFile();
			
			// Open a writer and begin writing
			bw = new BufferedWriter(new FileWriter(file));
			for (String line : linesToWrite)
			{
				bw.write(line + endl);
			}
			
			// Flush and close
			bw.flush();
			bw.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.e(this.toString(), "Error while writing file.");
		}
	}
	
	// Returns a list of files in the specified directory
	public List<String> listFiles(String targetDir)
	{
		// List to return
		List<String> files = new ArrayList<String>();
		
		// Open the directory if it exists and grab the file names
		ContextWrapper cw = new ContextWrapper(context);
		File directory = cw.getDir(targetDir, Context.MODE_PRIVATE);
		File[] names = null;
		if (directory.exists()) names = directory.listFiles();
		else return files;	// No target directory, return blank list
		
		// Transfer to list
		if (names != null)
		{
			for (int i = 0; i < names.length; i++)
			{
				files.add(names[i].getName());
			}
		}
		
		// Return; might be blank
		return files;
	}
	
	// Deletes a specified file in the specified directory
	// Returns true if a file was deleted and false otherwise
	public boolean deleteFile(String fileName, String targetDir)
	{
		try
		{
			// Open target directory
			ContextWrapper cw = new ContextWrapper(context);
			File directory = cw.getDir(targetDir, Context.MODE_PRIVATE);
			
			// Check if the directory exists
			if (directory.exists())
			{
				File file = new File(directory + File.separator + fileName);
				if (file.exists() && !file.isDirectory())
				{
					file.delete();
					return true;
				}
				else return false;
			}
			else return false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.e(this.toString(), "Error while deleting file.");
			return false;
		}
	}
	
}
