package org.smt.tasks;

import java.lang.ref.WeakReference;
import org.json.JSONException;
import org.json.JSONObject;
import org.smt.R;
import org.smt.activity.RegisterUserActivity;
import org.smt.model.UserDTO;
import org.smt.utils.GestorRed;
import org.smt.utils.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class RegisterUserTask extends AsyncTask<Void, Integer, JSONObject> {

	private WeakReference<Activity> weakActivity;
	private ProgressDialog pDialog;
	private UserDTO nuevoUsuario;

	public RegisterUserTask(Activity activity, UserDTO nuevoUsuario) {
		this.weakActivity = new WeakReference<Activity>(activity);
		this.nuevoUsuario=nuevoUsuario;
	}

	@Override
	public void onPreExecute() {
		Activity activity = this.weakActivity.get();
		pDialog = new ProgressDialog(activity);
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.setMessage("Registrando a usuario...");
		pDialog.setCancelable(false);
		pDialog.setMax(100);
		pDialog.setProgress(0);
		pDialog.show();
	}

	@Override
	protected JSONObject doInBackground(Void... jsonInputArr) {
		try {
		
			JSONObject userNew = new JSONObject();
			userNew.put("UserId", this.nuevoUsuario.getUserId());
			userNew.put("birthDate", "/Date("+nuevoUsuario.getBirthDate().getTime()+")/");
			userNew.put("nickName", this.nuevoUsuario.getNickName()); 
			userNew.put("password", this.nuevoUsuario.getPassword());
			userNew.put("email", this.nuevoUsuario.getEmail());
			userNew.put("Sex", this.nuevoUsuario.getSex());
			userNew.put("CP", this.nuevoUsuario.getCP());
			userNew.put("deviceToken",this.nuevoUsuario.getDeviceToken());
			userNew.put("token", this.nuevoUsuario.getToken());

			JSONObject userDto = new JSONObject();
			userDto.put("userdto", userNew);
//			userDto.put("token", Utils.getTokenDTO(this.weakActivity.get()));
			userNew.toString();
			JSONObject jsonResult = GestorRed.getInstance().register(userDto);
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
				if(jsonResult!=null&&!jsonResult.isNull("error")){
					mensajeError=jsonResult.getJSONObject("error");
					if(!mensajeError.isNull("code")){
						code=mensajeError.getInt("code");
					}
					RegisterUserActivity activity = (RegisterUserActivity) this.weakActivity.get();
					
					if (code==200 && !jsonResult.isNull("response")){
						
						if (activity != null) {

							pDialog.dismiss();
							activity.toLogin();
						}

					} else if(jsonResult.isNull("response")){
						messageToDisplay("Error Generico, intente un poco mas tarde!");
					} else {
						
							messageToDisplay(Utils.getMensaje(code));
					}

					
				}else {
					messageToDisplay("Error Generico, intente un poco mas tarde!");
				}
				pDialog.dismiss();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		
	}
	
	private void messageToDisplay(final String mensaje) {

		if (this.weakActivity.get() instanceof RegisterUserActivity){
			final Activity activity = (Activity) this.weakActivity.get();
			((Activity) activity).runOnUiThread(new Runnable() {
				public void run() {
					final TextView text=(TextView) activity.findViewById(R.id.showMessageErrorRegister);
					text.setText(mensaje);
					text.setVisibility(View.VISIBLE);
     	    	    		
    	    }
    	});
	
		}
    }
	
}
