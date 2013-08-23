package ch.cern.cern_app_droid.menu;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import ch.cern.cern_app_droid.static_information.StaticInformationCard;

import com.longevitysoft.android.xml.plist.domain.NsArray;
import com.longevitysoft.android.xml.plist.domain.NsDict;
import com.longevitysoft.android.xml.plist.domain.NsString;
import com.longevitysoft.android.xml.plist.domain.PListObject;
import com.longevitysoft.android.xml.plist.domain.PListObjectType;

public abstract class StaticInfoMenuParser {
	
	private static final String TAG = "StaticInfoMenuParser";
	
	public static ArrayList<MenuItem> getStaticInfoMenu(NsDict map) {
		NsArray items = map.getConfigurationArray("Root");
		if (items == null) {
			Log.e(TAG, "No menu contents");
			return null;
		}
		return getStaticInfoMenuItemList(items);
	}	

	private static ArrayList<MenuItem> getStaticInfoMenuItemList(NsArray items) {
		ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
		
		for (PListObject item : items) {
			if (item.getType() != PListObjectType.DICT) {
				Log.d(TAG, "Wrong item in menu [not DICT element]");
			} else {
				menuItems.add(getStaticInfoMenuItem((NsDict) item));
			}
		}		
		return menuItems;
	}
	
	private static MenuItem getStaticInfoMenuItem(NsDict dict) {
		MenuItem rv = new MenuItem();
		
		rv.title = dict.getConfigurationWithDefault("Title", new NsString("")).getValue();
		NsArray items = dict.getConfigurationArray("Items");
		
		if ( ((NsDict) items.get(0)).getConfigurationArray("Items") == null) {
			
			
			ArrayList<StaticInformationCard> menuItemCardList = new ArrayList<StaticInformationCard>();
			
			for (PListObject i : items) {
				NsDict item = (NsDict) i;
				
				StaticInformationCard card = new StaticInformationCard(
						item.getConfigurationWithDefault("Title", new NsString()).getValue(),
						item.getConfigurationWithDefault("Description", new NsString()).getValue(),
						item.getConfigurationWithDefault("Image", new NsString()).getValue());
				
				menuItemCardList.add(card);
			}
			
			rv.Action = new StaticInformationAction(menuItemCardList);			
			// menu item + menu data
			// rv.data = getStaticInformationDataSet(items);
		} else {
			// menu group + menu items
			for (PListObject i : items) {
				NsDict item = (NsDict) i;
				rv.items.add(getStaticInfoMenuItem(item));				
			}
		}
	
		//items should not be null
				
		return rv;
	}
	
	private static List<StaticInformationCard> getStaticInformationDataSet(NsArray dict) {
		//ToDo: new data object from array
		return null;
	}	
	
	private static StaticInformationCard getStaticInformationData() {
		
		return null;
	}
}
