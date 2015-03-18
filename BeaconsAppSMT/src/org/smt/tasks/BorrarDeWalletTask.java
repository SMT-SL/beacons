package org.smt.tasks;


import org.json.JSONException;
import org.json.JSONObject;
import org.smt.utils.GestorRed;
import org.smt.utils.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class BorrarDeWalletTask extends AsyncTask<Void, Integer, JSONObject> {

	private Context context;
	private int offerId;

	public BorrarDeWalletTask(Context activity, int offerIdd) {
		this.context = activity;
		this.offerId = offerIdd;
	}

	@Override
	public void onPreExecute() {
	}

	@Override
	protected JSONObject doInBackground(Void... jsonInputArr) {
		try {
			JSONObject jsonInput = new JSONObject();
			jsonInput.put("offerId", this.offerId);
			jsonInput.put("token", Utils.getTokenDTO(context));
			JSONObject jsonResult = GestorRed.getInstance().deleteFromWallet(jsonInput);
			return jsonResult;
		} catch (Exception e) {
			Log.e("LoginError", e.toString());
			return null;
		}
	}

	@Override
	public void onPostExecute(JSONObject jsonResult) {
		
		JSONObject mensajeError=null;
		int code=-1;
		if (jsonResult != null){
			
			try {
				if(!jsonResult.isNull("error")){
					mensajeError=jsonResult.getJSONObject("error");
					if(!mensajeError.isNull("code")){
						code=mensajeError.getInt("code");
					}
				}
				if(code!=200){
					Toast toast = Toast.makeText(context, "No se ha podido borrar, vuelve a intentar!! ", Toast.LENGTH_SHORT);
					toast.show();
				}
			} catch (JSONException e) {
		
				e.printStackTrace();
			}
		}
	}
	
}
