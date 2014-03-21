package ubicomp.tracker;

import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;

public class CustomLocation {

	public enum CustomLocationType {
		type1, type2, type3, typeX
	}

	private MarkerOptions markeroptions;
	private int radius;
	private int type;
	private int numberOfVisits;
	private long secondsSpent;

	public CustomLocation(MarkerOptions _marker, int _radius, int _type) {
		this.markeroptions = _marker;
		this.radius = _radius;
		this.type = _type;
		this.numberOfVisits = 0;
	}

	public void IncreaseNumberOfVisits() {
		this.numberOfVisits++;
	}

	public boolean Overlap(CustomLocation _location) {

		Location storedLocation = new Location("");
		storedLocation.setLatitude(this.markeroptions.getPosition().latitude);
		storedLocation.setLongitude(this.markeroptions.getPosition().longitude);

		Location currentLocation = new Location("");
		currentLocation
				.setLatitude(_location.markeroptions.getPosition().latitude);
		currentLocation
				.setLongitude(_location.markeroptions.getPosition().longitude);

		float distance = Math.abs(storedLocation.distanceTo(currentLocation));

		return (distance <= radius);
	}

	public int getRadius() {
		return this.radius;
	}
	
	public MarkerOptions getMarkerOptions() {
		return this.markeroptions;
	}

	@Override
	public String toString() {
		return  this.markeroptions.getTitle() + " "
				+ this.radius + " "
				+ this.type + " "
				+ this.markeroptions.getPosition().latitude + " " 
				+ this.markeroptions.getPosition().longitude + " "
				+ this.markeroptions.getSnippet();
	}

}
