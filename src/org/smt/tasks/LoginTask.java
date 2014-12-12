package org.smt.tasks;

import java.lang.ref.WeakReference;

import org.json.JSONObject;
import org.smt.activity.LoginActivity;
import org.smt.model.UserDTO;
import org.smt.utils.GestorRed;

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
		if (jsonResult != null) {

			Log.e("JSON recibido", jsonResult.toString());
			LoginActivity activity = (LoginActivity) this.weakActivity.get();

			if (activity != null) {
				UserDTO user = new UserDTO(jsonResult);
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
			if (activity != null) {
				activity.txtErrorPass.setText("Uuups! por favor, prueba de nuevo");
				activity.txtErrorPass.setVisibility(View.VISIBLE);
			}
		}

		pDialog.dismiss();
	}

}
