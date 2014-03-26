package ubicomp.tracker;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ubicomp.tracker.R;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


public class Statistics extends BaseMenu {
	
	static int HOMERADIUS = 20; // Average homesize
//	int timeSpentHome = 0;
//	LatLng locationHome;
//	CustomLocation customLocationHome = MainActivity.locationList.get(0);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics);
		this.applyIntelligence();
//	    Toast.makeText(this, "Total time spent home: " + timeSpentHome + "",Toast.LENGTH_SHORT).show();
		
	}

	private void applyIntelligence() {
		//this.findSpecialPlaces();MainActivity.locationList.ge
		this.timeSpentInPlaces();
	}

	public void findSpecialPlaces(View view) {
		LatLng locationHome = this.findHome();
		
		if(locationHome!=null) {
			this.addHomeLocation(locationHome);
			Toast.makeText(this, "home location: " + locationHome.latitude + ", " + locationHome.longitude,Toast.LENGTH_SHORT).show();

		}
	
		
	}

	private LatLng findHome() {
		//TODO not verified yet. works in theory.
		int maxTimeSpent  = 0; // time in milliseconds
		LatLng locationHome = null;
		
		for(int i=0; i<MainActivity.routesList.size()-1; i++) {
			int timeSpent = 0;
			Log.d("checking for home with time: ",""+MainActivity.routesList.get(i).getDate().getHours());

			LatLng candidateLocationHome = null;
			TrackedRoute currentRoute = MainActivity.routesList.get(i);
			if (currentRoute.getDate().getHours() > 18 || currentRoute.getDate().getHours() < 7) { // candidate home location, as you're usually at home between these times
				candidateLocationHome = currentRoute.getLocation(); // possible homelocation
				for(int j=0; j<MainActivity.routesList.size()-1; j++) {
					if (MainActivity.routesList.get(j).getDate().getHours() > 18 || MainActivity.routesList.get(j).getDate().getHours() < 7) { // candidate home location, as you're usually at home between these times
						if  (this.inRadius(Statistics.HOMERADIUS, candidateLocationHome.latitude, candidateLocationHome.longitude, MainActivity.routesList.get(j).getLocation().latitude, MainActivity.routesList.get(j).getLocation().longitude)) {// routepiece is near the previous routepiece
							Log.d("in radius?", "yes");
							timeSpent += (MainActivity.routesList.get(j+1).getDate().getTime() - MainActivity.routesList.get(j).getDate().getTime()); // add time spent at a certain route piece
						}
					}
				}
			}
			if (timeSpent > maxTimeSpent) { // better candidate for home location
				locationHome = candidateLocationHome;
			}	
		}
		if(locationHome!=null)Log.d("LocationHOme: " , "" + locationHome.latitude);
		return locationHome;
	}
	
	private void addHomeLocation(LatLng location) {
		if(!MainActivity.locationList.exists(location)) {
			MarkerOptions marker = new MarkerOptions();
			marker.position(location);
			marker.title("Home");
			CustomLocation home = new CustomLocation(marker, Statistics.HOMERADIUS, CustomLocationList.LocationTypes.Home.ordinal());
			home.setUserDefined(false);
			MainActivity.locationList.add(home);
		}
	}

	//TODO uncomment and fix null pointer exception on undefix this.customLocationHome. Fix by if check for looking at existing home places user defined or not
	private void timeSpentInPlaces() {
		this.calcTimeSpentHome();
	}

	/**
	 * For all the home locations (user/app defined) calculate the amount of time spent
	 * @return
	 */
	private void calcTimeSpentHome() {
		ArrayList<CustomLocation> homeLocations = MainActivity.locationList.findLocationType(CustomLocationList.LocationTypes.Home.ordinal());
		
		Log.d("TTTTTGTTTTTTTTTT ", "Hieesngsdlnf");
		TableLayout homeTable = (TableLayout)findViewById(R.id.homeTable);
		homeTable.setStretchAllColumns(true);
		homeTable.bringToFront();

	    
		for(CustomLocation loc: homeLocations) {
			int timeSpent = 0; // time in milliseconds
			for(int i=0; i<MainActivity.routesList.size()-1; i++) { // iterate over all route pieces
				TrackedRoute route = MainActivity.routesList.get(i); // get the location of a route piece
				LatLng homeLocation = loc.getMarkerOptions().getPosition();
				if (this.inRadius(loc.getRadius(), homeLocation.latitude, homeLocation.longitude, route.getLocation().latitude, route.getLocation().longitude)) {
					timeSpent += (MainActivity.routesList.get(i+1).getDate().getTime() - route.getDate().getTime()); // add time spent at a certain route piece
				}
			}
			TableRow row = new TableRow(this);
			TextView column1 = new TextView(this);
			column1.setText(loc.getMarkerOptions().getTitle());
			row.addView(column1);
	        TextView column2 = new TextView(this);
	        
	        DecimalFormat df = new DecimalFormat("####0.00");
	        column2.setText(df.format(timeSpent/3600000.0) + " hours");
	        
	        row.addView(column2);
	        homeTable.addView(row);
		}
//		return timeSpent;
	}
	
	private boolean inRadius(int radius, double latitude1, double longitude1, double latitude2, double longitude2) {
		return Math.sqrt((latitude1-latitude2)*(longitude1-longitude2)) < radius;
	}
	
}