package ch.cern.cern_app_droid;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

	public static boolean isNetworkAvailable(Context context) {
		
		if (context == null) {
			return false;
		}		
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    if (connectivityManager == null) {
	    	return false;
	    }
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
	}
		
}
