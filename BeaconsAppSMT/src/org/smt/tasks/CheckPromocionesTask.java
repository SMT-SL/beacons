package org.smt.tasks;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.smt.R;
import org.smt.activity.EasiActivity;
import org.smt.activity.ImageActivity;
import org.smt.activity.BuscarPromocionesActivity;
import org.smt.model.RegionInfoDTO;
import org.smt.model.OfferDetailsDTO;
import org.smt.app.BeaconsApp;
import org.smt.fragments.PromocionesFragment;
import org.smt.utils.GestorRed;
import org.smt.utils.Utils;

/**
 * 
 * @author Azam-smt
 *
 */
public class CheckPromocionesTask extends AsyncTask<Void, Integer, JSONObject>{

	private Context context;
	private List<RegionInfoDTO> regions;
	private String lat;
	private String lon;

	public CheckPromocionesTask(Context context,List<RegionInfoDTO> regions, String lat, String lon) {
	    
	 	this.context = context;
		this.regions = regions;
		this.lat = lat;
		this.lon = lon;
	}
	
	@Override
	public void onPreExecute(){
		if (context instanceof EasiActivity){
			displayHidePrgress(true);
			messageToDisplay("Buscando promociones ......");
		}
	
	}
	
	@Override
	protected JSONObject doInBackground(Void... jsonInputArr) {

		try{
			if (context instanceof BuscarPromocionesActivity){ //Comprobar si se puede mostrar mensaje o no
					messageToDisplay("Buscando promociones ......");
				}
				
			JSONObject jsonInput = new JSONObject();
				JSONArray beacons = new JSONArray();
				
				for (int i = 0; i<this.regions.size(); i++){
					JSONObject beacon = new JSONObject();
					beacon.put("major", this.regions.get(i).getMajor() );
					beacon.put("minor", this.regions.get(i).getMinor());
					beacons.put(beacon);
				}
				
				jsonInput.put("beacons", beacons);
				jsonInput.put("token", Utils.getTokenDTO(context));
				jsonInput.put("lat", lat);
				jsonInput.put("lon", lon);
				Log.e("JSON PARA GALDER", jsonInput.toString());
				JSONObject jsonResult = GestorRed.getInstance().getBeaconsPromotions(jsonInput);
			
				return jsonResult;
		}
		catch (Exception e) {
			return null;
		}

	}
	
	@Override
	public void onPostExecute(final JSONObject jsonResult){
		
		JSONObject mensajeError=null;
		int code=-1;
		JSONArray promEncontradas=null;
		
		if (jsonResult != null){
			if(PromocionesFragment.promotions!=null){
				PromocionesFragment.promotions.clear();
			}
			
			try {
				if(!jsonResult.isNull("error")){
					mensajeError=jsonResult.getJSONObject("error");
					if(!mensajeError.isNull("code")){
						code=mensajeError.getInt("code");
					}
					
				}
				if (code==200 && !jsonResult.isNull("response")){
					
					 promEncontradas = jsonResult.getJSONArray("response");
					 if (context instanceof BuscarPromocionesActivity){
						//Mostrar en la applicacion las ofertas encontradas
						mostrarPromocionEnApp(promEncontradas);
						messageToDisplay("Promociones encontradas");
									
					} else {
						//Mostrar notificacion
						displayNotifiacion(promEncontradas);
								
					}
								
				} else if(jsonResult.isNull("response")){
					messageToDisplay("Error Generico, intente un poco mas tarde!");
				} else{
					messageToDisplay(Utils.getMensaje(code));
				}
			} catch (JSONException e) {
					
				e.printStackTrace();
			}
			
		} else {
			messageToDisplay("Error Generico, intente un poco mas tarde!");
		} 
		
		
	} 



	/**
	 * 
	 * @param line
	 */
	private void messageToDisplay(final String line) {
		

		if (context instanceof BuscarPromocionesActivity){
			final Activity activity = (Activity) context;
			((Activity) activity).runOnUiThread(new Runnable() {
				public void run() {
					final TextView text=(TextView) activity.findViewById(R.id.txtStateBuscarPr);
					if(text!=null){
						text.setText(line);
					}
     	    	    		
    	    }
    	});
	
		}
    }
	
	/**
	 * 
	 * @param display
	 */
	private void displayHidePrgress(final boolean display) {
		final Activity activity = (Activity) context;
		(activity).runOnUiThread(new Runnable() {
    	    public void run() {
    	    	
    	    	 ProgressBar spinner=(ProgressBar) activity.findViewById(R.id.pbHeaderProgress);
    	    	if(spinner!=null){
    	    		if(display){
        	    		spinner.setVisibility(View.VISIBLE);
        	    	}else{
        	    		spinner.setVisibility(View.GONE);
        	    	}	
    	    	}
    	               	    	    		
    	    }
    	});
	  }
	
	
	private void displayNotifiacion(final JSONArray promEncontradas){
		if(promEncontradas!=null){
			for (int i= 0; i<promEncontradas.length(); i++){
				
				OfferDetailsDTO offer = null;
				try {
					if (!promEncontradas.isNull(i)){
						JSONObject o = (JSONObject) promEncontradas.get(i);
						offer = new OfferDetailsDTO(o);

						Intent targetIntent = null;
						Notification noti = null;
						NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
						String notifTitle = null, notifText = null;
						int notifSmallIcon = 0, notifId = 0;
						Bitmap notifBigIcon = null;
			
						if (offer.getOfferType()!=2){
							targetIntent = new Intent(Intent.ACTION_VIEW, Uri.parse( offer.getOfferURL()));
							notifBigIcon = BitmapFactory.decodeResource(context.getResources(),R.drawable.logo_deusto);
						} else {
							targetIntent = new Intent(context, ImageActivity.class);
							String web = offer.getOfferURL();
							targetIntent.putExtra("image", web);
							notifBigIcon = ((BeaconsApp) context.getApplicationContext()).getImageLoader().loadImageSync(offer.getOfferURL());
						}
			
						notifTitle = offer.getName();
						BeaconsApp.listOffer.add(offer);

						notifSmallIcon = R.drawable.ic_launcher;
						notifId = offer.getOfferId();
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	private void mostrarPromocionEnApp(final JSONArray promEncontradas){
		
		messageToDisplay("Buscando promociones ......");
		for (int i= 0; i<promEncontradas.length(); i++){
			
			OfferDetailsDTO offer = null;
			try {
				if (!promEncontradas.isNull(i)){
					
					JSONObject o = (JSONObject) promEncontradas.get(i);
					offer = new OfferDetailsDTO(o);
					boolean exists = false;
					
					for (OfferDetailsDTO ofDetails : PromocionesFragment.promotions){
						if (ofDetails.equals(offer)){
							exists = true;
							break;
						}
					}
					if  (!exists){
						PromocionesFragment.promotions.add(offer);
						PromocionesFragment.promotionsAdapter.notifyDataSetChanged();
				
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
	}
	

	}
