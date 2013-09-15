package ch.cern.cern_app_droid.rss;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	
	public void insertItem(RssItem r, String feed) {
		try {
			mDatabase = mHelper.getWritableDatabase();
		
			insertToOpenDatabase(r, feed);
		
			mDatabase.close();
		} catch (Exception e) 
		{ }
	}
	
	private void insertToOpenDatabase(RssItem r, String feed) {
		
		ContentValues v = new ContentValues();
		
		v.put(RssDatabaseHelper.COLUMN_FEED_NAME, feed);
		v.put(RssDatabaseHelper.COLUMN_TITLE, r.getTitle());
		v.put(RssDatabaseHelper.COLUMN_LINK, r.getLink());
		v.put(RssDatabaseHelper.COLUMN_DATE, r.getPubDate().getTime()); //Storing date as 64 bit integer 
		v.put(RssDatabaseHelper.COLUMN_DESCRIPTION, ""); // r.getDescription());
		Bitmap miniature = r.getMiniature();
		if (miniature != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			miniature.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			v.put(RssDatabaseHelper.COLUMN_IMAGE, baos.toByteArray());
		}
		v.put(RssDatabaseHelper.COLUMN_READ, (r.isRead() ? 1 : 0));
		v.put(RssDatabaseHelper.COLUMN_IMAGE, r.getTitle());
		
		mDatabase.insert(RssDatabaseHelper.TABLE_NAME, null, v);
	}
	
	public List<RssItem> getAllItems(String feed) {
		List<RssItem> rv = new ArrayList<RssItem>();
		mDatabase = mHelper.getReadableDatabase();
		
		Cursor cursor = mDatabase.query(RssDatabaseHelper.TABLE_NAME, null, RssDatabaseHelper.COLUMN_FEED_NAME + "=?", 
				new String[] { feed }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				RssItem r = new RssItem();
					r.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(RssDatabaseHelper.COLUMN_TITLE)));
					r.setLink(cursor.getString(cursor.getColumnIndexOrThrow(RssDatabaseHelper.COLUMN_LINK)));
					r.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(RssDatabaseHelper.COLUMN_DESCRIPTION)));
					try {
						byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(RssDatabaseHelper.COLUMN_IMAGE));
						ByteArrayInputStream is = new ByteArrayInputStream(image);
						r.setMiniature(BitmapFactory.decodeStream(is));
					} catch (Exception e) {	
						Log.d(TAG, "Error while decoding image from blob");
					}
					r.setPubDate(new Date(cursor.getInt(cursor.getColumnIndexOrThrow(RssDatabaseHelper.COLUMN_DATE))));
					r.setRead(cursor.getInt(cursor.getColumnIndexOrThrow(RssDatabaseHelper.COLUMN_READ)) == 1);
						
				
				rv.add(r);
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
		mDatabase.delete(RssDatabaseHelper.TABLE_NAME, RssDatabaseHelper.COLUMN_FEED_NAME + "=?", new String[]{feed});
		mDatabase.close();
	}
	
	public void replaceFeed(List<RssItem> items, String feed) {
		
		clearFeed(feed);
		
		mDatabase = mHelper.getWritableDatabase();
		
		for (RssItem r : items) {
			if (DatabaseUtils.queryNumEntries(mDatabase, RssDatabaseHelper.TABLE_NAME, 
				RssDatabaseHelper.COLUMN_LINK + "=?", new String[] { r.getLink() }) <= 0)
			insertToOpenDatabase(r, feed);
		}
		
		mDatabase.close();
	}
	
	public void updateItem(String feed, String link, Bitmap image, Boolean read) {
		if (image == null && read == null) {
			Log.d(TAG, "No image or read state passed to update [" + link + "]");
			return;
		}
		mDatabase = mHelper.getWritableDatabase();
		
		ContentValues v = new ContentValues();
		if (image != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			v.put(RssDatabaseHelper.COLUMN_IMAGE, baos.toByteArray());
		}
		if (read != null) {
			v.put(RssDatabaseHelper.COLUMN_READ, read.booleanValue());
		}
		
		mDatabase.update(RssDatabaseHelper.TABLE_NAME, v, 
				RssDatabaseHelper.COLUMN_FEED_NAME + "=? AND " + RssDatabaseHelper.COLUMN_LINK + "=?" , // WHERE 
				new String[] { feed, link }); //WHERE parameters
		
		mDatabase.close();
	}
	
	public void updateItemDescription(String feed, String link, String description) {
		if (link.isEmpty() || description.isEmpty() || feed.isEmpty()) {
			Log.d(TAG, "link: "+ link+ "desc: " +description + "feed: " + feed);
			return;
		}
	
		mDatabase = mHelper.getWritableDatabase();
		
		ContentValues v = new ContentValues();		
		
		v.put(RssDatabaseHelper.COLUMN_DESCRIPTION, description);		
		
		mDatabase.update(RssDatabaseHelper.TABLE_NAME, v, 
				RssDatabaseHelper.COLUMN_FEED_NAME + "=? AND " + RssDatabaseHelper.COLUMN_LINK + "=?" , // WHERE 
				new String[] { feed, link }); //WHERE parameters
		
		mDatabase.close();
	}
}
