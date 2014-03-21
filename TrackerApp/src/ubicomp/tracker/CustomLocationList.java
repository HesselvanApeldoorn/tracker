package ubicomp.tracker;

import java.util.ArrayList;

public class CustomLocationList {
	ArrayList<CustomLocation> customLocations = new ArrayList<CustomLocation>();

	public CustomLocationList() {
	};

	public void add(CustomLocation newLocation) {
		this.customLocations.add(newLocation);
	}

	public int size() {
		return this.customLocations.size();
	}

	public CustomLocation get(int index) {
		if (index > this.customLocations.size() - 1) {
			return null;
		} else {
			return this.customLocations.get(index);
		}
	}
	
	public void Overlap(CustomLocation currentLocation) {
		for(int i=0; i<this.customLocations.size(); i++) {
			if(currentLocation.Overlap(customLocations.get(i))) {
				customLocations.get(i).IncreaseNumberOfVisits();
			}
		}
	}

	/*
	 * public void LoadLocations(String storedLocations) {
	 * 
	 * FileInputStream fis; BufferedReader reader = null; File file = new
	 * File(storedLocations); try {
	 * 
	 * reader = new BufferedReader(new FileReader(file)); String line = null;
	 * MarkerOptions markerOptions = null; while ((line = reader.readLine()) !=
	 * null) { markerOptions = readOneMarker(line); customLocations.add(new
	 * CustomLocation(markerOptions, _radius, _type)) } reader.close();
	 * 
	 * 
	 * } catch (FileNotFoundException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } catch (IOException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); } }
	 * 
	 * private MarkerOptions readOneMarker(String line) { MarkerOptions
	 * markerOptions = new MarkerOptions(); String[] tokens = line.split(" ");
	 * if(tokens.length!=4){throw new IllegalArgumentException();} // 4 values
	 * in total String title = tokens[0]; Double latitude =
	 * Double.valueOf(tokens[1]); Double longitude = Double.valueOf(tokens[2]);
	 * String snippet = tokens[3];
	 * 
	 * markerOptions.title(title); markerOptions.position(new LatLng(latitude,
	 * longitude)); markerOptions.snippet(snippet);
	 * 
	 * return markerOptions; }
	 */
}
