package com.ly.weather.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.ly.weather.R;
import com.ly.weather.activity.WeatherActivity;
import com.ly.weather.util.ImageLoderPic;

/**
 * 
 * AppWidgetProvider 必须要接收android.appwidget.action.APPWIDGET_UPDATE 广播
 * 
 * @author Administrator
 * 
 */
public class MyWidgetProvider extends AppWidgetProvider {

	// 更新部件时调用，在第1次添加部件时也会调用
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		setOnClickEvent(appWidgetManager, appWidgetIds, context);
		updateWidget(context);
	}

	@Override
	// 部件从host中删除
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		System.out.println("onDeleted widget");
	}

	@Override
	// 第1次创建时调用，之后再创建不会调用
	public void onEnabled(Context context) {
		super.onEnabled(context);
		System.out.println("onEnabled widget");
	}

	@Override
	// 当最后一个部件实例 被删除时 调用 用于清除onEnabled执行的操作
	public void onDisabled(Context context) {
		super.onDisabled(context);
		System.out.println("onDisabled widget");
	}

	/**
	 * 设置用户选择城市的点击事件
	 * 
	 */
	public void setOnClickEvent(AppWidgetManager appWidgetManager,
			int[] appWidgetIds, Context context) {
		Intent intent = new Intent(context, WeatherActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);// 写成这个就回到了activity
		RemoteViews rv = new RemoteViews(context.getPackageName(),
				R.layout.layout_widget);
		rv.setOnClickPendingIntent(R.id.ly_widget, pendingIntent);
		for (int i = 0; i < appWidgetIds.length; i++) {
			appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		/*
		 * 接收 <action android:name="com.ly.weather.start"/>
		 * 在其他组件或activity或service中发送这些广播
		 */
		if (intent.getAction().equals("com.ly.weather.start")) {
			updateWidget(context);
			System.out.println("receive com.ly.weather.start");
		}
	}

	private void updateWidget(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		// RemoteViews处理异进程中的View
		RemoteViews rv = new RemoteViews(context.getPackageName(),
				R.layout.layout_widget);
		rv.setTextViewText(R.id.widget_city_name,
				prefs.getString("current_city", null));
		rv.setTextViewText(R.id.widget_tmp, prefs.getString("tmp", null));
		rv.setTextViewText(R.id.widget_wind, prefs.getString("wind", null));
		ImageLoderPic.showPic(rv, R.id.iv_widget,
				prefs.getString("nigheUrl", null),
				prefs.getString("dayUrl", null));
		rv.setTextViewText(R.id.widget_des,
				prefs.getString("weather_info", null));

		AppWidgetManager am = AppWidgetManager.getInstance(context);
		int[] appWidgetIds = am.getAppWidgetIds(new ComponentName(context,
				MyWidgetProvider.class));

		am.updateAppWidget(appWidgetIds, rv);// 更新 所有实例
	}
}
