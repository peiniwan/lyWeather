package com.ly.weather.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ly.weather.util.ActivityCollector;
/**
 * activity的基类
 * @author Administrator
 *
 */
public class BaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("BaseActivity", getClass().getSimpleName());
		ActivityCollector.addActivity(this);
	}

	// 表明将一个马上要销毁的活动从活动管理器里移除
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}
}
