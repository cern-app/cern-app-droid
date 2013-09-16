package ch.cern.cern_app_droid.menu;

import java.util.ArrayList;

public class MenuItem {

	enum Category {MENU_GROUP, FEED, LIVE, STATIC_INFO, VIDEO_SET, RECENT, UPCOMING, NAVIGATION_VIEW_ITEM, WEBCASTS }
	
	Category category;
	String categoryArgument;
	String categoryName;
	
	String title = null;
	String image;
	boolean isExpandedByDefault;
	
	public ItemAction Action;
	
	ArrayList<MenuItem> items;
	
	public MenuItem(String item) {
		this();
		title = item;
	}
	
	public MenuItem() {
		items = new ArrayList<MenuItem>();
	}
	
	public String getTitle() {
		return title;
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
