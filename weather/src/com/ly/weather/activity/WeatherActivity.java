package com.ly.weather.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.ly.weather.R;
import com.ly.weather.model.WeatherInfo;
import com.ly.weather.service.AutoUpdateService;
import com.ly.weather.util.HttpCallbackListener;
import com.ly.weather.util.HttpUtil;
import com.ly.weather.util.Utility;

public class WeatherActivity extends Activity implements OnClickListener {
	private LinearLayout weatherInfoLayout;

	/**
	 * 切换城市按钮
	 */
	private Button switchCity;
	/**
	 * 更新天气按钮
	 */
	private Button refreshWeather;

	private String cityName;

	private Button menu;

	private TextView publish_text;

	private ImageView line1;

	private LinearLayout qitaday;

	private TextView today_data;
	private TextView current_city;
	private ImageView curr_pic;
	private TextView weather_info;
	private TextView wind;
	private TextView tmp;

	private ImageView one_weather_pic;
	private TextView one_weather_info;
	private TextView one_tmp;
	private TextView one_date;

	private ImageView two_weather_pic;
	private TextView two_weather_info;
	private TextView two_tmp;
	private TextView two_date;

	private ImageView three_weather_pic;
	private TextView three_weather_info;
	private TextView three_one_tmp;
	private TextView three_one_date;

	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_activity);
		initView();

		cityName = getIntent().getStringExtra("cityName");
		if (!TextUtils.isEmpty(cityName)) {
			// 有县级代号时就去查询天气
			publish_text.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			line1.setVisibility(View.INVISIBLE);
			qitaday.setVisibility(View.INVISIBLE);
			queryFromServer(cityName);
		} else {
			// 没有市级代号时就直接显示本地天气
			showWeather();
		}

		refreshWeather.setOnClickListener(this);
		menu.setOnClickListener(this);
	}

	private void initView() {
		// 初始化layout
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		qitaday = (LinearLayout) findViewById(R.id.qitaday);
		line1 = (ImageView) findViewById(R.id.line1);
		// 上面布局
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		menu = (Button) findViewById(R.id.menu);
		publish_text = (TextView) findViewById(R.id.publish_text);
		// 第一天
		today_data = (TextView) findViewById(R.id.today_data);
		current_city = (TextView) findViewById(R.id.current_city);
		curr_pic = (ImageView) findViewById(R.id.curr_pic);
		weather_info = (TextView) findViewById(R.id.weather_info);
		wind = (TextView) findViewById(R.id.wind);
		tmp = (TextView) findViewById(R.id.tmp);
		// 第二天
		one_weather_pic = (ImageView) findViewById(R.id.one_weather_pic);
		one_weather_info = (TextView) findViewById(R.id.one_weather_info);
		one_tmp = (TextView) findViewById(R.id.one_tmp);
		one_date = (TextView) findViewById(R.id.one_date);
		// 第三天
		two_weather_pic = (ImageView) findViewById(R.id.two_weather_pic);
		two_weather_info = (TextView) findViewById(R.id.two_weather_info);
		two_tmp = (TextView) findViewById(R.id.two_tmp);
		two_date = (TextView) findViewById(R.id.two_date);
		// 第四天
		three_weather_pic = (ImageView) findViewById(R.id.three_weather_pic);
		three_weather_info = (TextView) findViewById(R.id.three_weather_info);
		three_one_tmp = (TextView) findViewById(R.id.three_one_tmp);
		three_one_date = (TextView) findViewById(R.id.three_one_date);
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
			publish_text.setText("同步中...");

			queryFromServer(prefs.getString("city_name", ""));
			break;
		default:
			break;
		}
	}

	/**
	 * 根据传入的地址和类型去向服务器查询天气信息
	 */
	private void queryFromServer(final String cityName) {
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
		// Log.d("weather", address);
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(final String response) {
				ArrayList<WeatherInfo> weatherList = Utility
						.handleWeatherResponse(WeatherActivity.this, response);

				// Log.d("weather", response);
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
						publish_text.setText("同步失败");
					}
				});
			}
		});
	}

	/**
	 * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
	 */
	private void showWeather() {

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Date date = new Date();
		int currHours = date.getHours();
		switchCity.setOnClickListener(this);
		BitmapUtils bitmapUtils = new BitmapUtils(this);
		// 第一天
		if (currHours > 17 || currHours < 7) {
			String url_1 = prefs.getString("one_night", "");
			bitmapUtils.display(curr_pic, url_1);
			String url_2 = prefs.getString("two_night", "");
			bitmapUtils.display(one_weather_pic, url_2);
			String url_3 = prefs.getString("three_night", "");
			bitmapUtils.display(two_weather_pic, url_3);
			String url_4 = prefs.getString("four_night", "");
			bitmapUtils.display(three_weather_pic, url_4);

		} else {
			String url_1 = prefs.getString("one_day", "");
			bitmapUtils.display(curr_pic, url_1);
			String url_2 = prefs.getString("two_day", "");
			bitmapUtils.display(one_weather_pic, url_2);
			String url_3 = prefs.getString("three_day", "");
			bitmapUtils.display(two_weather_pic, url_3);
			String url_4 = prefs.getString("four_day", "");
			bitmapUtils.display(three_weather_pic, url_4);
		}
		today_data.setText(prefs.getString("date_all", ""));
		current_city.setText(prefs.getString("city_name", ""));
		String shishi = prefs.getString("one_date", "");
		String[] split = shishi.split("日");
		publish_text.setText("同步完成" + split[1]);
		weather_info.setText(prefs.getString("one_weather_info", ""));
		wind.setText(prefs.getString("one_wind", ""));
		tmp.setText(prefs.getString("one_temp", ""));
		// 第二天
		one_weather_info.setText(prefs.getString("two_weather_info", ""));
		one_tmp.setText(prefs.getString("two_temp", ""));
		one_date.setText(prefs.getString("two_date", ""));
		// 第三天
		two_weather_info.setText(prefs.getString("three_weather_info", ""));
		two_tmp.setText(prefs.getString("three_temp", ""));
		two_date.setText(prefs.getString("three_date", ""));
		// 第四天
		three_weather_info.setText(prefs.getString("four_weather_info", ""));
		three_one_tmp.setText(prefs.getString("four_temp", ""));
		three_one_date.setText(prefs.getString("four_date", ""));

		weatherInfoLayout.setVisibility(View.VISIBLE);
		line1.setVisibility(View.VISIBLE);
		qitaday.setVisibility(View.VISIBLE);

		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
	}

}
