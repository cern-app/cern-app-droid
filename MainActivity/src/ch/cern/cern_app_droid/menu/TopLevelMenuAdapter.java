package ch.cern.cern_app_droid.menu;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.TextView;
import ch.cern.cern_app_droid.MainActivity;
import ch.cern.cern_app_droid.R;

public class TopLevelMenuAdapter extends ArrayAdapter<MenuItem> {
	

	private static final String TAG = "TopLevelMenuAdapter";

	ArrayList<MenuItem> mItems;
	MainActivity mContext;
	LayoutInflater mInflater;
	SparseBooleanArray expanded; // Remembers whether top level menu item is expanded or not
	
	SparseArray<SecondLevelMenu> mSubMenus;
	
	
	public TopLevelMenuAdapter(Context context, int textViewResourceId,
			List<MenuItem> objects) {
		super(context, textViewResourceId, objects);
		mItems = new ArrayList<MenuItem>();
		mItems.addAll(objects);
		mContext = (MainActivity) context;
		mSubMenus = new SparseArray<SecondLevelMenu>();
		expanded = new SparseBooleanArray();
		mInflater = LayoutInflater.from(context);
	}
	
//	public TopLevelMenuAdapter(MainActivity context, LayoutInflater inflater, ArrayList<MenuItem> items) {
//		mItems = items;
//		mInflater = inflater;
//		mContext = context;
//		mSubMenus = new SparseArray<SecondLevelMenu>();
//		expanded = new SparseBooleanArray();
//	}
	


	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LinearLayout v;
		
		v = (LinearLayout) mInflater.inflate(R.layout.menu_toplevel_row, null);
		//ToDo: Add own cache to make use of convertView 
		//		Possible with use of ViewHolder set as TAG for our views
		
		TextView t = (TextView) v.findViewById(R.id.menu_toplevel_title);
		final ImageView arrow = (ImageView) v.findViewById(R.id.menu_toplevel_expandImage);
		View onClickLayout = v.findViewById(R.id.menu_toplevel_onClickLayout);
		MenuItem m = mItems.get(position);
		t.setText(m.title);
		ImageView i = (ImageView) v.findViewById(R.id.menu_toplevel_image);
		
		//Set image for menu item (if exists)
		if (m.image != null && !m.image.isEmpty()) {
			try {
				InputStream is = mContext.getAssets().open("GUI/" + m.image);
				Drawable d = Drawable.createFromStream(is, m.image);
				i.setImageDrawable(d);
				i.setVisibility(View.VISIBLE);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			i.setVisibility(View.GONE);			
		}
		
		if (mSubMenus.get(position) == null) {
			final SecondLevelMenu slm = new SecondLevelMenu(mContext, 1);
			slm.setElements(mItems.get(position).items);
			mSubMenus.put(position, slm);
			expanded.put(position, true);
			v.addView(slm);
			
			onClickLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					expanded.put(position, !expanded.get(position));
					if (expanded.get(position))
						arrow.animate().rotation(0).start();
					else
						arrow.animate().rotation(90).start();
					slm.toggle();
					Log.d(TAG, "TopLevelAdapter.onClick; subMenus == null");
				}
			});
			
		} else {
			final SecondLevelMenu slm = mSubMenus.get(position);
			((LinearLayout)slm.getParent()).removeView(slm);
			v.addView(slm);
			
			onClickLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					expanded.put(position, !expanded.get(position));
					if (expanded.get(position))
						arrow.animate().rotation(0).start();
					else
						arrow.animate().rotation(90).start();
					slm.toggle();		
					Log.d(TAG, "TopLevelAdapter.onClick; subMenus != null");
				}
			});
		}
		return v;
	}

	private class SecondLevelMenu extends LinearLayout {

		boolean mVisible = true;
		int mLevel;
		SparseBooleanArray mExpanded;
		
		public SecondLevelMenu(Context context, int level) {
			super(context);
			this.setOrientation(LinearLayout.VERTICAL);
			mLevel = level;		
			mExpanded = new SparseBooleanArray();
		}
		
		private void setElements(ArrayList<MenuItem> items) {
			if (items.size() > 0) {
				int i = 0;
				for (final MenuItem s : items) {
					View v = mInflater.inflate(R.layout.menu_level2_row, null);
					TextView zt = (TextView) v.findViewById(R.id.menu_level2_title);
					View onClickView = v.findViewById(R.id.menu_level2_onClickLayout);
					final ImageView arrow = (ImageView) v.findViewById(R.id.menu_level2_expandImage);
					if (s.items.size() <= 0)
						arrow.setAlpha(0f);
					zt.setText(s.title);
					
					if (mLevel == 2) {
						zt.setX(40);
					}
					
					if (s.image != null && !s.image.isEmpty()) {
						try {
							InputStream is = mContext.getAssets().open("GUI/" + s.image);
							Drawable d = Drawable.createFromStream(is, s.image);
							ImageView image = (ImageView) v.findViewById(R.id.menu_level2_image);
							image.setImageDrawable(d);
							image.setVisibility(View.VISIBLE);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					onClickView.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							mContext.onMenuClick(s);							
						}
					});

					addView(v);
					
					final int t = i;
					if (s.items.size() > 0) {
						final SecondLevelMenu slm = new SecondLevelMenu(getContext(), mLevel + 1);
						slm.setElements(s.items);
						addView(slm);
						onClickView.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								slm.toggle();		
								mExpanded.put(t, !mExpanded.get(t));
								if (mExpanded.get(t))
									arrow.animate().rotation(90).start();
								else
									arrow.animate().rotation(0).start();
								Log.d(TAG, "SecondLevel.onClick; items.size > 0");
//								mContext.onMenuClick(s);
							}
						});
					} else {
						//ToDo: Set interface
					}
					i++;
				}				
			}
		}
		
		private void toggle() {
			mVisible = !mVisible;
			if (!mVisible) {
				collapse(this);
			}
			else {
				expand(this);
			}				
		}
	}		
	
	// Expands view to match its final height
	private void expand(final View v) {
		// http://stackoverflow.com/a/13381228/1349855
	    v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	    
	    final int initialHeight = v.getLayoutParams().height;
	    final int targetHeight = v.getMeasuredHeight() - initialHeight;

	    v.setVisibility(View.VISIBLE);
	    Animation a = new Animation()
	    {
	        @Override
	        protected void applyTransformation(float interpolatedTime, Transformation t) {
	        	if (interpolatedTime == 1) {
	        		v.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
	        	} else {
	        		v.getLayoutParams().height = initialHeight + (int)(targetHeight * interpolatedTime);
	        	}
	        	v.requestLayout();
	        }
	        @Override
	        public boolean willChangeBounds() {
	            return true;
	        }
	    };

	    // 5dp/ms
	    a.setDuration(500);
	    v.startAnimation(a);
	}
	
	private void collapse(final View v) {
		// http://stackoverflow.com/a/13381228/1349855
	    final int initialHeight = v.getMeasuredHeight();

	    Animation a = new Animation()
	    {
	        @Override
	        protected void applyTransformation(float interpolatedTime, Transformation t) {
	            if(interpolatedTime == 1){
	                v.setVisibility(View.GONE);
	            }else{
	                v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
	                v.requestLayout();
	            }
	        }

	        @Override
	        public boolean willChangeBounds() {
	            return true;
	        }
	    };

	    // 5dp/ms
	    a.setDuration(500);
	    v.startAnimation(a);
	}	
}