package ubicomp.tracker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ubicomp.tracker.CustomLocationList.LocationTypes;
import ubicomp.tracker.R;

import android.content.Intent;
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

	public double[] spentTime = new double[]{0,0,0,0,0,0}; // Total time spent in location of certain type. Default is zero
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics);
		this.calcTimes();
		this.calcCharacterType();
		
	}

	/**
	 * Calculates a definition of the user by looking at statistics
	 */
	private void calcCharacterType() {
		double max=0, totalTime=0;
		int type=-1;
		
		//Calculate where the person has spent the most time
		for(int i=0; i<this.spentTime.length;i++) {
			totalTime+=this.spentTime[i];
			if(this.spentTime[i]>max) {
				max = this.spentTime[i];
				type=i;
			}
		}
		TextView textView = (TextView)findViewById(R.id.characterType);
		if(type==-1) {
			textView.setText("Not enough data to characterize you");
		} else {
	        DecimalFormat df = new DecimalFormat("####0.00");
			textView.setText("Most time spent in: "+LocationTypes.values()[type] + " ("+df.format(this.spentTime[type])+" hours)");
		}
		textView.invalidate();

		//Define the person as a certain type
		String characterType="Not enough data available";
		if(this.spentTime[LocationTypes.Store.ordinal()]>0.02*totalTime) {
			characterType="shopper";
		} else if(this.spentTime[LocationTypes.Sport.ordinal()]>0.05*totalTime) {
			characterType="gym rat";
		} else if(this.spentTime[LocationTypes.Recreation.ordinal()]>0.05*totalTime) {
			characterType="partyboy";
		} else if(this.spentTime[LocationTypes.Home.ordinal()]>0.6*totalTime) {
			characterType="house keeper";
		} else if(this.spentTime[LocationTypes.Work.ordinal()]>this.spentTime[LocationTypes.Home.ordinal()]) {
			characterType="workaholic";
		}
		
		textView = (TextView)findViewById(R.id.mostTime);
		textView.setText("You are: " + characterType);
		textView.invalidate();
		
		
    }

	/**
	 * Calculate the time spent for all the location types
	 */
	private void calcTimes() {
		for(int i=0;i<LocationTypes.values().length; i++) {
			this.calcTimeSpent(LocationTypes.values()[i]);
		}
	}

	/**
	 * Called when pressing on a button. The function finds special locations
	 * @param view
	 */
	public void findSpecialPlaces(View view) {
		int amountAdded=0; //number of location types added
		for(int i=0;i<LocationTypes.values().length;i++) {
			LocationTypes type = LocationTypes.values()[i];
			if(this.addLocation(this.findLocation(type),type)) {
				amountAdded++; //A location type is added
			}
		}	
		String msg = "";
		if(amountAdded==0) {
			msg="No new locations added";
		} else {
			msg="New locations added"+" ("+amountAdded+" type(s))";
			
			//Refresh page
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Finds a location of a certain type if any
	 * @param type: the type of the location that needs to be found
	 * @return a position of a location
	 */
	private LatLng findLocation(LocationTypes type) {
		int maxTimeSpent  = 0; // time in milliseconds
		LatLng location = null;
		
		for(int i=0; i<MainActivity.routesList.size()-1; i++) {
			int timeSpent = 0;
			LatLng candidate = null;
			TrackedRoute currentRoute = MainActivity.routesList.get(i);
			Date date = MainActivity.routesList.get(i).getDate();

			if (locationConstraints(type,date)) { // candidate location, as you're usually at home between these times
				candidate = currentRoute.getLocation(); // possible location
				for(int j=0; j<MainActivity.routesList.size()-1; j++) {
					date = MainActivity.routesList.get(j).getDate();
					if (locationConstraints(type,date)) { // candidate location, as you're usually at home between these times
						if  (this.inRadius(this.defineRadius(type), candidate.latitude, candidate.longitude, MainActivity.routesList.get(j).getLocation().latitude, MainActivity.routesList.get(j).getLocation().longitude)) {// routepiece is near the previous routepiece
							timeSpent += (MainActivity.routesList.get(j+1).getDate().getTime() - date.getTime()); // add time spent at a certain route piece
						}
					}
				}
			}
			if (timeSpent > maxTimeSpent) { // better candidate for location
				location = candidate;
			}	
		}
		return location;
	}
	
	/**
	 * Specifies the constraints for certain locations
	 * @param type: type of the location
	 * @param date: date of the location
	 * @return true if location of certain type is found, false otherwise
	 */
	private boolean locationConstraints(LocationTypes type, Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		
		if(type.equals(CustomLocationList.LocationTypes.Home)) { //Timeslot: 7-18. Or saturday/sunday
			if(date.getHours() > 18 || date.getHours() < 7 || day==7 || day==1 ) { //7 and 1 are respectively representing Saturday and Sunday
				return true;
			} else {
				return false;
			}
		} else if(type.equals(CustomLocationList.LocationTypes.Work)) {
		     if(date.getHours() > 9 && date.getHours() < 17 && day!=1 && day!=7) { //7 and 1 are respectively representing Saturday and Sunday
		    	 return true;
		     } else {
		    	 return false;
		     }
		}
		return false;
		
	}

	/**
	 * Adds app defined location
	 * @param location: position of location defined by app
	 * @param type: type of location defined by app
	 */
	private boolean addLocation(LatLng location, LocationTypes type) {
		if(location!=null && !MainActivity.locationList.exists(location,this.defineRadius(type), type.ordinal())) {
			MarkerOptions marker = new MarkerOptions();
			marker.position(location);
			marker.title(type.toString());
			CustomLocation loc = new CustomLocation(marker, this.defineRadius(type), type.ordinal());
			loc.setUserDefined(false);
			MainActivity.locationList.add(loc);
			return true;
		} else {
			return false;
		}
	}

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
	        double spentTime = timeSpent/3600000.0;
	        column2.setText(df.format(spentTime) + " hours");
	        
	        this.spentTime[type.ordinal()]+=spentTime;
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