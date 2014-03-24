package ubicomp.tracker;

import java.util.Calendar;

import com.google.android.gms.maps.model.LatLng;

import ubicomp.tracker.R;

import android.os.Bundle;
import android.widget.Toast;


public class Statistics extends BaseMenu {
	
	static int HOMERADIUS = 100; // Average homesize
	int timeSpentHome = 0;
	LatLng locationHome;
	CustomLocation customLocationHome = MainActivity.locationList.get(0);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics);
		this.applyIntelligence();
	    Toast.makeText(this, "Total time spent home: " + timeSpentHome + "",Toast.LENGTH_SHORT).show();
		
	}

	private void applyIntelligence() {
		this.findSpecialPlaces();
		this.timeSpentInPlaces();
	}

	private void findSpecialPlaces() {
		this.locationHome = this.findHome();
		Toast.makeText(this, "latitude of home: " + this.locationHome.latitude + "",Toast.LENGTH_SHORT).show();
	}

	private LatLng findHome() {
		//TODO not verified yet. Should work in theory.
		int maxTimeSpent  = 0; // time in milliseconds
		LatLng locationHome = null;
		for(int i=0; i<MainActivity.routesList.size()-1; i++) {
			int timeSpent = 0;
			LatLng candidateLocationHome = null;
			if ((MainActivity.routesList.get(i).getDate().getHours() > 18) || (MainActivity.routesList.get(i+1).getDate().getHours()) < 7) { // candidate home location, as you're usually at home between these times
				candidateLocationHome = MainActivity.routesList.get(i).getLocation(); // possible homelocation
				for(int j=0; j<MainActivity.routesList.size()-1; j++) {
					if  (this.inRadius(Statistics.HOMERADIUS, candidateLocationHome.latitude, candidateLocationHome.longitude, MainActivity.routesList.get(j).getLocation().latitude, MainActivity.routesList.get(j).getLocation().longitude)) {// routepiece is near the previous routepiece
						timeSpent += (MainActivity.routesList.get(j+1).getDate().getTime() - MainActivity.routesList.get(j).getDate().getTime()); // add time spent at a certain route piece
					}
				}
			}
			if (timeSpent > maxTimeSpent) { // better candidate for home location
				locationHome = candidateLocationHome;
			}	
		}
		return locationHome;
	}
	

	private void timeSpentInPlaces() {
		this.timeSpentHome = calcTimeSpentHome();
	}

	private int calcTimeSpentHome() {
		int timeSpent = 0; // time in milliseconds
		for(int i=0; i<MainActivity.routesList.size()-1; i++) { // iterate over all route pieces
			TrackedRoute route = MainActivity.routesList.get(i); // get the location of a route piece
			LatLng homeLocation = this.customLocationHome.getMarkerOptions().getPosition();
			if (this.inRadius(this.customLocationHome.getRadius(), homeLocation.latitude, homeLocation.longitude, route.getLocation().latitude, route.getLocation().longitude)) {
				timeSpent += (MainActivity.routesList.get(i+1).getDate().getTime() - route.getDate().getTime()); // add time spent at a certain route piece
			}
		}
		return timeSpent;
	}
	
	private boolean inRadius(int radius, double latitude1, double longitude1, double latitude2, double longitude2) {
		return Math.sqrt((latitude1-latitude2)*(longitude1-longitude2)) < radius;
	}
	
}