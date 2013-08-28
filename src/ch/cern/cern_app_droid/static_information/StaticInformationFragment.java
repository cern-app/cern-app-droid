package ch.cern.cern_app_droid.static_information;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ch.cern.cern_app_droid.R;

import com.aphidmobile.flip.FlipViewController;

public class StaticInformationFragment extends Fragment {
	
	private static final String TAG = "StaticInformationFragment";
	
	ArrayList<StaticInformationCard> mCardsList;
	
	FlipViewController fView;
	
	public StaticInformationFragment() {
		mCardsList = new ArrayList<StaticInformationCard>();
	}

	public void setCardList(ArrayList<StaticInformationCard> cardsList) {
		mCardsList = cardsList;	
	}
	
	@Override
	public void onPause() {
		super.onPause();
		fView.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		fView.onResume();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		fView = new FlipViewController(inflater.getContext(), FlipViewController.HORIZONTAL);
		if (getResources().getBoolean(R.bool.isTablet)) {
			fView.setAdapter(new CardsAdapterTablet(inflater.getContext()));
		} else {
			fView.setAdapter(new CardsAdapter(inflater.getContext()));			
		}
		
		return fView;
		
//		View v = inflater.inflate(R.layout.staticinformation_card, null);
//		
//		StaticInformationCard card = mCardsList.get(0);
//		
//		((ImageView) v.findViewById(R.id.staticinformation_card_image)).setImageBitmap(card.getImageFile((getActivity())));
//		((TextView) v.findViewById(R.id.staticinformation_card_titleTextView)).setText(card.getTitle());
//		((TextView) v.findViewById(R.id.staticinformation_card_descriptionTextView)).setText(card.getDescription());

//		return fView;
	}
	
	private class CardsAdapter extends BaseAdapter {

		LayoutInflater inflater;
		
		public CardsAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return mCardsList.size();
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
				convertView = inflater.inflate(R.layout.staticinformation_card, null);
				CardLayoutHolder holder = new CardLayoutHolder();
				holder.titleTextView = 
						(TextView) convertView.findViewById(R.id.staticinformation_card_tablet_titleTextView1);
				holder.descriptionTextView = 
						(TextView) convertView.findViewById(R.id.staticinformation_card_tablet_descriptionTextView1);
				holder.imageImageView = 
						(ImageView) convertView.findViewById(R.id.staticinformation_card_tablet_image1);
				
				convertView.setTag(holder);
			}
			
			final StaticInformationCard card = mCardsList.get(position);
			
			CardLayoutHolder holder = (CardLayoutHolder) convertView.getTag();

			holder.titleTextView.setText(card.getTitle());
			holder.descriptionTextView.setText(card.getDescription());
			holder.imageImageView.setImageBitmap(card.getImageFile(getActivity()));
		
			return convertView;
		}
		
		private class CardLayoutHolder {
			TextView titleTextView;
			TextView descriptionTextView;
			ImageView imageImageView;
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
