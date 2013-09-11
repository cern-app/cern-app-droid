package ch.cern.cern_app_droid.static_information;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class StaticInformationCard {
	
	private static String CARDS_FOLDER = "cards/";

	private String mDescription;
	private String mImageFile;
	private String mTitle;
	
	private Bitmap mImage;
	
	public StaticInformationCard(String title, String description, String imageFile) {
		this.mDescription = description;
		this.mImageFile = imageFile;
		this.mTitle = title;
	}

	public String getDescription() {
		return mDescription;
	}

	public Bitmap getImageFile(Context context) {
		if (mImage == null) {
			mImage = getBitmapFromAsset(context, mImageFile); 
		}
		return mImage;
	}
	
	public String getTitle() {
		return mTitle;
	}
	
	public String getImageFilePath() {
		return mImageFile;
	}
	
	public static Bitmap getBitmapFromAsset(Context context, String strName)
    {
        AssetManager assetManager = context.getAssets();
        InputStream istr = null;
        try {
            istr = assetManager.open(CARDS_FOLDER + strName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        return bitmap;
    }

}
