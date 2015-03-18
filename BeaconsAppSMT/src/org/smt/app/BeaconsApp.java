package org.smt.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;
import org.smt.R;
import org.smt.activity.ImageActivity;
import org.smt.activity.BuscarPromocionesActivity;
import org.smt.fragments.PromocionesFragment;
import org.smt.model.OfferDetailsDTO;
import org.smt.model.RegionInfoDTO;
import org.smt.model.IBeaconsFound;
import org.smt.tasks.CheckPromocionesTask;
import org.smt.utils.Utils;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class BeaconsApp extends Application implements BootstrapNotifier, RangeNotifier, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

	private static List<IBeaconsFound> notifiedBeacons;
//	private MainActivity loginActivity;
	private static BuscarPromocionesActivity buscarPromocionesActivity;
	// private EasiActivity easiActivity;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private static final String TAG = "BeaconsApp";
	private BeaconManager mBeaconManager;
//	private Region mAllBeaconsRegion;
	public static List<RegionInfoDTO> regionsFound = new ArrayList<RegionInfoDTO>();
	public static ArrayList<OfferDetailsDTO> listOffer=new ArrayList<OfferDetailsDTO>();
	private BackgroundPowerSaver mBackgroundPowerSaver;
	@SuppressWarnings("unused")
	private RegionBootstrap mRegionBootstrap;
	private static Location mCurrentLocation;
	private static LocationClient mLocationClient;
	private List<Region> mListregions;
	private static Context context;

	@Override
	public void onCreate() {

		notifiedBeacons = new ArrayList<IBeaconsFound>();
		context=getApplicationContext();
		this.options = new DisplayImageOptions.Builder().cacheOnDisk(true).cacheInMemory(true).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(options).build();
		this.imageLoader = ImageLoader.getInstance();
		this.imageLoader.init(config);
		mLocationClient = new LocationClient(this, this, this);
		mLocationClient.connect();
		// ********************* Configuracion de Beacons
		// *************************
		this.mBeaconManager = BeaconManager.getInstanceForApplication(this);
		this.mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24")); // iBeacons
		this.mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")); // Estimotes
		this.mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=a7ae2eb7,i:4-19,i:20-21,i:22-23,p:24-24")); // easiBeacons
		setmBackgroundPowerSaver(new BackgroundPowerSaver(this));
		this.mBeaconManager.setForegroundBetweenScanPeriod(5000);
		this.mBeaconManager.setBackgroundBetweenScanPeriod(9000);

		this.mListregions=new ArrayList<Region>();
//		this.mListregions.add(new Region("myIdentifier1", null, Identifier.fromInt(1), Identifier.fromInt(1)));
		mListregions.add(new Region("myIdentifier2", null, Identifier.fromInt(1), Identifier.fromInt(2)));
		mListregions.add(new Region("myIdentifier3", null, Identifier.fromInt(1), Identifier.fromInt(3)));
		mListregions.add(new Region("myIdentifier4", null, Identifier.fromInt(1), Identifier.fromInt(4)));
		this.mListregions.add(new Region("myIdentifier5", null, Identifier.fromInt(1), Identifier.fromInt(5)));
		mListregions.add(new Region("myIdentifier6", null, Identifier.fromInt(1), Identifier.fromInt(6)));
		mListregions.add(new Region("myIdentifier7", null, Identifier.fromInt(1), Identifier.fromInt(7)));
		mRegionBootstrap = new RegionBootstrap(this, mListregions);
		
		Timer myTimerRegionExist = new Timer();
		MyTimerRegionExitTask mytimerExitTask=new MyTimerRegionExitTask();
		myTimerRegionExist.schedule(mytimerExitTask, 5000, 5000);
	}

	public boolean isAlreadyNotifiedBeacon(Beacon beacon, Date date) {
		for (int i = 0; i < notifiedBeacons.size(); i++) {
			if (notifiedBeacons.get(i).getBeacon().equals(beacon) && date.getTime() - notifiedBeacons.get(i).getDate().getTime() > 30000) {
				return true;
			}
		}
		return false;
	}

	public void addBeacon(Beacon beacon, Date date) {
		boolean exist = false;

		for (int i = 0; i < notifiedBeacons.size(); i++) {
			if (notifiedBeacons.get(i).equals(beacon)) {
				exist = true;
			}
		}

		if (!exist) {
			notifiedBeacons.add(new IBeaconsFound(beacon, date));
		}

	}

	public void deleteBeacon(Beacon beacon) {
		for (int i = 0; i < notifiedBeacons.size(); i++) {
			if (notifiedBeacons.get(i).getBeacon().equals(beacon)) {
				notifiedBeacons.remove(i);
			}
		}
	}

	public void refreshBeacon(Beacon beacon, Date date) {
		for (int i = 0; i < notifiedBeacons.size(); i++) {
			if (notifiedBeacons.get(i).getBeacon().equals(beacon) && notifiedBeacons.get(i).getDate().after(date)) {
				notifiedBeacons.remove(i);
				notifiedBeacons.add(new IBeaconsFound(beacon, date));
			}
		}
	}

	public ImageLoader getImageLoader() {
		return this.imageLoader;
	}

	public DisplayImageOptions getDisplayImageOptions() {
		return this.options;
	}

	@Override
	public void didDetermineStateForRegion(int arg0, Region arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void didEnterRegion(Region region) {
		boolean isNewRegion = isNewRegion(region);
		if (isNewRegion) {
			regionsFound.add(new RegionInfoDTO(region.getId2() != null ? region.getId2().toInt() : 0, region.getId3() != null ? region.getId3().toInt() : 0));
				if(mLocationClient.isConnected()){
					mCurrentLocation = mLocationClient.getLastLocation();
				}else{
					mLocationClient.connect();
				}
				if(mCurrentLocation==null){
					@SuppressWarnings("deprecation")
					String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
				
					if (locationProviders.contains("gps") || locationProviders.contains("network")) {
						LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
						String locationProvider = LocationManager.NETWORK_PROVIDER;
						mCurrentLocation = locationManager.getLastKnownLocation(locationProvider);
				}
				
				}
					if(buscarPromocionesActivity != null&&mCurrentLocation != null){
						new CheckPromocionesTask(this, regionsFound, String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude())).execute();
	
					}else if (mCurrentLocation != null&&Utils.getEstadoNotificacion(this)) {
						new CheckPromocionesTask(this, regionsFound, String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude())).execute();
					}
				
		}
	}

	public  static void findPromociones(Location location){
		if(regionsFound.size()>0){
			mCurrentLocation=location;
			new CheckPromocionesTask(context, regionsFound, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude())).execute();
		}
	}
	public List<RegionInfoDTO> getRangeList() {
		return regionsFound;
	}

	public boolean isNewRegion(Region region) {
		Log.e("Beacon found", "Major: " + region.getId2() != null ? region.getId2().toString() : "null" + " Minor: " + region.getId3() != null ? region.getId3().toString() : "null");
		Log.e("Region found ", "Major: " + region.getId2().toString() + " Minor: " + region.getId3().toString());
		boolean exists = true;
		for (RegionInfoDTO bi :regionsFound) {
			if (bi.getMajor() == (region.getId2() != null ? region.getId2().toInt() : 0) && (region.getId3() != null ? region.getId3().toInt() : 0) == bi.getMinor()) {
				bi.setExistedFromRegion(false);
				exists = false;
			}
		}
		return exists;
	}

	@Override
	public void didExitRegion(Region region) {
		for (RegionInfoDTO bi :regionsFound) {
			if (bi.getMajor() == (region.getId2() != null ? region.getId2().toInt() : 0) && (region.getId3() != null ? region.getId3().toInt() : 0) == bi.getMinor()) {
				bi.setExistedFromRegion(true);
				bi.setExistTime(System.currentTimeMillis());
				
			}
		}
		
	}
	private static void notifyDataSetChange() {
		final Activity activity = (Activity) buscarPromocionesActivity;
		((Activity) activity).runOnUiThread(new Runnable() {
    	    public void run() {
    	    	PromocionesFragment.promotionsAdapter.notifyDataSetChanged();
    	    }
    	});
	}
	private void removeNotificacionOfRegion(RegionInfoDTO region){
		NotificationManager nManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		for(int i=listOffer.size()-1;i>=0;i--){
			if(listOffer.get(i).getMajor()==region.getMinor()&&listOffer.get(i).getMinor()==region.getMajor()){
				nManager.cancel(listOffer.get(i).getOfferId());
			}
		}
	}
	
	private void removePromocionOfRegion(RegionInfoDTO region){
		for(int i=listOffer.size()-1;i>=0;i--){
			if(listOffer.get(i).getMajor()==region.getMajor()&&listOffer.get(i).getMinor()==region.getMinor()){
				listOffer.remove(i);
			}
		}
	}
	
	private void removeRegionFromList(RegionInfoDTO region) {
		Log.e("Region Exit ", "Major: " + region.getMajor() + " Minor: " + region.getMinor());
		if (regionsFound != null && regionsFound.size() > 0) {
			for (int i = 0; i < regionsFound.size(); i++) {
				if (regionsFound.get(i).getMajor() == region.getMajor() && region.getMinor() == regionsFound.get(i).getMinor()) {
					regionsFound.remove(i);
				}
			}

		}
	}
	public static void agregarNuevoPromociones(ArrayList<OfferDetailsDTO> promociones){
		boolean exists = false;
		boolean nuevasPromociones=false;
		for (OfferDetailsDTO promocionNuevo: promociones){
			exists=false;
	
		for (OfferDetailsDTO offerGuardado : listOffer){
			if (offerGuardado.equals(promocionNuevo)){
				exists = true;
				break;
			}
		}
		if  (!exists){
			listOffer.add(promocionNuevo);
			nuevasPromociones=true;
		}
		
		}
		if(nuevasPromociones){
			informarSobreNuevasPromociones();
		}
	}
	
	private static void informarSobreNuevasPromociones(){
		if (buscarPromocionesActivity != null) {
		
//			PromocionesFragment.promotions=listOffer;
			PromocionesFragment.promotionsAdapter.notifyDataSetChanged();
		}else{
			displayNotifiacion();
		}
	}
	
	private static  void displayNotifiacion(){
		if(listOffer!=null){
			for (int i= 0; i<listOffer.size(); i++){
				
					if (listOffer.get(i)!=null){
						OfferDetailsDTO nuevoPromocion = listOffer.get(i);

						Intent targetIntent = null;
						Notification noti = null;
						NotificationManager nManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
						String notifTitle = null, notifText = null;
						int notifSmallIcon = 0, notifId = 0;
						Bitmap notifBigIcon = null;
			
						if (nuevoPromocion.getOfferType()!=2){
							targetIntent = new Intent(Intent.ACTION_VIEW, Uri.parse( nuevoPromocion.getOfferURL()));
							notifBigIcon = BitmapFactory.decodeResource(context.getResources(),R.drawable.logo_deusto);
						} else {
							targetIntent = new Intent(context, ImageActivity.class);
							String web = nuevoPromocion.getOfferURL();
							targetIntent.putExtra("image", web);
							notifBigIcon = ((BeaconsApp) context.getApplicationContext()).getImageLoader().loadImageSync(nuevoPromocion.getOfferURL());
						}
			
						notifTitle = nuevoPromocion.getName();
//						BeaconsApp.listOffer.add(nuevoPromocion);

						notifSmallIcon = R.drawable.ic_launcher;
						notifId = nuevoPromocion.getOfferId();
						PendingIntent contentIntent = PendingIntent.getActivity(context, 0, targetIntent, PendingIntent.FLAG_ONE_SHOT);
						NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
							.setSmallIcon(notifSmallIcon)
							.setContentTitle(notifTitle)
							.setContentText(notifText)
							.setOnlyAlertOnce(true)
							.setAutoCancel(true)
							.setDefaults(Notification.DEFAULT_ALL)
							.setContentIntent(contentIntent)
							.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(notifBigIcon));
	 	  	
						noti = mBuilder.build();
						nManager.notify(notifId, noti);
					}

			}
		}
		
	}
	
	public  void clearAllNotificaciones(){
		NotificationManager nManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		for(int i=listOffer.size()-1;i>=0;i--){
				nManager.cancel(listOffer.get(i).getOfferId());
//				listOffer.remove(i);
			}
		
	}
	@Override
	public void didRangeBeaconsInRegion(Collection<Beacon> arg0, Region arg1) {
		Log.e("BeaconsApp", "****************** beacons *****************");

	}

	public static Location getLocation() {
		if(mLocationClient.isConnected()){
			mCurrentLocation = mLocationClient.getLastLocation();
		}else{
			mLocationClient.connect();
		}
		
		return mCurrentLocation;
	}

	public static void setLocation(Location loc) {
			mCurrentLocation = loc;
	}
	public void setBuscarPromocionesActivity(BuscarPromocionesActivity activity) {
		buscarPromocionesActivity = activity;
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		if(mLocationClient.isConnected()){
			mCurrentLocation = mLocationClient.getLastLocation();
		}else{
			mLocationClient.connect();
		}
		
		if(mCurrentLocation==null){
			@SuppressWarnings("deprecation")
			String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
			if (locationProviders.contains("gps") || locationProviders.contains("network")) {
				LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
				String locationProvider = LocationManager.NETWORK_PROVIDER;
				mCurrentLocation = locationManager.getLastKnownLocation(locationProvider);
			}
		}
		
		if(buscarPromocionesActivity != null&&regionsFound!=null && regionsFound.size()>0&&mCurrentLocation != null){
			new CheckPromocionesTask(this, regionsFound, String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude())).execute();
		}else if (mCurrentLocation != null&&regionsFound!=null && regionsFound.size()>0&&Utils.getEstadoNotificacion(this)) {
			new CheckPromocionesTask(this, regionsFound, String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude())).execute();
		}
				
		
	}

	class MyTimerRegionExitTask extends TimerTask{

		@Override
		public void run() {
			if(regionsFound!=null && !regionsFound.isEmpty())
			for (RegionInfoDTO beaconRegion :regionsFound) {
				if (beaconRegion.isExistedFromRegion() && (System.currentTimeMillis()-beaconRegion.getExistTime())>5000) {
					if (buscarPromocionesActivity != null) {
						//En este caso primero borraremos el region despues mandaremos a redibujar las promociones encontradas en promocionesFragments
						removePromocionOfRegion(beaconRegion);
						notifyDataSetChange();
					}else{
						//Siempre primero quitar las notifiaciones despues borra las promociones de este region. Despues borraremos el rgion.
						removeNotificacionOfRegion(beaconRegion);
						removePromocionOfRegion(beaconRegion);
					}
					removeRegionFromList(beaconRegion);
				}
			}
		}
		
	}
	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	public static String getTag() {
		return TAG;
	}

	public BackgroundPowerSaver getmBackgroundPowerSaver() {
		return mBackgroundPowerSaver;
	}

	public void setmBackgroundPowerSaver(BackgroundPowerSaver mBackgroundPowerSaver) {
		this.mBackgroundPowerSaver = mBackgroundPowerSaver;
	}
}
