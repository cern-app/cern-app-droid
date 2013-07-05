package ch.cern.cern_app_droid;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends Activity {
	ListView menuExpList;
	
	private static final String TAG = "MainActivity";
	
	private SlidingMenu mMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		menuExpList = (ListView) li.inflate(R.layout.menu, null);
		menuExpList.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		// menu stutters at first view otherwise
		// might want to set it only when sliding (requires changes in sliding library
		// 	or set it back after first expansion
		
		MenuParser mp = new MenuParser(this);
		
		menuExpList.setAdapter(new TopLevelMenuAdapter(this, li, mp.getMenu()));
		//ToDo: parse once, save in variable
		// 		Check for null in onCreate() and don't parse again if already parsed
		
		mMenu = new SlidingMenu(this);
		mMenu.setMode(SlidingMenu.LEFT);
        
		mMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        mMenu.setBehindOffset(100);
		mMenu.setFadeDegree(0.35f);
		mMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		mMenu.setMenu(menuExpList);
	}	
	
	@Override
	public void onBackPressed() {
		if (mMenu.isMenuShowing()) {
			mMenu.showContent();
		} else {
			super.onBackPressed();
		}
	}
}
