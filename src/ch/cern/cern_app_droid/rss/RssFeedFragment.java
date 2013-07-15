package ch.cern.cern_app_droid.rss;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.horrabin.horrorss.RssFeed;
import org.horrabin.horrorss.RssItemBean;

import android.app.ListFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ch.cern.cern_app_droid.R;

public class RssFeedFragment extends ListFragment implements RssReadListener{
	
	private static final String TAG = "RssFeedFragment";
	
	String mUrl;
	ArrayList<RssItemBean> mItems;
	RssFeedAdapter mAdapter;
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Log.d(TAG, "onActivityCreated()");
		
		ArrayList<RssItemBean> l = new ArrayList<RssItemBean>();
		//ToDo: get old feed data from database
		mItems = new ArrayList<RssItemBean>();
		mAdapter = new RssFeedAdapter(getActivity(), 0, l);
		setListAdapter(mAdapter);
	}
	
	public void setUrl(String url) {
		mUrl = url;
		loadRss();
	}

	private void loadRss() {
		RssFeedHandler r = new RssFeedHandler(this, mUrl);
		r.execute();
	}

	@Override
	public void onRssReadSuccessfuly(RssFeed result) {
		mItems = new ArrayList<RssItemBean>();
		mItems.addAll(result.getItems());
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onRssReadError() {
				
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
				holder.img.setWillNotCacheDrawing(true);
				v.setTag(holder);
			}
			
			ViewHolder holder = (ViewHolder) v.getTag();
			holder.img.setVisibility(View.GONE);
			RssItemBean item = mItems.get(position);
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
					imageDownloadJobs.put(position, new DownloadImageTask(holder.img, position));
					imageDownloadJobs.get(position).execute(img);
					holder.img.setVisibility(View.INVISIBLE);
				} else {
					holder.img.setVisibility(View.GONE);
				}
			}
			return v;
		}
		
		class ViewHolder {
            TextView title;
            TextView desc;
            TextView date;
            ImageView img;
        }
		
		@Override
		public int getCount() {
			return mItems.size();
		}		
		
		private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		    ImageView mImage;
		    int mPos;

		    public DownloadImageTask(ImageView bmImage, int pos) {
		        this.mImage = bmImage;
		        this.mPos = pos;
		    }
		    
		    @Override
		    protected void onPreExecute() {
		    	mImage.setVisibility(View.INVISIBLE);
		    }
		    
		    @Override
		    protected Bitmap doInBackground(String... urls) {
		        String url = urls[0];
		        Bitmap bitmap = null;
		        try {
		        	if (isCancelled())
		            	return null;
		            InputStream in = new java.net.URL(url).openStream();
		            if (isCancelled())
		            	return null;
		            bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(in), 100, 100, true);
		            if (isCancelled())
		            	return null;
		            //ToDo: Scale properly
		            //ToDo: Write and use cache that saves images miniatures by their urls
		            
		        } catch (Exception e) {
		            Log.e("Error", e.getMessage());
		            e.printStackTrace();
		        }
		        return bitmap;
		    }
		    
		    @Override
		    protected void onPostExecute(Bitmap result) {
		        mImage.setImageBitmap(result);
		        mImage.setVisibility(View.VISIBLE);
		    }
		}
	}
}
