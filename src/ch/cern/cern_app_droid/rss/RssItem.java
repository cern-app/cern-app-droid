package ch.cern.cern_app_droid.rss;

import org.horrabin.horrorss.RssItemBean;

import android.graphics.Bitmap;

public class RssItem extends RssItemBean {
	
	private Bitmap mMiniature;
	private boolean mRead = false;	
	
	public RssItem() {
		
	}
	
	public RssItem(RssItemBean item) {
		this.setAuthor(item.getAuthor());
		this.setCategory(item.getCategory());
		this.setDescription(item.getDescription());
		this.setLink(item.getLink());
		this.setPubDate(item.getPubDate());
		this.setTitle(item.getTitle());
	}

	public void setMiniature(Bitmap m) {
		mMiniature = m;
	}
	
	public Bitmap getMiniature() {
		return mMiniature;
	}

	public boolean isRead() {
		return mRead;
	}

	public void setRead(boolean mIsRead) {
		this.mRead = mIsRead;
	}	
}
