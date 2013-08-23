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

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import ch.cern.cern_app_droid.R;

public class RssFeedFragment extends ListFragment implements RssReadListener, OnItemClickListener{
	
	private static final String TAG = "RssFeedFragment";
	
	String mUrl;
	RssFeedAdapter mAdapter;
	RssDataSource mDataSource;
	private Map<String, Bitmap> bitmapCache;
	private Map<String, Integer> mLinkToPositionMap;
	
	public RssFeedFragment() {
		super();
		bitmapCache = new HashMap<String, Bitmap>();
		mLinkToPositionMap = new HashMap<String, Integer>();
	}
	
	public void setUrl(String url) {
		mUrl = url;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getListView().setOnItemClickListener(this);
	} 

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (isNetworkAvailable()) {
			String url = mAdapter.getItem(position).getLink();
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(browserIntent);
		} else {
			FeedItemWebView webViewFragment = new FeedItemWebView();
			webViewFragment.setContent(mAdapter.getItem(position));
//			getFragmentManager().beginTransaction().add(R.id.main_contentFrame, webViewFragment).show(webViewFragment).commit();
			getFragmentManager().beginTransaction().replace(R.id.main_contentFrame, webViewFragment).addToBackStack("view").commit();
		}
	}
	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d(TAG, "onAttach(); mUrl = " + mUrl);
		
		mDataSource = new RssDataSource(activity);
		
		if (mDataSource.hasItems(mUrl)) {
			Log.d(TAG, "Database has feed items");
			mAdapter = new RssFeedAdapter(activity, 0, mDataSource.getAllItems(mUrl));
		} else {
			Log.d(TAG, "Database has no feed items");
			mAdapter = new RssFeedAdapter(activity, 0,	new ArrayList<RssItem>());
		}
		
		setListAdapter(mAdapter);
		loadRss();
	}
	
	private void loadRss() {
		if (!isNetworkAvailable()){
			//Activity should be always attached at this point
			Toast.makeText(getActivity() ,"No internet connection", Toast.LENGTH_SHORT).show();
			return;
		}
		getActivity().setProgressBarIndeterminateVisibility(true);
		RssFeedHandler r = new RssFeedHandler(this, mUrl);
		r.execute();
	}

	@Override
	public void onRssReadSuccessfuly(RssFeed result) {
		if (!isVisible()) {
			return;
		}
		if (mAdapter != null && result.getItems().size() > 0) {
			
			ArrayList<RssItem> items = new ArrayList<RssItem>();
			List<RssItemBean> readItems = result.getItems();
			int pos = 0;
			for (RssItemBean item : readItems) {
				items.add(new RssItem(item));
				mLinkToPositionMap.put(item.getLink(), pos);
				++pos;
			}
			queueImageJobs(items);
			mAdapter.clear();
			mAdapter.addAll(items);

			mAdapter.notifyDataSetChanged();
			getActivity().setProgressBarIndeterminateVisibility(false);

			mDataSource.replaceFeed(items, mUrl);
		}
		getActivity().setProgressBarIndeterminateVisibility(false);
	}

	@Override
	public void onRssReadError() {
		getActivity().setProgressBarIndeterminateVisibility(false);
	}

	private void queueImageJobs(List<RssItem> items) {

		for (RssItem item : items) {
			if (item.getMiniature() == null) {
				String imageUrl = getFirstImageLink(item.getDescription());
				if (imageUrl == null) {
					continue;
				} else {
					DownloadImageTask task = new DownloadImageTask(item.getLink());
					task.execute(imageUrl);
				}
			}
		}
	}
	
	private void notifyMiniatureDownloaded(String itemLink, Bitmap miniature) {
		mDataSource.updateItem(mUrl, itemLink, miniature, null);
		Integer pos = mLinkToPositionMap.get(itemLink);
		if (pos != null) {
			mAdapter.getItem(pos).setMiniature(miniature);
			int firstVisible = getListView().getFirstVisiblePosition();
			if (pos >= firstVisible && pos <= firstVisible+getListView().getChildCount()) {
				mAdapter.notifyDataSetChanged();
			}
		} else {
			Log.d(TAG, "Something's wrong: no mapping from url to item id [" + itemLink + "]");
		}
	}
	
	private class RssFeedAdapter extends ArrayAdapter<RssItem> {
		
		public RssFeedAdapter(Context context, int textViewResourceId,
				List<RssItem> objects) {
			super(context, textViewResourceId, objects);
		}
		
		@Override
		public View getView(int position, View v, ViewGroup parent) {
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) getContext()
				        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.rss_item, null);
				ViewHolder holder = new ViewHolder();
				holder.title = (TextView) v.findViewById(R.id.rss_row_Title);
				holder.desc = (TextView) v.findViewById(R.id.rss_row_Description);
				holder.date = (TextView) v.findViewById(R.id.rss_row_Time);
				holder.img = (ImageView) v.findViewById(R.id.rss_row_Image);
				v.setTag(holder);
			}
			
			if (position == 0) {
				Log.d(TAG, "getView");
			}
			
			ViewHolder holder = (ViewHolder) v.getTag();
			holder.img.setVisibility(View.GONE);
			RssItem item = getItem(position);
			holder.title.setText(item.getTitle());
			holder.desc.setText(item.getLink());
			holder.date.setText(item.getPubDate().toLocaleString());
			if (item.getMiniature() != null) {
				holder.img.setImageBitmap(item.getMiniature());
				holder.img.setVisibility(View.VISIBLE);
			} else {				
				holder.img.setVisibility(View.GONE);
			}
			
			return v;
		}
		
		// typical hack, class holding pointers to views, kept in convertedView
		// so that there's no need to search for widgets every time 
		class ViewHolder {
            TextView title;
            TextView desc;
            TextView date;
            ImageView img;
        }
	}
	
	private boolean isNetworkAvailable() {
		Context context = getActivity();
		
		if (context == null)
			return false;
		
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

		String mFeedItemLink;
		
		public DownloadImageTask(String feedItemLink) {
			mFeedItemLink = feedItemLink;
		}
	    
	    @Override
	    protected Bitmap doInBackground(String... urls) {
	        String url = urls[0];
	        if (bitmapCache.containsKey(url)) {
	        	return bitmapCache.get(url);
	        }

	        Bitmap bitmap = null;
	        try {

	            InputStream in = new java.net.URL(url).openStream();
//	            bitmap = BitmapFactory.decodeStream(in);
//	            bitmap = scaleBitmap(bitmap);
	            
	            BitmapFactory.Options options = new BitmapFactory.Options();
	            options.inSampleSize = 8;
	            bitmap = BitmapFactory.decodeStream(in,null,options);
	            
//	            bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(in), 100, 100, true);

	            //ToDo: Should be scaled differently maybe?
	            
	        } catch (Exception e) {
	            Log.e("Error", e.getMessage());
	            e.printStackTrace();
	        }
	        bitmapCache.put(url, bitmap);
	        return bitmap;
	    }
	    
	    @Override
	    protected void onPostExecute(Bitmap result) {
	    	notifyMiniatureDownloaded(mFeedItemLink, result);
	    }
	    
	    private Bitmap scaleBitmap(Bitmap b) {
	    	int targetHeight = 100;
	    	int targetWidth = 100;
	    	
			int currHeight = b.getHeight();
			int currWidth = b.getWidth();

			int sampleSize = 1;
			if (currHeight > targetHeight || currWidth > targetWidth) {
				if (currWidth > currHeight) {
					sampleSize = Math.round((float) currHeight
							/ (float) targetHeight);
				} else {
					sampleSize = Math.round((float) currWidth
							/ (float) targetWidth);
				}
			}
			
			BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
			bmpOptions.inSampleSize = sampleSize;
			bmpOptions.inJustDecodeBounds = false;
			return null;
	    }
	}
}
