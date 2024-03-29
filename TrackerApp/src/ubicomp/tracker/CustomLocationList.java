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
	
//	public void Overlap(CustomLocation currentLocation) {
//		for(CustomLocation loc: this.customLocations) {
//			if(currentLocation.Overlap(loc)) {
//				loc.IncreaseNumberOfVisits();
//			}
//		}
//	}

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
    	Boolean userDefined = Boolean.parseBoolean(tokens[5]);
    	markerOptions.title(title);
    	markerOptions.position(new LatLng(latitude, longitude));
    	
    	CustomLocation newLocation = new CustomLocation(markerOptions, radius, type);
    	newLocation.setUserDefined(userDefined);
    	
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
	 * Check if location already exists
	 * @param location: location to be checked
	 * @param radius: radius of location to be checked
	 * @param type: type of location to be checked
	 * @return returns true if the location exists, false otherwise
	 */
	public boolean exists(LatLng location, int radius, int type) {
		MarkerOptions options = new MarkerOptions();
		options.position(location);
		
		CustomLocation customLocation = new CustomLocation(options,radius,type); 
		for(CustomLocation loc: this.customLocations) {
			if(loc.getType()==type && (loc.getMarkerOptions().getPosition().equals(location) || loc.overlap(customLocation))) {
				return true; //A similar location is stored already: same type and same location within certain radius
			}
		}
		return false;
	}
	
	public ArrayList<CustomLocation> findLocationType(int type) {
		ArrayList<CustomLocation> foundLocations= new ArrayList<CustomLocation>();
		
		for(CustomLocation loc: this.customLocations) {
			if(type==loc.getType()) {
				foundLocations.add(loc);
			}
		}
		return foundLocations;
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
