package ch.cern.cern_app_droid.rss;

import java.util.List;

import android.graphics.Bitmap;

public interface RssHandlerListener {

	public static enum RssHandlerError { NO_CONNECTION, TIMEOUT}
	
	public void onListUpdated(List<RssItem> items);
	public void onError(RssHandlerError error);
	public void onImageDownloaded(int position, Bitmap image);
	public void onDescriptionUpdated(int position, String newDescription);

	
}
