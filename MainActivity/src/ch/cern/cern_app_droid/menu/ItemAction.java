package ch.cern.cern_app_droid.menu;

import android.support.v4.app.Fragment;

public interface ItemAction {

	public Fragment getFragment();
	public void onReselected();
}
