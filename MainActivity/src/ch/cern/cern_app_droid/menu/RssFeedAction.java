package ch.cern.cern_app_droid.menu;

import ch.cern.cern_app_droid.rss.RssFeedFragmentPhone;
import android.support.v4.app.Fragment;

public class RssFeedAction implements ItemAction {
	
	String mUrl;
	RssFeedFragmentPhone mFragment;

	public RssFeedAction(String feedUrl) {
		mUrl = feedUrl;
	}

	@Override
	public Fragment getFragment() {
		if (mFragment == null) {
			mFragment = new RssFeedFragmentPhone();
			mFragment.setUrl(mUrl);
		}
		return mFragment;
	}

	@Override
	public void onReselected() {
		mFragment.onResume();
	}

}