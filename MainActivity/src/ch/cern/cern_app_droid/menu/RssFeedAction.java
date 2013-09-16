package ch.cern.cern_app_droid.menu;

import ch.cern.cern_app_droid.R;
import ch.cern.cern_app_droid.rss.RssFeedFragmentPhone;
import ch.cern.cern_app_droid.rss.RssFeedFragmentTablet;
import ch.cern.cern_app_droid.rss.RssHelper;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class RssFeedAction implements ItemAction {
	
	String mUrl;
	Fragment mFragment;

	public RssFeedAction(String feedUrl) {
		mUrl = feedUrl;
	}

	@Override
	public Fragment getFragment(Context context) {
		if (mFragment == null) {
			if (true){// context.getResources().getBoolean(R.bool.isTablet)) {
				mFragment = new RssFeedFragmentTablet();
			} else {
				mFragment = new RssFeedFragmentPhone();
			}
			Bundle args = new Bundle();
			args.putString(RssHelper.URL, mUrl);
			mFragment.setArguments(args);
		}
		return mFragment;
	}

	@Override
	public void onReselected() {
		mFragment.onResume();
	}

}