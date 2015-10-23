package com.ly.weather.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ly.weather.model.AddCity;
import com.ly.weather.model.City;
import com.ly.weather.model.Province;

public class CoolWeatherDB {
	/**
	 * 数据库名
	 */
	public static final String DB_NAME = "cool_weather";
	/**
	 * 数据库版本
	 */
	public static final int VERSION = 1;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;
	private String cityName;

	/**
	 * 将构造方法私有化
	 */
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}

	/**
	 * 获取CoolWeatherDB的实例。
	 */
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}

	/**
	 * 选中的城市添加到数据库
	 */
	public void saveAddCity(AddCity addCity) {
		if (addCity != null) {
			ContentValues values = new ContentValues();
			Cursor cursor = db.query("AddCity", null, null, null, null, null,
					null);
			while (cursor.moveToNext()) {
				cityName = cursor.getString(cursor.getColumnIndex("city_name"));
				if (addCity.getCityName() != cityName) {
					System.out.println("addCity.getCityName()"
							+ addCity.getCityName() + "-----------" + cityName);
					values.put("city_name", addCity.getCityName());
				}
			}
			System.out.println("coll --cityName" + cityName);

			db.insert("AddCity", null, values);
		}
	}

	/**
	 * 读取保存的城市
	 */
	public List<AddCity> loadAddCity() {
		List<AddCity> list = new ArrayList<AddCity>();
		Cursor cursor = db.query("AddCity", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				AddCity addCity = new AddCity();
				addCity.setCityName(cursor.getString(cursor
						.getColumnIndex("city_name")));
				list.add(addCity);

			} while (cursor.moveToNext());
		}
		return list;
	}

	/**
	 * 删除addcity表中的数据
	 */
	public void removeCity(String name) {
		int i = db.delete("AddCity", " city_name=?", new String[] { name });
		System.out.println("i--" + i);
	}

	/**
	 * 将Province实例存储到数据库。
	 */
	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			db.insert("Province", null, values);
		}
	}

	/**
	 * 从数据库读取全国所有的省份信息。
	 */
	public List<Province> loadProvinces() {
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor
						.getColumnIndex("province_name")));
				list.add(province);
			} while (cursor.moveToNext());
		}
		return list;
	}

	/**
	 * 将City实例存储到数据库。
	 */
	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}

	/**
	 * 从数据库读取某省下所有的城市信息。
	 */
	public List<City> loadCities(int provinceId) {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id = ?",
				new String[] { String.valueOf(provinceId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor
						.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor
						.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			} while (cursor.moveToNext());
		}
		return list;
	}

}
