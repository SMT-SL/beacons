package org.smt.fragments;

import java.util.ArrayList;

import org.smt.R;
import org.smt.activity.ImageActivity;
import org.smt.activity.MainActivity;
import org.smt.adapters.PromotionListAdapter;
import org.smt.model.OfferDetailsDTO;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
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
import android.widget.Toast;

import com.google.android.gms.location.LocationClient;
//import org.smt.activity.EasiActivity;
//import org.smt.activity.ImageActivity;

public class PromocionesFragment extends Fragment {
	public static final int REQUEST_BLUETOOTH_ENABLE = 1;
	// private CheckBox chkIos, chkAndroid;
	private ListView listPromociones;

	// private BeaconsAdapter _beaconsAdapter;
	// private static IBeaconProtocol _ibp;

//	private static ArrayList<Beacon> _beacons;
	public static ArrayList<OfferDetailsDTO> promotions;
	public static PromotionListAdapter promotionsAdapter;
	// private List<BeaconInfoDTO> list = new ArrayList<BeaconInfoDTO>();
	// private Messenger serviceMessenger = null;
	// private Menu _menu;
	// private final static int REQUEST_ENABLE_BT = 1;
	// private boolean saliendo;
	private static ProgressBar spinner;
	private static TextView txtState;
	private static Context context;
	private static View rootView;
	java.util.Timer timer;

	Location mCurrentLocation;
	LocationClient mLocationClient;

	public PromocionesFragment(Context context) {
//		this.context = context;
	}

	public PromocionesFragment() {
//		this.context =getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context =getActivity();
		@SuppressWarnings("deprecation")
		String locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

		if (locationProviders.contains("gps") || locationProviders.contains("network")) {

			LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			String locationProvider = LocationManager.NETWORK_PROVIDER;
			mCurrentLocation = locationManager.getLastKnownLocation(locationProvider);
		}
		if(promotions==null || mCurrentLocation==null){
			promotions = new ArrayList<OfferDetailsDTO>();
		}

		rootView = inflater.inflate(R.layout.fragment_promociones, container, false);
		promotionsAdapter = new PromotionListAdapter(context);

		listPromociones = (ListView) rootView.findViewById(R.id.listViewPromociones);
		setSpinner((ProgressBar) rootView.findViewById(R.id.pbHeaderProgress));
		listPromociones.setAdapter(promotionsAdapter);
		setTxtState((TextView) rootView.findViewById(R.id.txtState));

		

		listPromociones.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	
				Toast.makeText(context, "Stop Clicking me", Toast.LENGTH_SHORT).show();
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

		actualizarEstadoMensajesError();
		// saliendo = true;
		return rootView;
	}
	 @Override
	  public void onResume() {
	     super.onResume();
	  }

	public static void actualizarEstadoMensajesError() {
		txtState.setText("Comprobando la configuracion");
		RelativeLayout bluetoothMessage = (RelativeLayout) rootView.findViewById(R.id.blueToothError);
		final RelativeLayout locationMessage = (RelativeLayout) rootView.findViewById(R.id.localizacionError);
		boolean isBluetoothOk=isBloothActivated();
		boolean isLocationActivado=isLocationActivado();
		boolean isConfiguracionOk=false;
		if (isLocationActivado && isBluetoothOk) {
			locationMessage.setVisibility(View.GONE);
			bluetoothMessage.setVisibility(View.GONE);
			isConfiguracionOk=true;
		} else if(!isLocationActivado && !isBluetoothOk) {
			locationMessage.setVisibility(View.VISIBLE);
			bluetoothMessage.setVisibility(View.VISIBLE);
			isConfiguracionOk=false;
			
		}else if(!isLocationActivado){
			locationMessage.setVisibility(View.VISIBLE);
			bluetoothMessage.setVisibility(View.GONE);
			isConfiguracionOk=false;
			
		}else if(!isBluetoothOk){
			locationMessage.setVisibility(View.GONE);
			bluetoothMessage.setVisibility(View.VISIBLE);
			isConfiguracionOk=false;
		}

		if(!isConfiguracionOk){//Configuracion esta mal
			txtState.setText("Error en configuracion, compruebalo ");
		}else if(MainActivity.mCurrentLocation==null&&isConfiguracionOk){ //Configuracion esta ok, pero no hemos podido encontrar location
			txtState.setText("No se ha podido obtener localizacion");
		}else if(promotions!=null && promotions.size()>0&&isConfiguracionOk){//Configuracion esta bien y hemos encontrado promociones
			txtState.setText("Promicones encotnradas");
			spinner.setVisibility(View.GONE);
		}else if(!isPromotionsEncontrados() && isConfiguracionOk && !isRegionesEncontrados()){ //Todo esta bien excepto que no se ha encontrado ningun beacons
			txtState.setText("No se encontrado puntos de ofertas cercanas");
		}
		
	}

	private static boolean isPromotionsEncontrados(){
		return promotions!=null &&  promotions.size()>0;
	}
	private static boolean isRegionesEncontrados(){
		return ((MainActivity) context).regionsEncontrado!=null && ((MainActivity) context).regionsEncontrado.size()>0;
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
}
