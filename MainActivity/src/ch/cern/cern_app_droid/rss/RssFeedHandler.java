package ch.cern.cern_app_droid.rss;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.horrabin.horrorss.RssFeed;
import org.horrabin.horrorss.RssItemBean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import ch.cern.cern_app_droid.Utils;
import ch.cern.cern_app_droid.rss.RssFeedRetriever.RssFeedRetrieverListener;
import ch.cern.cern_app_droid.rss.RssHandlerListener.RssHandlerError;

public class RssFeedHandler implements RssFeedRetrieverListener {
	
	private static final String TAG = "RssFeedHandler";
	private static final String READABILITY_SERVICE="";
	private static final String THUMBNAIL_SERVICE="";
	
	RssHandlerListener mListener;
	boolean mIsDownloadingFeed;
	boolean mIsDownloadingImages;
	String mFeedUrl;
	RssDataSource mDataSource;
	Context context;
	volatile private boolean cancel;
	
	private Map<String, Integer> mLinkToPositionMap;
	private Map<String, String> mImageLinkToItemLinkMap;
	private Map<String, Bitmap> bitmapCache;
	
	private RssFeedRetriever mRetriever;
	private DownloadImageTask mImagesDownloader;
	
	public void setRssHandlerListener(RssHandlerListener listener) {
		mListener = listener;
	}
	
	public RssFeedHandler(String url) {
		mIsDownloadingFeed = false;
		mIsDownloadingImages = false;
		cancel = false;
		mFeedUrl = url;
	}
	
	public boolean isWorking() {
		return (mIsDownloadingFeed || mIsDownloadingImages);
	}
	
	
	public void startWork(Context context) {
		mIsDownloadingFeed = true;
		this.context = context;
		
		mLinkToPositionMap = new HashMap<String, Integer>();
		bitmapCache = new HashMap<String, Bitmap>();
		mDataSource = new RssDataSource(context);
		
		if (mDataSource.hasItems(mFeedUrl)) {
			Log.d(TAG, "Database has feed items");
			mListener.onListUpdated( mDataSource.getAllItems(mFeedUrl));
		} else {
			Log.d(TAG, "Database has no feed items");
		}
		
		loadRss();
	}
	
	public void cancel() {
		cancel = true;
		if (mRetriever != null) {
			mRetriever.cancel(true);
		}
		if (mImagesDownloader != null) {
			mImagesDownloader.cancel(true);
		}
	}	
	
	private void loadRss() {
		if (!Utils.isNetworkAvailable(context)){
			mListener.onError(RssHandlerError.NO_CONNECTION);
			return;
		}
		mRetriever = new RssFeedRetriever(this, mFeedUrl);
		mRetriever.execute();
	}

	@Override
	public void onRssReadSuccessfuly(RssFeed result) {
		if (cancel)
			return;

		if ( result.getItems().size() > 0 ) {
			
			ArrayList<RssItem> items = new ArrayList<RssItem>();
			List<RssItemBean> readItems = result.getItems();

			int pos = 0;
			for (RssItemBean item : readItems) {
				items.add(new RssItem(item));
				mLinkToPositionMap.put(item.getLink(), pos);
				++pos;
			}

			queueImageJobs(items);
			
			mDataSource.replaceFeed(items, mFeedUrl);
			mListener.onListUpdated(items);
		}

	}

	@Override
	public void onRssReadError() {
		mIsDownloadingFeed = false;
		if (mListener != null) {
			mListener.onError(RssHandlerError.TIMEOUT);
		}
	}
	
	private void queueImageJobs(List<RssItem> items) {
		if (cancel)
			return;
		
		mImageLinkToItemLinkMap = new HashMap<String, String>();
		ArrayList<String> images = new ArrayList<String>();
		int i = -1;
		for (RssItem item : items) {
			++i;
			if (item.getMiniature() == null) {
				String imageUrl = getFirstImageLink(item.getDescription());
				if (imageUrl != null) {
					images.add(imageUrl);
					mLinkToPositionMap.put(imageUrl, i);
					mImageLinkToItemLinkMap.put(imageUrl, item.getLink());
				}
			}
		}
		mIsDownloadingFeed = (images.size() != 0);
		if (mIsDownloadingFeed) {
			mImagesDownloader = new DownloadImageTask();
			mImagesDownloader.execute(images.toArray(new String[] {}));
		}
	}
	
	private String getFirstImageLink(String description) {
		if (description == null) {
			return null;
		}
		Matcher m = Pattern.compile("<img.+?src=[\"'](.+?)[\"'].+?>", Pattern.CASE_INSENSITIVE).matcher(description);
		if (m != null && m.find()) {
			return m.group(1);
		} else {
			return null;
		}		
	}
	
	private class DownloadImageTask extends AsyncTask<String, String, Void> {
		
		@Override
		protected void onPreExecute() {
			mIsDownloadingImages = true;
		}
	    
	    @Override
	    protected Void doInBackground(String... urls) {
	    	for (String url : urls) {
	    		
	    		if (this.isCancelled())
	    			return null;
	    		
		        if (bitmapCache.containsKey(url)) {
		        	continue;
		        }
	
		        Bitmap bitmap = null;
		        try {
	        	
		            InputStream in = new java.net.URL(THUMBNAIL_SERVICE+url).openStream();
		            
		            BitmapFactory.Options options = new BitmapFactory.Options();
		            bitmap = BitmapFactory.decodeStream(in,null,options);
		            
		        } catch (Exception e) {
		            Log.e("Error", e.getMessage());
		            e.printStackTrace();
		        }
		        bitmapCache.put(url, bitmap);
		        this.publishProgress(url);
	    	}
	        return null;
	    }
	    
	    @Override
	    protected void onProgressUpdate(String... values) {
	    	if (cancel)
	    		return;
	    	String url = values[0];
	    	Bitmap image = bitmapCache.get(url);
	    	mDataSource.updateItem(mFeedUrl, mImageLinkToItemLinkMap.get(url), image, null);
//	    	Log.d(TAG, "pos: " + mLinkToPositionMap.get(url));
    		mListener.onImageDownloaded(mLinkToPositionMap.get(url), image);
	    }
	    
	    @Override
	    protected void onPostExecute(Void result) {
	    	mIsDownloadingImages = false;
	    }
	}	
}
