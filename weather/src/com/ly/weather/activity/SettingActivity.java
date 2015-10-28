package com.ly.weather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.ly.weather.R;
import com.ly.weather.view.SettingItemView;

/**
 * 设置界面
 * 
 * @author Administrator
 * 
 */
public class SettingActivity extends BaseActivity {
	private SettingItemView notifiction;
	private SharedPreferences mPref;
	private SettingItemView service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_activity);
		mPref = PreferenceManager.getDefaultSharedPreferences(this);
		notifiction = (SettingItemView) findViewById(R.id.notifiction);
		service = (SettingItemView) findViewById(R.id.service);
		TextView about = (TextView) findViewById(R.id.about);
		about.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this,
						AboutActivity.class);
				startActivity(intent);
			}
		});
		initUpdate();
		initService();

	}

	/**
	 * 是否开启服务
	 */
	private void initService() {
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
	}

	/**
	 * 是否开启通知栏
	 */
	private void initUpdate() {
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
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(this, WeatherActivity.class);
		startActivity(intent);
		finish();
	}
}
