package com.smt.beaconsmagnum.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.smt.beaconsmagnum.R;
import com.smt.beaconsmagnum.utils.BeaconsApp;

public class LoginActivity extends Activity {
	
	private static final String[] DUMMY_CREDENTIALS = new String[] {
		"hugo@:4321", "alberto@smt.com:alberto" };
	
	private Button loginBtn;
	private TextView emailTxt;
	private TextView passTxt;

	private TextView loginStatusMessageView;
	private View loginStatusView;
	private View loginFormView;
	
	private String email;
	private String pass;
	
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences settings = getSharedPreferences(BeaconsApp.PREFS_NAME, 0);
	    String user = settings.getString("username", "");
	    if (!user.equals("")){
	    	toMain(false);
	    }
		
		setContentView(R.layout.activity_login);
		
		loginBtn = (Button) findViewById(R.id.loginBtn);
		loginBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				attemptLogin();
			}
		});
		
		emailTxt = (TextView) findViewById(R.id.userEmail);
		passTxt = (TextView) findViewById(R.id.password);
		
		loginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		loginStatusView = (View) findViewById(R.id.login_status);
		loginFormView = (View) findViewById(R.id.login_form);
	}
	
	public void attemptLogin(){
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		emailTxt.setError(null);
		passTxt.setError(null);

		// Store values at the time of the login attempt.
		email = emailTxt.getText().toString();
		pass = passTxt.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(pass)) {
			passTxt.setError("This field is required");
			focusView = passTxt;
			cancel = true;
		} else if (pass.length() < 4) {
			passTxt.setError("Password is too short");
			focusView = passTxt;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(email)) {
			emailTxt.setError("This field is required");
			focusView = emailTxt;
			cancel = true;
		} 
		else if (!email.contains("@")) {
			emailTxt.setError("Invalid email address");
			focusView = emailTxt;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			loginStatusMessageView.setText("signing in...");
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				return 0;
			}
			Boolean userOK= false;
			for (String credential : DUMMY_CREDENTIALS) {
				String[] pieces = credential.split(":");
				if (pieces[0].equals(email)) {
					// Account exists, return true if the password matches.
					if (pieces[1].equals(pass))
						// Pass is ok, so login success
						return 3;
					userOK = true;
				} 
			}
			if (userOK){
				return 2;
			} else return 1;
			
		}

		@Override
		protected void onPostExecute(final Integer result) {
			mAuthTask = null;
			showProgress(false);

			if (result == 3) {
				toMain(true);
			} else if (result == 2){
				passTxt
					.setError("Incorrect password");
				passTxt.requestFocus();
			} else if (result == 1){
				emailTxt
					.setError("Incorrect user");
				emailTxt.requestFocus();
			} else {
				passTxt
					.setError("Unknown error, sorry, try again");
				passTxt.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	
	public void toMain(boolean saveUser){
		
		if (saveUser){
			
		  int index = email.indexOf("@");
		  String user = email.substring(0, index);
		  
	      SharedPreferences settings = getSharedPreferences(BeaconsApp.PREFS_NAME, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      
	      editor.putString("username", user);

	      // Commit the edits!
	      editor.commit();

		}
//		Intent intent = new Intent("com.smt.beaconssmt.services.BeaconsMonitoringService");
//		startService(intent);
		Intent i = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(i);
		this.finish();
	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			loginStatusView.setVisibility(View.VISIBLE);
			loginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							loginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			loginFormView.setVisibility(View.VISIBLE);
			loginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							loginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			loginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

}
