package org.smt.tasks;

import java.lang.ref.WeakReference;

import org.json.JSONException;
import org.json.JSONObject;
import org.smt.activity.LoginActivity;
import org.smt.model.UserDTO;
import org.smt.utils.GestorRed;
import org.smt.utils.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

public class LoginTask extends AsyncTask<Void, Integer, JSONObject> {

	private WeakReference<Activity> weakActivity;
	private ProgressDialog pDialog;

	private String email;
	private String password;

	public LoginTask(Activity activity, String email, String password) {
		this.weakActivity = new WeakReference<Activity>(activity);
		this.email = email;
		this.password = password;
	}

	@Override
	public void onPreExecute() {
		Activity activity = this.weakActivity.get();
		pDialog = new ProgressDialog(activity);
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.setMessage("Esperando al login...");
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
			jsonInput.put("password", this.password);
			JSONObject jsonResult = GestorRed.getInstance().login(jsonInput);
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
		JSONObject jsonUser=null;
		int code=-1;
		LoginActivity activity = (LoginActivity) this.weakActivity.get();
		if (jsonResult != null) {
			try {
					if(jsonResult.getJSONObject("error")!=null){
						mensajeError=jsonResult.getJSONObject("error");
						code=mensajeError.getInt("code");
					}
					
					if(jsonResult.getJSONObject("response")!=null){
					 jsonUser=jsonResult.getJSONObject("response");
					}				 
				 
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		 if(mensajeError!=null && jsonUser!=null&& code==200){//respuesta correcta

				if (activity != null) {
					UserDTO user = new UserDTO(jsonUser);
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
					SharedPreferences.Editor edit = prefs.edit();
					edit.putLong("UserId", user.getUserId());
					edit.putString("nickName", user.getNickName());
					edit.putString("password", user.getPassword());
					edit.putString("fechaNacimiento", (Integer.toString(user.getBirthDate().getDay()))+"/"+Integer.toString(user.getBirthDate().getMonth())+"/"+Integer.toString(user.getBirthDate().getYear()));
					edit.putString("email", user.getEmail());
					edit.putInt("Sex", user.getSex());
					edit.putString("CP", String.valueOf(user.getCP()));
					edit.putString("userToken", user.getToken());
					edit.commit();
					activity.toMain();
				}

			
			} else if(code > 0){//respuesta recibida pero error en datos para logear
				
				showMessage(activity,Utils.getMensaje(code));
				Log.e("JSON recibido", jsonResult.toString());
				
			}else{//errores genericos al recibir respeusta como mensajeError es null o jsonUser is null
				showMessage(activity,"Error generico, vuelva a intentar mas tarde");
			}

		}else{//Se ha recibido jsonResult null, no se ha podido enviar la peticion o servidor se ha vuelto respusta null
			showMessage(activity,"Opss, No se ha podido conectar con servidor");
		}

		pDialog.dismiss();
	}

	private void showMessage(LoginActivity activity,final String message){
		
		if (activity != null) {
			activity.txtErrorPass.setText(message);
			activity.txtErrorPass.setVisibility(View.VISIBLE);
		}
	}
}
