package com.smt.beacons.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smt.beacons.R;
import com.smt.beacons.activity.EasiActivity;
import com.smt.beacons.holders.PromotionListHolder;
import com.smt.beacons.model.OfferDetailsDTO;
import com.smt.beacons.utils.BeaconsApp;
import com.smt.beacons.utils.MyImageLoadingListener;

public class PromotionListAdapter extends BaseAdapter {

//	List<OfferDetailsDTO> beacons;
	EasiActivity act;
	Context context;

	public PromotionListAdapter(Context context) {
		super();
//		beacons = _beacons;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		PromotionListHolder holder = null;

		if (convertView == null) {
			holder = new PromotionListHolder();
			convertView = li.inflate(R.layout.listitem_device, parent, false);

			holder.textPromo = ((TextView) convertView.findViewById(R.id.offer_name));
			holder.imagePromo= (ImageView) convertView.findViewById(R.id.image_promo);
			holder.textDesc = (TextView) convertView.findViewById(R.id.offer_desc);

			convertView.setTag(holder);

		} else {
			holder = (PromotionListHolder) convertView.getTag();
		}

		OfferDetailsDTO promo = EasiActivity.promotions.get(position);

		holder.textPromo.setText(promo.getName());
		if (!promo.getDescription().equals("null")){
			holder.textDesc.setText(promo.getDescription());
		}
//		holder.imagePromo.setImageDrawable(context.getResources().getDrawable(R.drawable.frigo_logo_blanco_cuadrado));
		holder.imagePromo.setImageDrawable(context.getResources().getDrawable(R.drawable.logo_smt));
		
		if (promo.getOfferType()==2){
			((BeaconsApp) context.getApplicationContext()).getImageLoader().displayImage(promo.getOfferURL(), holder.imagePromo, new MyImageLoadingListener());
		}

		return convertView;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return EasiActivity.promotions.size();
	}

	@Override
	public Object getItem(int arg0) {
		return EasiActivity.promotions.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}
