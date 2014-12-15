package com.smt.beacons.activity;

import com.smt.beacons.R;
import com.smt.beacons.services.BeaconsMonitoringService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;

public class WebViewActivity extends Activity {

	Button btnOk;
	private boolean saliendo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		
		String web = getIntent().getStringExtra("web");
		
		WebView webview = (WebView)findViewById(R.id.webView1);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.loadUrl(web);
		
		btnOk = (Button) findViewById(R.id.btnOk);
		btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
				
			}
		});
		
		saliendo = true;

	}
	
	@Override
	protected void onPause() {
		if (saliendo){
			Intent i = new Intent(getApplicationContext(), BeaconsMonitoringService.class);
			startService(i);
		}
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		saliendo = true;
		Intent i = new Intent(getApplicationContext(), BeaconsMonitoringService.class);
		stopService(i);
		super.onResume();
	}
	
	@Override
	public void onBackPressed() {
		saliendo = false;
		super.onBackPressed();
	}
}
