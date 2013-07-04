package ch.cern.cern_app_droid;

import java.util.ArrayList;

public class MenuItem {

	enum Category {MENU_GROUP, FEED, LIVE, STATIC_INFO, VIDEO_SET, RECENT, UPCOMING, NAVIGATION_VIEW_ITEM, WEBCASTS }
	
	Category category;
	String categoryArgument;
	String categoryName;
	
	String title = null;
	String image;
	boolean isExpandedByDefault;
	
	Object Action;
	//ToDo: Create interface and specific classes for arguments and identify type by enum
		// for example classes ImageList, Image and Tweet. Identified by field 'argumentType' and
		// casted when necessary
	
	ArrayList<MenuItem> items;
	
	public MenuItem(String item) {
		this();
		title = item;
	}
	
	public MenuItem() {
		items = new ArrayList<MenuItem>();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("categoryArgument: ").append(categoryArgument).append('\n');
		sb.append("categoryName: ").append(categoryName).append('\n');
		sb.append("title: ").append(title).append('\n');
		sb.append("icon: ").append(image).append('\n');
		return "";
//		return sb.toString();
	}
	
}
