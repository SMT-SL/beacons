package org.smt.tasks;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smt.R;
import org.smt.activity.BuscarPromocionesActivity;
import org.smt.model.OfferDetailsDTO;
import org.smt.model.WalletPromocion;
import org.smt.utils.GestorRed;
import org.smt.utils.Utils;

import org.smt.fragments.WalletFragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ObtenerPromocionWalltetTask extends AsyncTask<Void, Integer, JSONObject> {

	private Context context;

	public ObtenerPromocionWalltetTask(Context context) {

	 	this.context = context;
	}

	@Override
	public void onPreExecute() {
		displayHidePrgress(true);
		messageToDisplay("Buscando promociones en Wallet .....");
	}

	@Override
	protected JSONObject doInBackground(Void... jsonInputArr) {
		try {
			JSONObject jsonInput = new JSONObject();
			jsonInput.put("token", Utils.getTokenDTO(context));
			JSONObject jsonResult = GestorRed.getInstance().getPromotionFromWallet(jsonInput);
			return jsonResult;
		} catch (Exception e) {
			// Log.e(tag, msg)
			Log.e("LoginError", e.toString());
			return null;
		}

	}

	@Override
	public void onPostExecute(JSONObject jsonResult) {
		JSONObject mensajeError=null;
		int code=-1;
		JSONArray promEncontradas=null;
		
		if (jsonResult != null){
			
			try {
				if(!jsonResult.isNull("error")){
					mensajeError=jsonResult.getJSONObject("error");
					if(!mensajeError.isNull("code")){
						code=mensajeError.getInt("code");
					}
					
				}
				if (code==200 && !jsonResult.isNull("response")){
					
					 promEncontradas = jsonResult.getJSONArray("response");
						//Mostrar en la applicacion las ofertas encontradas
						mostrarPromocionEnApp(promEncontradas);
						messageToDisplay("Promociones encontradas");
								
					}else if(jsonResult.isNull("response")){
						messageToDisplay("Error Generico, intente un poco mas tarde!");
					} else{
						messageToDisplay(Utils.getMensaje(code));
					}
								
			} catch (JSONException e) {
					
				e.printStackTrace();
			}
			
		}else{
			messageToDisplay("Error Generico, intente un poco mas tarde!");
		} 
	}

private void mostrarPromocionEnApp(final JSONArray promEncontradas){
		
		for (int i= 0; i<promEncontradas.length(); i++){
			
			OfferDetailsDTO promocion = null;
			try {
				if (!promEncontradas.isNull(i)){
					
					JSONObject o = (JSONObject) promEncontradas.get(i);
					promocion = new OfferDetailsDTO(o);
					if(WalletFragment.walletPromocionList==null){
						WalletFragment.walletPromocionList=new ArrayList<OfferDetailsDTO>();
					}
					if  (!isExistePromocion(promocion, WalletFragment.walletPromocionList)){
						WalletFragment.walletPromocionList.add(promocion);
						WalletFragment.walletListAdapter.notifyDataSetChanged();
				
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
	}
private static boolean isExistePromocion(OfferDetailsDTO nuevoPromcion, List<OfferDetailsDTO> walletPromocionList){
	boolean exists=false;
	for (OfferDetailsDTO promocion :walletPromocionList){
		if (promocion.getOfferId()==(nuevoPromcion.getOfferId())){
			exists = true;
			break;
		}
	}
	return exists;
}

private void messageToDisplay(final String message) {
	

	if (context instanceof BuscarPromocionesActivity){
		final Activity activity = (Activity) context;
		((Activity) activity).runOnUiThread(new Runnable() {
			public void run() {
				final TextView text=(TextView) activity.findViewById(R.id.txtState);
				if(text!=null){
					text.setText(message);
				}
				
 	    	    		
	    }
	});

	}
}
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
}
