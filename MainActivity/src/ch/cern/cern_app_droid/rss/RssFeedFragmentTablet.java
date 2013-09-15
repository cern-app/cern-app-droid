package ch.cern.cern_app_droid.rss;

import java.util.ArrayList;
import java.util.List;

import com.triposo.barone.EllipsizingTextView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import ch.cern.cern_app_droid.R;

public class RssFeedFragmentTablet extends Fragment implements RssHandlerListener {

	private static String TAG = "RssFeedFragmentTablet";

	String mUrl;
	PagerAdapter mAdapter;
	RssFeedHandler mRssFeedHandler;	
	List<RssItem> mItems;
	
	public RssFeedFragmentTablet() {
		mItems = new ArrayList<RssItem>();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		Log.d(TAG, "onAttach(); mUrl = " + mUrl);
		
		mAdapter = new RssFeedAdapterTablet(activity.getLayoutInflater());

//		setListAdapter(mAdapter);
		
		mUrl = getArguments().getString(RssHelper.URL);
		mRssFeedHandler = new RssFeedHandler(mUrl);
		mRssFeedHandler.setRssHandlerListener(this);
		mRssFeedHandler.startWork(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewPager vPager = (ViewPager) inflater.inflate(R.layout.rss_layout_tablet, null);
		vPager.setAdapter(mAdapter);
		return vPager;
	}

	private class RssFeedAdapterTablet extends PagerAdapter {

		LayoutInflater inflater;
		ArrayList<View> mConvertedViews;
		
		//
		public RssFeedAdapterTablet(LayoutInflater inflater) {
			this.inflater = inflater;
			mConvertedViews = new ArrayList<View>();
		}
		
		@Override
		public int getCount() {
			int additional = mItems.size() % 4 == 0 ? 0 : 1;
			return ((mItems.size() / 4) + additional);
		}
		
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return (view == ((View) object));
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			mConvertedViews.add((View) object);
			container.removeView((View) object);
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View v;
			if ( mConvertedViews.isEmpty() ) {
				v = inflater.inflate(R.layout.rss_item_tablet, null);
				ViewHolder holder = new ViewHolder();
				holder.titleLT = (TextView) v.findViewById(R.id.rss_item_tablet_top_left).findViewById(R.id.rss_tablet_cell_Title);
				holder.descLT = (EllipsizingTextView)  v.findViewById(R.id.rss_item_tablet_top_left).findViewById(R.id.rss_tablet_cell_webView);
				holder.dateLT = (TextView)  v.findViewById(R.id.rss_item_tablet_top_left).findViewById(R.id.rss_tablet_cell_Time);
//				holder.imgLT = (ImageView)  v.findViewById(R.id.rss_item_tablet_top_left).findViewById(R.id.rss_tablet_cell_Image);

				holder.titleRT = (TextView) v.findViewById(R.id.rss_item_tablet_top_right).findViewById(R.id.rss_tablet_cell_Title);
				holder. descRT = (EllipsizingTextView)  v.findViewById(R.id.rss_item_tablet_top_right).findViewById(R.id.rss_tablet_cell_webView);
				holder. dateRT = (TextView)  v.findViewById(R.id.rss_item_tablet_top_right).findViewById(R.id.rss_tablet_cell_Time);
//				holder.  imgRT = (ImageView)  v.findViewById(R.id.rss_item_tablet_top_right).findViewById(R.id.rss_tablet_cell_Image);

				holder.titleLB = (TextView) v.findViewById(R.id.rss_item_tablet_bottom_left).findViewById(R.id.rss_tablet_cell_Title);
				holder. descLB = (EllipsizingTextView)  v.findViewById(R.id.rss_item_tablet_bottom_left).findViewById(R.id.rss_tablet_cell_webView);
				holder. dateLB = (TextView)  v.findViewById(R.id.rss_item_tablet_bottom_left).findViewById(R.id.rss_tablet_cell_Time);
//				holder.  imgLB = (ImageView)  v.findViewById(R.id.rss_item_tablet_bottom_left).findViewById(R.id.rss_tablet_cell_Image);

				holder.titleRB = (TextView) v.findViewById(R.id.rss_item_tablet_bottom_right).findViewById(R.id.rss_tablet_cell_Title);
				holder. descRB = (EllipsizingTextView)  v.findViewById(R.id.rss_item_tablet_bottom_right).findViewById(R.id.rss_tablet_cell_webView);
				holder. dateRB = (TextView)  v.findViewById(R.id.rss_item_tablet_bottom_right).findViewById(R.id.rss_tablet_cell_Time);
//				holder.  imgRB = (ImageView)  v.findViewById(R.id.rss_item_tablet_bottom_right).findViewById(R.id.rss_tablet_cell_Image);
				
				v.setTag(holder);
			} else {
				v = mConvertedViews.remove(0);
			}
			
			ViewHolder holder = (ViewHolder) v.getTag();
			RssItem item1, item2, item3, item4;
			item1 = mItems.get(4*position);
			fillView(item1, holder.titleLT, holder.descLT, holder.dateLT); // , holder.imgLT);
			Log.d(TAG, "item no " + (4*position) + "desc: " + item1.getDescription());
			if (4*position + 1 < mItems.size()) {
				item2 = mItems.get(4*position+1);
				v.findViewById(R.id.rss_item_tablet_top_right).setVisibility(View.VISIBLE);
				fillView(item2, holder.titleRT, holder.descRT, holder.dateRT); // , holder.imgRT);
			} else {
				v.findViewById(R.id.rss_item_tablet_top_right).setVisibility(View.INVISIBLE);
			}
			if (4*position + 2 < mItems.size()) {
				item3 = mItems.get(4*position+2);
				v.findViewById(R.id.rss_item_tablet_bottom_left).setVisibility(View.VISIBLE);
				fillView(item3, holder.titleLB, holder.descLB, holder.dateLB); // , holder.imgLB);
			} else {
				v.findViewById(R.id.rss_item_tablet_bottom_left).setVisibility(View.INVISIBLE);
			}
			if (4*position + 3 < mItems.size()) {
				item4 = mItems.get(4*position+3);
				v.findViewById(R.id.rss_item_tablet_bottom_right).setVisibility(View.VISIBLE);
				fillView(item4, holder.titleRB, holder.descRB, holder.dateRB); // , holder.imgRB);
			} else {
				v.findViewById(R.id.rss_item_tablet_bottom_right).setVisibility(View.INVISIBLE);
			}			
			container.addView(v);
			return v;
		}
		
		private void fillView(RssItem item, TextView titleV, EllipsizingTextView descV, TextView dateV) { //, ImageView imageV) {
//			int descLines = (descV.getHeight() - descV.getPaddingTop() - descV.getPaddingBottom()) / descV.getLineHeight();
			
			titleV.setText(item.getTitle());
			descV.setText(item.getDescription());//, "text/html; charset=UTF-8", null); //ToDo: Add readability			
			dateV.setText(item.getPubDate().toLocaleString());
		}
		 class ViewHolder {
			 TextView titleLT; TextView titleRT; TextView titleLB; TextView titleRB;
			 EllipsizingTextView descLT; EllipsizingTextView descRT; EllipsizingTextView descLB; EllipsizingTextView descRB; 
			 TextView dateLT; TextView dateRT; TextView dateLB; TextView dateRB;
			 ImageView imgLT; ImageView imgRT; ImageView imgLB; ImageView imgRB;
		 }
		// }

	}

	@Override
	public void onListUpdated(List<RssItem> items) {
		mItems = items;
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDescriptionUpdated(int position, String newDescription) {
		mItems.get(position).setDescription(newDescription);
		mAdapter.notifyDataSetChanged();		
	}
	
	@Override
	public void onError(RssHandlerError error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onImageDownloaded(int position, Bitmap image) {
		if (isVisible() && mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}
	
}
