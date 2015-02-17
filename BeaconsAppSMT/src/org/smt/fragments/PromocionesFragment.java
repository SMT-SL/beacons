package org.smt.fragments;

import java.util.ArrayList;
import java.util.List;

import org.smt.R;
import org.smt.activity.ImageActivity;
import org.smt.adapters.PromotionListAdapter;
import org.smt.app.BeaconsApp;
import org.smt.model.OfferDetailsDTO;
import org.smt.model.RegionInfoDTO;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class PromocionesFragment extends Fragment {
	public static final int REQUEST_BLUETOOTH_ENABLE = 1;
	private ListView listPromociones;
	public static  List<RegionInfoDTO> regionsEncontrado = new ArrayList<RegionInfoDTO>();
	public static PromotionListAdapter promotionsAdapter;
	private static ProgressBar spinner;
	private static TextView txtState;
	private static Context context;
	private static View rootView;
	java.util.Timer timer;


	public PromocionesFragment(Context context) {
	}

	public PromocionesFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context =getActivity();
		rootView = inflater.inflate(R.layout.fragment_promociones, container, false);
		if(BeaconsApp.getLocation()==null){
			@SuppressWarnings("deprecation")
			String locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

			if (locationProviders.contains("gps") || locationProviders.contains("network")) {

				LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
				String locationProvider = LocationManager.NETWORK_PROVIDER;
				BeaconsApp.setLocation(locationManager.getLastKnownLocation(locationProvider));
			}
		}
		
		if((BeaconsApp.listOffer==null||BeaconsApp.listOffer.isEmpty())&&BeaconsApp.getLocation()!=null){
			BeaconsApp.findPromociones(BeaconsApp.getLocation());
		}
//		if(BeaconsApp.listOffer==null || BeaconsApp.getLocation()==null){
//			BeaconsApp.listOffer = new ArrayList<OfferDetailsDTO>();
//		}

		
		promotionsAdapter = new PromotionListAdapter(context);

		listPromociones = (ListView) rootView.findViewById(R.id.listViewPromociones);
		setSpinner((ProgressBar) rootView.findViewById(R.id.pbHeaderProgress));
		listPromociones.setAdapter(promotionsAdapter);
		setTxtState((TextView) rootView.findViewById(R.id.txtStateBuscarPr));

		

		listPromociones.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	
				if (((OfferDetailsDTO) arg0.getAdapter().getItem(arg2)).getOfferType() != 2) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(((OfferDetailsDTO) arg0.getAdapter().getItem(arg2)).getOfferURL())));
				} else {
					 Intent i = new Intent(context,ImageActivity.class);
					 String web =
					 ((OfferDetailsDTO)arg0.getAdapter().getItem(arg2)).getOfferURL();
					 i.putExtra("image", web);
					 startActivity(i);
				}

			}
		});

		actualizarEstadoApp("");
		return rootView;
	}
	 @Override
	  public void onResume() {
		 promotionsAdapter.notifyDataSetChanged();
	     super.onResume();
	  }

	 private static void actualizarMensajeConfiguracion(final int locationState,final int bluetoohState){
		 
			((Activity) context).runOnUiThread(new Runnable() {
	    	    public void run() {
	    	    	final RelativeLayout locationMessage = (RelativeLayout) rootView.findViewById(R.id.localizacionError);
	    	    	final RelativeLayout bluetoothMessage = (RelativeLayout) rootView.findViewById(R.id.blueToothError);
	    	    	if(locationMessage!=null){
	    		    	locationMessage.setVisibility(locationState);
	    	    	}
	    	    	if(bluetoothMessage!=null){
	    	    		bluetoothMessage.setVisibility(bluetoohState);
	    	    	}
	    	    	
	    	    }
	    	});
	 }
	public static void actualizarEstadoApp(final String msg) {
		if(msg.isEmpty()){
			messageToDisplay("Comprobando la configuracion");
			
			
			boolean isBluetoothOk=isBloothActivated();
			boolean isLocationActivado=isLocationActivado();
			boolean isConfiguracionOk=false;
			if (isLocationActivado && isBluetoothOk) {
				actualizarMensajeConfiguracion(View.GONE,View.GONE);
				isConfiguracionOk=true;
			} else if(!isLocationActivado && !isBluetoothOk) {
				actualizarMensajeConfiguracion(View.VISIBLE,View.VISIBLE);
				isConfiguracionOk=false;
				
			}else if(!isLocationActivado){
				actualizarMensajeConfiguracion(View.VISIBLE,View.GONE);
				isConfiguracionOk=false;
				
			}else if(!isBluetoothOk){
				actualizarMensajeConfiguracion(View.GONE,View.VISIBLE);
				isConfiguracionOk=false;
			}

			if(!isConfiguracionOk){//Configuracion esta mal
				messageToDisplay("Error en configuracion, compruebalo ");
			}else if(BeaconsApp.getLocation()==null&&isConfiguracionOk){ //Configuracion esta ok, pero no hemos podido encontrar location
				messageToDisplay("No se ha podido obtener localizacion");
			}else if(BeaconsApp.listOffer!=null && BeaconsApp.listOffer.size()>0&&isConfiguracionOk){//Configuracion esta bien y hemos encontrado promociones
				messageToDisplay("Promicones encotnradas");
			
				spinner.setVisibility(View.GONE);
			}else if(!isPromotionsEncontrados() && isConfiguracionOk && !isRegionesEncontrados()){ //Todo esta bien excepto que no se ha encontrado ningun beacons
				
				messageToDisplay("No se encontrado puntos de ofertas cercanas");
			}
		}else{
			messageToDisplay(msg);
		}
		
		
	}

	private static boolean isPromotionsEncontrados(){
		return BeaconsApp.listOffer!=null &&  BeaconsApp.listOffer.size()>0;
	}
	private static boolean isRegionesEncontrados(){
		return (BeaconsApp.regionsFound!=null && BeaconsApp.regionsFound.size()>0);
	}
	private static boolean  isLocationActivado() {
		@SuppressWarnings("deprecation")
		String locationProviders = Settings.Secure.getString( context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (!locationProviders.contains("gps") && !locationProviders.contains("network")) {
			return false;
		}
		return true;
	}

	private static boolean isBloothActivated() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			return false;
		}
		return true;
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
	
	private static void messageToDisplay(final String line) {
		if(context!=null){
			((Activity) context).runOnUiThread(new Runnable() {
	    	    public void run() {
	    	    	if(txtState!=null){
	    	    		txtState.setText(line);
	    	    	}
	    	    	
	     	    	    		
	    	    }
	    	});
	    }
		
	}
		
}
