package ch.cern.cern_app_droid.rss;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class RssFeedFragmentTablet {

	private class RssFeedAdapterTablet extends ArrayAdapter<RssItem> {

		//
		public RssFeedAdapterTablet(Context context, int textViewResourceId,
				List<RssItem> objects) {
			super(context, textViewResourceId, objects);
		}
		//
		// @Override
		// public int getCount() {
		// int additional = super.getCount() % 4 == 0 ? 0 : 1;
		// return (super.getCount()) / 4 + additional;
		// }
		//
		// @Override
		// public View getView(int position, View v, ViewGroup parent) {
		// if (v == null) {
		// LayoutInflater inflater = (LayoutInflater) getContext()
		// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// v = inflater.inflate(R.layout.rss_item_tablet, null);
		// ViewHolder holder = new ViewHolder();
		// holder.titleLT = (TextView)
		// v.findViewById(R.id.rss_item_tablet_top_left).findViewById(R.id.rss_tablet_cell_Title);
		// holder.descLT = (WebView)
		// v.findViewById(R.id.rss_item_tablet_top_left).findViewById(R.id.rss_tablet_cell_webView);
		// holder.dateLT = (TextView)
		// v.findViewById(R.id.rss_item_tablet_top_left).findViewById(R.id.rss_tablet_cell_Time);
		// // holder.imgLT = (ImageView)
		// v.findViewById(R.id.rss_item_tablet_top_left).findViewById(R.id.rss_tablet_cell_Image);
		//
		// Log.d(TAG, "titleLt = null: " + Boolean.valueOf(holder.titleLT ==
		// null));
		// holder.titleRT = (TextView)
		// v.findViewById(R.id.rss_item_tablet_top_right).findViewById(R.id.rss_tablet_cell_Title);
		// holder. descRT = (WebView)
		// v.findViewById(R.id.rss_item_tablet_top_right).findViewById(R.id.rss_tablet_cell_webView);
		// holder. dateRT = (TextView)
		// v.findViewById(R.id.rss_item_tablet_top_right).findViewById(R.id.rss_tablet_cell_Time);
		// // holder. imgRT = (ImageView)
		// v.findViewById(R.id.rss_item_tablet_top_right).findViewById(R.id.rss_tablet_cell_Image);
		//
		// holder.titleLB = (TextView)
		// v.findViewById(R.id.rss_item_tablet_bottom_left).findViewById(R.id.rss_tablet_cell_Title);
		// holder. descLB = (WebView)
		// v.findViewById(R.id.rss_item_tablet_bottom_left).findViewById(R.id.rss_tablet_cell_webView);
		// holder. dateLB = (TextView)
		// v.findViewById(R.id.rss_item_tablet_bottom_left).findViewById(R.id.rss_tablet_cell_Time);
		// // holder. imgLB = (ImageView)
		// v.findViewById(R.id.rss_item_tablet_bottom_left).findViewById(R.id.rss_tablet_cell_Image);
		//
		// holder.titleRB = (TextView)
		// v.findViewById(R.id.rss_item_tablet_bottom_right).findViewById(R.id.rss_tablet_cell_Title);
		// holder. descRB = (WebView)
		// v.findViewById(R.id.rss_item_tablet_bottom_right).findViewById(R.id.rss_tablet_cell_webView);
		// holder. dateRB = (TextView)
		// v.findViewById(R.id.rss_item_tablet_bottom_right).findViewById(R.id.rss_tablet_cell_Time);
		// // holder. imgRB = (ImageView)
		// v.findViewById(R.id.rss_item_tablet_bottom_right).findViewById(R.id.rss_tablet_cell_Image);
		// v.setTag(holder);
		// }
		//
		// if (position == 0) {
		// Log.d(TAG, "getView");
		// }
		//
		// ViewHolder holder = (ViewHolder) v.getTag();
		// RssItem item1, item2, item3, item4;
		// item1 = getItem(4*position);
		// fillView(item1, holder.titleLT, holder.descLT, holder.dateLT); // ,
		// holder.imgLT);
		// if (4*position + 1 < super.getCount()) {
		// item2 = getItem(position+1);
		// v.findViewById(R.id.rss_item_tablet_top_right).setVisibility(View.VISIBLE);
		// fillView(item2, holder.titleRT, holder.descRT, holder.dateRT); // ,
		// holder.imgRT);
		// } else {
		// v.findViewById(R.id.rss_item_tablet_top_right).setVisibility(View.GONE);
		// }
		// if (4*position + 2 < super.getCount()) {
		// item3 = getItem(position+2);
		// v.findViewById(R.id.rss_item_tablet_bottom_left).setVisibility(View.VISIBLE);
		// fillView(item3, holder.titleLB, holder.descLB, holder.dateLB); // ,
		// holder.imgLB);
		// } else {
		// v.findViewById(R.id.rss_item_tablet_bottom_left).setVisibility(View.GONE);
		// }
		// if (4*position + 3 < super.getCount()) {
		// item4 = getItem(position+3);
		// v.findViewById(R.id.rss_item_tablet_bottom_right).setVisibility(View.VISIBLE);
		// fillView(item4, holder.titleRB, holder.descRB, holder.dateRB); // ,
		// holder.imgRB);
		// } else {
		// v.findViewById(R.id.rss_item_tablet_bottom_right).setVisibility(View.GONE);
		// }
		//
		//
		// return v;
		// }
		//
		// private void fillView(RssItem item, TextView titleV, WebView descV,
		// TextView dateV) { //, ImageView imageV) {
		// titleV.setText(item.getTitle());
		// descV.loadData(item.getDescription(), "text/html; charset=UTF-8",
		// null); //ToDo: Add readability
		// dateV.setText(item.getPubDate().toLocaleString());
		// // if (item.getMiniature() != null) {
		// // imageV.setImageBitmap(item.getMiniature());
		// // imageV.setVisibility(View.VISIBLE);
		// // } else {
		// // imageV.setVisibility(View.GONE);
		// // }
		// }
		//
		// class ViewHolder {
		// TextView titleLT; TextView titleRT; TextView titleLB; TextView
		// titleRB;
		// WebView descLT; WebView descRT; WebView descLB; WebView descRB;
		// TextView dateLT; TextView dateRT; TextView dateLB; TextView dateRB;
		// // ImageView imgLT; ImageView imgRT; ImageView imgLB; ImageView
		// imgRB;
		// }
		// }

	}
}
