package com.smt.beacons.utils;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Application;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.smt.beacons.easibeacons.IBeacon;
import com.smt.beacons.model.IBeaconsFound;

public class BeaconsApp  extends Application { 
	
	private static List<IBeaconsFound> notifiedBeacons;
	
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	@Override
	public void onCreate() {
		notifiedBeacons = new ArrayList<IBeaconsFound>();
		
		options = new DisplayImageOptions.Builder()
//      .showImageOnLoading(R.drawable.ic_stub) // resource or drawable
//      .showImageForEmptyUri(R.drawable.ic_empty) // resource or drawable
//      .showImageOnFail(R.drawable.ic_error) // resource or drawable
//      .resetViewBeforeLoading(false)  // default
//      .delayBeforeLoading(1000)
//      .cacheInMemory(false) // default
//      .cacheOnDisk(false) // default
//      .preProcessor(...)
//      .postProcessor(...)
//      .extraForDownloader(...)
//      .considerExifParams(false) // default
//      .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
//      .bitmapConfig(Bitmap.Config.ARGB_8888) // default
//      .decodingOptions(...)
//      .displayer(new SimpleBitmapDisplayer()) // default
//      .handler(new Handler()) // default
		.cacheOnDisk(true)
		.cacheInMemory(true)
        .build();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(options).build();
		
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
		
		super.onCreate();
	}
	
	public boolean isAlreadyNotifiedBeacon(IBeacon beacon, Date date){
		for (int i = 0 ; i<notifiedBeacons.size(); i++){
			if (notifiedBeacons.get(i).getBeacon().equals(beacon) && date.getTime()-notifiedBeacons.get(i).getDate().getTime()>30000){
				return true;
			}
		}
		return false;
	}
	
	public void addBeacon(IBeacon beacon, Date date){
		boolean exist = false;
		
		for (int i = 0 ; i<notifiedBeacons.size(); i++){
			if (notifiedBeacons.get(i).getBeacon().equals(beacon)){
				exist = true;
			}
		}
		
		if (!exist){
			notifiedBeacons.add(new IBeaconsFound(beacon, date));
		}
		
	}
	
	public void deleteBeacon(IBeacon beacon){
		for (int i = 0 ; i<notifiedBeacons.size(); i++){
			if (notifiedBeacons.get(i).getBeacon().equals(beacon)){
				notifiedBeacons.remove(i);
			}
		}
	}
	
	public void refreshBeacon(IBeacon beacon, Date date){
		for (int i = 0 ; i<notifiedBeacons.size(); i++){
			if (notifiedBeacons.get(i).getBeacon().equals(beacon) && notifiedBeacons.get(i).getDate().after(date)){
				notifiedBeacons.remove(i);
				notifiedBeacons.add(new IBeaconsFound(beacon, date));
			}
		}
	}
	
	public ImageLoader getImageLoader(){
		return imageLoader;
	}
	
	public DisplayImageOptions getDisplayImageOptions(){
		return options;
	}
	     
	
}
