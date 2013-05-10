/**
 * 
 * MainActivity.java - activity definition for main menu
 * Description - shows the user their options for knowledge browsing in the form of buttons
 * 
 */

package com.example.minecraftapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity
{
	
	// Views to be instantiated
	ImageButton buttonBlockGuide;
	ImageButton buttonCreatureGlossary;
	ImageButton buttonGeneralKnowledge;
	ImageButton buttonMaterialsCalculator;

	// Called when the activity receives a create intent
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Set layout
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Instantiate views
		buttonBlockGuide = (ImageButton)findViewById(R.id.button_block_guide);
		buttonCreatureGlossary = (ImageButton)findViewById(R.id.button_creature_glossary);
		buttonGeneralKnowledge = (ImageButton)findViewById(R.id.button_general_knowledge);
		buttonMaterialsCalculator = (ImageButton)findViewById(R.id.button_materials_calculator);
	}

	// Called when the user hits the menu softkey
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// Called when user taps block guide button
	public void onClickButtonBlockGuide(View view)
	{
		Toast.makeText(getBaseContext(), "Clicked Block Guide", Toast.LENGTH_SHORT).show();
	}
	
	// Called when user taps creature glossary button
	public void onClickButtonCreatureGlossary(View view)
	{
		Toast.makeText(getBaseContext(), "Clicked Creature Glossary", Toast.LENGTH_SHORT).show();
	}

	// Called when user taps general knowledge button
	public void onClickButtonGeneralKnowledge(View view)
	{
		Toast.makeText(getBaseContext(), "Clicked General Knowledge", Toast.LENGTH_SHORT).show();
	}

	// Called when user taps materials calculator button
	public void onClickButtonMaterialsCalculator(View view)
	{
		Toast.makeText(getBaseContext(), "Clicked Materials Calculator", Toast.LENGTH_SHORT).show();
	}

};
