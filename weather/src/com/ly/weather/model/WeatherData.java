package com.ly.weather.model;

import java.util.ArrayList;

public class WeatherData {
	public String date;// 日期
	public String status;// 更新状态
	public ArrayList<Weath> results;// 返回结果

	// @Override
	// public String toString() {
	// return "WeatherData [results=" + results + ",status" + status + "]";
	// }

	public class Weath {
		public String currentCity;// 当前城市
		public String pm25;// 2.5
		public ArrayList<IndexInfo> index;// 生活指南
		public ArrayList<WeatherInfo> weather_data;// 天气详情

//		@Override
//		public String toString() {
//			return "weath [currentCity=" + currentCity + ", index=" + index
//					+ ", weather_data=" + weather_data + "]";
//		}

	}

	public class IndexInfo {
		public String title;// 标题
		public String tipt;// xx指数
		public String des;// 指数描述

//		@Override
//		public String toString() {
//			return "indexInfo [title=" + title + ", des=" + des + "]";
//		}
	}

	public class WeatherInfo {
		public String date; // 日期
		public String dayPictureUrl; // 白天天气图片
		public String nightPictureUrl; // 晚上天气图片
		public String weather;// 天气描述
		public String wind;// 风力
		public String temperature;// 温度

//		@Override
//		public String toString() {
//			return "weatherInfo [date=" + date + ", weather=" + weather + "]"
//					+ ",temperature" + temperature + "]";
//		}
	}

}
