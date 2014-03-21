package ubicomp.tracker;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ubicomp.tracker.R;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class MarkLocation extends BaseMenu  implements OnMapLongClickListener {

	private GoogleMap googleMap;

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
//			Marker marker = this.googleMap // TODO why doens't zooming etc work if removing this
//					.addMarker(new MarkerOptions().position(
//							new LatLng(latitude, longitude)).title(
//							"Groningen city"));
//			MarkLocation.markers.add(marker);

			
			// check if map is created successfully or not
			if (this.googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! Unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
		
		this.googleMap.setOnMapLongClickListener(this);
		
		this.loadMarkers();
		this.loadRoutes();
	}


	private void loadRoutes() {
		FileInputStream fis;
		LatLng location = null;
		LatLng previousLocation = null;
		
		
		try {
			fis = openFileInput(MainActivity.savedRoutes);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		    String line = null;
		    Date firstDate = new Date();
	    	SimpleDateFormat dateFormat = new SimpleDateFormat(MainActivity.dateFormat, Locale.US) ;
		    if ((line = reader.readLine()) != null) { // read first date
		    	String[] tokens = line.split(" ");
		    	if(tokens.length!=3){throw new IllegalArgumentException();} // 3 values in total
		    	try {
			        firstDate = dateFormat.parse(tokens[0]);
		        } catch (ParseException e) {
			        // TODO Auto-generated catch block
			        e.printStackTrace();
		        }
		    }
		    while ((line = reader.readLine()) != null) {
		    	location = readOneLocation(line);
		    	String[] tokens = line.split(" ");
		    	if(tokens.length!=3){throw new IllegalArgumentException();} // 3 values in total
		    	Date date = new Date();
		    	try {
			        date = dateFormat.parse(tokens[0]);
		        } catch (ParseException e) {
			        // TODO Auto-generated catch block
			        e.printStackTrace();
		        }
		    	
		    	Date currentDate = new Date();
		    	double timespan = currentDate.getTime() - firstDate.getTime();
		    	double diff = currentDate.getTime() - date.getTime();

		    	int colourPercentage = (int) ((diff/timespan)*255);
		    	
		    	int color = Color.rgb(255-colourPercentage, 0, colourPercentage); // Recent routes more red. Older routes more blue
		    	
		    	if (previousLocation == null) previousLocation = location;
		        PolylineOptions rectOptions = new PolylineOptions();
		        rectOptions.color(color);
		        rectOptions.add(new LatLng(location.latitude, location.longitude));
		        rectOptions.add(new LatLng(previousLocation.latitude, previousLocation.longitude));
		        this.googleMap.addPolyline(rectOptions);
		        previousLocation = location;
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

	private LatLng readOneLocation(String line) {
		String[] tokens = line.split(" ");
    	if(tokens.length!=3){throw new IllegalArgumentException();} // 3 values in total
    	Double latitude = Double.valueOf(tokens[1]);
    	Double longitude = Double.valueOf(tokens[2]);
		return new LatLng(latitude, longitude);
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

		final EditText inputLocation = new EditText(this); // Set up input for location
		inputLocation.setInputType(InputType.TYPE_CLASS_TEXT); // Specify the type of input expected
		inputLocation.setHint("Enter name of location");
		
		final EditText inputRadius = new EditText(this); // Set up input for radius
		inputRadius.setInputType(InputType.TYPE_CLASS_NUMBER); // Specify the type of input expected
		inputRadius.setHint("Enter radius of location");
		
		
		TextView rgTitle = new TextView(this);
		rgTitle.setText("Type of location");
		/* Begin of Radiogroup */
		final RadioGroup rg = new RadioGroup(this);
		RadioButton rb1 = new RadioButton(this), rb2 = new RadioButton(this), rb3 = new RadioButton(this), rb4 = new RadioButton(this), rb5 = new RadioButton(this), rb6 = new RadioButton(this);
		rb1.setText(CustomLocationList.LocationTypes.Home.toString());
		rb2.setText(CustomLocationList.LocationTypes.Work.toString());
		rb3.setText(CustomLocationList.LocationTypes.Sport.toString());
		rb4.setText(CustomLocationList.LocationTypes.Store.toString());
		rb5.setText(CustomLocationList.LocationTypes.Recreation.toString());
		rb6.setText(CustomLocationList.LocationTypes.Other.toString());

		rg.addView(rb1);
		rg.addView(rb2);
		rg.addView(rb3);
		rg.addView(rb4);
		rg.addView(rb5);
		rg.addView(rb6);
		rg.check(rb1.getId()); //First one is checked by default
		/* End of Radiogroup*/
		
		//Layout of items
		LinearLayout layout= new LinearLayout(this);
		layout.setOrientation(1); //1 is for vertical orientation
		layout.addView(inputLocation);
		layout.addView(inputRadius);
		layout.addView(rgTitle);
		layout.addView(rg);

	    alertDialog.setView(layout);
	    alertDialog.setPositiveButton(android.R.string.ok, null);
	    
		// Set up the buttons
		alertDialog.setPositiveButton("Add location", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	int maxRadius = 100;
		    	if(Integer.parseInt(inputRadius.getText().toString())>maxRadius) { //TODO Stop this method and keep dialog open!
		    		Toast.makeText(getApplicationContext(), "The radius has exceeded the maximum of " + maxRadius , Toast.LENGTH_SHORT).show();
//		    	} else if(inputRadius.getText().equals("") || inputLocation.getText().equals("")) {
//		    	    Toast.makeText(getApplicationContext(), "Please fill in all the fields" , Toast.LENGTH_SHORT).show();
		    	} else {
		    		int id = rg.getCheckedRadioButtonId();
		    		int checkedIndex = rg.indexOfChild(rg.findViewById(id));
		    		addMarker(inputLocation.getText().toString(), Integer.parseInt(inputRadius.getText().toString()), checkedIndex, location); //Add marker
		    	}
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
	 * @param radius
	 * @param checkedIndex
	 * @param location
	 */
	private void addMarker(String locationName, int radius, int checkedIndex, LatLng location) {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(new LatLng(location.latitude, location.longitude));
		markerOptions.title(locationName);
//		DecimalFormat df = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance());
		//XXX Spaces in snippet cannot be removed, because of the way we are saving/loading for now
		markerOptions.snippet("Latitude:" + location.latitude + ",Longitude:" + location.longitude+",radius:" + radius);
		
		CircleOptions circleOptions = new CircleOptions().center(markerOptions.getPosition()).radius(radius); // In meters
        this.googleMap.addCircle(circleOptions);
		this.googleMap.addMarker(markerOptions);
		
		saveMarker(markerOptions, radius, checkedIndex);
	}
	
	
	/**
	 * Saves a marker to variable
	 * @param marker
	 * @param checkedIndex 
	 * @param radius 
	 */
	private void saveMarker(MarkerOptions marker, int radius, int checkedIndex) { //TODO remove method if it will no be expanded
		MainActivity.locationList.add(new CustomLocation(marker, radius, checkedIndex));
	}
	
	/**
	 * Loads markers from variable
	 */
	private void loadMarkers() {
		CustomLocation loc = null;
		for(int i=0; i<MainActivity.locationList.size(); i++) {
			loc = MainActivity.locationList.get(i);
			CircleOptions circleOptions = new CircleOptions().center(loc.getMarkerOptions().getPosition()).radius(loc.getRadius()); // In meters
	        this.googleMap.addCircle(circleOptions);
			this.googleMap.addMarker(loc.getMarkerOptions());
		}
	}


	/**
	 * Fits all the markers in screen
	 * @param view
	 */
	public void onZoomToFit(View view) {
		if(MainActivity.locationList.size()>0) {
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			CustomLocation loc = null;
			for(int i=0; i<MainActivity.locationList.size(); i++) { //Calculate bounds of all markers
				loc = MainActivity.locationList.get(i);
			    builder.include(loc.getMarkerOptions().getPosition());
			}
			LatLngBounds bounds = builder.build();
			
			int padding = 30; // offset from edges of the map (pixels)
			CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
			
			this.googleMap.animateCamera(cu);
		} else {
			Toast.makeText(this.getApplicationContext(), "No markers saved currently", Toast.LENGTH_SHORT).show();
		}
	}
}

