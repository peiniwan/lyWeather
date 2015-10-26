package com.ly.weather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.ly.weather.R;
import com.ly.weather.view.SettingItemView;

public class SettingActivity extends BaseActivity {
	private SettingItemView notifiction;// 是否开启通知栏
	private SharedPreferences mPref;
	private SettingItemView service;
	private SettingItemView window;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_activity);
		mPref = PreferenceManager.getDefaultSharedPreferences(this);
		notifiction = (SettingItemView) findViewById(R.id.notifiction);
		service = (SettingItemView) findViewById(R.id.service);
		window = (SettingItemView) findViewById(R.id.window);

		boolean autoUpdate = mPref.getBoolean("notifiction", true);
		// 再次进来就判断
		if (autoUpdate) {
			notifiction.setChecked(true);
		} else {
			notifiction.setChecked(false);
		}
		notifiction.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 判断当前的勾选状态
				if (notifiction.isChecked()) {
					// 设置不勾选
					notifiction.setChecked(false);
					// 更新sp
					mPref.edit().putBoolean("notifiction", false).commit();
				} else {
					notifiction.setChecked(true);
					// 更新sp
					mPref.edit().putBoolean("notifiction", true).commit();
				}
			}
		});

		boolean serviceSetting = mPref.getBoolean("service", true);
		// 再次进来就判断
		if (serviceSetting) {
			service.setChecked(true);
		} else {
			service.setChecked(false);
		}
		service.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 判断当前的勾选状态
				if (service.isChecked()) {
					// 设置不勾选
					service.setChecked(false);
					// 更新sp
					mPref.edit().putBoolean("service", false).commit();
				} else {
					service.setChecked(true);
					// 更新sp
					mPref.edit().putBoolean("service", true).commit();
				}
			}
		});

		boolean windowSetting = mPref.getBoolean("window", true);
		// 再次进来就判断
		if (windowSetting) {
			window.setChecked(true);
		} else {
			window.setChecked(false);
		}
		window.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 判断当前的勾选状态
				if (window.isChecked()) {
					// 设置不勾选
					window.setChecked(false);
					// 更新sp
					mPref.edit().putBoolean("window", false).commit();
				} else {
					window.setChecked(true);
					// 更新sp
					mPref.edit().putBoolean("window", true).commit();
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(this, WeatherActivity.class);
		startActivity(intent);
		finish();
	}
}
