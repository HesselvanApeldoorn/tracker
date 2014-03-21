package ubicomp.tracker;

import com.google.android.gms.maps.model.LatLng;

import ubicomp.tracker.R;

import android.os.Bundle;


public class Statistics extends BaseMenu {
	
	int timeSpentHome;
	LatLng locationHome;
	CustomLocation customLocationHome;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics);
		applyIntelligence();
	}

	private void applyIntelligence() {
		findSpecialPlaces();
		timeSpentInPlaces();
	}

	private void findSpecialPlaces() {
		this.locationHome = findHome();
	}

	private LatLng findHome() {
		//TODO pseudo code, not finished yet, probably crap
		int maxTimeSpent  = 0; // time in seconds
		for(int i=0; i<MainActivity.savedRoutes.size; i++) {
			if (MainActivity.savedRoutes[i].time > 18:00 && MainActivity.savedRoutes.time < 7:00) { // candidate home location, as you're usually at home between these times
				LatLng candidateLocationHome = MainActivity.savedRoutesList[i].location; // possible homelocation
				int timeSpent = 0;
				for(int j=0; j<MainActivity.savedLocations.size; j++) {
					if (abs(candidateLocationHome - MainActivity.savedLocations[j].location) < 100meter) { // routepiece is near the previous routepiece.
						timeSpent += MainActivity.savedRoutesList.get(j+1).time - MainActivity.savedRoutesList.get(j).time; // add time spent at a certain route piece
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
		this.timeSpentHome = calcTimeSpentHome(this.customLocationHome);
	}

	private int calcTimeSpentHome(CustomLocation home) {
		//TODO pseudocode, update as soon as an arraylist of savedroutes is available
		int timeSpent = 0;
		for(int i=0; i<MainActivity.savedRoutes.size; i++) { // iterate over all route pieces
			LatLng route = MainActivity.savedRoutesList.get(i).location; // get the location of a route piece
			if (abs(route - home) < 100meter) { // possibly replace this by an overlap function
				timeSpent += MainActivity.savedRoutesList.get(i+1).time - MainActivity.savedRoutesList.get(i).time; // add time spent at a certain route piece
			}
		}
		return timeSpent;
	}
	
}