package com.sellsapp.Public;

import android.content.Context;

import com.lidroid.xutils.BitmapUtils;

//防止Bitmap对象多次创建消耗过多的内存
public class BitmapManager {

	private BitmapManager() {
	}
	private static BitmapUtils bitmapUtils;
	
	public static synchronized BitmapUtils getBitmapUtils(Context context) {
		if(null == bitmapUtils) {
			bitmapUtils = new BitmapUtils(context);
		}
		return bitmapUtils;
	}
}
