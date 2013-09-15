package ch.cern.cern_app_droid.rss;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.horrabin.horrorss.RssFeed;
import org.horrabin.horrorss.RssItemBean;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.renderscript.Mesh.TriangleMeshBuilder;
import android.util.Log;
import android.util.Pair;
import ch.cern.cern_app_droid.Utils;
import ch.cern.cern_app_droid.rss.RssFeedRetriever.RssFeedRetrieverListener;
import ch.cern.cern_app_droid.rss.RssHandlerListener.RssHandlerError;

public class RssFeedHandler implements RssFeedRetrieverListener {

	private static final String TAG = "RssFeedHandler";
	private static final String READABILITY_SERVICE = "";
	private static final String THUMBNAIL_SERVICE = "";

	RssHandlerListener mListener;
	boolean mIsDownloadingFeed;
	boolean mIsDownloadingImages;
	boolean mIsDownloadingDescriptions;
	String mFeedUrl;
	RssDataSource mDataSource;
	Context context;
	volatile private boolean cancel;

	private Map<String, Integer> mLinkToPositionMap;
	private Map<String, String> mImageLinkToItemLinkMap;
	private Map<String, Bitmap> bitmapCache;

	private RssFeedRetriever mRetriever;
	private DownloadImageTask mImagesDownloader;
	private DownloadReadability mReadabilityRetriever;

	public void setRssHandlerListener(RssHandlerListener listener) {
		mListener = listener;
	}

	public RssFeedHandler(String url) {
		mIsDownloadingFeed = false;
		mIsDownloadingImages = false;
		mIsDownloadingDescriptions = false;
		cancel = false;
		mFeedUrl = url;
	}

	public boolean isWorking() {
		return (mIsDownloadingFeed || mIsDownloadingImages || mIsDownloadingDescriptions);
	}

	public void startWork(Context context) {
		mIsDownloadingFeed = true;
		this.context = context;

		mLinkToPositionMap = new HashMap<String, Integer>();
		bitmapCache = new HashMap<String, Bitmap>();
		mDataSource = new RssDataSource(context);

		if (mDataSource.hasItems(mFeedUrl)) {
			Log.d(TAG, "Database has feed items");
			mListener.onListUpdated(mDataSource.getAllItems(mFeedUrl));
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
		if (mReadabilityRetriever != null) {
			mReadabilityRetriever.cancel(true);
		}
	}

	private void loadRss() {
		if (!Utils.isNetworkAvailable(context)) {
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

		if (result.getItems().size() > 0) {

			ArrayList<RssItem> items = new ArrayList<RssItem>();
			List<RssItemBean> readItems = result.getItems();
			ArrayList<String> miniatureUrls = new ArrayList<String>();

			int pos = 0;
			for (RssItemBean item : readItems) {
				items.add(new RssItem(item));
				miniatureUrls.add(getFirstImageLink(item.getDescription()));
				mLinkToPositionMap.put(item.getLink(), pos);
				++pos;
			}

			queueImageJobs(items, miniatureUrls);
			mDataSource.replaceFeed(items, mFeedUrl);
			mListener.onListUpdated(items);
			
			mReadabilityRetriever = new DownloadReadability();
			mReadabilityRetriever.execute(items);
		}
	}

	@Override
	public void onRssReadError() {
		mIsDownloadingFeed = false;
		if (mListener != null) {
			mListener.onError(RssHandlerError.TIMEOUT);
		}
	}

	private void queueImageJobs(List<RssItem> items, List<String> miniatureUrls) {
		if (cancel)
			return;

		mImageLinkToItemLinkMap = new HashMap<String, String>();
		ArrayList<String> images = new ArrayList<String>();
		int i = -1;
		for (RssItem item : items) {
			++i;
			if (item.getMiniature() == null) {
				String imageUrl = miniatureUrls.get(i);
				if (imageUrl != null) {
					images.add(imageUrl);
					mLinkToPositionMap.put(imageUrl, i);
					mImageLinkToItemLinkMap.put(imageUrl, item.getLink());
				}
			}
		}
		if (!cancel) {
			mImagesDownloader = new DownloadImageTask();
			mImagesDownloader.execute(images.toArray(new String[] {}));
		}
	}

	private String getFirstImageLink(String description) {
		if (description == null) {
			return null;
		}
		Matcher m = Pattern.compile("<img.+?src=[\"'](.+?)[\"'].+?>",
				Pattern.CASE_INSENSITIVE).matcher(description);
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
			Log.d(TAG, "Urls length: " + urls.length);
			for (String url : urls) {

				if (this.isCancelled())
					return null;

				if (bitmapCache.containsKey(url)) {
					continue;
				}

				Bitmap bitmap = null;
				try {

					InputStream in = new java.net.URL(THUMBNAIL_SERVICE + url)
							.openStream();

					BitmapFactory.Options options = new BitmapFactory.Options();
					bitmap = BitmapFactory.decodeStream(in, null, options);

				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
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
			mDataSource.updateItem(mFeedUrl, mImageLinkToItemLinkMap.get(url),
					image, null);
			mListener.onImageDownloaded(mLinkToPositionMap.get(url), image);
		}

		@Override
		protected void onPostExecute(Void result) {
			mIsDownloadingImages = false;
		}
	}

	private class DownloadReadability extends
			AsyncTask<List<RssItem>, Pair<Integer, String>, Void> {

		private String getContent(String url) {
			try {
				URL u = new URL(READABILITY_SERVICE + url);
				URLConnection urlConnection = u.openConnection();
				urlConnection.setConnectTimeout(1000);
				InputStream is = urlConnection.getInputStream();
				char[] buffer = new char[1024];
				Writer writer = new StringWriter();
				try {
					Reader reader = new BufferedReader(new InputStreamReader(
							is, "UTF-8"));
					int n;
					while ((n = reader.read(buffer)) != -1) {
						writer.write(buffer, 0, n);
					}
				} finally {
					is.close();
				}
				JSONObject j = new JSONObject(writer.toString());
				return j.getString("content");

			} catch (Exception ex) {
				Log.d(TAG, "Readability exception", ex);
				return "";
			}
		}

		@Override
		protected Void doInBackground(List<RssItem>... params) {
			Log.d(TAG, "Readability do in background");
			if (READABILITY_SERVICE.isEmpty()) {
				Log.d(TAG, "No readability service available");
				return null;
			}
			List<RssItem> list = params[0];
			int size = list.size();
			RssItem item;
			for (int i = 0; i < size; i++) {
				if (this.isCancelled()) {
					return null;
				}
				item = list.get(i);
				if (!item.getDescription().isEmpty()) {
					continue;
				}
				String link = item.getLink();
				String simple = getContent(link);
				if (simple.isEmpty()) {
					continue;
				}
				simple = simple.replaceAll("<script.*?>.*?</script>", "")
						.replaceAll("\\<.*?\\>", "")
						.replaceAll("\\(.*?\\)", "")
						.replaceAll("[\r\n]+", "\r\n").trim();
				simple = StringUtils.normalizeSpace(simple);
				simple = StringEscapeUtils.unescapeHtml(simple);
				mDataSource.updateItemDescription(mFeedUrl, link, simple);
				publishProgress(new Pair<Integer, String>(i, simple));
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Pair<Integer, String>... values) {
			mListener.onDescriptionUpdated(values[0].first, values[0].second);
		}

	}

	public List<RssItem> getItemsFromDatabase() {
		return mDataSource.getAllItems(mFeedUrl);
	}
}
