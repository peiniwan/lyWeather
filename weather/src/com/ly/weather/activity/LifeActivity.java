package com.ly.weather.activity;

import java.io.Serializable;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;

import com.ly.weather.R;
import com.ly.weather.model.WeatherData.IndexInfo;
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
		IndexView chuangyi_iv = (IndexView) findViewById(R.id.chuangyi);
		IndexView ciche_iv = (IndexView) findViewById(R.id.ciche);
		IndexView lvyou_iv = (IndexView) findViewById(R.id.lvyou);
		IndexView ganmo_iv = (IndexView) findViewById(R.id.ganmo);
		IndexView sport_iv = (IndexView) findViewById(R.id.sport);
		IndexView ziwaixian_iv = (IndexView) findViewById(R.id.ziwaixian);

		ArrayList<IndexInfo> index = WeatherActivity.index;

		chuangyi_iv.setTitle(index.get(0).title, index.get(0).des,
				index.get(0).tipt);
		ciche_iv.setTitle(index.get(1).title, index.get(1).des,
				index.get(1).tipt);
		lvyou_iv.setTitle(index.get(2).title, index.get(2).des,
				index.get(2).tipt);
		ganmo_iv.setTitle(index.get(3).title, index.get(3).des,
				index.get(3).tipt);
		sport_iv.setTitle(index.get(4).title, index.get(4).des,
				index.get(4).tipt);
		ziwaixian_iv.setTitle(index.get(5).title, index.get(5).des,
				index.get(5).tipt);
	}
}
