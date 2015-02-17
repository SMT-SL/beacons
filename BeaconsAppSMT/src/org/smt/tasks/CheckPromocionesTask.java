package org.smt.tasks;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
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
			messageToDisplay("Buscando promociones ......");
	
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
//			if(BeaconsApp.listOffer!=null){
//				BeaconsApp.listOffer.clear();
//			}
			
			try {
				if(!jsonResult.isNull("error")){
					mensajeError=jsonResult.getJSONObject("error");
					if(!mensajeError.isNull("code")){
						code=mensajeError.getInt("code");
					}
					
				}
				if (code==200 && !jsonResult.isNull("response")){
					
					 promEncontradas = jsonResult.getJSONArray("response");
					 messageToDisplay("Promociones encontradas");
					 BeaconsApp.agregarNuevoPromociones(getPromocionEncontradas(promEncontradas));
									
								
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
		
		PromocionesFragment.actualizarEstadoApp(line);
    }
	

	
	
	private ArrayList<OfferDetailsDTO> getPromocionEncontradas(final JSONArray promEncontradas){
		ArrayList<OfferDetailsDTO> promociones=new ArrayList<OfferDetailsDTO>();
		if(promEncontradas!=null){
			for (int i= 0; i<promEncontradas.length(); i++){
				
				OfferDetailsDTO offer = null;
				
				try {
						if (!promEncontradas.isNull(i)){
						
						JSONObject o = (JSONObject) promEncontradas.get(i);
						offer = new OfferDetailsDTO(o);
							promociones.add(offer);
					
						}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		

		}
		
		return promociones;
	}
	

	}
