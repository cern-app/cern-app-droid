package ch.cern.cern_app_droid.rss;

import org.horrabin.horrorss.RssFeed;

public interface RssReadListener {

	public void onRssReadSuccessfuly(RssFeed result);
	public void onRssReadError();

	
}
