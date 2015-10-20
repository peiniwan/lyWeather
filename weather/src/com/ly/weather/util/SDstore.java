package com.ly.weather.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import android.os.Environment;
/**
 * 缓存到sd卡
 * @author Administrator
 *
 */
public class SDstore {
	public static final String CACHE_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/ly_weather";

	/**
	 * 向SD卡写文件
	 */
	public static void write2sd(String url, String result) {
		try {
			String fileName = MD5Encoder.encode(url);
			File file = new File(CACHE_PATH, fileName);
			File parentFile = file.getParentFile();
			if (!parentFile.exists()) {// 如果文件夹不存在, 创建文件夹
				parentFile.mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(result.getBytes());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 向SD卡读文件
	 */
	public static String read2sd(String url) {
		try {
			String fileName = MD5Encoder.encode(url);
			File file = new File(CACHE_PATH, fileName);
			if (file.exists()) {
				FileInputStream fis = new FileInputStream(file);
				BufferedReader br = new BufferedReader(new InputStreamReader(
						fis));
				String result = br.readLine();
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;

	}
}
