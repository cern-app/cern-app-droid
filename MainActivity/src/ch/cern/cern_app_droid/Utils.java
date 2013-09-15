package ch.cern.cern_app_droid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Layout;
import android.text.style.LeadingMarginSpan.LeadingMarginSpan2;

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
