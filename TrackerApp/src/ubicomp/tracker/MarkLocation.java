package ubicomp.tracker;

import java.text.DecimalFormat;

import ubicomp.tracker.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


public class MarkLocation extends BaseMenu  implements OnMapLongClickListener {

	private GoogleMap googleMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mark_location);

		try {
			// Loading map
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		double latitude = 53.2191700;
		double longtitude = 6.5666700;

		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			// create specific Location
			CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(
					latitude, longtitude));
			// Specify presentation zoom
			CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
			// set both camera and zoom to the above values
			googleMap.moveCamera(center);
			googleMap.animateCamera(zoom);

			// create and place marker at the position created above
			googleMap
					.addMarker(new MarkerOptions().position(
							new LatLng(latitude, longtitude)).title(
							"Hello Groningen!"));

			// enables the Set my Location Button
			googleMap.getUiSettings().setMyLocationButtonEnabled(true);
			
			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
		
		this.googleMap.setOnMapLongClickListener(this);
	}


	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
	}

	@Override
	public void onMapLongClick(LatLng location) {
		this.inputLocationName(location); //Open an input dialog for location name and add marker
	}
		
	//inputText(title) will show an alert dialog where the user can input text
	private void inputLocationName(final LatLng location) {
		Builder alertDialog = new Builder(this);
		alertDialog.setTitle("Name of location: ");

		final EditText input = new EditText(this); // Set up the input
		input.setInputType(InputType.TYPE_CLASS_TEXT); // Specify the type of input expected
		alertDialog.setView(input);

		// Set up the buttons
		alertDialog.setPositiveButton("Add location", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	addMarker(input.getText().toString(), location); //Add marker
		    }
		});
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	dialog.cancel(); //cancel adding marker
		    }
		});

		alertDialog.show();
	}
	
	// Add marker with user defined title
	private void addMarker(String locationName, LatLng location) {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(new LatLng(location.latitude, location.longitude));
		markerOptions.title(locationName);
		DecimalFormat df = new DecimalFormat("#.###");
		markerOptions.snippet("Latitude: " + df.format(location.latitude) + ", Longitude: " + df.format(location.longitude));
		this.googleMap.addMarker(markerOptions);
	}


}

