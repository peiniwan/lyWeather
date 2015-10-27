package com.ly.weather.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ly.weather.R;

/**
 * 生活指南里的自定义控件和属性
 * 
 * @author Administrator
 * 
 */
public class IndexView extends RelativeLayout {
	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.ly.weather";
	// 自定义属性
	private String mTitle;
	private String mZs;
	private String mDes;
	// 控件对象
	private TextView tv_des;
	private TextView tv_title;
	private TextView tv_zs;

	// 自定义控件都要实现这三个方法，在这三个方法中都去调用initView()初始化
	public IndexView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public IndexView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 根据属性名称,获取属性的值，来给控件赋值。NAMESPACE常量写上边
		mTitle = attrs.getAttributeValue(NAMESPACE, "title");
		mZs = attrs.getAttributeValue(NAMESPACE, "zs");
		mDes = attrs.getAttributeValue(NAMESPACE, "des");
		initView();

	}

	public IndexView(Context context) {
		super(context);
		initView();
	}

	private void initView() {
		// 将自定义好的布局文件设置给当前的IndexView
		View.inflate(getContext(), R.layout.life, this);
		tv_des = (TextView) findViewById(R.id.tv_des);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_zs = (TextView) findViewById(R.id.tv_zs);
		setTitle(mTitle, mDes, mZs);
	}

	public void setTitle(String title, String des, String zs) {
		tv_des.setText(des);
		tv_title.setText(title);
		tv_zs.setText(zs);
	}

}
