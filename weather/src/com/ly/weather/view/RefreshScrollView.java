package com.ly.weather.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ly.weather.R;

/**
 * 自定义ScrollView
 * 
 * @author Administrator
 * 
 */
public class RefreshScrollView extends ScrollView implements
		OnItemClickListener {

	private static final int STATE_PULL_REFRESH = 0;// 下拉刷新
	private static final int STATE_RELEASE_REFRESH = 1;// 松开刷新
	private static final int STATE_REFRESHING = 2;// 正在刷新
	private View mHeaderView;
	private int startY = -1;// 滑动起点的y坐标
	private int mHeaderViewHeight;
	private int mCurrrentState = STATE_PULL_REFRESH;// 当前状态
	private TextView tvTitle;
	private TextView tvTime;
	private ImageView ivArrow;
	private ProgressBar pbProgress;
	private RotateAnimation animUp;
	private RotateAnimation animDown;

	public RefreshScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initHeaderView();
	}

	public RefreshScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initHeaderView();
	}

	public RefreshScrollView(Context context) {
		super(context);
		initHeaderView();
	}

	private void initHeaderView() {
		mHeaderView = View.inflate(getContext(), R.layout.refresh_header, null);
		this.addView(mHeaderView, 0);
		tvTitle = (TextView) mHeaderView.findViewById(R.id.tv_title);
		tvTime = (TextView) mHeaderView.findViewById(R.id.tv_time);
		ivArrow = (ImageView) mHeaderView.findViewById(R.id.iv_arr);
		pbProgress = (ProgressBar) mHeaderView.findViewById(R.id.pb_progress);
		mHeaderView.measure(0, 0);
		// 测量之后该view的getMeasuredHeight()就会返回刚才测量所得的高，getMeasuredWidth返回测量所得宽
		mHeaderViewHeight = mHeaderView.getMeasuredHeight();
		mHeaderView.setPadding(0, 10, 0, 0);// 隐藏头布局
		// initArrowAnim();
		// tvTime.setText("最后刷新时间:" + getCurrentT''.ime());
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

	}

}
