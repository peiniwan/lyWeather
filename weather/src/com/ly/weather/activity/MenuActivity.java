package com.ly.weather.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.ly.weather.R;

public class MenuActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_activity);
	}
}
