package com.smt.beaconsmagnum.activities;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.smt.beaconsmagnum.R;

public class WebViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		
		String web = getIntent().getStringExtra("web");
		
		WebView webview = (WebView)findViewById(R.id.webView1);
		webview.loadUrl(web);

	}
	
	@Override
	protected void onPause() {
		MainActivity.setSaliendo(true);
		super.onPause();
	}
}
