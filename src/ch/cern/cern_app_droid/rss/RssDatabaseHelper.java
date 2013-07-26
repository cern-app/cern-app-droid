package ch.cern.cern_app_droid.rss;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RssDatabaseHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "cern.db";
	public static final String TABLE_NAME = "rss_feed";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_FEED_NAME = "feed";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_IMAGE = "image";
	public static final String COLUMN_LINK = "link";
	public static final String COLUMN_READ = "read";
	
	private static final int VERSION = 1;
	
	private static final String QUERY_CREATE = "create table " + TABLE_NAME + " ("
											+ COLUMN_ID + " integer primary key autoincrement, "
											+ COLUMN_FEED_NAME + " TEXT NOT NULL, "
											+ COLUMN_TITLE + " TEXT NOT NULL, " 
											+ COLUMN_LINK + " TEXT NOT NULL, "
											+ COLUMN_DATE + " INTEGER NOT NULL DEFAULT (strftime('%s','now')), "
											+ COLUMN_DESCRIPTION + " TEXT, "
											+ COLUMN_IMAGE + " BLOB, "
											+ COLUMN_READ + " SMALLINT NOT NULL DEFAULT 0"
											+ " );";

	public RssDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
		if (context == null)
			Log.d("RSSDatabaseHelper", "RssDatabaseHelper(); context == null");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(QUERY_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

}
