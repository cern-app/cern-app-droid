package ch.cern.cern_app_droid.rss;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
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
import ch.cern.cern_app_droid.Utils;

public class RssFeedFragmentPhone extends ListFragment implements OnItemClickListener, RssHandlerListener {
	
	private static final String TAG = "RssFeedFragmentPhone";

	ArrayAdapter<RssItem> mAdapter;
	String mUrl;
	RssFeedHandler mRssFeedHandler;	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getListView().setOnItemClickListener(this);
	}	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		
		mAdapter = new RssFeedAdapter(activity, 0, new ArrayList<RssItem>());

		setListAdapter(mAdapter);
		
		mUrl = getArguments().getString(RssHelper.URL);
		mRssFeedHandler = new RssFeedHandler(mUrl);
		mRssFeedHandler.setRssHandlerListener(this);
		mRssFeedHandler.startWork(activity);
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
				holder.desc = (TextView) v
						.findViewById(R.id.rss_row_Description);
				holder.date = (TextView) v.findViewById(R.id.rss_row_Time);
				holder.img = (ImageView) v.findViewById(R.id.rss_row_Image);
				v.setTag(holder);
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (Utils.isNetworkAvailable(getActivity())) {
			String url = mAdapter.getItem(position).getLink();
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(browserIntent);
		} else {
			FeedItemWebView webViewFragment = new FeedItemWebView();
			webViewFragment.setContent(mAdapter.getItem(position));
			getFragmentManager().beginTransaction().replace(R.id.main_contentFrame, webViewFragment).addToBackStack("view").commit();
		}
	}

	@Override
	public void onListUpdated(List<RssItem> items) {
		mAdapter.clear();
		mAdapter.addAll(items);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onError(RssHandlerError error) {
		Activity a = getActivity();
		if (a != null) {
			Toast.makeText(a, "No internet connection", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onImageDownloaded(int position, Bitmap image) {
		mAdapter.getItem(position).setMiniature(image);
		if (!isVisible())
			return;
		mAdapter.notifyDataSetChanged();		
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mRssFeedHandler.cancel();
	}
	
	@Override
	public void onDescriptionUpdated(int position, String newDescription) {
		mAdapter.notifyDataSetChanged();		
	}
}
