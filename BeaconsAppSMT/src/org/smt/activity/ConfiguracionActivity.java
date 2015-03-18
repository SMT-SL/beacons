package org.smt.activity;


import java.util.ArrayList;

import org.smt.R;
import org.smt.adapters.ConfiguracionAdapter;
import org.smt.model.ConfiguracionApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Activity used for changing configuration of applications.
 * @author Azam-smt
 *
 */
public class ConfiguracionActivity extends Activity {
	public static  ConfiguracionAdapter configAdapter;
	private ArrayList<ConfiguracionApp> itemsConfig;
	private ListView listConfiguraciones;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuracion);
		crearListaConfiguracion();
		configAdapter = new ConfiguracionAdapter(this,itemsConfig);
		listConfiguraciones = (ListView) findViewById(R.id.listViewConfig);
		listConfiguraciones.setAdapter(configAdapter);
		listConfiguraciones.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
					Intent i = new Intent(ConfiguracionActivity.this, ChangePasswordActivity.class);
					startActivity(i);
				}

		});
	}
	
	private void crearListaConfiguracion() {
		if(itemsConfig==null){
			itemsConfig=new ArrayList<ConfiguracionApp>();
		}
		if(itemsConfig.isEmpty()){
		  itemsConfig.add(new ConfiguracionApp(0,0,"Change Password"));
		  itemsConfig.add(new ConfiguracionApp(1,1,"Change Notificacion"));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
