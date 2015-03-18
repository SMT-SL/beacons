package org.smt.adapters;

import java.util.ArrayList;

import org.smt.R;
import org.smt.model.ConfiguracionApp;
import org.smt.utils.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Toast;



public class ConfiguracionAdapter extends BaseAdapter{
	private static final int ITEM_NOTIFICATION = 0;
	private static final int ITEM_PASSWORD = 1;
	
	private CheckBox chckBox;
	private ArrayList<ConfiguracionApp> arrayConfigs;
	Context context;

	public ConfiguracionAdapter(Context context,ArrayList<ConfiguracionApp> tiposConfig) {
		super();
		this.context = context;
		arrayConfigs=tiposConfig;

	}	
	@Override
	public int getViewTypeCount() {
	    return arrayConfigs.size();
	}
	
	@Override
	public int getItemViewType(int position) {
		
	    return arrayConfigs.get(position).getTypeConfig();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		int type = getItemViewType(position);
		   if (row  == null) {
			   
			   if (type == ITEM_PASSWORD) {
				   //Controlar activacion o desactivacion de notificaciones
				   convertView = inflater.inflate(R.layout.item_list_config_notificacion, parent, false);
				   chckBox= (CheckBox) convertView.findViewById(R.id.checkBoNotification);
				   chckBox.setChecked(Utils.getEstadoNotificacion(context));//Poner estado de notificacion
				   chckBox.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if(chckBox.isChecked()){
					   			showMessage("Notificaciones Activado");
					   			Utils.guardarConfigNotific(true,context);
					   			
				   			}else{
				   				
				   				showMessage("Notificaciones desactivado");
				   				Utils.guardarConfigNotific(false,context);
				   			}
							

						}
					});
			   }
			   
			   if (type == ITEM_NOTIFICATION) {
		                convertView = inflater.inflate(R.layout.item_list_config_password, parent, false);
		                
			   } // else {
//		                 //infalte layout of normaltype
//		    	}
		   
		   }

		return convertView;
	}
	@Override
	public int getCount() {
		return arrayConfigs.size();
	}
	@Override
	public Object getItem(int position) {
		return arrayConfigs.get(position);
	}
	@Override
	public long getItemId(int position) {
		return arrayConfigs.get(position).getPos();
	}
	
	private void showMessage(String msg){
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.show();
	}
	
}
