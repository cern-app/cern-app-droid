package ch.cern.cern_app_droid.static_information;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ch.cern.cern_app_droid.R;

public class StaticInformationCardFragment extends Fragment {
	
	private static String TITLE_KEY = "title";
	private static String DESC_KEY = "description";
	private static String IMAGE_KEY = "image";
	
	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
	}
	
	public static StaticInformationCardFragment newInstance(StaticInformationCard card) {

		StaticInformationCardFragment pageFragment = new StaticInformationCardFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE_KEY, card.getTitle());
        bundle.putString(DESC_KEY, card.getDescription());
        bundle.putString(IMAGE_KEY, card.getImageFilePath());
        pageFragment.setArguments(bundle);
        return pageFragment;
        
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.staticinformation_card, null);

		Bundle args = getArguments();

		((TextView) v.findViewById(R.id.staticinformation_card_tablet_titleTextView1))
					.setText(args.getString(TITLE_KEY));
		((TextView) v.findViewById(R.id.staticinformation_card_tablet_descriptionTextView1))
					.setText(args.getString(DESC_KEY));
		((ImageView) v.findViewById(R.id.staticinformation_card_tablet_image1))
					.setImageBitmap(StaticInformationCard.getBitmapFromAsset(inflater.getContext(), args.getString(IMAGE_KEY)));
//		holder.imageImageView.setImageBitmap(card.getImageFile(getActivity()));
	
		return v;
	}
	
}
