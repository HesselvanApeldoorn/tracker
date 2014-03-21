package ubicomp.tracker;

import com.google.android.gms.maps.model.LatLng;

import ubicomp.tracker.R;

import android.os.Bundle;
import android.widget.Toast;


public class Statistics extends BaseMenu {
	
	int timeSpentHome = 0;
	LatLng locationHome;
	CustomLocation customLocationHome = MainActivity.locationList.get(0);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics);
		this.applyIntelligence();
	    Toast.makeText(this, timeSpentHome + "",Toast.LENGTH_SHORT).show();
		
	}

	private void applyIntelligence() {
		this.findSpecialPlaces();
		this.timeSpentInPlaces();
	}

	private void findSpecialPlaces() {
//		this.locationHome = this.findHome();
	}

//	private LatLng findHome() {
//		//TODO pseudo code, not finished yet, probably crap
//		int maxTimeSpent  = 0; // time in seconds
//		for(int i=0; i<MainActivity.savedRoutes.size; i++) {
//			if (MainActivity.savedRoutes[i].time > 18:00 && MainActivity.savedRoutes.time < 7:00) { // candidate home location, as you're usually at home between these times
//				LatLng candidateLocationHome = MainActivity.savedRoutesList[i].location; // possible homelocation
//				int timeSpent = 0;
//				for(int j=0; j<MainActivity.savedLocations.size; j++) {
//					if (abs(candidateLocationHome - MainActivity.savedLocations[j].location) < 100meter) { // routepiece is near the previous routepiece.
//						timeSpent += MainActivity.savedRoutesList.get(j+1).time - MainActivity.savedRoutesList.get(j).time; // add time spent at a certain route piece
//					}
//				}
//			}
//			if (timeSpent > maxTimeSpent) { // better candidate for home location
//				locationHome = candidateLocationHome;
//			}	
//		}
//		return locationHome;
//	}
	

	private void timeSpentInPlaces() {
		this.timeSpentHome = calcTimeSpentHome(this.customLocationHome);
	}

	private int calcTimeSpentHome(CustomLocation home) {
		int timeSpent = 0;
		for(int i=0; i<MainActivity.routesList.size()-1; i++) { // iterate over all route pieces
			TrackedRoute route = MainActivity.routesList.get(i); // get the location of a route piece
			LatLng homeLocation = home.getMarkerOptions().getPosition();
			if (inRadius(home.getRadius(), homeLocation.latitude, homeLocation.longitude, route.getLocation().latitude, route.getLocation().longitude)) {
				timeSpent += (MainActivity.routesList.get(i+1).getDate().getTime() - route.getDate().getTime()); // add time spent at a certain route piece
			}
		}
		return timeSpent;
	}
	
	private boolean inRadius(int radius, double latitude1, double longitude1, double latitude2, double longitude2) {
		return Math.sqrt((latitude1-latitude2)*(longitude1-longitude2)) < radius;
	}
	
}