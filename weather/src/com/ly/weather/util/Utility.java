package com.ly.weather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ly.weather.R;
import com.ly.weather.db.CoolWeatherDB;
import com.ly.weather.model.City;
import com.ly.weather.model.Province;
import com.ly.weather.model.WeatherInfo;

public class Utility {
	private static ArrayList<WeatherInfo> arrayList;

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
					if (city.getCityName() == null) {
						city.setCityName(cityName);
						city.setCityCode(cityCode);
						city.setProvinceId(i);
						coolWeatherDB.saveCity(city);
					}
				}
				Province province = new Province();
				if (province.getProvinceName() == null) {
					province.setProvinceName(provinceName);
					// 将解析出来的数据存储到Province表
					coolWeatherDB.saveProvince(province);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 解析服务器返回的JSON数据，并将解析出的数据存储到本地。
	 */
	public static List<WeatherInfo> handleWeatherResponse(Context context,
			String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			String status = jsonObject.getString("status");
			String date = jsonObject.getString("date");
			JSONArray results = jsonObject.getJSONArray("results");
			JSONObject obj = results.getJSONObject(0);
			String currentCity = obj.getString("currentCity");
			String pm25 = obj.getString("pm25 ");
			JSONArray weather_data = obj.getJSONArray("weather_data");
			for (int i = 0; i < weather_data.length(); i++) {
				JSONObject weather_dataObj = weather_data.getJSONObject(0);
				String week = weather_dataObj.getString("date");
				String weather = weather_dataObj.getString("weather");
				String temperature = weather_dataObj.getString("temperature");
				String wind = weather_dataObj.getString("wind");
				Log.d("util", status + date + currentCity + pm25 + date
						+ weather + temperature);
				WeatherInfo weatherInfo = new WeatherInfo();
				weatherInfo.setWeek(week);
				weatherInfo.setTemperature(temperature);
				weatherInfo.setWeather(weather);
				weatherInfo.setWind(wind);
				arrayList = new ArrayList<WeatherInfo>();
				arrayList.add(weatherInfo);
			}
			saveWeatherInfo(context, status, date, currentCity, pm25);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return arrayList;

	}

	/**
	 * 将服务器返回的所有天气信息存储到SharedPreferences文件中。
	 */
	public static void saveWeatherInfo(Context context, String status,
			String date, String currentCity, String pm25) {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", false);
		editor.putString("date", date);
		editor.putString("city_name", currentCity);
		editor.putString("pm25", pm25);
		editor.commit();
	}
}
