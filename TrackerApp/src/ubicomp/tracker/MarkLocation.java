package ubicomp.tracker;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import ubicomp.tracker.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class MarkLocation extends BaseMenu  implements OnMapLongClickListener {

	private GoogleMap googleMap;
	private ArrayList<Marker> markers = new ArrayList<Marker>();
	
	//Markers are saved as: marker.getTitle() + marker.getPosition().latitude + marker.getPosition().longitud + marker.getSnippet() +"\n"
	private final String fileName = "savedLocations"; //[title, lat, lng, snippet]

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mark_location);

		try {
			initilizeMap();	// Loading map
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		double latitude = 53.2191700;
		double longitude = 6.5666700;

		if (this.googleMap == null) {
			this.googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			// create specific Location
			CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(
					latitude, longitude));
			// Specify presentation zoom
			CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
			// set both camera and zoom to the above values
			this.googleMap.moveCamera(center);
			this.googleMap.animateCamera(zoom);
			this.googleMap.setMyLocationEnabled(true); //shows current location button
			// create and place marker at the position created above
			
			// create and place marker at the position created above
			Marker marker = this.googleMap // TODO why doens't zooming etc work if removing this
					.addMarker(new MarkerOptions().position(
							new LatLng(latitude, longitude)).title(
							"Groningen city"));
			this.markers.add(marker);

			
			// check if map is created successfully or not
			if (this.googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! Unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
		
		this.googleMap.setOnMapLongClickListener(this);
		
		this.loadMarkers();
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
		
	/**
	 * inputText(title) will show an alert dialog where the user can input text
	 * @param location
	 */
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
	
	/**
	 *  Add marker with user defined title
	 * @param locationName
	 * @param location
	 */
	private void addMarker(String locationName, LatLng location) {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(new LatLng(location.latitude, location.longitude));
		markerOptions.title(locationName);
		DecimalFormat df = new DecimalFormat("#.###");
		//XXX Spaces in snippet cannot be removed, because of the way we are saving/loading for now
		markerOptions.snippet("Latitude:" + df.format(location.latitude) + ",Longitude:" + df.format(location.longitude));
		Marker marker = this.googleMap.addMarker(markerOptions);
		this.markers.add(marker);
		
		saveMarker(marker);
	}
	
	
	/**
	 * Saves a marker to file
	 * @param marker
	 */
	private void saveMarker(Marker marker) {
	    FileOutputStream fos;
		try {
			fos = openFileOutput(this.fileName, Context.MODE_APPEND);
	        OutputStreamWriter osw = new OutputStreamWriter(fos);
			String output = marker.getTitle() + " " + marker.getPosition().latitude + " " + marker.getPosition().longitude + " " + marker.getSnippet() + "\n";
		    osw.write(output);
		    osw.flush();
		    osw.close();
		    fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads markers from file
	 */
	private void loadMarkers() {
		FileInputStream fis;
		try {
			fis = openFileInput(this.fileName);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		    String line = null;
	    	MarkerOptions previousMarkerOptions = null;
	    	MarkerOptions markerOptions = null;
		    while ((line = reader.readLine()) != null) {
		    	markerOptions = readOneMarker(line);
		    	if (previousMarkerOptions == null) previousMarkerOptions = markerOptions;
		    	Marker marker = this.googleMap.addMarker(markerOptions);
		        this.markers.add(marker);
		        PolylineOptions rectOptions = new PolylineOptions()
		        .add(new LatLng(markerOptions.getPosition().latitude, markerOptions.getPosition().longitude))
		        .add(new LatLng(previousMarkerOptions.getPosition().latitude, previousMarkerOptions.getPosition().longitude));
		        Polyline polyline = this.googleMap.addPolyline(rectOptions);
		        previousMarkerOptions = markerOptions;
		    }
		    reader.close();
		    fis.close();
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Creates MarkerOptions from one line
	 * @param line comprises: title, lat, lng, snippet
	 * @return
	 */
	private MarkerOptions readOneMarker(String line) {
		MarkerOptions markerOptions = new MarkerOptions();
		String[] tokens = line.split(" ");
    	if(tokens.length!=4){throw new IllegalArgumentException();} // 4 values in total
    	String title = tokens[0];
    	title = "grote markten";
    	Double latitude = Double.parseDouble(tokens[1]);
    	Double longitude = Double.parseDouble(tokens[2]);
    	String snippet = tokens[3];
    	
    	markerOptions.title(title);
    	markerOptions.position(new LatLng(latitude, longitude));
    	markerOptions.snippet(snippet);
    	
		return markerOptions;
	}

	/**
	 * Fits all the markers in screen
	 * @param view
	 */
	public void onZoomToFit(View view) {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (Marker marker : this.markers) { //Calculate bounds of all markers
		    builder.include(marker.getPosition());
		}
		LatLngBounds bounds = builder.build();
		
		int padding = 30; // offset from edges of the map (pixels)
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
		
		this.googleMap.animateCamera(cu); //Use moveCamera if animation is not the way to go
	}
}

