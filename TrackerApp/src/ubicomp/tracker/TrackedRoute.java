package ubicomp.tracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLng;

public class TrackedRoute {

	private Date date;
	private LatLng location;
	
	public TrackedRoute(Date date, LatLng location) {
		this.date = date;
		this.location = location;
	}

	public TrackedRoute(Date date, double lon, double lat) {
		this.date = date;
		this.location = new LatLng(lat,lon);
	}
	
	public LatLng getLocation() {
	    return this.location;
    }

	public void setLocation(LatLng location) {
	    this.location = location;
    }

	public Date getDate() {
	    return this.date;
    }

	public void setDate(Date date) {
	    this.date = date;
    }

	@Override
	public String toString() {
	    String dateString = new SimpleDateFormat(MainActivity.dateFormat,Locale.US).format(this.date).toString();
		return dateString + " " + this.location.latitude + " " + this.location.longitude;
	}
	
}
