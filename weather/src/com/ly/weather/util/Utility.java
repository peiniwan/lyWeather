package com.ly.weather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ly.weather.R;
import com.ly.weather.db.CoolWeatherDB;
import com.ly.weather.model.City;
import com.ly.weather.model.Index;
import com.ly.weather.model.Province;
import com.ly.weather.model.WeatherInfo;

public class Utility {
	private static ArrayList<WeatherInfo> weatherList;
	private static WeatherInfo weatherInfo;
	private static ArrayList<Index> indexList;

	public static void json(Context context, CoolWeatherDB coolWeatherDB) {
		try {
			// 读取 json文件
			InputStream is = context.getResources().openRawResource(
					R.raw.cityinfo);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			String json = response.toString();
			// 将字符串json转换为json对象，以便于取出数据
			JSONObject jsonObject = new JSONObject(json);
			// 解析info数组，解析中括号括起来的内容就表示一个数组，使用JSONArray对象解析
			JSONArray provinceArray = jsonObject.getJSONArray("城市代码");
			// 遍历JSONArray数组
			for (int i = 0; i < provinceArray.length(); i++) {
				// 取出省对象
				JSONObject provinceObj = provinceArray.getJSONObject(i);
				// 获得省的名字
				String provinceName = provinceObj.getString("省");
				// 中括号括起来的内容就表示一个JSONArray，所以这里要再创建一个JSONArray对象
				JSONArray cityArray = provinceObj.getJSONArray("市");
				for (int j = 0; j < cityArray.length(); j++) {
					JSONObject cityObj = cityArray.getJSONObject(j);
					String cityName = cityObj.getString("市名");
					String cityCode = cityObj.getString("编码");
					City city = new City();

					city.setCityName(cityName);
					city.setCityCode(cityCode);
					city.setProvinceId(i);
					coolWeatherDB.saveCity(city);

				}
				Province province = new Province();

				province.setProvinceName(provinceName);
				// 将解析出来的数据存储到Province表
				coolWeatherDB.saveProvince(province);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 解析服务器返回的JSON数据，并将解析出的数据存储到本地。
	 */
	public static void handleWeatherResponse(Context context, String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			String dateAll = jsonObject.getString("date");
			JSONArray results = jsonObject.getJSONArray("results");
			JSONObject obj = results.getJSONObject(0);
			String currentCity = obj.getString("currentCity");
			String pm25 = obj.getString("pm25");

			JSONArray weather_data = obj.getJSONArray("weather_data");
			weatherList = new ArrayList<WeatherInfo>();
			for (int i = 0; i < weather_data.length(); i++) {
				JSONObject weather_dataObj = weather_data.getJSONObject(i);
				String date = weather_dataObj.getString("date");
				String weather = weather_dataObj.getString("weather");
				String dayPictureUrl = weather_dataObj
						.getString("dayPictureUrl");
				String nightPictureUrl = weather_dataObj
						.getString("nightPictureUrl");
				String temperature = weather_dataObj.getString("temperature");
				String wind = weather_dataObj.getString("wind");
				weatherInfo = new WeatherInfo();
				weatherInfo.setDate(date);
				weatherInfo.setDayPictureUrl(dayPictureUrl);
				weatherInfo.setNightPictureUrl(nightPictureUrl);
				weatherInfo.setTemperature(temperature);
				weatherInfo.setWeather(weather);
				weatherInfo.setWind(wind);
				// Log.d("util", weatherInfo.toString());
				weatherList.add(weatherInfo);
			}

			JSONArray indexArray = obj.getJSONArray("index");
			indexList = new ArrayList<Index>();
			for (int i = 0; i < indexArray.length(); i++) {
				JSONObject indexObj = indexArray.getJSONObject(i);
				String title = indexObj.getString("title");
				String tipt = indexObj.getString("tipt");
				String des = indexObj.getString("des");

				Index index = new Index();
				index.setDes(des);
				index.setTipt(tipt);
				index.setTitle(title);
				indexList.add(index);
			}

			for (Index Index : indexList) {
				String des = Index.getDes();
				String tipt = Index.getTipt();
				String title = Index.getTitle();
				Log.d("util", des + tipt + title);
			}

			saveWeatherInfo(context, dateAll, currentCity, pm25);
			// Log.d("util", dateAll + currentCity + pm25);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 将服务器返回的所有天气信息存储到SharedPreferences文件中。
	 */
	public static void saveWeatherInfo(Context context, String date,
			String currentCity, String pm25) {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("date_all", date);
		editor.putString("city_name", currentCity);
		editor.putString("pm25", pm25);
		// 第一天
		editor.putString("one_weather_info", weatherList.get(0).getWeather());
		editor.putString("one_wind", weatherList.get(0).getWind());
		editor.putString("one_temp", weatherList.get(0).getTemperature());
		editor.putString("one_date", weatherList.get(0).getDate());
		editor.putString("one_day", weatherList.get(0).getDayPictureUrl());
		editor.putString("one_night", weatherList.get(0).getNightPictureUrl());
		// 第二天
		editor.putString("two_weather_info", weatherList.get(1).getWeather());
		editor.putString("two_temp", weatherList.get(1).getTemperature());
		editor.putString("two_date", weatherList.get(1).getDate());
		editor.putString("two_day", weatherList.get(1).getDayPictureUrl());
		editor.putString("two_night", weatherList.get(1).getNightPictureUrl());
		// 第三天
		editor.putString("three_weather_info", weatherList.get(2).getWeather());
		editor.putString("three_temp", weatherList.get(2).getTemperature());
		editor.putString("three_date", weatherList.get(2).getDate());
		editor.putString("three_day", weatherList.get(2).getDayPictureUrl());
		editor.putString("three_night", weatherList.get(2).getNightPictureUrl());
		// 第四天
		editor.putString("four_weather_info", weatherList.get(3).getWeather());
		editor.putString("four_temp", weatherList.get(3).getTemperature());
		editor.putString("four_date", weatherList.get(3).getDate());
		editor.putString("four_day", weatherList.get(3).getDayPictureUrl());
		editor.putString("four_night", weatherList.get(3).getNightPictureUrl());
		// 穿衣
		editor.putString("chuangyi_des", indexList.get(0).getDes());
		editor.putString("chuangyi_tipt", indexList.get(0).getTipt());
		editor.putString("chuangyi_title", indexList.get(0).getTitle());
		// 洗车
		editor.putString("xi_des", indexList.get(1).getDes());
		editor.putString("xi_tipt", indexList.get(1).getTipt());
		editor.putString("xi_title", indexList.get(1).getTitle());
		//旅游
		editor.putString("lv_des", indexList.get(2).getDes());
		editor.putString("lv_tipt", indexList.get(2).getTipt());
		editor.putString("lv_title", indexList.get(2).getTitle());
		// 感冒
		editor.putString("gan_des", indexList.get(3).getDes());
		editor.putString("gan_tipt", indexList.get(3).getTipt());
		editor.putString("gan_title", indexList.get(3).getTitle());
		// 运动
		editor.putString("sport_des", indexList.get(4).getDes());
		editor.putString("sport_tipt", indexList.get(4).getTipt());
		editor.putString("sport_title", indexList.get(4).getTitle());
		// 紫外线
		editor.putString("zi_des", indexList.get(5).getDes());
		editor.putString("zi_tipt", indexList.get(5).getTipt());
		editor.putString("zi_title", indexList.get(5).getTitle());
		editor.commit();
	}

}
