package org.smt.tasks;

import java.lang.ref.WeakReference;

import org.json.JSONException;
import org.json.JSONObject;
import org.smt.R;
import org.smt.activity.RecoverPassword;
import org.smt.utils.GestorRed;
import org.smt.utils.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class RecoverPasswordTask extends AsyncTask<Void, Integer, JSONObject> {

	private WeakReference<Activity> weakActivity;
	private ProgressDialog pDialog;

	private String email;


	public RecoverPasswordTask(Activity activity, String email) {
		this.weakActivity = new WeakReference<Activity>(activity);
		this.email = email;

	}

	@Override
	public void onPreExecute() {
		Activity activity = this.weakActivity.get();
		pDialog = new ProgressDialog(activity);
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.setMessage("Recuperando contraseña recuperada...");
		pDialog.setCancelable(false);
		pDialog.setMax(100);
		pDialog.setProgress(0);
		pDialog.show();
	}

	@Override
	protected JSONObject doInBackground(Void... jsonInputArr) {
		try {
			JSONObject jsonInput = new JSONObject();
			jsonInput.put("email", this.email);
			JSONObject jsonResult = GestorRed.getInstance().getRecoverPassword(jsonInput);
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
		try {
			if (jsonResult != null){
				if(!jsonResult.isNull("error")){
					mensajeError=jsonResult.getJSONObject("error");
					if(!mensajeError.isNull("code")){
						code=mensajeError.getInt("code");
					}
				}
				if(code==200){
					messageToDisplay("Se ha enviado su nueva contraseña a su email.");
					
				}else{
					messageToDisplay(Utils.getMensaje(code));
				}
			}else {
				messageToDisplay("Error Generico, intente un poco mas tarde!");
			} 
		} catch (JSONException e) {
		e.printStackTrace();
	}
		pDialog.dismiss();
	}
/**
 * 
 * @param msg
 */
	private void messageToDisplay(final String msg) {
		if (this.weakActivity.get() instanceof RecoverPassword){

			final Activity activity = (Activity) this.weakActivity.get();
			((Activity) activity).runOnUiThread(new Runnable() {
				public void run() {
					final TextView text=(TextView) activity.findViewById(R.id.textEmailError);
					if(text!=null){
						text.setText(msg);
						text.setVisibility(View.VISIBLE);
					}
				}
			});
		}
	}

}
