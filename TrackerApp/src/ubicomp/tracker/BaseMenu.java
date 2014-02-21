package ubicomp.tracker;

import java.io.File;

import ubicomp.tracker.R;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

// Base class for the main menu
public class BaseMenu  extends FragmentActivity {
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    return true;
	}

	//Handles item actions from main_menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	  switch (item.getItemId()) {
	  case R.id.action_settings:
		  Toast.makeText(getApplicationContext(), "Settings item tapped", Toast.LENGTH_SHORT).show();
	      return true;
	  case R.id.action_resetLocations: //TODO refactor filename
		  deleteFile("savedLocations");
		  return true;
	  case R.id.action_quit:
		  Intent i = new Intent(Intent.ACTION_MAIN);
		  i.addCategory(Intent.CATEGORY_HOME);
		  startActivity(i);
		  return true;

	  }

	  return false;
	}
}