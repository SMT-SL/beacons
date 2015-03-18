package org.smt.tasks;

import java.lang.ref.WeakReference;

import org.json.JSONException;
import org.json.JSONObject;
import org.smt.R;
import org.smt.fragments.PerfilFragment;
import org.smt.model.UserDTO;
import org.smt.utils.GestorRed;
import org.smt.utils.Utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ModificarPerfilTask extends AsyncTask<Void, Integer, JSONObject> {

	private WeakReference<PerfilFragment> weakFragment;
	private ProgressDialog pDialog;

	private UserDTO datosModificUsuario;


	public ModificarPerfilTask(PerfilFragment fragment, UserDTO nuevoDatosUsuario) {
		this.weakFragment = new WeakReference<PerfilFragment>(fragment);
		this.datosModificUsuario=nuevoDatosUsuario;
	}

	@Override
	public void onPreExecute() {
		Fragment activity = this.weakFragment.get();
		pDialog = new ProgressDialog(activity.getActivity());
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.setMessage("Guardando las modificaciones......");
		pDialog.setCancelable(false);
		pDialog.setMax(100);
		pDialog.setProgress(0);
		pDialog.show();
	}

	@Override
	protected JSONObject doInBackground(Void... jsonInputArr) {
		try {
			JSONObject userNuevoDatos = new JSONObject();
			userNuevoDatos.put("UserId", this.datosModificUsuario.getUserId());
			userNuevoDatos.put("birthDate", "/Date("+datosModificUsuario.getBirthDate().getTime()+")/");
			userNuevoDatos.put("nickName", this.datosModificUsuario.getNickName()); 
			userNuevoDatos.put("email", this.datosModificUsuario.getEmail());
			userNuevoDatos.put("Sex", this.datosModificUsuario.getSex());
			userNuevoDatos.put("CP", this.datosModificUsuario.getCP());
			userNuevoDatos.put("password", "");
			userNuevoDatos.put("deviceToken",this.datosModificUsuario.getDeviceToken());
		
			JSONObject userDto = new JSONObject();
			userDto.put("userdto", userNuevoDatos);
			userDto.put("token", Utils.getTokenDTO(this.weakFragment.get().getActivity()));
			JSONObject jsonResult = GestorRed.getInstance().modifyUserPerfil(userDto);
			return jsonResult;
			
		} catch (Exception e) {
			// Log.e(tag, msg)
			Log.e("LoginError", e.toString());
			return null;
		}

	} //txtViewPerfilShowStatus

	@Override
	public void onPostExecute(JSONObject jsonResult) {
		JSONObject mensajeError=null;
		int code=-1;
		JSONObject userDetails=null;
		
		try {
				if(jsonResult!=null&&!jsonResult.isNull("error")){
					mensajeError=jsonResult.getJSONObject("error");
					if(!mensajeError.isNull("code")){
						code=mensajeError.getInt("code");
					}
//					BuscarPromocionesActivity activity = (BuscarPromocionesActivity) this.weakFragment.get();
					
					if (code==200 && !jsonResult.isNull("response")){
						userDetails = jsonResult.getJSONObject("response");
						
						if (this.weakFragment != null) {
							UserDTO user = new UserDTO(userDetails);
							SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(weakFragment.get().getActivity());
							SharedPreferences.Editor edit = prefs.edit();
							edit.putLong("UserId", user.getUserId());
							edit.putString("nickName", user.getNickName());
							edit.putString("password", user.getPassword());
							edit.putString("email", user.getEmail());
							edit.putString("fechaNacimiento", (Integer.toString(user.getBirthDate().getDay()))+"/"+Integer.toString(user.getBirthDate().getMonth())+"/"+Integer.toString(user.getBirthDate().getYear()));
							edit.putInt("Sex", user.getSex());
							edit.putString("CP", String.valueOf(user.getCP()));
							edit.commit();
							this.weakFragment.get().activarModiPerfil(false);
							messageToDisplay("Los datos se han sido modificado correctamente");
							pDialog.dismiss();
						}

					} else if(jsonResult.isNull("response")){
						messageToDisplay("Error Generico, intente un poco mas tarde!");
					} else {
						
							messageToDisplay(Utils.getMensaje(code));
					}

					pDialog.dismiss();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

	}
	
	private void messageToDisplay(final String mensaje) {

		if (this.weakFragment.get() instanceof PerfilFragment){
			final Activity activity = (Activity) this.weakFragment.get().getActivity();
			((Activity) activity).runOnUiThread(new Runnable() {
				public void run() {
					final TextView text=(TextView) activity.findViewById(R.id.txtViewPerfilShowStatus);;
					text.setText(mensaje);
					text.setVisibility(View.VISIBLE);
     	    	    		
    	    }
    	});
	
		}
    }
	
}
