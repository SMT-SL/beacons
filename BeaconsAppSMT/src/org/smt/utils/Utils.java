package org.smt.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;

public class Utils {
	/**
	 * The Log tag for debugging messages
	 */
	public final static String LOG_TAG = "easiBeacon";
	public static String PREFS_NAME = "prefsBeacons";
	private static int FRGMENT_NUMBER=0;
	public static Boolean isPasswordSame(final String password1,final String password2){
	
		return password1.equals(password2);
	}
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static JSONObject getTokenDTO(Context context){
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		PackageInfo pInfo;
		JSONObject tokenDTO = new JSONObject();
		try {
			pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			tokenDTO.put("userId", prefs.getLong("UserId", 0));
			tokenDTO.put("token", prefs.getString("userToken", ""));
			tokenDTO.put("version",  pInfo.versionName);
			tokenDTO.put("Idioma", "");
			
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tokenDTO;
	}
	
	public static String getMensaje(final int codigoError){
	
		 String message="";
		 
			switch (codigoError){
				
				case -1:
					message="No se ha podido realizar operacion correctamente!!";
					break;
				
				case 101:
					message="Error de token, por favor hace login otra vez";
					break;
					
				case 102:
					message="Ya existe nickname elegido";
					break;
					
				case 103:
					message="Ya existe el email escrito";
					break;
					
				case 104:
					message="Uuups! email o contraseña esta incorrecta";
					break;
					
				case 105:
					message="Para seguir usando la app, debes actaulizar la version";
					break;
				case 107:
					message="Para seguir usando la app, debes validar tu email";
					break;
				case 108:
					message="La contraseña actual es erronea";
					break;	
				case 201:
					message="No se han encontrado promociones";
					break;
				
				case 202:
					message="Promociones ya esta guardada";
					break;
				
				case 203:
					message="No promocion guardada en wallet";
					break;
				
				case 400:
					message="Error generico, vuelva a intentar mas tarde";
					break;
		
				case 401:
					message="Error en Base de datos, vuelva a intentar mas tarde";
					break;
			}
			
		return message;
	}
	public static Boolean isformatoFechaCorrecto(String fechaa){
		Boolean correcto=false;
		Date date = null;
		SimpleDateFormat simpleFormat=null;
		try {
			simpleFormat= new SimpleDateFormat("dd/MM/yyyy");
			simpleFormat.setLenient(false);
			date=simpleFormat.parse(fechaa);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		if(date!=null){
			correcto=true;
			}		
		return correcto;
	}
	
	public static void setfragmentNumberEnActivity(int fragmentNumber){
		FRGMENT_NUMBER=fragmentNumber;
	}
	public static int getfragmentNumberEnActivity(){
		return FRGMENT_NUMBER;
	}
	/**
	 * 
	 * @param object
	 * @param obList
	 * @return
	 */
	public static Boolean isExistObjectInList(Object object,ArrayList<Object> obList){
		boolean exists=false;
		for (Object objeto :obList){
			if (objeto.equals(object)){
				exists = true;
				break;
			}
		}
		return exists;
	}
	
	public static void guardarConfigNotific(boolean isActivado,Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
			edit.putBoolean("Notificaciones",isActivado);
			edit.commit();
	}
	
	public static Boolean getEstadoNotificacion(Context context){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		return settings.getBoolean("Notificaciones", true);
	}
}
