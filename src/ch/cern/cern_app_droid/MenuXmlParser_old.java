package ch.cern.cern_app_droid;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Pair;
import android.util.Xml;

public class MenuXmlParser_old {

	private static final String ns = null;
	private static final String TAG = "MenuXmlParser";
	XmlPullParser parser;
	
	
	public ArrayList<MenuItem> loadMenuFromServer() {
		return null;
	}

	public ArrayList<MenuItem> parse(InputStream in) throws XmlPullParserException,
			IOException {
		try {
			Log.d(TAG, "started parsing");
			parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_DOCDECL, false);
			parser.setInput(in, null);		
			
			parser.nextTag(); //parser.require(XmlPullParser.START_TAG, ns, "plist");
			skipToTag("array"); // first array contains all menu elements
			return readMenuItemList();
		} finally {
			in.close();
		}
	}
	
	private String getEventString(int event) {
		switch (event) {
		case XmlPullParser.START_DOCUMENT:
			return "Start document";
		case XmlPullParser.START_TAG:
			return "Start tag";
		case XmlPullParser.END_DOCUMENT:
			return "End document";
		case XmlPullParser.END_TAG:
			return "End tag";
		case XmlPullParser.COMMENT:
			return "Comment";
		case XmlPullParser.TEXT:
			return "Text";
		case XmlPullParser.IGNORABLE_WHITESPACE:
			return "Whitespace";
		default:
			return "Unknown";
		}
	}
	
	int level = 0;
	private ArrayList<MenuItem> readMenuItemList() throws XmlPullParserException, IOException {
		++level;
		parser.require(XmlPullParser.START_TAG, ns, "array");
		
		ArrayList<MenuItem> rv = new ArrayList<MenuItem>();
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (!skipToTag("dict")) { //skip to next element in menu
				--level;
				return rv;
			} else {
				MenuItem mi = readMenuItem();
				if (mi.title != null) {
					rv.add(mi);
					Log.d(TAG, "adding mi");
				}
				else
					Log.d(TAG, "not adding mi");
			}
		}
		return rv;
	}

	private MenuItem readMenuItem() throws XmlPullParserException, IOException { //reads menu item; cursor is at <dict>
		
		MenuItem rv = new MenuItem();
		
		boolean stop = false;
		while ( !stop ) { //jump to next <key> or </dict> tag

			int event = parser.getEventType(); //parser.next();
			while (event != XmlPullParser.START_TAG	|| !parser.getName().equals("key")) {
				if ( (event == XmlPullParser.END_TAG && parser.getName().equals("dict")) 
						|| event == XmlPullParser.END_DOCUMENT) { // we have finished with item
					stop = true;
					Log.d(TAG, "Menu item ended ( </dict> ) @ line " + parser.getLineNumber());
					break;
				}
				event = parser.next();
			}

			if (stop) {				
				Log.d(TAG, rv.toString());
				return rv;
			}
			
			parser.next();
			String key = parser.getText();
			if (key.equals("Items")) {
				skipToTag("array");
				rv.items = readMenuItemList();
			} else if (key.equals("Category name")) {
				skipToTag("string"); parser.next();
				rv.categoryName = parser.getText();
			} else if (key.equals("Image")) {
				skipToTag("string"); parser.next();
				rv.image= parser.getText();
			} else if (key.equals("Url")) {
				skipToTag("string"); parser.next();
				rv.categoryArgument = parser.getText();
			} else if (key.equals("Name")) {
				skipToTag("string"); parser.next();
				rv.title = parser.getText();
				Log.d(TAG, "Setting title");
			} else if (key.equals("Expanded")) {
				skipToTag("integer"); parser.next();
				String v = parser.getText();
			} else if (key.equals("Image name")) {
				skipToTag("string"); parser.next();
				rv.image= parser.getText();
			} else if (key.equals("ControllerID")) {
				skipToTag("string"); parser.next();
			}
		}		
		Log.d(TAG, rv.toString());
		return rv;
	}
	
	private boolean skipToTag(String text) { return skipToTag(text, true); }
	
	private boolean skipToTag(String text, boolean opening) {
		try {
			int event = parser.next();
			while ( (opening && event != XmlPullParser.START_TAG) || (!opening && event != XmlPullParser.END_TAG) 
					|| !parser.getName().equals(text)) {
				if (event == XmlPullParser.END_DOCUMENT )
					return false;
				if (opening) {
					if (event == XmlPullParser.END_TAG && parser.getName().equals(text))
						return false;
				} else {
					if (event == XmlPullParser.START_TAG && parser.getName().equals(text))
						return false;
				}
				event = parser.next();
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
