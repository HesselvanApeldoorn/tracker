package com.example.test;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	public void changeWelcome(View view) {
//		TextView text = (TextView)findViewById(R.id.textView1);
//		text.setText("Clck fo next screen");
		
		Intent nextScreen = new Intent(getApplicationContext(), Location.class);
		startActivity(nextScreen);

	}
	

}
