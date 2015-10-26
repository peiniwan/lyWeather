package com.ly.weather.util;

import java.util.Date;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.RemoteViews;

import com.ly.weather.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 使用ImageLoader加载图片,xutils不能获取bitmap对象
 * 
 * @author Administrator
 * 
 */
public class ImageLoderPic {
	public static void showPic(final RemoteViews mRemoteViews, final int iv,
			String nightPictureUrl, String dayPictureUrl) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.loading)
				// 不知道为什么不能显示默认图片
				.showImageForEmptyUri(R.drawable.loading)
				.showImageOnFail(R.drawable.loading).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		Date date = new Date();
		int currHours = date.getHours();

		if (currHours > 17 || currHours < 7) {
			ImageLoader.getInstance().loadImage(nightPictureUrl, options,
					new SimpleImageLoadingListener() {// 下载成功此方法调用
						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							super.onLoadingComplete(imageUri, view, loadedImage);
							mRemoteViews.setImageViewBitmap(iv, loadedImage);// 设置图片
							// System.out.println(nightPictureUrl);
						}

					});

		} else {
			ImageLoader.getInstance().loadImage(dayPictureUrl, options,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							super.onLoadingComplete(imageUri, view, loadedImage);
							mRemoteViews.setImageViewBitmap(iv, loadedImage);
							// System.out.println(loadedImage.getHeight());
							// System.out.println(oneWeatherInfo.dayPictureUrl);
						}

					});
		}
	}
}
