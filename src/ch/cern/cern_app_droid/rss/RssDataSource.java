package ch.cern.cern_app_droid.rss;

import java.util.ArrayList;
import java.util.List;

import org.horrabin.horrorss.RssItemBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class RssDataSource {
	
	private static final String TAG = "RssDataSource";
	
	private RssDatabaseHelper mHelper;
	private SQLiteDatabase mDatabase;
	
	public RssDataSource(Context ctx) {
		mHelper = new RssDatabaseHelper(ctx);
	}
	
	public void open() { 
		mDatabase = mHelper.getWritableDatabase();
		// call it first time for later caching (first call can take long time)
	}
	
	public void close() {
		mHelper.close();
	}
	
	public void insertItem(RssItemBean r, String feed) {
		mDatabase = mHelper.getWritableDatabase();
		
		insertToOpenDatabase(r, feed);
		
		mDatabase.close();
	}
	
	private void insertToOpenDatabase(RssItemBean r, String feed) {
		
		Log.d(TAG, "inserting to database (feed: " + feed);
		
		ContentValues v = new ContentValues();
		
		v.put(RssDatabaseHelper.COLUMN_FEED_NAME, feed);
		v.put(RssDatabaseHelper.COLUMN_TITLE, r.getTitle());
		v.put(RssDatabaseHelper.COLUMN_LINK, r.getLink());
//		v.put(RssDatabaseHelper.COLUMN_DATE, r.getPubDate()); 
		//ToDo: Put date into database, how?
		v.put(RssDatabaseHelper.COLUMN_DESCRIPTION, r.getDescription());
//		v.put(RssDatabaseHelper.COLUMN_IMAGE, r.getTitle());
		//ToDo: Put image in database
		
		mDatabase.insert(RssDatabaseHelper.TABLE_NAME, null, v);
		Log.d(TAG, "inserted ( " + r.getTitle() + " : " + r.getLink() + ")" );
	}
	
	public List<RssItemBean> getAllItems(String feed) {
		List<RssItemBean> rv = new ArrayList<RssItemBean>();
//		String query = "SELECT * FROM ? WHERE ?=?";
		mDatabase = mHelper.getReadableDatabase();
		
		Log.d(TAG, "getAllItems(); got readable database ");
//		Cursor cursor = mDatabase.rawQuery(query, 
//				new String[] {RssDatabaseHelper.TABLE_NAME, RssDatabaseHelper.COLUMN_FEED_NAME, feed});
		Cursor cursor = mDatabase.query(RssDatabaseHelper.TABLE_NAME, null, RssDatabaseHelper.COLUMN_FEED_NAME + "=?", 
				new String[] { feed }, null, null, null);
		if (cursor.moveToFirst()) {
			Log.d(TAG, "cursor has elements ");
			do {
				RssItemBean r = new RssItemBean(
						cursor.getString(cursor.getColumnIndexOrThrow(RssDatabaseHelper.COLUMN_TITLE)),
						cursor.getString(cursor.getColumnIndexOrThrow(RssDatabaseHelper.COLUMN_LINK)),
						cursor.getString(cursor.getColumnIndexOrThrow(RssDatabaseHelper.COLUMN_DESCRIPTION))
						);
				rv.add(r);
				Log.d(TAG, "added item, title: " + r.getTitle());
			} while (cursor.moveToNext());
		}
		
		cursor.close();		
		mDatabase.close();		
		
		return rv;
	}
	
	public boolean hasItems(String feed) {
		mDatabase = mHelper.getReadableDatabase();
				
		return DatabaseUtils.queryNumEntries(mDatabase, RssDatabaseHelper.TABLE_NAME, 
				RssDatabaseHelper.COLUMN_FEED_NAME + "=?", new String[] { feed }) > 0;
	}
	
	public void clearFeed(String feed) {
		mDatabase = mHelper.getWritableDatabase();
		mDatabase.delete(RssDatabaseHelper.TABLE_NAME, null, null);
		mDatabase.close();
	}
	
	public void replaceFeed(List<RssItemBean> items, String feed) {
		Log.d(TAG, "Replacing feed");
		clearFeed(feed);
		
		mDatabase = mHelper.getWritableDatabase();
		
		Log.d(TAG, "Got writeable database: " + mDatabase);
		
		for (RssItemBean r : items) {
			insertToOpenDatabase(r, feed);
		}
		
		mDatabase.close();
	}
}
