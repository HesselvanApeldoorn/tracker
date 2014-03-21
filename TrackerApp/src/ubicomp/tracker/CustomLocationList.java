package ubicomp.tracker;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CustomLocationList {
	ArrayList<CustomLocation> customLocations = new ArrayList<CustomLocation>();
	public enum LocationTypes { Home, Work, Sport, Store, Recreation, Other};

	public CustomLocationList() {
	};
	
	public void Overlap(CustomLocation currentLocation) {
		for(int i=0; i<this.customLocations.size(); i++) {
			if(currentLocation.Overlap(this.customLocations.get(i))) {
				this.customLocations.get(i).IncreaseNumberOfVisits();
			}
		}
	}

	public void loadFile(Context context) {
		FileInputStream fis;
		try {
			fis = context.openFileInput(MainActivity.savedLocations);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		    String line = null;
	    	CustomLocation newLocation = null;
		    while ((line = reader.readLine()) != null) {
		    	newLocation = readOneMarker(line);
		    	this.customLocations.add(newLocation);
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
	 * Creates CustomLocation from one line
	 * @param line comprises: title, radius, type, lat, lng, snippet
	 * @return newLocation: one location read from file
	 */
	private CustomLocation readOneMarker(String line) {
		MarkerOptions markerOptions = new MarkerOptions();
		String[] tokens = line.split(" ");
    	if(tokens.length!=6){throw new IllegalArgumentException();} // 6 values in total
    	String title = tokens[0];
    	int radius = Integer.parseInt(tokens[1]);
    	int type = Integer.parseInt(tokens[2]);
	   	Double latitude = Double.valueOf(tokens[3]);
	   	Double longitude = Double.valueOf(tokens[4]);
	   	String snippet = tokens[5];
    	
    	markerOptions.title(title);
    	markerOptions.position(new LatLng(latitude, longitude));
    	markerOptions.snippet(snippet);
    	
    	CustomLocation newLocation = new CustomLocation(markerOptions, radius, type);
    	
		return newLocation;
	}
	
	public void saveToFile(Context context) {
	    FileOutputStream fos;
		try {
			fos = context.openFileOutput(MainActivity.savedLocations, Context.MODE_PRIVATE);
	        OutputStreamWriter osw = new OutputStreamWriter(fos);
	        
	        String output = this.createString();
		    osw.append(output);
		    osw.close();
		    fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Create one string from all the markers
	 * @return all markers in a string;
	 */
	private String createString() {
		String output = "";
		for(CustomLocation loc: this.customLocations) {
			output+=loc +"\n";
		}
		return output;
	}
		
	/* ***** ArrayList methods ***** */
	public void add(CustomLocation newLocation) {
		this.customLocations.add(newLocation);
	}

	public int size() {
		return this.customLocations.size();
	}

	public void clear() {
		this.customLocations.clear();
	}
	public CustomLocation get(int index) {
		if (index > this.customLocations.size() - 1) {
			return null;
		} else {
			return this.customLocations.get(index);
		}
	}

	
}
