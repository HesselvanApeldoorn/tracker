package com.example.test;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

public class Location extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		TextView text = (TextView)findViewById(R.id.textView1);
//		text.setText("SHIThhhhhTcdWOK");
		setContentView(R.layout.location);

	}
	public void changeWelcome(View view) {
		
		Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(nextScreen);

	}
}