package com.sellsapp.basic.widget;


import android.app.ProgressDialog;
import android.content.Context;

import com.sellsapp.R;



/**
 * 自定义的ProgressDialog
 * @author violet
 *
 */
public class RefreshDialog extends ProgressDialog {
	
	public RefreshDialog(Context context) {
		super(context);
		this.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		this.setMessage(context.getResources().getString(R.string.app_refresh_loading));
	}
	
	public RefreshDialog(Context context,String msg) {
		super(context);
		this.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		this.setMessage(msg);
	}

}