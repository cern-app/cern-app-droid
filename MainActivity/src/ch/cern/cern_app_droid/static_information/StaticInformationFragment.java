package ch.cern.cern_app_droid.static_information;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ch.cern.cern_app_droid.R;

public class StaticInformationFragment extends Fragment {
	
	private static final String TAG = "StaticInformationFragment";

	public static final String TITLE = "title";
	
	ArrayList<StaticInformationCard> mCardsList;
	PagerAdapter mAdapter;
	
	ViewPager vPager;

	
	public StaticInformationFragment() {
		mCardsList = new ArrayList<StaticInformationCard>();
	}
	
	@Override
	public void onAttach(Activity activity) {		
		super.onAttach(activity);
	}

	public void setCardList(ArrayList<StaticInformationCard> cardsList) {
		mCardsList.addAll(cardsList);	
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView()");
		vPager = new ViewPager(inflater.getContext());
		
		vPager.setId(mCardsList.get(0).hashCode());
		vPager.setPageTransformer(true, new ZoomOutPageTransformer());
		
		if (getResources().getBoolean(R.bool.isTablet)) {
			mAdapter = new CardsAdapterTablet(inflater);
		} else {
			mAdapter = new CardsPagerAdapter(inflater);
		}
		
		vPager.setAdapter(mAdapter);
		return vPager;
		
	}
	
	//source: https://developer.android.com/training/animation/screen-slide.html
	//licence: Creative Commons Attribution 2.5 (as per footer)
	private class ZoomOutPageTransformer implements ViewPager.PageTransformer {
	    private static final float MIN_SCALE = 0.85f;
	    private static final float MIN_ALPHA = 0.5f;

	    public void transformPage(View view, float position) {
	        int pageWidth = view.getWidth();
	        int pageHeight = view.getHeight();

	        if (position < -1) { // [-Infinity,-1)
	            // This page is way off-screen to the left.
	            view.setAlpha(0);

	        } else if (position <= 1) { // [-1,1]
	            // Modify the default slide transition to shrink the page as well
	            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
	            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
	            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
	            if (position < 0) {
	                view.setTranslationX(horzMargin - vertMargin / 2);
	            } else {
	                view.setTranslationX(-horzMargin + vertMargin / 2);
	            }

	            // Scale the page down (between MIN_SCALE and 1)
	            view.setScaleX(scaleFactor);
	            view.setScaleY(scaleFactor);

	            // Fade the page relative to its size.
	            view.setAlpha(MIN_ALPHA +
	                    (scaleFactor - MIN_SCALE) /
	                    (1 - MIN_SCALE) * (1 - MIN_ALPHA));

	        } else { // (1,+Infinity]
	            // This page is way off-screen to the right.
	            view.setAlpha(0);
	        }
	    }
	}
	
	private class CardsPagerAdapter extends PagerAdapter {

		LayoutInflater inflater;
		ArrayList<View> mConvertedViews;
		
		public CardsPagerAdapter(LayoutInflater inflater) {
			this.inflater = inflater;
			mConvertedViews = new ArrayList<View>();
		}
		
		@Override
		public int getCount() {
			return mCardsList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return (view == ((View) object));
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View v;
			if ( mConvertedViews.isEmpty() ) {
				v = inflater.inflate(R.layout.staticinformation_card, null);
				Holder holder = new Holder();
				holder.title = (TextView) v.findViewById(R.id.staticinformation_card_tablet_titleTextView1); 
				holder.description = (TextView) v.findViewById(R.id.staticinformation_card_tablet_descriptionTextView1);
				holder.image = (ImageView) v.findViewById(R.id.staticinformation_card_tablet_image1);
				v.setTag(holder);
			} else {
				v = mConvertedViews.remove(0);
			}
			Holder holder = (Holder) v.getTag();
			
			StaticInformationCard card = mCardsList.get(position);
			holder.title.setText(card.getTitle());
			holder.description.setText(card.getDescription());
			holder.image.setImageBitmap(card.getImageFile(inflater.getContext()));
			
			container.addView(v);
			return v;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			mConvertedViews.add((View) object);
			container.removeView((View) object);
		}
		
		private class Holder {
			public TextView title;
			public TextView description;
			public ImageView image;
		}
	}
	
	private class CardsAdapterTablet extends PagerAdapter {

		LayoutInflater inflater;
		ArrayList<View> mConvertedViews;
		
		public CardsAdapterTablet(LayoutInflater inflater) {
			this.inflater = inflater;
			mConvertedViews = new ArrayList<View>();
		}
		
		@Override
		public int getCount() {
			return mCardsList.size() / 2 + (mCardsList.size() % 2);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			View v;
			if ( mConvertedViews.isEmpty() ) {
				v = inflater.inflate(R.layout.staticinformation_card_tablet, null);
				Holder holder = new Holder();
				holder.titleTextView1 = 
						(TextView) v.findViewById(R.id.staticinformation_card_tablet_titleTextView1);
				holder.descriptionTextView1 = 
						(TextView) v.findViewById(R.id.staticinformation_card_tablet_descriptionTextView1);
				holder.imageImageView1 = 
						(ImageView) v.findViewById(R.id.staticinformation_card_tablet_image1);
				holder.titleTextView2 = 
						(TextView) v.findViewById(R.id.staticinformation_card_tablet_titleTextView2);
				holder.descriptionTextView2 = 
						(TextView) v.findViewById(R.id.staticinformation_card_tablet_descriptionTextView2);
				holder.imageImageView2 = 
						(ImageView) v.findViewById(R.id.staticinformation_card_tablet_image2);
				
				v.setTag(holder);
			} else {
				v = mConvertedViews.remove(0);
			}
			
			Holder holder = (Holder) v.getTag();

			final StaticInformationCard card1 = mCardsList.get(2*position);

			holder.titleTextView1.setText(card1.getTitle());
			holder.descriptionTextView1.setText(card1.getDescription());
			holder.imageImageView1.setImageBitmap(card1.getImageFile(getActivity()));
			
			if (2*position + 1 < mCardsList.size()) {
				final StaticInformationCard card2 = mCardsList.get(2*position + 1);
				holder.titleTextView2.setVisibility(View.VISIBLE);
				holder.descriptionTextView2.setVisibility(View.VISIBLE);
				holder.imageImageView2.setVisibility(View.VISIBLE);
				holder.titleTextView2.setText(card2.getTitle());
				holder.descriptionTextView2.setText(card2.getDescription());
				holder.imageImageView2.setImageBitmap(card2.getImageFile(getActivity()));
			} else {
				holder.titleTextView2.setVisibility(View.INVISIBLE);
				holder.descriptionTextView2.setVisibility(View.INVISIBLE);
				holder.imageImageView2.setVisibility(View.INVISIBLE);
			}
			
			container.addView(v);
			return v;
		}
		
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return (view == (View) object);
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			mConvertedViews.add((View) object);
			container.removeView((View) object);
		}
		
		private class Holder {
			TextView titleTextView1;
			TextView descriptionTextView1;
			ImageView imageImageView1;
			TextView titleTextView2;
			TextView descriptionTextView2;
			ImageView imageImageView2;
		}		
	}
}
