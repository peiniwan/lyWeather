package com.ly.weather.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ly.weather.R;
import com.ly.weather.model.WeatherInfo;
import com.ly.weather.util.HttpCallbackListener;
import com.ly.weather.util.HttpUtil;
import com.ly.weather.util.Utility;

public class WeatherActivity extends Activity implements OnClickListener {
	private LinearLayout weatherInfoLayout;
	/**
	 * 显示城市市名
	 */
	private TextView cityNameText;
	/**
	 * 用于显示发布时间
	 */
	private TextView publishText;
	/**
	 * 用于显示天气描述信息
	 */
	private TextView weatherDespText;
	/**
	 * 用于显示气温1
	 */
	private TextView temp1Text;
	/**
	 * 用于显示气温2
	 */
	private TextView temp2Text;
	/**
	 * 用于显示当前日期
	 */
	private TextView currentDateText;
	/**
	 * 切换城市按钮
	 */
	private Button switchCity;
	/**
	 * 更新天气按钮
	 */
	private Button refreshWeather;

	private List<WeatherInfo> weatherList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		// 初始化各控件
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);

		String cityName = getIntent().getStringExtra("cityName");
		if (!TextUtils.isEmpty(cityName)) {
			// 有县级代号时就去查询天气
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryFromServer(cityName, "cityName");
		} else {
			// 没有县级代号时就直接显示本地天气
			showWeather();
		}
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("同步中...");
			queryFromServer("cityName", "cityCode");
			break;
		default:
			break;
		}
	}

	/**
	 * 根据传入的地址和类型去向服务器查询天气信息
	 */
	private void queryFromServer(final String cityName, final String type) {
		String cityNameU8 = null;
		try {
			cityNameU8 = URLEncoder.encode(cityName, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		// 要加上ak对应的mcode，并且需要转码
		String address = "http://api.map.baidu.com/telematics/v3/weather?location="
				+ cityNameU8
				+ "&output=json&ak=vZ8GucwXI62RHVG2lPPFC4Gs"
				+ "&mcode=51:18:C7:9F:D3:9D:6E:85:F8:13:55:B2:18:7F:2E:C7:16:63:E7:40;com.ly.weather ";
		Log.d("weather", address);
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(final String response) {
				weatherList = Utility.handleWeatherResponse(
						WeatherActivity.this, response);

				Log.d("weather", response);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						showWeather();
					}
				});

			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						publishText.setText("同步失败");
					}
				});
			}
		});
	}

	/**
	 * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
	 */
	private void showWeather() {
		for (WeatherInfo weatherInfo : weatherList) {
			String week = weatherInfo.getWeather();
		}
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(weatherList.get(0).getTemperature());
		weatherDespText.setText(weatherList.get(0).getWeather());
		publishText.setText(weatherList.get(0).getWeek());
		currentDateText.setText(prefs.getString("date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
	}

}
