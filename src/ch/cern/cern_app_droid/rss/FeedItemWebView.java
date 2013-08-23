package ch.cern.cern_app_droid.rss;

import org.horrabin.horrorss.RssItemBean;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import ch.cern.cern_app_droid.R;

public class FeedItemWebView extends Fragment {
	
	private RssItemBean mItem;
	
	public void setContent(RssItemBean item) {
		mItem = item;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.rss_item_webview, null);
		WebView webView = (WebView) v.findViewById(R.id.rssItemWebView_webView);
		String content = mItem.getDescription();
		content.replaceAll("<img.*?>", "");
		webView.loadData(content, "text/html; charset=UTF-8", null);
		return v;
	}
}
