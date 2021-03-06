package com.ly.weather.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ly.weather.R;
import com.ly.weather.db.CoolWeatherDB;
import com.ly.weather.model.AddCity;
import com.ly.weather.model.City;
import com.ly.weather.model.Province;
import com.ly.weather.util.Utility;

/**
 * 选择城市activity，软件一进来此activity
 * 
 * @author Administrator
 * 
 */
public class ChooseAreaActivity extends BaseActivity {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;

	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();// 存放城市
	private SharedPreferences prefs;
	private AddCity addCity;// 添加的城市

	/**
	 * 省列表
	 */
	private List<Province> provinceList;
	/**
	 * 市列表
	 */
	private List<City> cityList;
	/**
	 * 选中的省份
	 */
	private Province selectedProvince;
	/**
	 * 当前选中的级别
	 */
	private int currentLevel;
	/**
	 * 是否从CityActivity中跳转过来。
	 */
	private boolean isFromCityActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		isFromCityActivity = getIntent().getBooleanExtra("from_city_activity",
				false);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		// 已经选择了城市且不是从CityActivity跳转过来，才会直接跳转到WeatherActivity，否则会一进来就又调到WeatherActivity
		if (prefs.getBoolean("city_selected", false) && !isFromCityActivity) {
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_activity);

		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);

		coolWeatherDB = CoolWeatherDB.getInstance(this);
		addCity = new AddCity();

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(position);// 点0-北京-查询城市
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					String cityName = cityList.get(position).getCityName();
					Intent intent = new Intent(ChooseAreaActivity.this,
							WeatherActivity.class);// 选择的城市名传递到WeatherActivity
					intent.putExtra("cityName", cityName);

					addCity.setCityName(cityName);
					coolWeatherDB.saveAddCity(addCity);// 保存到数据库
					System.out.println("ChooseAreaActivity" + cityName);
					startActivity(intent);
					finish();
				}
			}
		});
		queryProvinces(); // 默认加载省级数据
	}

	/**
	 * 从数据库查询查询全国所有的省
	 */
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();// notifyDataSetChanged前先clear
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			Utility.json(this, coolWeatherDB);// 软件一进来先解析json（存放的城市信息）
			queryProvinces();
		}
	}

	/**
	 * 从数据库查询选中省内所有的市
	 */
	private void queryCities() {
		cityList = coolWeatherDB.loadCities(selectedProvince.getId() - 1);// 点0-北京-数据库里是1，所以-1
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			Utility.json(this, coolWeatherDB);
			queryCities();// 第一次进来开始解析json数据，插入数据库。第二进来直接从数据库获取
		}
	}

	/**
	 * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出。
	 */
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			finish();
		}
	}
}
