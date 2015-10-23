package com.ly.weather.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.ly.weather.R;
import com.ly.weather.db.CoolWeatherDB;
import com.ly.weather.model.AddCity;

public class MenuActivity extends BaseActivity implements OnClickListener {
	private Button bt_add;
	private Button bt_setting;
	private TextView tv_local;
	private ListView lv;
	private SharedPreferences prefs;
	private ArrayList<String> mArrayList = new ArrayList<String>();
	private ArrayList<String> cities;
	private CoolWeatherDB weatherDB;
	private ArrayAdapter<String> adapter;

	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = new MyLocationListener();
	private ArrayList<String> local_list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.menu_activity);
		initView();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		weatherDB = CoolWeatherDB.getInstance(this);
		cities = queryCities();
		adapter = new ArrayAdapter<String>(this, R.layout.menu_city_item,
				R.id.tv_city_name, cities);
		adapter.notifyDataSetChanged();
		lv.setAdapter(adapter);
		bt_add.setOnClickListener(this);
		tv_local.setOnClickListener(this);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(MenuActivity.this,
						WeatherActivity.class);
				intent.putExtra("cityName", cities.get(position));
				startActivity(intent);
			}
		});
		// 长按监听
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				String name = cities.get(position);
				showAlertDialog(name);
				return true;// 返回true点击事件就不会触发了
			}
		});
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		setLocationOption();
		mLocationClient.start();// 开始定位
	}

	/**
	 * 展示弹窗
	 * 
	 * @param name
	 */
	protected void showAlertDialog(final String name) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_layout, null);
		dialog.setView(view, 0, 0, 0, 0);// 设置边距为0
		Button btnOK = (Button) view.findViewById(R.id.btn_ok);
		Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				weatherDB.removeCity(name);
				mArrayList.clear();// adapter清除，如果不写这个，刷新时还是原来的list
				queryCities();// 重新赋值
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private void initView() {
		bt_add = (Button) findViewById(R.id.bt_add);
		// bt_setting = (Button) findViewById(R.id.bt_setting);
		tv_local = (TextView) findViewById(R.id.tv_local);
		lv = (ListView) findViewById(R.id.lv);
	}

	/**
	 * 从数据库查询选中的城市
	 */
	private ArrayList<String> queryCities() {

		List<AddCity> loadAddCity = weatherDB.loadAddCity();
		if (loadAddCity.size() > 0) {
			for (AddCity city : loadAddCity) {
				mArrayList.add(city.getCityName());
			}
		}
		return mArrayList;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_add:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			prefs.edit().putBoolean("city_selected", false).commit();
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			break;
		case R.id.tv_local:

			if (mLocationClient != null && mLocationClient.isStarted())
				mLocationClient.requestLocation();
			Intent intent_local = new Intent(this, WeatherActivity.class);
			String time = local_list.get(0);
			String cityName = local_list.get(1);

			System.out.println(time + cityName);
			intent_local.putExtra("cityName", cityName);
			intent_local.putExtra("time", time);
			startActivity(intent_local);
			break;

		default:
			break;
		}
	}

	/**
	 * 设置相关参数
	 */
	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.disableCache(true);// 禁止启用缓存定位
		mLocationClient.setLocOption(option);
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation arg0) {
			if (arg0 == null)
				return;
			local_list = new ArrayList<String>();
			local_list.add(arg0.getTime());// 时间
			local_list.add(arg0.getDistrict());// 区，县
			local_list.add(arg0.getCity());// 城市
			local_list.add(arg0.getProvince());// 省
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		tv_local.setText("当前位置(" + local_list.get(3) + local_list.get(2)
				+ local_list.get(1) + ")");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLocationClient.stop();// 停止定位
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, WeatherActivity.class);
		startActivity(intent);
		finish();
	}

}
