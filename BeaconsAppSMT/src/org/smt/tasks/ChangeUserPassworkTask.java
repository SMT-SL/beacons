package org.smt.tasks;

import java.lang.ref.WeakReference;

import org.json.JSONException;
import org.json.JSONObject;
import org.smt.R;
import org.smt.activity.ChangePasswordActivity;
import org.smt.utils.GestorRed;
import org.smt.utils.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ChangeUserPassworkTask extends AsyncTask<Void, Integer, JSONObject> {

	private WeakReference<Activity> weakActivity;
	private ProgressDialog pDialog;

	private String currentPassword;
	private String newPassword;

	public ChangeUserPassworkTask(Activity activity, String passWordCurrent,String passWordNew) {
		this.weakActivity = new WeakReference<Activity>(activity);
		this.currentPassword=passWordCurrent;
		this.newPassword=passWordNew;

	}

	@Override
	public void onPreExecute() {
		Activity activity = this.weakActivity.get();
		pDialog = new ProgressDialog(activity);
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.setMessage("Cambiando la contraseña ...");
		pDialog.setCancelable(false);
		pDialog.setMax(100);
		pDialog.setProgress(0);
		pDialog.show();
	}

	@Override
	protected JSONObject doInBackground(Void... jsonInputArr) {
		try {
			JSONObject jsonInput = new JSONObject();

			jsonInput.put("currentpass", this.currentPassword);
			jsonInput.put("newpass", this.newPassword);
			jsonInput.put("token", Utils.getTokenDTO(this.weakActivity.get()));
		
			JSONObject jsonResult = GestorRed.getInstance().changeUserPassword(jsonInput);
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
				ChangePasswordActivity activity = (ChangePasswordActivity) this.weakActivity.get();
				
				if (code==200 && !jsonResult.isNull("response")){
					
					pDialog.dismiss();
					activity.completePasswordChange();		
								
				} else if(jsonResult.isNull("response")){
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

		pDialog.dismiss();
	}
	/**
	 * 
	 * @param line
	 */
	private void messageToDisplay(final String line) {
		

		if (this.weakActivity.get() instanceof ChangePasswordActivity){
			final Activity activity = (Activity) this.weakActivity.get();
			((Activity) activity).runOnUiThread(new Runnable() {
				public void run() {
					final TextView text=(TextView) activity.findViewById(R.id.txtViewShowStatusPassword);
					if(text!=null){
						text.setVisibility(View.VISIBLE);
						text.setText(line);
					}
     	    	    		
    	    }
    	});
	
		}
    }
}
