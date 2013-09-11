package ch.cern.cern_app_droid.rss;

import org.horrabin.horrorss.RssFeed;
import org.horrabin.horrorss.RssParser;

import android.os.AsyncTask;
import android.util.Log;

public class RssFeedHandler extends AsyncTask<Void, Integer, RssFeed> {

	private static String TAG = "RssFeedHandler";

	private RssReadListener mListener;
	private String mUrl;

	public RssFeedHandler(RssReadListener listener, String url) {
		mListener = listener;
		mUrl = url;
	}

	@Override
	protected RssFeed doInBackground(Void... params) {
		RssParser rss = new RssParser(mUrl);
		Log.d(TAG, "Getting feed from " + mUrl);
		try {
			RssFeed feed = rss.load();
			//ToDo: manually set http connection and lower timeout
			return feed;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(RssFeed result) {
		if (result == null) {
			mListener.onRssReadError();
		} else {
			mListener.onRssReadSuccessfuly(result);
		}
	}

}
