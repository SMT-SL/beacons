package com.smt.beacons.tasks;

import java.lang.ref.WeakReference;
import java.util.Date;

import org.json.JSONObject;

import com.smt.beacons.activity.LoginActivity;
import com.smt.beacons.model.UserDTO;
import com.smt.beacons.utils.GestorRed;
import com.smt.beacons.utils.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;



public class LoginTask extends AsyncTask<Void, Integer, JSONObject>{

	private WeakReference<Activity> weakActivity;
	private ProgressDialog pDialog;
	
	private String email;
	private String password;
	
	
	public LoginTask(Activity activity,String email,String password){
		this.weakActivity = new WeakReference<Activity>(activity);
		this.email = email;
		this.password = password;
	}
	
	@Override
	public void onPreExecute(){
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
		try{
			JSONObject jsonInput = new JSONObject();
			jsonInput.put("email", this.email);
			jsonInput.put("password",this.password);
			JSONObject jsonResult = GestorRed.getInstance().login(jsonInput);
			return jsonResult;
		}
		catch (Exception e) {
			return null;
		}

	}
	
	@Override
	public void onPostExecute(JSONObject jsonResult){
		if (jsonResult != null){
			
			Log.e("JSON recibido", jsonResult.toString());
			
//			if (jsonResult.has("error")){
//				LoginActivity activity = (LoginActivity) this.weakActivity.get();
//				if (activity != null){
//					try{
//						String message = jsonResult.getString("error");
//						if (message.contains("password") || message.contains("email")){
//							activity.txtErrorEmail.setText("Usuario incorrecto");
//							activity.txtErrorEmail.setVisibility(View.VISIBLE);
//						} else {
//							activity.txtErrorPass.setText("Contraseña incorrecta");
//							activity.txtErrorPass.setVisibility(View.VISIBLE);
//						}
//						
//					} catch (Exception e){
//					}
//				}
//			} else {
//				try{
//					LoginActivity activity = (LoginActivity) this.weakActivity.get();
//					activity.txtErrorEmail.setVisibility(View.GONE);
//					activity.txtErrorPass.setVisibility(View.GONE);
//				    
//					if (activity != null){
//						try{
//						      SharedPreferences settings = activity.getSharedPreferences(Utils.PREFS_NAME, 0);
//						      SharedPreferences.Editor editor = settings.edit();
//						      
//						      editor.putString("username", email);
//		
//						      // Commit the edits!
//						      editor.commit();
//						      activity.toMain();
//						} catch (Exception e){
//						}
//					}
//					
//				} catch(Exception e){
//					Toast.makeText(weakActivity.get(), "Servidor responde pero no hay Json", Toast.LENGTH_LONG).show();
//				}
//			}
			LoginActivity activity = (LoginActivity) this.weakActivity.get();
			
			if (activity != null){
				UserDTO user = new UserDTO(jsonResult);
				  
//				  SharedPreferences settings = activity.getSharedPreferences(Utils.PREFS_NAME, 0);
//				  SharedPreferences.Editor editor = settings.edit();
//				  Log.e("USERNAME", user.getEmail());
//				  editor.putString("username", user.getEmail());
//				  editor.commit();
				  
				  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
				  SharedPreferences.Editor edit = prefs.edit();
				  edit.putLong("UserId", user.getUserId()); 
				  edit.putString("nickName", user.getNickName());
				  edit.putString("password", user.getPassword());
				  edit.putString("email", user.getEmail());
				  edit.putString("Sex", user.getSex());
				  edit.putString("CP", String.valueOf(user.getCP()));
				  edit.commit();
				  
				  activity.toMain();
			}
			
		} else {
			LoginActivity activity = (LoginActivity) this.weakActivity.get();
			if (activity != null){
				activity.txtErrorPass.setText("Uuups! por favor, prueba de nuevo");
				activity.txtErrorPass.setVisibility(View.VISIBLE);
			}
		}
		
		pDialog.dismiss();
	}

}

