package com.smt.beacons.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class GestorRed {

	private static GestorRed instance;

	public static GestorRed getInstance() {
		if (instance == null) {
			instance = new GestorRed();
		}
		return instance;
	}
	
	public String getServerUrl(){
		return serverUrl;
	}
	
//	private String serverUrl = "https://inztantapi.azurewebsites.net/";
//	private String serverUrl = "https://inztantw.trafficmanager.net/";
	private String serverUrl = "http://beaconsws.azurewebsites.net/";
//	private String serverAsierUrl = "https://192.168.54.133/Inztant/ServiciosWeb.Nucleo/";
//	private String serverGalderUrl = "https://192.168.54.130/Inztant/ServiciosWeb.Nucleo/";
	
	
	private String loginUrl = serverUrl+"UserManagement.svc/json/login";
	private String beaconsFoundUrl = serverUrl+"UserManagement.svc/json/beaconDetected";
		
	public JSONObject login(JSONObject jsonInput) {
		try {
			JSONObject resultado = this.getJSONObjectFromURL(loginUrl, jsonInput);
			return resultado;
		} catch (Exception e) {
			return null;
		}
	}
	
	public JSONArray getBeaconsPromotions(JSONObject jsonInput) {
		try {
			JSONArray resultado = this.getJSONArrayFromURL(beaconsFoundUrl, jsonInput);
			return resultado;
		} catch (Exception e) {
			return null;
		}
	}
	
//	public String signUp(JSONObject jsonInput) {
//		try {
//			String completeLoginUrl = this.signupUrl; //  "this.serverUrl" + "/" + "this.loginUrl";
//			String resultado = this.getStringFromURL(completeLoginUrl, jsonInput);
//			return resultado;
//		} catch (Exception e) {
//			return null;
//		}
//	}

	// ///////////// Private methods ////////////////////

	private JSONObject getJSONObjectFromURL(String url, JSONObject json) {
		JSONObject jsonObject = null;
		HttpClient httpClient = null;
		try {
			Log.i("URL de WS", url);
			httpClient = new MyHttpClient();
			HttpPost request = new HttpPost(url);
			StringEntity params = new StringEntity(json.toString(), HTTP.UTF_8);
			request.addHeader("Content-Type", "application/json");
			request.setEntity(params);
			BasicResponseHandler responseHandler = new BasicResponseHandler();
			String responseBody = httpClient.execute(request, responseHandler);
			httpClient.getConnectionManager().shutdown();
			jsonObject = new JSONObject(responseBody);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (HttpResponseException e) {
			// Para el login
			if (url.contains(loginUrl)) {
				try {
					JSONObject jsonResponse = new JSONObject();
					jsonResponse.put("error", e.getMessage());
					return jsonResponse;
				} catch (Exception e2) {
					e.printStackTrace();
				}
			}
			// Si el servidor esta dormido habria que actuar aqui.
			if (e.getMessage().equals("El Token No pudo ser Validado")) {
				e.printStackTrace();
				return null;
			}
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Log.i("JSON devuelto por WS", jsonObject.toString());
			return jsonObject;
		} catch (Exception e) {
			Log.i("JSON NULL",
					"La respuesta no puede ser parseada. Ha habido un error");
			return null;
		}
	}

	private JSONObject getJSONObjectFromURL(String url) {
		JSONObject jsonObject = null;
		HttpClient httpClient = null;
		try {
			Log.i("URL de WS", url);
			httpClient = new MyHttpClient();
			HttpGet request = new HttpGet(url);
			BasicResponseHandler responseHandler = new BasicResponseHandler();
			String responseBody = httpClient.execute(request, responseHandler);
			httpClient.getConnectionManager().shutdown();
			jsonObject = new JSONObject(responseBody);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Log.i("JSON devuelto por WS", jsonObject.toString());
			return jsonObject;
		} catch (Exception e) {
			Log.i("JSON NULL",
					"La respuesta no puede ser parseada. Ha habido un error");
			return null;
		}
	}

	private JSONArray getJSONArrayFromURL(String url, JSONObject json) {
		JSONArray jsonArray = null;
		HttpClient httpClient = null;
		Log.i("JSONArrayFromURL", url);
		try {
			Log.i("JSON enviado a WS", json.toString());
			httpClient = new MyHttpClient();
			HttpPost request = new HttpPost(url);
			StringEntity params = new StringEntity(json.toString(), HTTP.UTF_8);
			request.addHeader("Content-Type", "application/json");
			request.setEntity(params);
			BasicResponseHandler responseHandler = new BasicResponseHandler();
			String responseBody = httpClient.execute(request, responseHandler);
			System.out.println(responseBody);
			httpClient.getConnectionManager().shutdown();
			jsonArray = new JSONArray(responseBody);
			Log.i("JSON Array recibido desde WS", jsonArray.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (HttpResponseException e) {
			// Si el servidor esta dormido habria que actuar aqui.
			if (e.getMessage().equals("El Token No pudo ser Validado")) {
				e.printStackTrace();
				return null;
			}
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			// Si no hay conexi�n, pasa por aqui.
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
			jsonArray = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Log.i("JSON devuelto por WS", jsonArray.toString());
			return jsonArray;
		} catch (Exception e) {
			Log.i("JSON NULL",
					"La respuesta no puede ser parseada. Ha habido un error");
			return new JSONArray();
		}
	}

	private JSONArray getJSONArrayFromURL(String url) {
		JSONArray jsonArray = null;
		HttpClient httpClient = null;
		try {
			httpClient = new MyHttpClient();
			HttpGet request = new HttpGet(url);
			BasicResponseHandler responseHandler = new BasicResponseHandler();
			String responseBody = httpClient.execute(request, responseHandler);
			System.out.println(responseBody);
			httpClient.getConnectionManager().shutdown();
			jsonArray = new JSONArray(responseBody);
			Log.i("JSON Array recibido desde WS", jsonArray.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (HttpResponseException e) {
			// Si el servidor esta dormido habria que actuar aqui.
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
			jsonArray = new JSONArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Log.i("JSON devuelto por WS", jsonArray.toString());
			return jsonArray;
		} catch (Exception e) {
			Log.i("JSON NULL",
					"La respuesta no puede ser parseada. Ha habido un error");
			return null;
		}
	}

	private String getStringFromURL(String url) {
		String responseBody = null;
		HttpClient httpClient = null;
		try {
			httpClient = new MyHttpClient();
			HttpGet request = new HttpGet(url);
			BasicResponseHandler responseHandler = new BasicResponseHandler();
			responseBody = httpClient.execute(request, responseHandler);
			System.out.println(responseBody);
			httpClient.getConnectionManager().shutdown();
			Log.i("String recibido desde WS", responseBody);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (HttpResponseException e) {
			// Si el servidor esta dormido habria que actuar aqui.
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseBody;
	}

	private String getStringFromURL(String url, JSONObject json) {
		String responseBody = null;
		HttpClient httpClient = null;
		try {
			Log.i("JSON enviado a WS", json.toString());
			Log.i("URL de WS", url);
			httpClient = new MyHttpClient();
			HttpPost request = new HttpPost(url);
			StringEntity params = new StringEntity(json.toString(), HTTP.UTF_8);
			request.addHeader("Content-Type", "application/json");
			request.setEntity(params);
			BasicResponseHandler responseHandler = new BasicResponseHandler();
			responseBody = httpClient.execute(request, responseHandler);
			httpClient.getConnectionManager().shutdown();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (responseBody != null) {
			Log.i("String devuelto por WS", responseBody);
		}
		return responseBody;
	}

}
