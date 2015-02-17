package org.smt.adapters;

import org.smt.R;
import org.smt.app.BeaconsApp;
import org.smt.fragments.WalletFragment;
import org.smt.holders.PromotionListHolder;
import org.smt.model.OfferDetailsDTO;
import org.smt.tasks.BorrarDeWalletTask;
import org.smt.utils.MyImageLoadingListener;
import org.smt.utils.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WalletListaAdapter  extends BaseAdapter{
	
		Context context;

		public WalletListaAdapter(Context context) {
			super();
			this.context = context;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			PromotionListHolder holder = null;

			if (convertView == null) {
				holder = new PromotionListHolder();
				convertView = li.inflate(R.layout.item_list_wallet, parent, false);

				holder.textPromo = ((TextView) convertView.findViewById(R.id.offer_name));
				holder.imagePromo = (ImageView) convertView.findViewById(R.id.image_promo);
				holder.textDesc = (TextView) convertView.findViewById(R.id.offer_desc);
				holder.addBtn = (ImageView) convertView.findViewById(R.id.add_btn);

				convertView.setTag(holder);

			} else {
				holder = (PromotionListHolder) convertView.getTag();
			}

			final OfferDetailsDTO promo = WalletFragment.getWalletPromocionList().get(position);
			holder.addBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int code=WalletFragment.removeFromWalletList(promo.getOfferId());;
					
					if(code==200){//Agregamos en backend despues de comprobar que ya podemos agregar promocion en wallet
						Toast toast = Toast.makeText(context, "Se ha borrado la promocion seleccionado", Toast.LENGTH_SHORT);
						toast.show();
						new BorrarDeWalletTask(context,promo.getOfferId()).execute();
					}else{
						Toast toast = Toast.makeText(context, Utils.getMensaje(code), Toast.LENGTH_SHORT);
						toast.show();
					}

				}
			});
			holder.textPromo.setText(promo.getName());
			if (promo.getDescription()!=null) {
				holder.textDesc.setText(promo.getDescription());
			}

			holder.imagePromo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher));

			if (promo.getOfferType() == 2) {
				((BeaconsApp) context.getApplicationContext()).getImageLoader().displayImage(promo.getOfferURL(), holder.imagePromo, new MyImageLoadingListener());
			}

			return convertView;

		}

		@Override
		public int getCount() {
			if(WalletFragment.getWalletPromocionList()!=null){
				return WalletFragment.getWalletPromocionList().size();
			}
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			return WalletFragment.getWalletPromocionList().get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

	}


