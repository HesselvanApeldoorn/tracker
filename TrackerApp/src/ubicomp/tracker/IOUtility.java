package ubicomp.tracker;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


import android.content.Context;


public class IOUtility {
	public void writeMarkers(String markersOutputFile, CustomLocationList locationlist, Context context) {
		FileOutputStream fos;
		try {
			fos = context.openFileOutput(MainActivity.savedLocations, Context.MODE_APPEND);
	        OutputStreamWriter osw = new OutputStreamWriter(fos);
	        StringBuilder output = new StringBuilder();
	        for(int i=0; i<locationlist.size(); i++) {
	        	output.append(locationlist.get(i).toString());
	        }
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
}
