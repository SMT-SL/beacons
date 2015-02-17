package org.smt.activity;

import org.smt.R;
import org.smt.tasks.LoginTask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity{


		private EditText txtEmail;
		private EditText txtPass;
		private Button btnLogin;
		public TextView txtErrorPass;
		private TextView txtForgetPassword;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
//			requestWindowFeature(Window.FEATURE_NO_TITLE);
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_login);
//			verifyBluetooth();
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
			String user = settings.getString("email", "");
			if (!user.equals("")) {
				toMain();
			}
			txtErrorPass = (TextView) findViewById(R.id.text_user_error);
			
			txtForgetPassword = (TextView) findViewById(R.id.txt_recover_pass);
			txtEmail = (EditText) findViewById(R.id.editEmail);
			txtPass = (EditText) findViewById(R.id.editPassword);
			btnLogin = (Button) findViewById(R.id.btnLogin);
			btnLogin.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (txtEmail.getText().toString() == null || txtEmail.getText().toString().equals("")) {
						if(txtErrorPass!=null){
							txtErrorPass.setText("Debes escribir un email");
							txtErrorPass.setVisibility(View.VISIBLE);
						}
					} else if (txtPass.getText().toString() == null || txtPass.getText().toString().equals("")) {
						if(txtErrorPass!=null){
							txtErrorPass.setText("Debes escribir una contraseña");
							txtErrorPass.setVisibility(View.VISIBLE);
						}
					} else {
						if(txtErrorPass!=null){
							txtErrorPass.setText("");
							txtErrorPass.setVisibility(View.GONE);
						}
						new LoginTask(LoginActivity.this, txtEmail.getText().toString(), txtPass.getText().toString()).execute();
					}

				}
			});
			
			txtForgetPassword.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					toRecuperarPassword();

				}
			});
		}

		@Override
		protected void onStop() {
			super.onStop();
		}

		@Override
		protected void onPause() {
			super.onPause();
		}

		@Override
		protected void onResume() {

			super.onResume();
		}


		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// Handle action bar item clicks here. The action bar will
			// automatically handle clicks on the Home/Up button, so long
			// as you specify a parent activity in AndroidManifest.xml.
			int id = item.getItemId();
			if (id == R.id.action_settings) {
				return true;
			}
			return super.onOptionsItemSelected(item);
		}

		public void toMain() {

			Intent i = new Intent(LoginActivity.this, BuscarPromocionesActivity.class);
			startActivity(i);
			finish();

		}

		public void toRecuperarPassword() {
			Intent i = new Intent(LoginActivity.this, RecoverPassword.class);
			startActivity(i);

		}
	}
	

