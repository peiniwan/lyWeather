package com.ly.weather.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;

import com.ly.weather.R;
import com.ly.weather.view.IndexView;

public class LifeActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.life_activity);
		initView();
		// init(R.id.chuangyi, 0);
	}

	private void initView() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		IndexView chuangyi_iv = (IndexView) findViewById(R.id.chuangyi);
		IndexView ciche_iv = (IndexView) findViewById(R.id.ciche);
		IndexView lvyou_iv = (IndexView) findViewById(R.id.lvyou);
		IndexView ganmo_iv = (IndexView) findViewById(R.id.ganmo);
		IndexView sport_iv = (IndexView) findViewById(R.id.sport);
		IndexView ziwaixian_iv = (IndexView) findViewById(R.id.ziwaixian);
		chuangyi_iv.setTitle(prefs.getString("chuangyi_title", ""),
				prefs.getString("chuangyi_des", ""),
				prefs.getString("chuangyi_tipt", ""));
		ciche_iv.setTitle(prefs.getString("xi_title", ""),
				prefs.getString("xi_des", ""), prefs.getString("xi_tipt", ""));
		lvyou_iv.setTitle(prefs.getString("lv_title", ""),
				prefs.getString("lv_des", ""), prefs.getString("lv_tipt", ""));
		ganmo_iv.setTitle(prefs.getString("gan_title", ""),
				prefs.getString("gan_des", ""), prefs.getString("gan_tipt", ""));
		sport_iv.setTitle(prefs.getString("sport_title", ""),
				prefs.getString("sport_des", ""),
				prefs.getString("sport_tipt", ""));
		ziwaixian_iv.setTitle(prefs.getString("zi_title", ""),
				prefs.getString("zi_des", ""), prefs.getString("zi_tipt", ""));
	}
}
