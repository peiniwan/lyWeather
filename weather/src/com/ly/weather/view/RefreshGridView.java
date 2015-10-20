package com.ly.weather.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 自定义GridView
 * 
 * @author Administrator
 * 
 */
public class RefreshGridView extends GridView {

	public RefreshGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initHeaderView();
	}

	public RefreshGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initHeaderView();
	}

	public RefreshGridView(Context context) {
		super(context);
		initHeaderView();
	}

	private void initHeaderView() {

	}

}
