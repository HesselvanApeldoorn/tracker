package ubicomp.tracker;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.maps.model.LatLng;

import ubicomp.tracker.R;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends BaseMenu implements
ConnectionCallbacks, OnConnectionFailedListener, LocationListener{

	//TODO remove all Log.d and unnecessary toast 
	LocationManager locManager;

	public static final String savedLocations = "savedLocations"; // user markers filename
	public static final String savedRoutes = "savedRoutes"; // tracked routes filename
	public static final String dateFormat = "yyyy-MM-dd-HH-mm-ss"; //format used for storing date and time
	public static final CustomLocationList locationList = new CustomLocationList(); // User markers list
	public static final ArrayList<TrackedRoute> routesList = new ArrayList<TrackedRoute>(); // Tracked routes list
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.createResourceStubs();
        MainActivity.locationList.loadFile(this.getApplicationContext()); //Load markers from file into application on startup
        this.loadTrackedRoutes();
        
        
//        //TODO remove this code:
//        Date date1 = new Date(2014,3,26, 19,30); //year,month,day,hour, minutes
//        Date date2 = new Date(2014,3,26, 19,40); //year,month,day,hour, minutes
//        Date date4 = new Date(2014,3,28, 19,40); //year,month,day,hour, minutes
//        Date date5 = new Date(2014,3,29, 19,40); //year,month,day,hour, minutes
//
//		double latitude = 53.2191700;
//		double longitude = 6.5666700;
//        MainActivity.routesList.add(new TrackedRoute(date1, new LatLng(latitude, longitude)));
//        MainActivity.routesList.add(new TrackedRoute(date2, new LatLng(latitude, longitude)));
//
//        date1 = new Date(2014,3,27,10,20);
//        date2 = new Date(2014,3,27,10,30); 
//        Date date3 = new Date(2014,3,27,10,40); 
//
//    	latitude = 54.2191700;
//		longitude = 6.5666700;
//		Double latitude1 = 54.2191701;
//		Double longitude1 = 6.5666701;
//		Double latitude2 = 54.2191701;
//		Double longitude2= 6.5666701;
//        MainActivity.routesList.add(new TrackedRoute(date1, new LatLng(latitude, longitude)));
//        MainActivity.routesList.add(new TrackedRoute(date2, new LatLng(latitude1, longitude1)));     
//        MainActivity.routesList.add(new TrackedRoute(date3, new LatLng(latitude2, longitude2)));        
//
//        MainActivity.routesList.add(new TrackedRoute(date4, new LatLng(latitude, longitude)));
//        MainActivity.routesList.add(new TrackedRoute(date5, new LatLng(latitude, longitude)));
//
//        //TODO end of to be removed code
        
        final LocationListener locLis = this;
        
		Switch tracking = (Switch) findViewById(R.id.switch_tracking);
        tracking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                	if(locManager == null){
                		Toast.makeText(getApplicationContext(), "Requesting GPS service..", Toast.LENGTH_SHORT).show();
                		locManager = (LocationManager)getSystemService(
                    	        Context.LOCATION_SERVICE);
                	}
        	        Toast.makeText(getApplicationContext(), "tracking on", Toast.LENGTH_SHORT).show();
        	        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locLis);
        		    locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locLis);
                } else {
        	        Toast.makeText(getApplicationContext(), "tracking off", Toast.LENGTH_SHORT).show();
        	        locManager.removeUpdates(locLis);
                }
            }
        });
	}
	
	/**
	 * Load tracked routes from file into variable
	 */
	private void loadTrackedRoutes() {
		FileInputStream fis = null;
		
		try {
            fis = openFileInput(MainActivity.savedRoutes);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		    
		    String line = null;
	    	TrackedRoute route;
            while ((line = reader.readLine()) != null) {
            	route = readOneRoute(line);
            	MainActivity.routesList.add(route);
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
	 * Read one route from file
	 * @param line: one line read from a file
	 * @return a route object
	 */
	private TrackedRoute readOneRoute(String line) {
		String[] tokens = line.split(" ");
    	if(tokens.length!=3){throw new IllegalArgumentException();} // 3 values in total
    	Date date = new Date();
    	try {
	    	SimpleDateFormat dateFormat = new SimpleDateFormat(MainActivity.dateFormat, Locale.US) ;
	        date = dateFormat.parse(tokens[0]);
        } catch (ParseException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
    	Double latitude = Double.valueOf(tokens[1]);
    	Double longitude = Double.valueOf(tokens[2]);
		return new TrackedRoute(date, new LatLng(latitude, longitude));
	}
	
	private void createResourceStubs() {
		try {
			openFileOutput(MainActivity.savedLocations, MODE_APPEND).close();
		    openFileOutput(MainActivity.savedRoutes, Context.MODE_APPEND).close();				
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void openStatistics(View view) {
		Intent statistics_screen = new Intent(getApplicationContext(), Statistics.class);
		startActivity(statistics_screen);
	}
	
	public void openMarkLocation(View view) {
		Intent markLocation_screen = new Intent(getApplicationContext(), MarkLocation.class);
		startActivity(markLocation_screen);
	}

	//TODO call this method on every exception catch
	public void shutDown() {
		MainActivity.locationList.saveToFile(this.getApplicationContext()); // Save all the markers to file on closing the application
	    this.saveRoutesToFile();
	    
	    //Clear arrayList as android saves these objects.
	    MainActivity.locationList.clear();
	    MainActivity.routesList.clear();
	}
	/**
	 * This method is called upon closing the app and saves the variables to files
	 */
	@Override
	public void onBackPressed() {	    
	    this.shutDown();
	    
	    finish();
	    return;
	}   
	
	/**
	 * Saves the tracked routes from a variable to a file
	 */
	private void saveRoutesToFile() {
		FileOutputStream fos;
		try {
			fos = openFileOutput(MainActivity.savedRoutes, Context.MODE_APPEND);
	        OutputStreamWriter osw = new OutputStreamWriter(fos);
	        
	        String output = "";
	        for(TrackedRoute route: MainActivity.routesList) {
	        	output += route + "\n";
	        }
		    osw.write(output);
		    osw.flush();
		    osw.close();
		    fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*************************************** CONNECT LOCATION CLIENT ***************************************/


	/*
	 * Called when the Activity becomes visible.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
	}

	/*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		super.onStop();
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    // TODO change 0, 0 parameters
	    if(locManager != null){
		    locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		    locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
	    }
	}
	

	/*************************************** DEFINE LOCATION SERVICES CALLBACKS ***************************************/

	@Override
	public void onConnected(Bundle dataBundle) {
		// Display the connection status
		
	}

	
	@Override
	public void onLocationChanged(android.location.Location location) {
		//TODO --> Check if the current location Overlaps one of the CustomLocations
	    Date date = new Date();
		MainActivity.routesList.add(new TrackedRoute(date, new LatLng(location.getLatitude(), location.getLongitude())));
	    	
	}

	@Override
	public void onProviderDisabled(String arg0) {
		Toast.makeText(getApplicationContext(), "GPS disabled", Toast.LENGTH_SHORT).show();		
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		Toast.makeText(getApplicationContext(), "GPS enabled", Toast.LENGTH_SHORT).show();		
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * Called by Location Services if the connection to the location client
	 * drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the user with
			 * the error.
			 */
			GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
		}
	}
	
	/*************************************** CHECK FOR GOOGLE PLAY SERVICES ***************************************/
	// Global constants
	/*
	 * Define a request code to send to Google Play services This code is
	 * returned in Activity.onActivityResult
	 */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	// Define a DialogFragment that displays the error dialog
	public static class ErrorDialogFragment extends DialogFragment {
		// Global field to contain the error dialog
		private Dialog mDialog;

		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		// Set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}

	/*
	 * Handle results returned to the FragmentActivity by Google Play services
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Decide what to do based on the original request code
		switch (requestCode) {
		case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
			switch (resultCode) {
			case Activity.RESULT_OK:
				/*
				 * Try the request again
				 */
				break;
			}
		}
	}
}
