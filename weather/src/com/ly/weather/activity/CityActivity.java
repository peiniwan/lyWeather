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

import com.ly.weather.R;
import com.ly.weather.db.CoolWeatherDB;
import com.ly.weather.model.AddCity;

/**
 * 城市管理中心
 * 
 * @author Administrator
 * 
 */
public class CityActivity extends BaseActivity implements OnClickListener {
	private Button bt_add;// 添加城市
	private ListView lv;
	private SharedPreferences prefs;
	private ArrayList<String> mArrayList = new ArrayList<String>();// 保存添加的城市
	private ArrayList<String> cities;// adapter的数据
	private CoolWeatherDB weatherDB;
	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.menu_activity);
		initView();

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		weatherDB = CoolWeatherDB.getInstance(this);
		cities = queryCities();// 从数据库查询选中的城市
		adapter = new ArrayAdapter<String>(this, R.layout.menu_city_item,
				R.id.tv_city_name, cities);
		adapter.notifyDataSetChanged();
		lv.setAdapter(adapter);

		bt_add.setOnClickListener(this);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(CityActivity.this,
						WeatherActivity.class);// 传递到WeatherActivity
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

	/**
	 * 初始化view
	 */
	private void initView() {
		bt_add = (Button) findViewById(R.id.bt_add);
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

	/**
	 * 点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_add:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			// 将城市是为选择至为false，否则一到了ChooseAreaActivity就又进了WeatherActivity
			prefs.edit().putBoolean("city_selected", false).commit();
			intent.putExtra("from_city_activity", true);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, WeatherActivity.class);
		startActivity(intent);
		finish();
	}

}
