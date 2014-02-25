package ubicomp.tracker;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

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

	// Global variable to hold the current location
	android.location.Location mCurrentLocation;
	LocationClient mLocationClient;
	LocationRequest mLocationRequest;
	LocationManager locManager;
	private final String fileName = "savedLocations"; //[title, lat, lng, snippet]
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.mLocationClient = new LocationClient(this, this, this);
        this.mLocationRequest = LocationRequest.create();
        this.locManager = (LocationManager)getSystemService(
    	        Context.LOCATION_SERVICE);

        final LocationListener locLis = this;
        
		Switch tracking = (Switch) findViewById(R.id.switch_tracking);
        tracking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
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
		mLocationClient.connect();
	}

	/*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		mLocationClient.disconnect();
		super.onStop();
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    // TODO change 0, 0 parameters 
	    locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	    locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
	}
	

	/*************************************** DEFINE LOCATION SERVICES CALLBACKS ***************************************/

	@Override
	public void onConnected(Bundle dataBundle) {
		// Display the connection status
		
	}

	@Override
	public void onLocationChanged(android.location.Location location) {
	    FileOutputStream fos;
		try {
			fos = openFileOutput(this.fileName, Context.MODE_APPEND);
	        OutputStreamWriter osw = new OutputStreamWriter(fos);
			String output = "AutoTitle" + " " + location.getLatitude() + " " + location.getLongitude() + " " + "Latitude:" + location.getLatitude() + ",Longitude:" + location.getLongitude() + "\n";
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
