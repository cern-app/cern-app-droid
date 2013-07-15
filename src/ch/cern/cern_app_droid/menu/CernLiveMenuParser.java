package ch.cern.cern_app_droid.menu;

import java.util.ArrayList;

import android.util.Log;

import com.longevitysoft.android.xml.plist.domain.NsArray;
import com.longevitysoft.android.xml.plist.domain.NsDict;
import com.longevitysoft.android.xml.plist.domain.PListObject;
import com.longevitysoft.android.xml.plist.domain.PListObjectType;

public abstract class CernLiveMenuParser {
	
	private static final String TAG = "CernLiveMenuParser";
	
	private static final String CATEGORY_LIVE_CMS_STATUS = "CMS Status";
	private static final String CATEGORY_LIVE_DAQ_STATUS = "DAQ Status";
	private static final String CATEGORY_LIVE_IMAGESET = "ImageSet";
	private static final String CATEGORY_LIVE_LIVE_EVENTS = "Live Events";
	private static final String CATEGORY_LIVE_NEWS = "News";
	private static final String CATEGORY_LIVE_SINGLEIMAGE = "SingleImage";
	private static final String CATEGORY_LIVE_STATUS = "Status";
	private static final String CATEGORY_LIVE_TWEET = "Tweet";
	
	
	public static ArrayList<MenuItem> getCernLiveMenu(NsDict map) {
		NsArray items = map.getConfigurationArray("Root");
		if (items == null) {
			Log.e(TAG, "No menu contents");
			return null;
		}
		return getCernLiveMenuItemList(items);
	}	

	private static ArrayList<MenuItem> getCernLiveMenuItemList(NsArray items) {
		ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
		
		for (PListObject item : items) {
			if (item.getType() != PListObjectType.DICT) {
				Log.d(TAG, "Wrong item in menu [not DICT element]");
			} else {
				menuItems.add(getCernLiveMenuItem((NsDict) item));
			}
		}		
		return menuItems;
	}
	
	private static MenuItem getCernLiveMenuItem(NsDict dict) {
		MenuItem rv = new MenuItem();
		
		rv.title = dict.getConfiguration("ExperimentName").getValue();
		rv.items = getLiveMenuItems(dict.getConfigurationArray("Data"));	
		
		return rv;
	}

	private static ArrayList<MenuItem> getLiveMenuItems(NsArray data) {
		ArrayList<MenuItem> rv = new ArrayList<MenuItem>();

		for (PListObject item : data) {
			NsDict itemDict = (NsDict) item;

			String category = itemDict.getConfiguration("Category name")
					.getValue();

			if (category.equals("Status")) {
				rv.add(getStatusMenuItem(itemDict));
			} else if (category.equals("News")) {
				rv.addAll(getNewsMenuItemList(itemDict));
			} else if (category.equals("Live Events")) {
				//ToDo: fill in rest of categories
			}
		}

		return rv;
	}
	
	private static ArrayList<MenuItem> getNewsMenuItemList(NsDict dict) {
		ArrayList<MenuItem> rv = new ArrayList<MenuItem>();
			
		NsArray feeds = dict.getConfigurationArray("Feeds");
		if (feeds != null)
			for (PListObject f : feeds) {
				NsDict feed = (NsDict) f;
				MenuItem m = new MenuItem();
				m.title = feed.getConfiguration("Name").getValue();
				m.categoryArgument = feed.getConfiguration("Url").getValue();
				rv.add(m);
			}
		
		NsArray tweets = dict.getConfigurationArray("Tweets");
		if (tweets != null)
			for (PListObject t : tweets) {
				NsDict tweet = (NsDict) t;
				MenuItem m = new MenuItem();
				m.title = tweet.getConfiguration("Name").getValue();
				m.categoryArgument = tweet.getConfiguration("Url").getValue();
				rv.add(m);
			}
			
		return rv;
	}
	
//	private ArrayList<MenuItem> getLiveStatusMenuItems(NsArray arr) {
//		ArrayList<MenuItem> rv = new ArrayList<MenuItem>();
//		for (PListObject item : arr) {
//			if (item.getType() != PListObjectType.DICT) {
//				Log.d(TAG, "Wrong item in CERNLive Status data");
//			} else {
//				NsDict d = (NsDict) item;
//				MenuItem m = new MenuItem();
//				m.title = d.getConfiguration("Category name").getValue();
//			}		
//		}
//		return rv;
//	}
	
	private static MenuItem getStatusMenuItem(NsDict dict) {
		MenuItem rv = new MenuItem();
		rv.title = dict.getConfiguration("Category name").getValue();
		//ToDo: set menu item 
		return rv;
	}
	
	
	
}
