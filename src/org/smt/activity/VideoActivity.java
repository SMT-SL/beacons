package org.smt.activity;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.smt.R;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends Activity {

	VideoView videoView;
	Uri uriYouTube;
	String videoUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);

		String videoURL = getIntent().getStringExtra("videoURL");
		videoView = (VideoView) findViewById(R.id.videoView);
		MediaController mediaController = new MediaController(this);
		mediaController.setAnchorView(videoView);
		videoView.setMediaController(mediaController);
		videoView.requestFocus();

		YourAsyncTask truitonTask = new YourAsyncTask();
		truitonTask.execute();

	}

	void startPlaying(String url) {
		uriYouTube = Uri.parse(url);
		videoView.setVideoURI(uriYouTube);
		videoView.start();
	}

	private class YourAsyncTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(VideoActivity.this, "",
					"Loading Video wait...", true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				String url = "http://www.youtube.com/watch?v=OtLa7wDpuOU";
				videoUrl = getUrlVideoRTSP(url);
				Log.e("Video url for playing=========>>>>>", videoUrl);
			} catch (Exception e) {
				Log.e("Login Soap Calling in Exception", e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();

			videoView.setVideoURI(Uri.parse(videoUrl));
			MediaController mc = new MediaController(VideoActivity.this);
			videoView.setMediaController(mc);
			videoView.requestFocus();
			videoView.start();
			mc.show();
		}

	}

	public static String getUrlVideoRTSP(String urlYoutube) {
		try {
			String gdy = "http://gdata.youtube.com/feeds/api/videos/";
			DocumentBuilder documentBuilder = DocumentBuilderFactory
					.newInstance().newDocumentBuilder();
			String id = extractYoutubeId(urlYoutube);
			URL url = new URL(gdy + id);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			Document doc = documentBuilder.parse(connection.getInputStream());
			Element el = doc.getDocumentElement();
			NodeList list = el.getElementsByTagName("media:content");// /media:content
			String cursor = urlYoutube;
			for (int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				if (node != null) {
					NamedNodeMap nodeMap = node.getAttributes();
					HashMap<String, String> maps = new HashMap<String, String>();
					for (int j = 0; j < nodeMap.getLength(); j++) {
						Attr att = (Attr) nodeMap.item(j);
						maps.put(att.getName(), att.getValue());
					}
					if (maps.containsKey("yt:format")) {
						String f = maps.get("yt:format");
						if (maps.containsKey("url")) {
							cursor = maps.get("url");
						}
						if (f.equals("1"))
							return cursor;
					}
				}
			}
			return cursor;
		} catch (Exception ex) {
			Log.e("Get Url Video RTSP Exception======>>", ex.toString());
		}
		return urlYoutube;

	}

	protected static String extractYoutubeId(String url)
			throws MalformedURLException {
		String id = null;
		try {
			String query = new URL(url).getQuery();
			if (query != null) {
				String[] param = query.split("&");
				for (String row : param) {
					String[] param1 = row.split("=");
					if (param1[0].equals("v")) {
						id = param1[1];
					}
				}
			} else {
				if (url.contains("embed")) {
					id = url.substring(url.lastIndexOf("/") + 1);
				}
			}
		} catch (Exception ex) {
			Log.e("Exception", ex.toString());
		}
		return id;
	}

}
