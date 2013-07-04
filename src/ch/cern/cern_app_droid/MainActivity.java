package ch.cern.cern_app_droid;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ListView;


import com.longevitysoft.android.xml.plist.PListXMLHandler;
import com.longevitysoft.android.xml.plist.PListXMLParser;
import com.longevitysoft.android.xml.plist.domain.NsArray;
import com.longevitysoft.android.xml.plist.domain.NsDict;
import com.longevitysoft.android.xml.plist.domain.NsInteger;
import com.longevitysoft.android.xml.plist.domain.NsString;
import com.longevitysoft.android.xml.plist.domain.PListObject;
import com.longevitysoft.android.xml.plist.domain.PListObjectType;

public class MainActivity extends Activity {
	ListView menuExpList;
	
	private static final String TAG = "MainActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		menuExpList = (ListView) findViewById(R.id.menuExpListView);
//		setData();	
		
		MenuParser mp = new MenuParser(this);
		
		LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		menuExpList.setAdapter(new TopLevelMenuAdapter(this, li, mp.getMenu()));
	}
	
	
	ArrayList<String> groupItems = new ArrayList<String>();
	ArrayList<ArrayList<String>> childItems = new ArrayList<ArrayList<String>>();

	
		
	ArrayList<MenuItem> asd;
	

	void setData() {
		
		asd = new ArrayList<MenuItem>();
		asd.add(new MenuItem("1. Top menu"));	
         
//		groupItems.add("Mobile");

		ArrayList<MenuItem> child = new ArrayList<MenuItem>();
		child.add(new MenuItem("1.1 Item"));
		child.add(new MenuItem("1.2 SubMenu"));
		child.get(1).items.add(new MenuItem("1.2.1 SubMenuItem"));
		child.add(new MenuItem("1.3 PHP"));
		// childItems.add(child); // children for mobile
		asd.get(0).items = child;

		asd.add(new MenuItem("2. Top menu"));
		child = new ArrayList<MenuItem>();
		child.add(new MenuItem("2.1 asdwq"));
		child.add(new MenuItem("2.2 qweqwe"));
		child.add(new MenuItem("2.3 dfgfdg"));
		child.get(2).items.add(new MenuItem("2.3.1 SubMenuItem"));
		child.get(2).items.add(new MenuItem("2.3.2 SubMenuItem"));
		child.get(2).items.add(new MenuItem("2.3.3 SubMenuItem"));
		child.add(new MenuItem("2.4 asdasd"));
		asd.get(1).items = child;
		asd.add(new MenuItem("3. Top menu"));
		child = new ArrayList<MenuItem>();
		child.add(new MenuItem("3.1 asdwq"));
		child.add(new MenuItem("3.2 qweqwe"));
		child.add(new MenuItem("3.3 dfgfdg"));
		child.get(2).items.add(new MenuItem("3.3.1 SubMenuItem"));
		child.get(2).items.add(new MenuItem("3.3.2 SubMenuItem"));
		child.get(2).items.add(new MenuItem("3.3.3 SubMenuItem"));
		child.add(new MenuItem("3.4 asdasd"));
		asd.get(2).items = child;
		asd.add(new MenuItem("4. Top menu"));
		child = new ArrayList<MenuItem>();
		child.add(new MenuItem("4.1 asdwq"));
		child.add(new MenuItem("4.2 qweqwe"));
		child.add(new MenuItem("4.3 dfgfdg"));
		child.get(2).items.add(new MenuItem("4.3.1 SubMenuItem"));
		child.get(2).items.add(new MenuItem("4.3.2 SubMenuItem"));
		child.get(2).items.add(new MenuItem("4.3.3 SubMenuItem"));
		child.add(new MenuItem("4.4 asdasd"));
		asd.get(3).items = child;		
    }
	
}
