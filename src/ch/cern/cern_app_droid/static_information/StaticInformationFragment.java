package ch.cern.cern_app_droid.static_information;

import java.util.ArrayList;

import com.aphidmobile.flip.FlipViewController;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ch.cern.cern_app_droid.R;

public class StaticInformationFragment extends Fragment {
	
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
		fView.setAdapter(new CardsAdapter(inflater.getContext()));
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
						(TextView) convertView.findViewById(R.id.staticinformation_card_titleTextView);
				holder.descriptionTextView = 
						(TextView) convertView.findViewById(R.id.staticinformation_card_descriptionTextView);
				holder.imageImageView = 
						(ImageView) convertView.findViewById(R.id.staticinformation_card_image);
				
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
	
	

}
