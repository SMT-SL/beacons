package com.smt.beacons.tasks;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.smt.beacons.R;
import com.smt.beacons.activity.EasiActivity;
import com.smt.beacons.activity.ImageActivity;
import com.smt.beacons.model.BeaconInfoDTO;
import com.smt.beacons.model.OfferDetailsDTO;
import com.smt.beacons.utils.BeaconsApp;
import com.smt.beacons.utils.GestorRed;


public class CheckBeaconsTask extends AsyncTask<Void, Integer, JSONArray>{

	private Context context;
//	private ProgressDialog pDialog;
	private List<BeaconInfoDTO> beacons;
	private String lat;
	private String lon;
	
	public CheckBeaconsTask(Context context,List<BeaconInfoDTO> beacons, String lat, String lon){
		this.context = context;
		this.beacons = beacons;
		this.lat = lat;
		this.lon = lon;
	}
	
	@Override
	public void onPreExecute(){
//		Activity activity = this.weakActivity.get();
//		pDialog = new ProgressDialog(activity);
//        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pDialog.setMessage("Esperando...");
//        pDialog.setCancelable(false);
//        pDialog.setMax(100);
//        pDialog.setProgress(0);
//        pDialog.show();
	}
	
	@Override
	protected JSONArray doInBackground(Void... jsonInputArr) {
		try{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			long userId = prefs.getLong("UserId", 0);
			JSONObject jsonInput = new JSONObject();
			JSONArray beacons = new JSONArray();
			for (int i = 0; i<this.beacons.size(); i++){
				JSONObject beacon = new JSONObject();
				beacon.put("major", this.beacons.get(i).getMajor() );
				beacon.put("minor", this.beacons.get(i).getMinor());
				beacons.put(beacon);
			}
			jsonInput.put("beacons", beacons);
			jsonInput.put("userId", userId);
			jsonInput.put("lat", lat);
			jsonInput.put("lon", lon);
			Log.e("JSON PARA GALDER", jsonInput.toString());
			JSONArray jsonResult = GestorRed.getInstance().getBeaconsPromotions(jsonInput);
			return jsonResult;
		}
		catch (Exception e) {
			return null;
		}

	}
	
	@Override
	public void onPostExecute(JSONArray jsonResult){
		if (jsonResult != null){
			EasiActivity.promotions.clear();
			Log.e("JSON RETURNED", jsonResult.toString());
			for (int i= 0; i<jsonResult.length(); i++){
				
				try {
					
					OfferDetailsDTO offer = null;
					if (jsonResult.get(i)!=null){
						JSONObject o = (JSONObject) jsonResult.get(i);
						offer = new OfferDetailsDTO(o);
					}
					
					if (context instanceof EasiActivity){
						
						boolean exists = false;
						for (OfferDetailsDTO o : EasiActivity.promotions){
							if (o.equals(offer)){
								exists = true;
								break;
							}
						}
						if  (!exists){
							EasiActivity.promotions.add(offer);
							EasiActivity.promotionsAdapter.notifyDataSetChanged();
						}
						
						
					} else {
//						Toast.makeText(this, "Beacon "+ibeacon.getUuidHexStringDashed(), Toast.LENGTH_SHORT).show();
					  	Intent targetIntent = null;
					  	Notification noti = null;
				  		NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				  		String notifTitle = null, notifText = null;
				  		int notifSmallIcon = 0, notifId = 0;
				  		Bitmap notifBigIcon = null;
				  		
				  		if (offer.getOfferType()!=2){
				  			targetIntent = new Intent(Intent.ACTION_VIEW, Uri.parse( offer.getOfferURL()));
//				  			notifBigIcon = BitmapFactory.decodeResource(context.getResources(),R.drawable.frigo_logo_blanco_cuadrado);
				  			notifBigIcon = BitmapFactory.decodeResource(context.getResources(),R.drawable.logo_deusto);
						} else {
							targetIntent = new Intent(context, ImageActivity.class);
							String web = offer.getOfferURL();
							targetIntent.putExtra("image", web);
							notifBigIcon = ((BeaconsApp) context.getApplicationContext()).getImageLoader().loadImageSync(offer.getOfferURL());
						}
				  		
						notifTitle = "Bienvenido!";
//						notifText = "Hace un d�a perfecto para tomarse un Magnum Frac, hoy 4x3!";
//				  		notifSmallIcon = R.drawable.frigo_logo_negro_cuadrado;
						notifSmallIcon = R.drawable.logo_smt;
				  		notifId = offer.getOfferId();
//				  		nManager.cancel(1133);
				  		
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
					
					
				} catch (JSONException e) {
					
					e.printStackTrace();
				}
			}
			
		} 
//		pDialog.dismiss();
	}

}

