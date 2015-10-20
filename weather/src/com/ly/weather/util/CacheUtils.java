package com.ly.weather.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 缓存到内存 (没有用到。因为保存起来就和原来不一样了，但是保存到SD卡一样)
 * 
 * @author Administrator
 * 
 */
public class CacheUtils {
	public static final String PREF_NAME = "config";

	/**
	 * 缓存原理：设置缓存 key 是url, value是json（解析出来的）
	 */
	public static void setCache(String key, String value, Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		sp.edit().putString(key, value).commit();
	}

	/**
	 * 获取缓存 key 是url
	 */
	public static String getCache(String key, Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		return sp.getString(key, "");

	}
}
