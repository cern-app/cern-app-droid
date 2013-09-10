package ch.cern.cern_app_droid.static_information;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterViewFlipper;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ch.cern.cern_app_droid.R;

import com.aphidmobile.flip.FlipViewController;

public class StaticInformationFragment extends Fragment {
	
	private static final String TAG = "StaticInformationFragment";
	
	ArrayList<StaticInformationCard> mCardsList;
	CardsPagerAdapter mAdapter;
	
	FlipViewController fView;
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
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView()");
		vPager = new ViewPager(inflater.getContext());
		
		vPager.setId(mCardsList.get(0).hashCode());
		vPager.setPageTransformer(true, new ZoomOutPageTransformer());
		
		if (getResources().getBoolean(R.bool.isTablet)) {
			mAdapter = new CardsPagerAdapter(getFragmentManager());
		} else {
			mAdapter = new CardsPagerAdapter(getFragmentManager());
		}
		
		vPager.setAdapter(mAdapter);
		return vPager;
		
	}
	
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
	
	private class CardsPagerAdapter extends FragmentPagerAdapter {

		public CardsPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public int getCount() {
			return mCardsList.size();
		}
		
		@Override
		public Fragment getItem(int pos) {
			return StaticInformationCardFragment.newInstance(mCardsList.get(pos));
		}
		
	}
	
	private class CardsAdapterTablet extends BaseAdapter {

		LayoutInflater inflater;
		
		public CardsAdapterTablet(Context context) {
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			Log.d(TAG, "mCardsList.size() = " + mCardsList.size());
			Log.d(TAG, "count: " + (mCardsList.size() / 2 + (mCardsList.size() % 2)));
			return mCardsList.size() / 2 + (mCardsList.size() % 2);
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.staticinformation_card_tablet, null);
				CardLayoutHolder holder = new CardLayoutHolder();
				holder.titleTextView1 = 
						(TextView) convertView.findViewById(R.id.staticinformation_card_tablet_titleTextView1);
				holder.titleTextView1.setText("Tytul1");
				holder.descriptionTextView1 = 
						(TextView) convertView.findViewById(R.id.staticinformation_card_tablet_descriptionTextView1);
				holder.imageImageView1 = 
						(ImageView) convertView.findViewById(R.id.staticinformation_card_tablet_image1);
				holder.titleTextView2 = 
						(TextView) convertView.findViewById(R.id.staticinformation_card_tablet_titleTextView2);
				holder.descriptionTextView2 = 
						(TextView) convertView.findViewById(R.id.staticinformation_card_tablet_descriptionTextView2);
				holder.imageImageView2 = 
						(ImageView) convertView.findViewById(R.id.staticinformation_card_tablet_image2);
				
				convertView.setTag(holder);
			}
			
			CardLayoutHolder holder = (CardLayoutHolder) convertView.getTag();

			final StaticInformationCard card1 = mCardsList.get(2*position);

			holder.titleTextView1.setText("title1: " + card1.getTitle());
			holder.descriptionTextView1.setText(card1.getDescription());
			holder.imageImageView1.setImageBitmap(card1.getImageFile(getActivity()));
			
			if (2*position + 1 < mCardsList.size()) {
				final StaticInformationCard card2 = mCardsList.get(2*position + 1);
				holder.titleTextView2.setVisibility(View.VISIBLE);
				holder.descriptionTextView2.setVisibility(View.VISIBLE);
				holder.imageImageView2.setVisibility(View.VISIBLE);
				holder.titleTextView2.setText("title2: " + card2.getTitle());
				holder.descriptionTextView2.setText(card2.getDescription());
				holder.imageImageView2.setImageBitmap(card2.getImageFile(getActivity()));
			} else {
				holder.titleTextView2.setVisibility(View.INVISIBLE);
				holder.descriptionTextView2.setVisibility(View.INVISIBLE);
				holder.imageImageView2.setVisibility(View.INVISIBLE);
			}
		
			return convertView;
		}
		
		private class CardLayoutHolder {
			TextView titleTextView1;
			TextView descriptionTextView1;
			ImageView imageImageView1;
			TextView titleTextView2;
			TextView descriptionTextView2;
			ImageView imageImageView2;
		}
		
	}
	
	

}
