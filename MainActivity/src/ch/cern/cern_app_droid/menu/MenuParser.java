package ch.cern.cern_app_droid.menu;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.longevitysoft.android.xml.plist.PListXMLHandler;
import com.longevitysoft.android.xml.plist.PListXMLParser;
import com.longevitysoft.android.xml.plist.domain.NsArray;
import com.longevitysoft.android.xml.plist.domain.NsDict;
import com.longevitysoft.android.xml.plist.domain.NsInteger;
import com.longevitysoft.android.xml.plist.domain.NsString;
import com.longevitysoft.android.xml.plist.domain.PListObject;
import com.longevitysoft.android.xml.plist.domain.PListObjectType;

public class MenuParser {
	private static final String TAG = "MenuParser";

	Context context;

	public MenuParser(Context context) {
		this.context = context;
	}

	public ArrayList<MenuItem> getMenu() {
		return getMenu(getDictFromFile("menu.xml"));
	}

	public NsDict getDictFromFile(String file) {
		PListXMLParser p = new PListXMLParser();
		p.initParser();
		p.setHandler(new PListXMLHandler());

		try {
			p.parse(context.getAssets().open(file));

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return (NsDict) ((PListXMLHandler) p.getHandler()).getPlist()
				.getRootElement();
	}

	private ArrayList<MenuItem> getMenu(NsDict map) {
		NsArray items = map.getConfigurationArray("Menu Contents");
		if (items == null) {
			Log.e(TAG, "No menu contents");
			return null;
		}
		return getMenuItemList(items);
	}

	// Returns list of menu items created from dictionaries in an array
	private ArrayList<MenuItem> getMenuItemList(NsArray items) {
		ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();

		for (PListObject item : items) {
			if (item.getType() != PListObjectType.DICT) {
				Log.d(TAG, "Wrong item in menu [not DICT element]");
			} else {
				menuItems.add(getMenuItem((NsDict) item));
			}
		}
		return menuItems;
	}

	private MenuItem getMenuItem(NsDict dict) {
		MenuItem rv = new MenuItem();

		rv.isExpandedByDefault = (dict.getConfigurationIntegerWithDefault("Expanded",
				new NsInteger(1)).getValue() == 1); // expanded group by default

		String imageName = dict.getConfigurationWithDefault("Image name",
				new NsString("")).getValue();
		String image = dict.getConfigurationWithDefault("Image", new NsString(""))
				.getValue(); 
		
		if (image.isEmpty()) {
			if (!imageName.isEmpty()) {
				rv.image = imageName;
			}
		} else {
			if (imageName.isEmpty()) {
				rv.image = image;
			} else {
				Log.e(TAG, "Image and ImageName not empy");
			}
		}
		// ToDo: difference between "Image" and "Image name"
		

		String category = dict.getConfigurationWithDefault("Category name",
				new NsString("")).getValue();
		rv.categoryName = category;

		rv.title = dict.getConfigurationWithDefault("Name",
				new NsString("No name")).toString();

		
		if (category.equals("LIVE")) {
			rv.title = "LIVE";
			rv.items = CernLiveMenuParser.getCernLiveMenu(getDictFromFile("CERNLive.xml"));
			
		} else if (category.equals("StaticInfo")) {
			
			rv.title = "About CERN";
			rv.items = StaticInfoMenuParser.getStaticInfoMenu(getDictFromFile("StaticInformation.xml"));
			
		} else if (category.equals("Feed")) {
			rv.Action = new RssFeedAction(dict.getConfigurationWithDefault("Url",
					new NsString("")).getValue());
			
		} else if (category.equals("Menu group")) {
			NsArray items = dict.getConfigurationArray("Items");
			if (items != null) {
				rv.items = getMenuItemList(items);
			}
			
		} else if (category.equals("NavigationViewItem")){
			String controller = dict.getConfigurationWithDefault("ControllerID", new NsString("")).getValue();
			if (controller.equals("WebcastsControllerID")) {
				
			} 
			
		} else if (category.equals("ModalViewItem")) {
			String controller = dict.getConfigurationWithDefault("ControllerID", new NsString("")).getValue();
			if (controller.equals("AppSettingsControllerID")) {
				
			} else if (controller.equals("AppSettingsControllerID")) {
				
			}
		}
		// ToDo: handle various categories
		
		return rv;
	}
}
