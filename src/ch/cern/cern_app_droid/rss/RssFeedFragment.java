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
import ch.cern.cern_app_droid.R;

public class RssFeedFragment extends ListFragment implements RssReadListener, OnItemClickListener{
	
	private static final String TAG = "RssFeedFragment";
	
	String mUrl;
//	ArrayList<RssItemBean> mItems;
	RssFeedAdapter mAdapter;
	RssDataSource mDataSource;
	private Map<String, Bitmap> bitmapCache;
	
	public RssFeedFragment() {
		super();
		bitmapCache = new HashMap<String, Bitmap>();
	}
	
	public void setUrl(String url) {
		mUrl = url;
//		loadRss();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getListView().setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String url = mAdapter.getItem(position).getLink();
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browserIntent);
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
			mAdapter = new RssFeedAdapter(activity, 0,	new ArrayList<RssItemBean>());
		}
		
		setListAdapter(mAdapter);
		loadRss();
	}
	

	private void loadRss() {
		getActivity().setProgressBarIndeterminateVisibility(true);
		RssFeedHandler r = new RssFeedHandler(this, mUrl);
		r.execute();
	}

	@Override
	public void onRssReadSuccessfuly(RssFeed result) {
		if (mAdapter != null) {
			
			ArrayList<RssItemBean> items = new ArrayList<RssItemBean>();
			items.addAll(result.getItems());
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
	
	private class RssFeedAdapter extends ArrayAdapter<RssItemBean> {
		
		private SparseArray<DownloadImageTask> imageDownloadJobs;

		public RssFeedAdapter(Context context, int textViewResourceId,
				List<RssItemBean> objects) {
			super(context, textViewResourceId, objects);
			imageDownloadJobs = new SparseArray<RssFeedFragment.RssFeedAdapter.DownloadImageTask>();
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
//			RssItemBean item = mItems.get(position);
			RssItemBean item = getItem(position);
			holder.title.setText(item.getTitle());
			holder.desc.setText(item.getLink());
			holder.date.setText(item.getPubDate().toLocaleString());
			
			String desc = item.getDescription();
			if (desc != null) {
				Matcher m = Pattern.compile("<img.+?src=[\"'](.+?)[\"'].+?>", Pattern.CASE_INSENSITIVE).matcher(desc);
				if (m.find()) {
					String img = m.group(1);
					if (imageDownloadJobs.get(position) != null) {
						imageDownloadJobs.get(position).cancel(true);
						imageDownloadJobs.delete(position);
					}
					imageDownloadJobs.put(position, new DownloadImageTask(holder.img, Long.valueOf(holder.img.getId())));
					imageDownloadJobs.get(position).execute(img);
					holder.img.setVisibility(View.INVISIBLE);
				} else {
					holder.img.setVisibility(View.GONE);
				}
			}
			return v;
		}
		
		//smart hack, class holding pointers to views, kept in convertedView
		//so that there's no need to search for widgets every time 
		class ViewHolder {
            TextView title;
            TextView desc;
            TextView date;
            ImageView img;
        }
		
		private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		    ImageView mImage;
		    long mTag;
		    String temp;

		    public DownloadImageTask(ImageView bmImage, long tag) {
		        this.mImage = bmImage;
		        this.mTag = tag;
		    }
		    
		    @Override
		    protected void onPreExecute() {
		    	mImage.setVisibility(View.INVISIBLE);
		    	mImage.setImageDrawable(null);
		    }
		    
		    @Override
		    protected Bitmap doInBackground(String... urls) {
		        String url = urls[0];
		        if (bitmapCache.containsKey(url)) {
		        	return bitmapCache.get(url);
		        }
		        if (mTag == 0) {
		        	Log.d(TAG, "doInBackground: " + url);
		        }
		        temp = url;
		        Bitmap bitmap = null;
		        try {

		            InputStream in = new java.net.URL(url).openStream();
//		            bitmap = BitmapFactory.decodeStream(in);
//		            bitmap = scaleBitmap(bitmap);
		            
		            BitmapFactory.Options options=new BitmapFactory.Options();
		            options.inSampleSize = 8;
		            bitmap = BitmapFactory.decodeStream(in,null,options);
		            
//		            bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(in), 100, 100, true);

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
		    	
		        mImage.setImageBitmap(result);
		        mImage.setVisibility(View.VISIBLE);
		        if (mTag == 0)
		        	Log.d(TAG, "setting image [ " + temp +" ]");
	    	
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

	
}
