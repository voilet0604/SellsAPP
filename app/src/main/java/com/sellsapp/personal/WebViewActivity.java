package com.sellsapp.personal;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sellsapp.R;
import com.sellsapp.basic.widget.TopBarView;
import com.sellsapp.utils.AppManager;

//显示服务条款和关于我们.html的weiview
public class WebViewActivity extends Activity {

	@ViewInject(R.id.bar)
	private TopBarView bar;
	
	@ViewInject(R.id.webview)
	private WebView wv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_web_view);
		ViewUtils.inject(this);
		AppManager.getAppManager().addActivity(this);
		String type = getIntent().getStringExtra("type");
		if("1".equals(type)) {
			bar.setTitle("服务条款");
		}else {
			bar.setTitle("关于我们");
		}
		wv.loadUrl(getIntent().getStringExtra("url"));
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getAppManager().finishActivity();
	}
}
