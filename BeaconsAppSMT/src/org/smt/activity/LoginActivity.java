package org.smt.activity;

import org.altbeacon.beacon.BeaconManager;
import org.smt.R;
import org.smt.app.BeaconsApp;
import org.smt.tasks.LoginTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

	// private BeaconManager mBeaconManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		verifyBluetooth();
		// mBeaconManager = BeaconManager.getInstanceForApplication(this);
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		String user = settings.getString("email", "");
		if (!user.equals("")) {
			toMain();
		}

		txtEmail = (EditText) findViewById(R.id.editPassword);
		txtPass = (EditText) findViewById(R.id.editText2);
		btnLogin = (Button) findViewById(R.id.btnEditar);
		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (txtEmail.getText().toString() == null || txtEmail.getText().toString().equals("")) {
					Toast.makeText(LoginActivity.this, "Debes escribir un email", Toast.LENGTH_SHORT).show();
				} else if (txtPass.getText().toString() == null || txtPass.getText().toString().equals("")) {
					Toast.makeText(LoginActivity.this, "Debes escribir una contraseña", Toast.LENGTH_SHORT).show();
				} else {
					new LoginTask(LoginActivity.this, txtEmail.getText().toString(), txtPass.getText().toString()).execute();
					// toMain();
				}

			}
		});

		txtErrorEmail = (TextView) findViewById(R.id.text_user_error);
		txtErrorPass = (TextView) findViewById(R.id.text_pass_error);
	}

	@Override
	protected void onStop() {
		((BeaconsApp) this.getApplication()).setLoginActivity(null);
		super.onStop();
	}

	@Override
	protected void onPause() {
		((BeaconsApp) this.getApplication()).setLoginActivity(null);
		super.onPause();
	}

	@Override
	protected void onResume() {

		((BeaconsApp) this.getApplication()).setLoginActivity(this);
		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	private void verifyBluetooth() {

		try {
			if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Bluetooth not enabled");
				builder.setMessage("Please enable bluetooth in settings and restart this application.");
				builder.setPositiveButton(android.R.string.ok, null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						finish();
						System.exit(0);
					}
				});
				builder.show();
			}
		} catch (RuntimeException e) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Bluetooth LE not available");
			builder.setMessage("Sorry, this device does not support Bluetooth LE.");
			builder.setPositiveButton(android.R.string.ok, null);
			builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					finish();
					System.exit(0);
				}

			});
			builder.show();

		}

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

		Intent i = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(i);
		finish();

	}

}
