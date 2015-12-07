package com.sellsapp.basic.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sellsapp.R;
import com.sellsapp.basic.MainActivity;
import com.sellsapp.personal.LoginActivity;
import com.sellsapp.utils.AppManager;

/**
 * @Description:每个应用都需要用到的顶部导航条自定义，包含有返回，标题等五个控件
 * @author http://blog.csdn.net/finddreams
 */ 
public class TopBarView extends LinearLayout{

	public TextView mTvBack;
	public TextView mTvTitle;
	public ImageView mIvRight;
	public TextView mTvRight;
	private Activity mActivity;
	private Context context;
	private int backType=0;
	
	private Callback callback;
	
	public void setCallBack(Callback callback) {
		this.callback = callback;
	}
	public TopBarView(Context context) {
		super(context);
		this.context=context;
	}
	public TopBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		LayoutInflater.from(context).inflate(R.layout.top_bar_view, this, true);
		mTvBack=(TextView)this.findViewById(R.id.top_back_tv);
		mTvTitle=(TextView)this.findViewById(R.id.top_title);
		mTvRight=(TextView)this.findViewById(R.id.top_right_tv);
		mIvRight=(ImageView)this.findViewById(R.id.top_right_btn);
		mTvRight.setOnClickListener(onClickListener);
		mIvRight.setOnClickListener(onClickListener);
		mTvBack.setOnClickListener(onClickListener);
	}
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.top_right_tv:
				callback.onClick(v);
				break;
			case R.id.top_right_btn:
				break;
			case R.id.top_back_tv:
					AppManager.getAppManager().finishActivity();
				break;
			}
		}
	};

	public void setActivity(Activity activity) {
		this.mActivity=activity;
	}
	public void setTitle(String title) {
		mTvTitle.setText(title);
	}
	public void setTitle(String title,int size) {
		mTvTitle.setText(title);
		mTvTitle.setTextSize(14);
	}
	public void setTitle(String title,int size,boolean lines) {
		mTvTitle.setText(title);
		mTvTitle.setLines(2);
		mTvTitle.setTextSize(14);
	}
	public void setRightText(String text) {
		mTvRight.setText(text);
	}
	/**
	 * 返回的类型，1代表直接返回到MianActivity
	 * @param type
	 */
	public void setBackType(int type){
		this.backType=type;
	}
	
	public interface Callback{
		void onClick(View view);
	}
}
