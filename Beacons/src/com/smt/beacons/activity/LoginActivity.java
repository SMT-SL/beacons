package com.smt.beacons.activity;

import com.smt.beacons.R;
import com.smt.beacons.services.BeaconsMonitoringService;
import com.smt.beacons.tasks.LoginTask;
import com.smt.beacons.utils.Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	private EditText txtEmail;
	private EditText txtPass;
	private Button btnLogin;
	public TextView txtErrorEmail;
	public TextView txtErrorPass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
	    String user = settings.getString("email", "");
	    if (!user.equals("")){
	    	toMain();
	    }
		
		txtEmail = (EditText) findViewById(R.id.editText1);
		txtPass = (EditText) findViewById(R.id.editText2);
		btnLogin = (Button) findViewById(R.id.button1);
		btnLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				if (txtEmail.getText().toString() == null || txtEmail.getText().toString().equals("")){
					Toast.makeText(LoginActivity.this, "Debes escribir un email", Toast.LENGTH_SHORT).show();
				} else if (txtPass.getText().toString() == null || txtPass.getText().toString().equals("")){
					Toast.makeText(LoginActivity.this, "Debes escribir una contraseña", Toast.LENGTH_SHORT).show();
				} else {
					new LoginTask(LoginActivity.this, txtEmail.getText().toString(), txtPass.getText().toString()).execute();
//					toMain();
				}
				
			}
		});
		
		txtErrorEmail = (TextView) findViewById(R.id.text_user_error);
		txtErrorPass = (TextView) findViewById(R.id.text_pass_error);
	}
	

	@Override
	protected void onResume() {
		Intent i = new Intent(getApplicationContext(), BeaconsMonitoringService.class);
		stopService(i);
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
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
	
	public void toMain(){
		
		Intent i = new Intent(LoginActivity.this, EasiActivity.class);
		startActivity(i);
		finish();
	}
	
}
