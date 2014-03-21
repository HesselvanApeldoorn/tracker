package ubicomp.tracker;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;

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

	public static final String savedLocations = "savedLocations"; //[title, lat, lng, snippet]
	public static final String savedRoutes = "savedRoutes"; //[lat, lng]
	public static final String dateFormat = "yyyy-MM-dd-HH-mm-ss"; //format used for storing date and time
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.createResourceStubs();
        
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
		
	    FileOutputStream fos;
	    String dateString = new SimpleDateFormat(MainActivity.dateFormat,Locale.US).format(new Date()).toString();
		try {
			fos = openFileOutput(MainActivity.savedRoutes, Context.MODE_APPEND);
	        OutputStreamWriter osw = new OutputStreamWriter(fos);
			String output = dateString + " " + location.getLatitude() + " " + location.getLongitude() + "\n";
			Toast.makeText(getApplicationContext(), dateString, Toast.LENGTH_LONG).show();
		    osw.write(output);
		    osw.flush();
		    osw.close();
		    fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Toast.makeText(getApplicationContext(), "Updated location " + location.getLatitude(), Toast.LENGTH_SHORT).show();		
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
