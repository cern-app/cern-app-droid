package ch.cern.cern_app_droid;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ListView;
import ch.cern.cern_app_droid.menu.MenuItem;
import ch.cern.cern_app_droid.menu.MenuParser;
import ch.cern.cern_app_droid.menu.TopLevelMenuAdapter;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends FragmentActivity  {
	ListView menuExpList;

	private static final String TAG = "MainActivity";

	private SlidingMenu mMenu;
	
	private Fragment mContent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);

		LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		menuExpList = (ListView) li.inflate(R.layout.menu, null);
		menuExpList.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		// menu stutters at first expansion otherwise
		// might want to set it only when sliding (requires changes in sliding
		// library or set it back after first expansion

		MenuParser mp = new MenuParser(this);
		final FragmentManager fragmentManager = getSupportFragmentManager();
		
//		menuExpList.setAdapter(new TopLevelMenuAdapter(this, li, mp.getMenu()));
		menuExpList.setAdapter(new TopLevelMenuAdapter(this, 0, mp.getMenu()));

		// ToDo: parse once, save in variable
		// Check for null in onCreate() and don't parse again if already parsed
		// Remember expanded/collapsed menus
		
		
		fragmentManager.beginTransaction()
			.replace(R.id.main_contentFrame, new StartScreen())
			.commit();		
		
		setMenu();

	}

	private void setMenu() {
		mMenu = new SlidingMenu(this);
		mMenu.setMode(SlidingMenu.LEFT);

		mMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		mMenu.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d("SlidingMenu", "OnTouch");
				return false;
			}
		});
		mMenu.setBehindScrollScale(1.0f);
		mMenu.setBehindOffset(100);
		mMenu.setFadeDegree(0.35f);
		mMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		mMenu.setMenu(menuExpList);
	}

	@Override
	public void onBackPressed() {
		if (mMenu.isMenuShowing()) {
			mMenu.toggle();
		} else {
			if (getFragmentManager().getBackStackEntryCount() > 0) {
				getFragmentManager().popBackStack();
			} else {
				super.onBackPressed();
			}
		}
	}

	public void onMenuClick(MenuItem item) {
		Log.d(TAG, "onItemClick()");
		if (item != null && item.Action != null && item.Action.getFragment(this) != null) {
			setProgressBarIndeterminateVisibility(false);
			getSupportFragmentManager().beginTransaction().replace(R.id.main_contentFrame, item.Action.getFragment(this)).commit();
			mMenu.toggle();
		}
		
	}
}
