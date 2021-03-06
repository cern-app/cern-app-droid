package ch.cern.cern_app_droid.menu;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import ch.cern.cern_app_droid.static_information.StaticInformationCard;
import ch.cern.cern_app_droid.static_information.StaticInformationFragment;

public class StaticInformationAction implements ItemAction {
	
	private static final String TAG = "StaticInformationAction";
	
	private ArrayList<StaticInformationCard> mCardsList;
	private StaticInformationFragment mFragment;

	private String mTitle;

	@Override
	public Fragment getFragment(Context context) {
		Log.d(TAG, "Requested fragment (getFragment())");
		
		mFragment = new StaticInformationFragment();
		mFragment.setCardList(mCardsList);
		
		return mFragment;
	}
	
	@Override
	public void onReselected() {
		if (mFragment != null) {
			mFragment.onResume();
		}
	}
	
	public StaticInformationAction(ArrayList<StaticInformationCard> cardsList){//, String title) {
		setCardList(cardsList);
//		mTitle = title;
	}
	
	public StaticInformationAction() {}
	
	public ArrayList<StaticInformationCard> getCardList() {
		return mCardsList;
	}
	
	public void setCardList(ArrayList<StaticInformationCard> list) {
		mCardsList = list;
	}
}
