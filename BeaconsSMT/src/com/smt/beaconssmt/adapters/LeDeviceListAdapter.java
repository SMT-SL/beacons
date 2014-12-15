package com.smt.beaconssmt.adapters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Utils;
import com.smt.beaconssmt.R;
import com.smt.beaconssmt.activities.MainActivity;


/**
 * Displays basic information about beacon.
 *
 * @author wiktor@estimote.com (Wiktor Gworek)
 */
public class LeDeviceListAdapter extends BaseAdapter {

  private ArrayList<Beacon> beacons;
  private LayoutInflater inflater;

  public LeDeviceListAdapter(Context context) {
    this.inflater = LayoutInflater.from(context);
    this.beacons = new ArrayList<Beacon>();
  }

  public void replaceWith(Collection<Beacon> newBeacons) {
    this.beacons.clear();
    this.beacons.addAll(newBeacons);
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return beacons.size();
  }

  @Override
  public Beacon getItem(int position) {
    return beacons.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View view, ViewGroup parent) {
    view = inflateIfRequired(view, position, parent);
    bind(getItem(position), view);
    return view;
  }

  private void bind(Beacon beacon, View view) {
	  Date fecha = new Date();
    ViewHolder holder = (ViewHolder) view.getTag();
    if (beacon.getMinor() == 64444){
//    	holder.beaconImageView.setImageResource(R.drawable.beacon_blue);
//    	holder.macTextView.setText(String.format("%s (%.2fm)","'Has ganado 2 entradas para el Coca-Cola Music Xperience sólo por estar aquí y ahora'", Utils.computeAccuracy(beacon)));
    	holder.beaconImageView.setImageResource(R.drawable.realmadridbarcelona);
		holder.macTextView.setText(String.format("%s (%.2fm)","Hola "+MainActivity.user+"! Son las "+fecha.getHours()+":"+fecha.getMinutes()+":"+fecha.getSeconds()+" y hoy, "+fecha.getDate()+" del "+fecha.getMonth()+", es el día del fútbol: \n \nParticipa en la porra del clásico Real Madrid - FC Barcelona en nuestro local, y podrás ganar premios alucinantes", Utils.computeAccuracy(beacon)));
    } else if (beacon.getMinor() == 36328){
//		holder.beaconImageView.setImageResource(R.drawable.beacon_purple);
		holder.beaconImageView.setImageResource(R.drawable.realmadridbarcelona);
//		holder.macTextView.setText(String.format("%s (%.2fm)","Promoción 'Vente al curso de peluquería de L'Oreal al que Coca-Cola y Bacardi te invitan en la sala 5'", Utils.computeAccuracy(beacon)));
		holder.macTextView.setText(String.format("%s (%.2fm)","Hola "+MainActivity.user+"! Son las "+fecha.getHours()+":"+fecha.getMinutes()+":"+fecha.getSeconds()+" y hoy, "+fecha.getDate()+" del "+fecha.getMonth()+", es el día del fútbol: \n \nParticipa en la porra del clásico Real Madrid - FC Barcelona en nuestro local, y podrás ganar premios alucinantes", Utils.computeAccuracy(beacon)));
	} else if (beacon.getMinor() == 31394) {
//		holder.beaconImageView.setImageResource(R.drawable.beacon_green);
		holder.beaconImageView.setImageResource(R.drawable.whatsredlogo);
//		holder.macTextView.setText(String.format("%s (%.2fm)","Promoción 'Hoy, con tu Coca-Cola light + Bacardi Blanco te invitamos a hacerte un peinado de trenzas en la zona de la piscina'", Utils.computeAccuracy(beacon)));
//		holder.macTextView.setText(String.format("%s (%.2fm)","Hola "+MainActivity.user+"! Son las "+fecha.getHours()+":"+fecha.getMinutes()+":"+fecha.getSeconds()+" y hoy, "+fecha.getDate()+" del "+fecha.getMonth()+", es el día del fútbol: \n \nParticipa en la porra del clásico Real Madrid - FC Barcelona en nuestro local, y podrás ganar premios alucinantes", Utils.computeAccuracy(beacon)));
		holder.macTextView.setText(String.format("%s (%.2fm)","Hola "+MainActivity.user+", descárgate Whats Red, tu nuevo asistente personal de ocio", Utils.computeAccuracy(beacon)));
	}
//    holder.macTextView.setText(String.format("MAC: %s (%.2fm)", beacon.getMacAddress(), Utils.computeAccuracy(beacon)));
    holder.majorTextView.setText("Major: " + beacon.getMajor());
    holder.minorTextView.setText("Minor: " + beacon.getMinor());
    holder.measuredPowerTextView.setText("MPower: " + beacon.getMeasuredPower());
    holder.rssiTextView.setText("RSSI: " + beacon.getRssi());
  }

  private View inflateIfRequired(View view, int position, ViewGroup parent) {
    if (view == null) {
      view = inflater.inflate(R.layout.device_item, null);
      view.setTag(new ViewHolder(view));
    }
    return view;
  }

  static class ViewHolder {
    final TextView macTextView;
    final TextView majorTextView;
    final TextView minorTextView;
    final TextView measuredPowerTextView;
    final TextView rssiTextView;
    final ImageView beaconImageView;

    ViewHolder(View view) {
      macTextView = (TextView) view.findViewWithTag("mac");
      majorTextView = (TextView) view.findViewWithTag("major");
      minorTextView = (TextView) view.findViewWithTag("minor");
      measuredPowerTextView = (TextView) view.findViewWithTag("mpower");
      rssiTextView = (TextView) view.findViewWithTag("rssi");
      beaconImageView = (ImageView) view.findViewWithTag("img");
    }
  }
}

