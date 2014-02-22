package ubicomp.tracker;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;

import ubicomp.tracker.R;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import android.widget.TextView;
import android.widget.Toast;

public class Location extends BaseMenu implements
	ConnectionCallbacks, OnConnectionFailedListener, LocationListener
{

	// Global variable to hold the current location
	android.location.Location mCurrentLocation;
	LocationClient mLocationClient;
	LocationRequest mLocationRequest;
	private final String fileName = "savedLocations"; //[title, lat, lng, snippet]

	/*************************************** CONNECT LOCATION CLIENT ***************************************/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location);
		mLocationClient = new LocationClient(this, this, this);
        mLocationRequest = LocationRequest.create();

		 // Use high accuracy
        mLocationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(1);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(1);
	}

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

	    Toast.makeText(this, "GPS tracking started",
	        Toast.LENGTH_SHORT).show();

	 // Start location updates; 5s/5m
	    LocationManager locManager = (LocationManager)getSystemService(
	        Context.LOCATION_SERVICE);
	    locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
	        0, 0, this);

	}
	

	/*************************************** DEFINE LOCATION SERVICES CALLBACKS ***************************************/

	@Override
	public void onConnected(Bundle dataBundle) {
		// Display the connection status
		TextView text = (TextView) findViewById(R.id.textView1);
		text.setText("Connected! Latitude: "
				+ mLocationClient.getLastLocation().getLatitude()
				+ ", Longtitude: "
				+ mLocationClient.getLastLocation().getLongitude());
	}

	@Override
	public void onLocationChanged(android.location.Location location) {
//		MarkerOptions markerOptions = new MarkerOptions();
//		markerOptions.position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
//		markerOptions.title("nothing");
		DecimalFormat df = new DecimalFormat("#.###");
//		//XXX Spaces in snippet cannot be removed, because of the way we are saving/loading for now
//		markerOptions.snippet("Latitude:" + df.format(mCurrentLocation.getLatitude()) + ",Longitude:" + df.format(mCurrentLocation.getLongitude()));
	    FileOutputStream fos;
		try {
			fos = openFileOutput(this.fileName, Context.MODE_APPEND);
	        OutputStreamWriter osw = new OutputStreamWriter(fos);
			String output = "nothing" + " " + df.format(mCurrentLocation.getLatitude()) + " " + df.format(mCurrentLocation.getLongitude()) + " " + "crap" + "\n";
		    osw.write(output);
		    osw.flush();
		    osw.close();
		    fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String loc = "Updated! Latitude: "
				+ location.getLatitude()
				+ ", Longtitude: "
				+ location.getLongitude();
		TextView text = (TextView) findViewById(R.id.textView1);
//		View lv = findViewById(R.layout.location);
		text.setText(loc);
		text.invalidate();
		
		Toast.makeText(getApplicationContext(), "Updated location", Toast.LENGTH_SHORT).show();		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
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
			showErrorDialog(connectionResult.getErrorCode());
		}
	}

	// Added this method, else can't show error code.
	void showErrorDialog(int code) {
		GooglePlayServicesUtil.getErrorDialog(code, this, 0).show();
	}

	public void openMainActivity(View view) {
		Intent mainActivity_screen = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(mainActivity_screen);
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



//	private boolean servicesConnected() {
//		// Check that Google Play services is available
//		int resultCode = GooglePlayServicesUtil
//				.isGooglePlayServicesAvailable(this);
//		// If Google Play services is available
//		if (ConnectionResult.SUCCESS == resultCode) {
//			// In debug mode, log the status
//			Log.d("Location Updates", "Google Play services is available.");
//			// Continue
//			return true;
//			// Google Play services was not available for some reason
//		} else {
//			// Get the error code
//			int errorCode = 0;// connectionResult.getErrorCode();
//			// Get the error dialog from Google Play services
//			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
//					errorCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
//
//			// If Google Play services can provide an error dialog
//			if (errorDialog != null) {
//				// Create a new DialogFragment for the error dialog
//				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
//				// Set the dialog in the DialogFragment
//				errorFragment.setDialog(errorDialog);
//				// Show the error dialog in the DialogFragment
//				errorFragment.show(getSupportFragmentManager(),
//						"Location Updates");
//			}
//			return false;
//		}
//	}

}