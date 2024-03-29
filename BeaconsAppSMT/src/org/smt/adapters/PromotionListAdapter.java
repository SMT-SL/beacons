package org.smt.adapters;

import org.smt.R;
import org.smt.app.BeaconsApp;
import org.smt.holders.PromotionListHolder;
import org.smt.model.OfferDetailsDTO;
import org.smt.tasks.GuardarEnWalletTask;
import org.smt.utils.MyImageLoadingListener;
import org.smt.utils.Utils;

import org.smt.fragments.WalletFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PromotionListAdapter extends BaseAdapter {

	Context context;

	public PromotionListAdapter(Context context) {
		super();
		this.context = context;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		PromotionListHolder holder = null;

		if (convertView == null) {
			holder = new PromotionListHolder();
			convertView = li.inflate(R.layout.item_list_promociones, parent, false);

			holder.textPromo = ((TextView) convertView.findViewById(R.id.offer_name));
			holder.imagePromo = (ImageView) convertView.findViewById(R.id.image_promo);
			holder.textDesc = (TextView) convertView.findViewById(R.id.offer_desc);
			holder.addBtn = (ImageView) convertView.findViewById(R.id.add_btn);

			convertView.setTag(holder);

		} else {
			holder = (PromotionListHolder) convertView.getTag();
		}
		final OfferDetailsDTO promo = BeaconsApp.listOffer.get(position);
		holder.addBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int code=WalletFragment.addToWalletList(promo);
				if(code==200){//Agregamos en backend despues de comprobar que ya podemos agregar promocion en wallet
					Toast toast = Toast.makeText(context, "Promocion guardado en tu Wallet", Toast.LENGTH_SHORT);
					toast.show();
					new GuardarEnWalletTask(context,promo.getOfferId(),promo.getMajor(),promo.getMinor()).execute();
				}else{
					Toast toast = Toast.makeText(context, Utils.getMensaje(code), Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
		holder.textPromo.setText(promo.getName());
		if (!promo.getDescription().equals("null")) {
			holder.textDesc.setText(promo.getDescription());
		}
		// holder.imagePromo.setImageDrawable(context.getResources().getDrawable(R.drawable.frigo_logo_blanco_cuadrado));
		holder.imagePromo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher));

		if (promo.getOfferType() == 2) {
			((BeaconsApp) context.getApplicationContext()).getImageLoader().displayImage(promo.getOfferURL(), holder.imagePromo, new MyImageLoadingListener());
		}

		return convertView;

	}

	@Override
	public int getCount() {
		return BeaconsApp.listOffer.size();
	}

	@Override
	public Object getItem(int arg0) {
		return BeaconsApp.listOffer.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

}
