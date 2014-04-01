package ubicomp.tracker;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends BaseMenu implements
ConnectionCallbacks, OnConnectionFailedListener, LocationListener{

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
        	        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, locLis);
        		    locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, locLis);
                } else {
        	        Toast.makeText(getApplicationContext(), "tracking off", Toast.LENGTH_SHORT).show();
        	        locManager.removeUpdates(locLis);
                }
            }
        });
        
        this.fakeRoutes();
	}
	
	
	private void fakeRoutes() {
		//DePintelier ---> Antonis's house 
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 10),6.562472409479678,53.21821331153359));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 20),6.562262691937566,53.21810807577438));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 30),6.562519588913838,53.21802380404714));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 40),6.563000337479954,53.21810084312677));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 50),6.563277317397496,53.21815088131894));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 1, 10),6.563535880585936,53.21820200711145));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 20),6.564019255037001,53.21828428361736));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 30),6.564400297003701,53.21834940991778));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 40),6.564873658042927,53.21848087018186));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 50),6.565294234825947,53.2186075638999));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 2, 10),6.565488376653141,53.21864389765787));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 20),6.566180495425835,53.21882959512244));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 30),6.567049864421188,53.21901839983541));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 40),6.567930136365805,53.21916998338647));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 50),6.568021242422264,53.21902875585243));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 3, 10),6.568213093043534,53.21908889163849));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 20),6.569336368126601,53.2193560220129));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 30),6.570022279162711,53.21947402925301));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 40),6.570661421647587,53.2196138021903));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 50),6.57150243145942,53.21983403796948));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 4, 10),6.572095379189276,53.22003209791963));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 20),6.572855292727631,53.22032404813394));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 30),6.573611028933685,53.22059546876568));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 40),6.57473940967445,53.22096970832911));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 50),6.57526347772091,53.22058131629242));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 5, 10),6.575796911962348,53.22027353425727));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 20),6.576056424039695,53.2203889809761));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 30),6.576521232629093,53.22058299915683));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 40),6.577214443727398,53.22085144176958));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 50),6.577957206648666,53.22101854667677));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 6, 10),6.578650790683751,53.22115588026267));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 20),6.578679525938567,53.22136591644339));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 30),6.578946860615663,53.2214287751361));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 40),6.579662072194124,53.22156040706825));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 50),6.580251021473289,53.2216754323734));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 7, 10),6.580549169884968,53.22177970629707));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 20),6.581115315130472,53.22198733965116));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 30),6.581457266037642,53.22210377374444));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 2, 20, 0, 40),6.581665986860847,53.2221310222725));

		//Antonis's house --> Zernike
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 3, 8, 55, 00),6.581488820305341,53.22213270792168));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 3, 8, 56, 00),6.577579995083863,53.22552840469385));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 3, 8, 57, 00),6.574591826728547,53.22784036135174));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 3, 8, 58, 00),6.571871150829535,53.22663277109498));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 3, 8, 59, 00),6.567368855555116,53.22987556991116));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 3, 10, 0, 00),6.563117326983258,53.23181708923012));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 3, 10, 1, 00),6.556498634745019,53.23349683336902));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 3, 10, 2, 00),6.547224687198532,53.23181271927296));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 3, 10, 3, 00),6.544038778011747,53.23656133442402));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 3, 10, 4, 00),6.538304754248243,53.23609450258314));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 3, 10, 5, 00),6.537356665221877,53.237973));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 3, 12, 5, 00),6.537356665221877,53.237975));
		MainActivity.routesList.add(new TrackedRoute(new Date(2014, 3, 3, 14, 5, 00),6.537356665221877,53.237977));

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
	    if(locManager != null){
		    locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
		    locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
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
