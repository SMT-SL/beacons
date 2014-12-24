package org.smt.utils;




import org.smt.R;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


public class MyImageLoadingListener implements ImageLoadingListener{

	@Override
	public void onLoadingStarted(String imageUri, View view) {
		((ImageView) view).setImageResource(R.drawable.ic_launcher);
	}

	@Override
	public void onLoadingFailed(String imageUri, View view,
			FailReason failReason) {
		
	}

	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		
	}

	@Override
	public void onLoadingCancelled(String imageUri, View view) {
		
	}

}
