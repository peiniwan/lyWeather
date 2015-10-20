package com.ly.weather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.ly.weather.R;
import com.ly.weather.db.CoolWeatherDB;
import com.ly.weather.model.City;
import com.ly.weather.model.Province;

/**
 * jsonArray解析城市数据
 * 
 * @author Administrator
 * 
 */
public class Utility {
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
}
