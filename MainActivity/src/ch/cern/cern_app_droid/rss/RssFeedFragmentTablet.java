package ch.cern.cern_app_droid.rss;

import java.util.ArrayList;
import java.util.List;

import com.triposo.barone.EllipsizingTextView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import ch.cern.cern_app_droid.MyLeadingMarginSpan2;
import ch.cern.cern_app_droid.R;
import ch.cern.cern_app_droid.Utils;

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
		public int getItemPosition(Object object) {
			return POSITION_NONE;
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
				holder.descLT = (TextView)  v.findViewById(R.id.rss_item_tablet_top_left).findViewById(R.id.rss_tablet_cell_Description);
				holder.dateLT = (TextView)  v.findViewById(R.id.rss_item_tablet_top_left).findViewById(R.id.rss_tablet_cell_Time);
				holder.imgLT = (ImageView)  v.findViewById(R.id.rss_item_tablet_top_left).findViewById(R.id.rss_tablet_cell_Image);

				holder.titleRT = (TextView) v.findViewById(R.id.rss_item_tablet_top_right).findViewById(R.id.rss_tablet_cell_Title);
				holder. descRT = (TextView)  v.findViewById(R.id.rss_item_tablet_top_right).findViewById(R.id.rss_tablet_cell_Description);
				holder. dateRT = (TextView)  v.findViewById(R.id.rss_item_tablet_top_right).findViewById(R.id.rss_tablet_cell_Time);
				holder.  imgRT = (ImageView)  v.findViewById(R.id.rss_item_tablet_top_right).findViewById(R.id.rss_tablet_cell_Image);

				holder.titleLB = (TextView) v.findViewById(R.id.rss_item_tablet_bottom_left).findViewById(R.id.rss_tablet_cell_Title);
				holder. descLB = (TextView)  v.findViewById(R.id.rss_item_tablet_bottom_left).findViewById(R.id.rss_tablet_cell_Description);
				holder. dateLB = (TextView)  v.findViewById(R.id.rss_item_tablet_bottom_left).findViewById(R.id.rss_tablet_cell_Time);
				holder.  imgLB = (ImageView)  v.findViewById(R.id.rss_item_tablet_bottom_left).findViewById(R.id.rss_tablet_cell_Image);

				holder.titleRB = (TextView) v.findViewById(R.id.rss_item_tablet_bottom_right).findViewById(R.id.rss_tablet_cell_Title);
				holder. descRB = (TextView)  v.findViewById(R.id.rss_item_tablet_bottom_right).findViewById(R.id.rss_tablet_cell_Description);
				holder. dateRB = (TextView)  v.findViewById(R.id.rss_item_tablet_bottom_right).findViewById(R.id.rss_tablet_cell_Time);
				holder.  imgRB = (ImageView)  v.findViewById(R.id.rss_item_tablet_bottom_right).findViewById(R.id.rss_tablet_cell_Image);
				
				v.setTag(holder);
			} else {
				v = mConvertedViews.remove(0);
			}
		
			
			ViewHolder holder = (ViewHolder) v.getTag();
			View cell;
			RssItem item1, item2, item3, item4;
			item1 = mItems.get(4*position);
			cell = v.findViewById(R.id.rss_item_tablet_top_left);
			if (item1.getDescription().isEmpty()) {
				v.findViewById(R.id.rss_item_tablet_top_left).findViewById(R.id.rss_tablet_cell_progressBar).setVisibility(View.VISIBLE);
			} else {
				v.findViewById(R.id.rss_item_tablet_top_left).findViewById(R.id.rss_tablet_cell_progressBar).setVisibility(View.GONE);
			}
			
			fillView(item1, holder.titleLT, holder.descLT, holder.dateLT, holder.imgLT, cell);
			if (4*position + 1 < mItems.size()) {
				cell = v.findViewById(R.id.rss_item_tablet_top_right);
				item2 = mItems.get(4*position+1);
				cell.setVisibility(View.VISIBLE);
				if (item2.getDescription().isEmpty()) {
					cell.findViewById(R.id.rss_tablet_cell_progressBar).setVisibility(View.VISIBLE);
				} else {
					cell.findViewById(R.id.rss_tablet_cell_progressBar).setVisibility(View.GONE);
				}
				fillView(item2, holder.titleRT, holder.descRT, holder.dateRT, holder.imgRT, cell);
			} else {
				v.findViewById(R.id.rss_item_tablet_top_right).setVisibility(View.INVISIBLE);
			}
			if (4*position + 2 < mItems.size()) {
				item3 = mItems.get(4*position+2);
				cell = v.findViewById(R.id.rss_item_tablet_bottom_left);
				cell.setVisibility(View.VISIBLE);
				if (item3.getDescription().isEmpty()) {
					cell.findViewById(R.id.rss_tablet_cell_progressBar).setVisibility(View.VISIBLE);
				} else {
					cell.findViewById(R.id.rss_tablet_cell_progressBar).setVisibility(View.GONE);
				}
				fillView(item3, holder.titleLB, holder.descLB, holder.dateLB, holder.imgLB, cell);
			} else {
				v.findViewById(R.id.rss_item_tablet_bottom_left).setVisibility(View.INVISIBLE);
			}
			if (4*position + 3 < mItems.size()) {
				cell = v.findViewById(R.id.rss_item_tablet_bottom_right);
				item4 = mItems.get(4*position+3);
				cell.setVisibility(View.VISIBLE);
				if (item4.getDescription().isEmpty()) {
					cell.findViewById(R.id.rss_tablet_cell_progressBar).setVisibility(View.VISIBLE);
				} else {
					cell.findViewById(R.id.rss_tablet_cell_progressBar).setVisibility(View.GONE);
				}
				fillView(item4, holder.titleRB, holder.descRB, holder.dateRB, holder.imgRB, cell);
			} else {
				v.findViewById(R.id.rss_item_tablet_bottom_right).setVisibility(View.INVISIBLE);
			}			
			container.addView(v);
			return v;
		}
		
		private void fillView(final RssItem item, TextView titleV, final TextView descV, TextView dateV, final ImageView imageV, View cell) {
			titleV.setText(item.getTitle());
			dateV.setText(item.getPubDate().toLocaleString());
			descV.setText(item.getDescription());
//			imageV.setBackground(new BitmapDrawable(getResources(), item.getMiniature()));
			final boolean hasMiniature = (item.getMiniature() != null);
			if (!hasMiniature) {
				imageV.setVisibility(View.GONE);
			} else {
				imageV.setImageBitmap(item.getMiniature());
				imageV.setVisibility(View.VISIBLE);
			}
			final ViewTreeObserver vto = imageV.getViewTreeObserver();
			vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
				@Override
				public void onGlobalLayout() {
					if (hasMiniature) {
						int leftMargin = imageV.getWidth() + 10;
						SpannableString ss = new SpannableString(descV.getText());
						ss.setSpan(new MyLeadingMarginSpan2(8, leftMargin), 0, ss.length(), 0);
						descV.setText(ss);
					}
				    descV.setMaxLines(
				    		 (descV.getHeight() - descV.getPaddingBottom() - descV.getPaddingTop())
				    		 /descV.getLineHeight());
				    try {
				    	imageV.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				    } catch (NoSuchMethodError x) {
				    	imageV.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				    }
				    
				}
			});
			
			cell.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					Log.d(TAG, "onClick insinde");
					if (Utils.isNetworkAvailable(getActivity())) {
						String url = item.getLink();
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						startActivity(browserIntent);
					} else {
						FeedItemWebView webViewFragment = new FeedItemWebView();
						webViewFragment.setContent(item);
						getFragmentManager().beginTransaction().replace(R.id.main_contentFrame, webViewFragment).addToBackStack("view").commit();
					}
				}
			});
		}
		
		 class ViewHolder {
			 TextView titleLT; TextView titleRT; TextView titleLB; TextView titleRB;
			 TextView descLT; TextView descRT; TextView descLB; TextView descRB; 
			 TextView dateLT; TextView dateRT; TextView dateLB; TextView dateRB;
			 ImageView imgLT; ImageView imgRT; ImageView imgLB; ImageView imgRB;
		 }
	}

	@Override
	public void onListUpdated(List<RssItem> items) {
		if (mItems.isEmpty()) {
			mItems = items;
		} else {
			int i = 0 ;
			String first = mItems.get(0).getTitle();
			while (i < items.size() && !items.get(i).getTitle().equals(first)) {
				i++;
			}
			if (i > 0) {
				mItems.addAll(0, items.subList(0, i));
			}
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDescriptionUpdated(int position, String newDescription) {
		if (mItems.size() - 1 < position) {
			return;
		}
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
			mItems.get(position).setMiniature(image);
			mAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onDetach() {
		while (getFragmentManager().getBackStackEntryCount() > 0) {
			getFragmentManager().popBackStack();
		}
		mRssFeedHandler.cancel();
		super.onDetach();
	}
	
}
