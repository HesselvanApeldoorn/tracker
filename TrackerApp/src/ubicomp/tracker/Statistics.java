package ubicomp.tracker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ubicomp.tracker.CustomLocationList.LocationTypes;
import ubicomp.tracker.R;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


public class Statistics extends BaseMenu {
	
	static int HOMERADIUS = 20; // Average homesize
	static int WORKRADIUS = 50; // Average worksize
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
		LatLng locationWork = this.findWork();
		
		if(locationHome!=null) {
			this.addLocation(locationHome,  CustomLocationList.LocationTypes.Home);
			Toast.makeText(this, "home location: " + locationHome.latitude + ", " + locationHome.longitude,Toast.LENGTH_SHORT).show();
		}
		if(locationWork!=null) {
			this.addLocation(locationWork, CustomLocationList.LocationTypes.Work);
			Toast.makeText(this, "work location: " + locationWork.latitude + ", " + locationWork.longitude,Toast.LENGTH_SHORT).show();
		}
		
	}

	private LatLng findHome() {
		//TODO not verified yet. works in theory.
		int maxTimeSpent  = 0; // time in milliseconds
		LatLng location = null;
		
		for(int i=0; i<MainActivity.routesList.size()-1; i++) {
			int timeSpent = 0;
			Log.d("checking for home with time: ",""+MainActivity.routesList.get(i).getDate().getHours());

			LatLng candidate = null;
			TrackedRoute currentRoute = MainActivity.routesList.get(i);
			if (currentRoute.getDate().getHours() > 18 || currentRoute.getDate().getHours() < 7) { // candidate home location, as you're usually at home between these times
				candidate = currentRoute.getLocation(); // possible homelocation
				for(int j=0; j<MainActivity.routesList.size()-1; j++) {
					if (MainActivity.routesList.get(j).getDate().getHours() > 18 || MainActivity.routesList.get(j).getDate().getHours() < 7) { // candidate home location, as you're usually at home between these times
						if  (this.inRadius(Statistics.HOMERADIUS, candidate.latitude, candidate.longitude, MainActivity.routesList.get(j).getLocation().latitude, MainActivity.routesList.get(j).getLocation().longitude)) {// routepiece is near the previous routepiece
							Log.d("in radius?", "yes");
							timeSpent += (MainActivity.routesList.get(j+1).getDate().getTime() - MainActivity.routesList.get(j).getDate().getTime()); // add time spent at a certain route piece
						}
					}
				}
			}
			if (timeSpent > maxTimeSpent) { // better candidate for home location
				location = candidate;
			}	
		}
		if(location!=null)Log.d("LocationHOme: " , "" + location.latitude);
		return location;
	}
	
	private LatLng findWork() {
		//TODO not verified yet. works in theory.
		int maxTimeSpent  = 0; // time in milliseconds
		LatLng location = null;
		
		for(int i=0; i<MainActivity.routesList.size()-1; i++) {
			int timeSpent = 0;
			Log.d("checking for work with time: ",""+MainActivity.routesList.get(i).getDate().getHours());

			LatLng candidate = null;
			TrackedRoute currentRoute = MainActivity.routesList.get(i);
			Date routeDate = currentRoute.getDate();
			if (routeDate.getHours() > 9 && routeDate.getHours() < 17 && routeDate.getDay()<6) { // candidate work location, as you're usually at work between these times
				candidate = currentRoute.getLocation(); // possible homelocation
				for(int j=0; j<MainActivity.routesList.size()-1; j++) {
					routeDate=MainActivity.routesList.get(j).getDate();
					if (routeDate.getHours() > 9 && routeDate.getHours() < 17 && routeDate.getDay()<6) { // candidate work location, as you're usually at work between these times
						if  (this.inRadius(Statistics.WORKRADIUS, candidate.latitude, candidate.longitude, MainActivity.routesList.get(j).getLocation().latitude, MainActivity.routesList.get(j).getLocation().longitude)) {// routepiece is near the previous routepiece
							Log.d("in radius?", "yes");
							timeSpent += (MainActivity.routesList.get(j+1).getDate().getTime() - MainActivity.routesList.get(j).getDate().getTime()); // add time spent at a certain route piece
						}
					}
				}
			}
			if (timeSpent > maxTimeSpent) { // better candidate for home location
				location = candidate;
			}	
		}
		if(location!=null)Log.d("Locationwork: " , "" + location.latitude);
		return location;
	}
	private void addLocation(LatLng location, LocationTypes type) {
		if(!MainActivity.locationList.exists(location)) {
			MarkerOptions marker = new MarkerOptions();
			marker.position(location);
			marker.title(type.toString());
			CustomLocation loc = new CustomLocation(marker, Statistics.HOMERADIUS, type.ordinal());
			loc.setUserDefined(false);
			MainActivity.locationList.add(loc);
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

		TableLayout homeTable = (TableLayout)findViewById(R.id.homeTable);
		homeTable.setStretchAllColumns(true);
		homeTable.bringToFront();
		
		TableRow th = new TableRow(this);
		TextView ch1 = new TextView(this);
		ch1.setTypeface(Typeface.DEFAULT_BOLD);
		ch1.setText("Home");
		TextView ch2 = new TextView(this);
		ch2.setTypeface(Typeface.DEFAULT_BOLD);
		ch2.setText("Hours spent");
	    th.addView(ch1);
	    th.addView(ch2);
	    homeTable.addView(th);
	    
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