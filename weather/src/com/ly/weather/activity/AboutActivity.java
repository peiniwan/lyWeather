package com.ly.weather.activity;

import com.ly.weather.R;

import android.os.Bundle;
import android.view.Window;

public class AboutActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about_activity);

	}
}
