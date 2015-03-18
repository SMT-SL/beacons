package org.smt.fragments;

import java.util.ArrayList;

import org.smt.R;
import org.smt.activity.ImageActivity;
import org.smt.adapters.WalletListaAdapter;
import org.smt.model.OfferDetailsDTO;
import org.smt.tasks.ObtenerPromocionWalltetTask;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class WalletFragment extends Fragment {
	
	private ListView walletListView;

	public static ArrayList<OfferDetailsDTO> walletPromocionList;
	public static WalletListaAdapter walletListAdapter;
	private ProgressBar spinner;
	private TextView txtState;
	private Context context;
	private View rootView;

	public WalletFragment(Context context) {
		this.context = context;
	}
	public WalletFragment() {
	
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context =getActivity();
		setWalletPromocionList();
		
		rootView = inflater.inflate(R.layout.fragment_wallet, container, false);
		walletListAdapter = new WalletListaAdapter(context);

		walletListView = (ListView) rootView.findViewById(R.id.listViewWallet);
		setSpinner((ProgressBar) rootView.findViewById(R.id.pbHeaderProgress));
		walletListView.setAdapter(walletListAdapter);
		setTxtState((TextView) rootView.findViewById(R.id.txtState));

		walletListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
//				Toast.makeText(context, "Stop Clicking me", Toast.LENGTH_SHORT).show();
				if (((OfferDetailsDTO) arg0.getAdapter().getItem(arg2)).getOfferType() != 2) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(((OfferDetailsDTO) arg0.getAdapter().getItem(arg2)).getOfferURL())));
				} else {
					 Intent i = new Intent(getActivity(), ImageActivity.class);
					 String web =
					 ((OfferDetailsDTO)arg0.getAdapter().getItem(arg2)).getOfferURL();
					 i.putExtra("image", web);
					 startActivity(i);
				}

			}
		});
		actualizarEstadoTexto();
		// saliendo = true;
		return rootView;
	}

	private void actualizarEstadoTexto(){
		if(txtState!=null){
			if(walletPromocionList==null || walletPromocionList.size()==0){
				txtState.setText("No tienes guardado ninguna promocion en Wallet");
			}else{
				txtState.setText("Promociones guardadas en Wallet");
			}
		}
	}
	public TextView getTxtState() {
		return txtState;
	}

	public void setTxtState(TextView txtState) {
		this.txtState = txtState;
	}

	public ProgressBar getSpinner() {
		return spinner;
	}

	public void setSpinner(ProgressBar spinner) {
		this.spinner = spinner;
	}

	public static ArrayList<OfferDetailsDTO> getWalletPromocionList() {
		return walletPromocionList;
	}

	private  void setWalletPromocionList() {
//		WalletFragment.walletPromocionList = walletPromocionList;
		new ObtenerPromocionWalltetTask(this.context).execute();

	}
	
	public  static int addToWalletList(OfferDetailsDTO promocionAguardar) {
		int code=-1;
		if(!isExistPromocion(promocionAguardar)){
			OfferDetailsDTO wPromocionObject = new OfferDetailsDTO();
			if (walletPromocionList == null) {
				walletPromocionList = new ArrayList<OfferDetailsDTO>();
			}
			wPromocionObject.setDescription(promocionAguardar.getDescription());
			wPromocionObject.setLocationTiendaPromociones("");
			wPromocionObject.setName(promocionAguardar.getName());
			wPromocionObject.setOfferId(promocionAguardar.getOfferId());
			wPromocionObject.setOfferURL(promocionAguardar.getOfferURL());
			wPromocionObject.setThumbnail(promocionAguardar.getThumbnail());
			walletPromocionList.add(wPromocionObject);
			
			code=200;
		}else{
			code= 202;
		}
		
		return code;
	}
	
	private static boolean isExistPromocion(OfferDetailsDTO promocionAguardar){
		Boolean existePromocion=false;
		if(walletPromocionList!=null&& walletPromocionList.size()>0){
			for(int i=0;i<walletPromocionList.size();i++){
				if(walletPromocionList.get(i).getOfferId()==promocionAguardar.getOfferId()){
					i=walletPromocionList.size();
					existePromocion=true;
				}
			}
		}
		
		return existePromocion;
	}

	public static int removeFromWalletList(final int offerId ) {
		int code = -1;
		if(walletPromocionList!=null&& walletPromocionList.size()>0){
			for(int i=0;i<walletPromocionList.size();i++){
				if(walletPromocionList.get(i).getOfferId()==offerId){
					walletPromocionList.remove(i);
					WalletFragment.walletListAdapter.notifyDataSetChanged();
					code=200;
				}
			}
		}
		return code;
		
	}
}
