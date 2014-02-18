package com.example.test;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends BaseMenu {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	
	public void openLocation(View view) {
		Intent location_screen = new Intent(getApplicationContext(), Location.class);
		startActivity(location_screen);
	}
	
	public void openStatistics(View view) {
		Intent statistics_screen = new Intent(getApplicationContext(), Statistics.class);
		startActivity(statistics_screen);
	}
	
	public void openMarkLocation(View view) {
		Intent markLocation_screen = new Intent(getApplicationContext(), MarkLocation.class);
		startActivity(markLocation_screen);
	}
}
