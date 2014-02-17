package com.example.test;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;

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
}
