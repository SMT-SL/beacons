package org.smt.tasks;


import org.json.JSONException;
import org.json.JSONObject;
import org.smt.fragments.WalletFragment;
import org.smt.utils.GestorRed;
import org.smt.utils.Utils;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class GuardarEnWalletTask extends AsyncTask<Void, Integer, JSONObject> {

	private Context context;
	private int offerId;
	private int major;
	private int menor;

	public GuardarEnWalletTask(Context activity, int offerIdd,int major,int menor) {
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
			jsonInput.put("major", this.major);
			jsonInput.put("menor", this.menor);
			jsonInput.put("token", Utils.getTokenDTO(context));
			JSONObject jsonResult = GestorRed.getInstance().addToWallet(jsonInput);
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
		try {
			if (jsonResult != null){
				if(!jsonResult.isNull("error")){
					mensajeError=jsonResult.getJSONObject("error");
					if(!mensajeError.isNull("code")){
						code=mensajeError.getInt("code");
					}
				}
				if(code==200){
					Toast toast = Toast.makeText(context, "Se ha guardado promocion correctamente en su wallet!! ", Toast.LENGTH_SHORT);
					toast.show();
				}else{
					Toast toast = Toast.makeText(context, "Se ha generado error al guardar promocion seleccionada en wallet, vuelve a intentar!! ", Toast.LENGTH_SHORT);
					toast.show();
					WalletFragment.removeFromWalletList(this.offerId);
				} 
			}else{//cuando respuesta desde backend es null quere decir ha sido un error .. no se han guardado en backend
				Toast toast = Toast.makeText(context, "Se ha generado error al guardar promocion seleccionada en wallet, vuelve a intentar!! ", Toast.LENGTH_SHORT);
				toast.show();
				WalletFragment.removeFromWalletList(this.offerId);
			} 
			} catch (JSONException e) {
			
			e.printStackTrace();
		}
	}
	
}
