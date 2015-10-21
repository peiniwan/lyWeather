package com.ly.weather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ly.weather.R;

public class MenuActivity extends Activity implements OnClickListener {
	private Button bt_add;
	private Button bt_setting;
	private TextView tv_local;
	private ListView lv;
	private StringBuilder builder;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.menu_activity);
		initView();
		builder = new StringBuilder();
		String[] city = builder.toString().split(",");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.menu_city_item, R.id.tv_city_name, city);
		adapter.notifyDataSetChanged();
		lv.setAdapter(adapter);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		bt_add.setOnClickListener(this);
		bt_setting.setOnClickListener(this);
		tv_local.setOnClickListener(this);
	}

	private void initView() {
		bt_add = (Button) findViewById(R.id.bt_add);
		bt_setting = (Button) findViewById(R.id.bt_setting);
		tv_local = (TextView) findViewById(R.id.tv_local);
		lv = (ListView) findViewById(R.id.lv);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_add:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			prefs.edit().putBoolean("city_selected", false).commit();
			startActivityForResult(intent, 0);
			finish();
			break;
		case R.id.tv_local:

			break;
		case R.id.bt_setting:

			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 0:
			if (resultCode == RESULT_OK) {
				String cityName = data.getStringExtra("cityName");
				System.out.println("cityName" + cityName);
				builder.append(cityName);
			}
			break;

		default:
			break;
		}

	}

}
