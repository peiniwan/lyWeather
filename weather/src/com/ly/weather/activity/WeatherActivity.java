package com.ly.weather.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.ly.weather.R;
import com.ly.weather.model.WeatherData;
import com.ly.weather.model.WeatherData.IndexInfo;
import com.ly.weather.model.WeatherData.WeatherInfo;
import com.ly.weather.service.AutoUpdateService;
import com.ly.weather.util.ActivityCollector;
import com.ly.weather.util.HttpCallbackListener;
import com.ly.weather.util.HttpUtil;
import com.ly.weather.util.ImageLoderPic;
import com.ly.weather.util.SDstore;

/**
 * 主页面，显示天气
 * 
 * @author Administrator
 * 
 */
public class WeatherActivity extends BaseActivity implements OnClickListener,
		OnRefreshListener {

	/**
	 * 上面的按钮
	 */
	private Button switchCity;// 切换城市按钮
	private Button dingwei;// 更新天气按钮
	private Button menu;// 菜单

	/**
	 * 第一天的信息
	 */
	private LinearLayout weatherInfoLayout;// layout
	private TextView publish_text;// 同步中（发布时间）
	private Button lifezhinan;// 生活指南
	private Button pm25;// pm25
	private TextView today_data;// 日期
	private TextView current_city;// 城市
	private ImageView curr_pic;// 图片
	private TextView weather_info;// 天气描述
	private TextView wind;// 风力
	private TextView tmp;// 温度
	/**
	 * 其他天
	 */
	private ImageView line1;// 线
	private GridView qitaday;// 其他天GridView
	private ImageView qita_weather_pic;// 图片
	private TextView qita_weather_info;// 天气描述
	private TextView qita_tmp;// 温度
	private TextView qita_date;// 星期天

	/**
	 * 天气信息
	 */
	private ArrayList<WeatherInfo> qitaList;// 其他天的天气信息
	private ArrayList<WeatherInfo> weatherList;// 4天的天气信息
	private WeatherData weatherData;// 4天的全部天气信息
	private WeatherInfo oneWeatherInfo;// 第一天的天气信息
	public static ArrayList<IndexInfo> index;// 4天的生活指南

	private String cityName;// 从选择的activity传递过来的城市名
	private SharedPreferences prefs;
	public static String currentCity;// 从网络获取的当前城市
	private SwipeRefreshLayout srl;// 下拉刷新控件
	private NotificationManager mNotificationManager;
	private RemoteViews mRemoteViews;
	/**
	 * 定位相关
	 */
	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = new MyLocationListener();
	private ArrayList<String> local_list;// 存放定位出来的信息

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_activity);

		initView();// 初始化控件

		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// 获取传递过来的地址信息
		cityName = getIntent().getStringExtra("cityName");

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if (!TextUtils.isEmpty(cityName)) {
			// 有市级代号时就去查询天气
			publish_text.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			line1.setVisibility(View.INVISIBLE);
			qitaday.setVisibility(View.INVISIBLE);
			queryFromServer(cityName);
		} else {// 没有就读取保存的json数据去展示界面
			currentCity = prefs.getString("current_city", "");// 获取保存的城市名,有可能一进去没网就没有保存城市，进去空指针
			if (currentCity != null) {
				String result = SDstore.read2sd(setAddress(currentCity));// 获取保存的json数据
				parseData(result);// 解析并展示界面
			}
		}

		dingwei.setOnClickListener(this);
		menu.setOnClickListener(this);
		lifezhinan.setOnClickListener(this);
		menu.setOnClickListener(this);
		switchCity.setOnClickListener(this);
		// 下拉刷新设置参数
		srl.setOnRefreshListener(this);
		srl.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_red_light,
				android.R.color.holo_orange_light);

		// 定位相关
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		setLocationOption();// 设置定位参数
		mLocationClient.start();// 开始定位
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		// 初始化layout
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		line1 = (ImageView) findViewById(R.id.line1);
		srl = (SwipeRefreshLayout) findViewById(R.id.srl);

		// 上面布局
		switchCity = (Button) findViewById(R.id.switch_city);
		dingwei = (Button) findViewById(R.id.dingwei);
		menu = (Button) findViewById(R.id.menu);
		publish_text = (TextView) findViewById(R.id.publish_text);
		lifezhinan = (Button) findViewById(R.id.lifezhinan);
		pm25 = (Button) findViewById(R.id.pm25);

		// 其他天
		qitaday = (GridView) findViewById(R.id.qitaday);
		today_data = (TextView) findViewById(R.id.today_data);
		current_city = (TextView) findViewById(R.id.current_city);
		curr_pic = (ImageView) findViewById(R.id.curr_pic);
		weather_info = (TextView) findViewById(R.id.weather_info);
		wind = (TextView) findViewById(R.id.wind);
		tmp = (TextView) findViewById(R.id.tmp);

	}

	/**
	 * 点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			startActivity(new Intent(this, CityActivity.class));
			finish();
			break;
		case R.id.dingwei:
			if (mLocationClient != null && mLocationClient.isStarted()) {
				mLocationClient.requestLocation();
				if (local_list.get(1) != null) {
					String cityName = local_list.get(1);
					publish_text.setText("定位中...");
					queryFromServer(cityName);
				} else {
					Toast.makeText(this, "请确保网络通畅", Toast.LENGTH_SHORT).show();
				}
			}
			break;
		case R.id.lifezhinan:
			startActivity(new Intent(this, LifeActivity.class));
			break;
		case R.id.menu:
			startActivity(new Intent(this, SettingActivity.class));
			finish();
		default:
			break;
		}
	}

	/**
	 * GridView其他天的适配器
	 * 
	 * @author Administrator
	 * 
	 */
	class WheatherAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return qitaList.size();
		}

		@Override
		public Object getItem(int position) {
			return qitaList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(WeatherActivity.this,
					R.layout.other_weather, null);
			WeatherInfo weatherInfo = qitaList.get(position);

			qita_weather_pic = (ImageView) view.findViewById(R.id.weather_pic);
			qita_weather_info = (TextView) view.findViewById(R.id.weather_info);
			qita_tmp = (TextView) view.findViewById(R.id.tmp);
			qita_date = (TextView) view.findViewById(R.id.date);

			qita_weather_info.setText(weatherInfo.weather);
			qita_tmp.setText(weatherInfo.temperature);
			qita_date.setText(weatherInfo.date);
			showPic(qita_weather_pic, weatherInfo.dayPictureUrl,
					weatherInfo.nightPictureUrl);
			return view;
		}

	}

	/**
	 * 设置接口
	 */
	public static String setAddress(String cityName) {
		String cityNameU8 = null;
		try {
			cityNameU8 = URLEncoder.encode(cityName, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String address = "http://api.map.baidu.com/telematics/v3/weather?location="
				+ cityNameU8
				+ "&output=json&ak=vZ8GucwXI62RHVG2lPPFC4Gs"
				+ "&mcode=51:18:C7:9F:D3:9D:6E:85:F8:13:55:B2:18:7F:2E:C7:16:63:E7:40;com.ly.weather ";
		System.out.println("address--------" + address);
		return address;

	}

	/**
	 * 根据传入的地址和类型去向服务器查询天气信息
	 */
	public void queryFromServer(final String cityName) {
		final String address = setAddress(cityName);
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(final String response) {
				SDstore.write2sd(address, response);// 保存数据

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (response != null) {
							parseData(response);// 子线程刷新ui
						}
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
	 * 解析数据
	 * 
	 * @param result
	 */
	protected void parseData(String result) {
		prefs.edit().putBoolean("city_selected", true).commit();
		Gson gson = new Gson();
		weatherData = gson.fromJson(result, WeatherData.class);

		if (weatherData.status.equals("success")) {
			index = weatherData.results.get(0).index;
			weatherList = weatherData.results.get(0).weather_data;
			oneWeatherInfo = weatherData.results.get(0).weather_data.get(0);

			qitaList = new ArrayList<WeatherInfo>();
			for (int i = 1; i < weatherList.size(); i++) {
				qitaList.add(weatherList.get(i));
			}
			showWeather();// 展示天气
			showNotification();// 展示通知栏
		}

	}

	/**
	 * 展示天气
	 */
	private void showWeather() {
		if (qitaList != null) {
			WheatherAdapter wheatherAdapter = new WheatherAdapter();
			qitaday.setAdapter(wheatherAdapter);
		}
		today_data.setText(weatherData.date);
		current_city.setText(weatherData.results.get(0).currentCity);

		prefsData();// 保存数据，再次进来直接展示

		String shishi = oneWeatherInfo.date;
		String[] split = shishi.split("日");
		System.out.println("split[1]--------" + split[1]);
		publish_text.setText("同步完成" + split[1]);

		weather_info.setText(oneWeatherInfo.weather);
		wind.setText(oneWeatherInfo.wind);
		tmp.setText(oneWeatherInfo.temperature);
		// pm2.5有可能返回来
		if (weatherData.results.get(0).pm25.equals("")) {
			pm25.setVisibility(View.INVISIBLE);
		} else {
			pm25.setText("PM2.5指数:" + weatherData.results.get(0).pm25);
		}
		// 展示天气图片
		showPic(curr_pic, oneWeatherInfo.dayPictureUrl,
				oneWeatherInfo.nightPictureUrl);

		weatherInfoLayout.setVisibility(View.VISIBLE);
		line1.setVisibility(View.VISIBLE);
		qitaday.setVisibility(View.VISIBLE);

		boolean serviceSetting = prefs.getBoolean("service", true);
		if (serviceSetting == true) {
			Intent intent = new Intent(this, AutoUpdateService.class);
			intent.putExtra("currentCity", currentCity);
			startService(intent);
			System.out.println("开启服务了！");
		}
		Intent widgetIntent = new Intent("com.ly.weather.start");
		sendBroadcast(widgetIntent);
	}

	/**
	 * 保存天气数据
	 */
	private void prefsData() {
		Editor edit = prefs.edit();
		edit.putString("current_city", weatherData.results.get(0).currentCity);
		edit.putString("today_data", weatherData.date);
		edit.putString("wind", oneWeatherInfo.wind);
		edit.putString("tmp", oneWeatherInfo.temperature);
		edit.putString("weather_info", oneWeatherInfo.weather);
		edit.putString("dayUrl", oneWeatherInfo.dayPictureUrl);
		edit.putString("nigheUrl", oneWeatherInfo.nightPictureUrl);
		edit.commit();
	}

	/**
	 * 展示图片
	 */
	private void showPic(ImageView iv, String dayUrl, String nigheUrl) {
		Date date = new Date();
		int currHours = date.getHours();

		BitmapUtils bitmapUtils = new BitmapUtils(this);
		if (currHours > 17 || currHours < 7) {
			bitmapUtils.display(iv, nigheUrl);
		} else {
			bitmapUtils.display(iv, dayUrl);
		}
	}

	/**
	 * 设置定位相关参数
	 */
	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setPriority(LocationClientOption.GpsFirst);
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.disableCache(true);// 禁止启用缓存定位
		option.setTimeOut(5000);
		mLocationClient.setLocOption(option);
	}

	/**
	 * 定位监听
	 * 
	 * @author Administrator
	 * 
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation arg0) {
			if (arg0 == null) {
				return;
			}
			local_list = new ArrayList<String>();
			local_list.add(arg0.getTime());// 时间
			local_list.add(arg0.getDistrict());// 区，县
			local_list.add(arg0.getCity());// 城市
			local_list.add(arg0.getProvince());// 省
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLocationClient.stop();// 停止定位
	}

	/**
	 * 检测网络,返回FALSE说明没网
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						NetworkInfo netWorkInfo = info[i];
						if (netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
							return true;
						} else if (netWorkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
							return true;
						}
					}
				}
			}
		}

		return false;

	}

	/**
	 * OnRefreshListener接口必须实现的方法，在这里下拉刷新
	 */
	@Override
	public void onRefresh() {
		boolean networkAvailable = checkNetworkAvailable(this);
		if (networkAvailable == true) {
			String currentCity = prefs.getString("current_city", "");// 更新的时候应该重新获取保存的城市名
			publish_text.setText("同步中...");
			queryFromServer(currentCity);

			if (weatherData != null && weatherData.status.equals("success")) {
				srl.setRefreshing(false);
			}
		} else {
			publish_text.setText("同步中...");
		}
	}

	/**
	 * 展示通知栏
	 */
	public void showNotification() {
		if (prefs.getBoolean("notifiction", true) == true) {// 第一次进来没有，返回true，展示通知栏
			String currentCity = prefs.getString("current_city", "");
			mRemoteViews = new RemoteViews(getPackageName(),
					R.layout.notification);// 填充通知栏布局

			// 显示图片
			ImageLoderPic.showPic(mRemoteViews, R.id.iv_notification,
					oneWeatherInfo.nightPictureUrl,
					oneWeatherInfo.dayPictureUrl);

			mRemoteViews.setTextViewText(R.id.notification_city_name,
					currentCity);
			mRemoteViews.setTextViewText(R.id.notification_des,
					oneWeatherInfo.weather);
			mRemoteViews.setTextViewText(R.id.notification_tmp,
					oneWeatherInfo.temperature);

			Intent resultIntent = new Intent(this, WeatherActivity.class);// 点击通知回到activity
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);// 这里获取PendingIntent是通过创建TaskStackBuilder对象
			stackBuilder.addParentStack(WeatherActivity.class);
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
					0, PendingIntent.FLAG_UPDATE_CURRENT);// 表示更新的PendingIntent
			mRemoteViews.setOnClickPendingIntent(R.id.ly_notification,
					resultPendingIntent);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(
					this);
			builder.setContent(mRemoteViews).setSmallIcon(R.drawable.noti);// 这个是和时间那一排的那个图标，必须得写
			Notification notify = builder.build();
			notify.flags = Notification.FLAG_ONGOING_EVENT;// 发起正在运行事件（活动中）
			mNotificationManager.notify(0, notify);
		} else {
			mNotificationManager.cancel(0);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		ActivityCollector.finishAll();
	}
}
