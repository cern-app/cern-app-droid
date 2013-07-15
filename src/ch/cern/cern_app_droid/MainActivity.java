package ch.cern.cern_app_droid;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
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
		setContentView(R.layout.activity_main);

		LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		menuExpList = (ListView) li.inflate(R.layout.menu, null);
		menuExpList.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		// menu stutters at first expansion otherwise
		// might want to set it only when sliding (requires changes in sliding
		// library or set it back after first expansion

		MenuParser mp = new MenuParser(this);
		final FragmentManager fragmentManager = getFragmentManager();
		
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
			super.onBackPressed();
		}
	}

	public void onMenuClick(MenuItem item) {
		Log.d(TAG, "onItemClick()");
//		Log.d(TAG, "item == null :: " + String.valueOf(item == null));
//		Log.d(TAG, "item.Action == null :: " + String.valueOf(item.Action == null));
//		Log.d(TAG, "item.Action.getFragment() == null :: " + String.valueOf(item.Action.getFragment() == null));
		if (item != null && item.Action != null && item.Action.getFragment() != null) {
			getFragmentManager().beginTransaction().replace(R.id.main_contentFrame, item.Action.getFragment()).commit();
		}
		
	}
}
