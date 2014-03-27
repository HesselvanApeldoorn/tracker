package ubicomp.tracker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics);
		this.calcTimes();
		
	}

	private void calcTimes() {
		this.calcTimeSpent(LocationTypes.Home);
		this.calcTimeSpent(LocationTypes.Work);
		this.calcTimeSpent(LocationTypes.Sport);
		this.calcTimeSpent(LocationTypes.Store);
		this.calcTimeSpent(LocationTypes.Recreation);
		this.calcTimeSpent(LocationTypes.Other);


	}

	public void findSpecialPlaces(View view) {
		LatLng locationHome = this.findLocation(CustomLocationList.LocationTypes.Home);
		LatLng locationWork = this.findLocation(CustomLocationList.LocationTypes.Work);

		
		if(locationHome!=null) {
			this.addLocation(locationHome,  CustomLocationList.LocationTypes.Home);
			Toast.makeText(this, "home location: " + locationHome.latitude + ", " + locationHome.longitude,Toast.LENGTH_SHORT).show();
		}
		if(locationWork!=null) {
			this.addLocation(locationWork, CustomLocationList.LocationTypes.Work);
			Toast.makeText(this, "work location: " + locationWork.latitude + ", " + locationWork.longitude,Toast.LENGTH_SHORT).show();
		}		
	}
	
	private LatLng findLocation(LocationTypes type) {
		//TODO not verified yet. works in theory.
		int maxTimeSpent  = 0; // time in milliseconds
		LatLng location = null;
		
		for(int i=0; i<MainActivity.routesList.size()-1; i++) {
			int timeSpent = 0;
			LatLng candidate = null;
			TrackedRoute currentRoute = MainActivity.routesList.get(i);
			Date date = MainActivity.routesList.get(i).getDate();
			Log.d("routes index: ", ""+i +" date: " +date);

			if (locationConstraints(type,date)) { // candidate home location, as you're usually at home between these times
				Log.d("candidate: ","yes");
				candidate = currentRoute.getLocation(); // possible homelocation
				for(int j=0; j<MainActivity.routesList.size()-1; j++) {
					date = MainActivity.routesList.get(j).getDate();
					if (locationConstraints(type,date)) { // candidate home location, as you're usually at home between these times
						if  (this.inRadius(this.defineRadius(type), candidate.latitude, candidate.longitude, MainActivity.routesList.get(j).getLocation().latitude, MainActivity.routesList.get(j).getLocation().longitude)) {// routepiece is near the previous routepiece
							timeSpent += (MainActivity.routesList.get(j+1).getDate().getTime() - date.getTime()); // add time spent at a certain route piece
						}
					}
				}
			}
			if (timeSpent > maxTimeSpent) { // better candidate for home location
				location = candidate;
			}	
		}
		return location;
	}
	
	private boolean locationConstraints(LocationTypes type, Date date) {
		if(type.equals(CustomLocationList.LocationTypes.Home)) {
			if(date.getHours() > 18 || date.getHours() < 7) {
				return true;
			} else {
				return false;
			}
		} else if(type.equals(CustomLocationList.LocationTypes.Work)) {
			 Calendar calendar = Calendar.getInstance();
			 calendar.setTime(date);
		     int day = calendar.get(Calendar.DAY_OF_WEEK);
				Log.d("date for work: ", "hours: " + date.getHours() + " day " + day);

		     if(date.getHours() > 9 && date.getHours() < 17 && day!=1 && day!=7) { //7 and 1 are respectively representing Saturday and Sunday
		    	 Log.d("Day of week: ", "hours: " + date.getHours() + " min: " + date.getMinutes() +  " day: "+ date.getDay());
		    	 return true;
		     } else {
		    	 return false;
		     }
		}
		return false;
		
	}

	private void addLocation(LatLng location, LocationTypes type) {
		if(!MainActivity.locationList.exists(location)) {
			MarkerOptions marker = new MarkerOptions();
			marker.position(location);
			marker.title(type.toString());
			CustomLocation loc = new CustomLocation(marker, this.defineRadius(type), type.ordinal());
			loc.setUserDefined(false);
			MainActivity.locationList.add(loc);
		}
	}

	//TODO uncomment and fix null pointer exception on undefix this.customLocationHome. Fix by if check for looking at existing home places user defined or not
//	private void timeSpentInPlaces() {
//		this.calcTimeSpentHome();
//	}

	/**
	 * For all the home locations (user/app defined) calculate the amount of time spent
	 * @return
	 */
	private void calcTimeSpent(LocationTypes type) {
		ArrayList<CustomLocation> locations = MainActivity.locationList.findLocationType(type.ordinal());

		TableLayout table = (TableLayout)findViewById(R.id.table);
		table.setStretchAllColumns(true);
		table.bringToFront();
		
		TableRow th = new TableRow(this);
		TextView ch1 = new TextView(this);
		ch1.setTypeface(Typeface.DEFAULT_BOLD);
		ch1.setText(type.toString());
		TextView ch2 = new TextView(this);
		ch2.setTypeface(Typeface.DEFAULT_BOLD);
		ch2.setText("Hours spent");
	    th.addView(ch1);
	    th.addView(ch2);
	    table.addView(th);
	    
		for(CustomLocation loc: locations) {
			int timeSpent = 0; // time in milliseconds
			for(int i=0; i<MainActivity.routesList.size()-1; i++) { // iterate over all route pieces
				TrackedRoute route = MainActivity.routesList.get(i); // get the location of a route piece
				LatLng location = loc.getMarkerOptions().getPosition();
				if (this.inRadius(loc.getRadius(), location.latitude, location.longitude, route.getLocation().latitude, route.getLocation().longitude)) {
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
	        table.addView(row);
		}
	}
	
	private int defineRadius(LocationTypes type) {
		if(type.equals(CustomLocationList.LocationTypes.Home)) {
			return Statistics.HOMERADIUS;
		} else if(type.equals(CustomLocationList.LocationTypes.Work)){
			return Statistics.WORKRADIUS;
		} else {
			return 0;
		}
	}
	
	private boolean inRadius(int radius, double latitude1, double longitude1, double latitude2, double longitude2) {		
		return Math.sqrt((latitude1-latitude2)*(longitude1-longitude2)) < radius;
	}
	
}