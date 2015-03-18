package org.smt.activity;

import org.smt.R;
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

/**
 * 
 * @author Azam-smt
 *
 */
public class MainActivity extends Activity {


	private Button btnStartSession;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnStartSession=(Button)  findViewById(R.id.btnStartSession);
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		String user = settings.getString("email", "");
		if (!user.equals("")) {
			toMain();
		}

		btnStartSession.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				toLogin();

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
	public void toLogin() {

		Intent i = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(i);

	}
	public void toMain() {

		Intent i = new Intent(MainActivity.this, BuscarPromocionesActivity.class);
		startActivity(i);
		finish();

	}
	public void toRegister(View view) {

		Intent i = new Intent(MainActivity.this, RegisterUserActivity.class);
		startActivity(i);

	}
	

}
