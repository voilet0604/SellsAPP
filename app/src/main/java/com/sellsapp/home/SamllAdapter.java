package com.sellsapp.home;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.sellsapp.R;
import com.sellsapp.Public.BaseConfig;
import com.sellsapp.Public.BitmapManager;
import com.sellsapp.basic.MyBaseAdapter;
import com.sellsapp.models.BookInfo;

/**
 * 首页书籍列表的adapter
 *
 */
public class SamllAdapter extends MyBaseAdapter <BookInfo, ListView>{


	public SamllAdapter(Context context, List<BookInfo> list) {
		super(context, list);
	}
	@SuppressLint("ViewHolder") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view ;
		ViewHolder viewHolder;
		if(null == convertView) {
			view  = View.inflate(context, R.layout.small_item, null);
			viewHolder = new ViewHolder();
			viewHolder.iv = (ImageView) view.findViewById(R.id.iv_result_img);
			viewHolder.tvName = (TextView) view.findViewById(R.id.tv_result_bookname);
			viewHolder.tvPrice = (TextView) view.findViewById(R.id.tv_result_pric);
			view.setTag(viewHolder);
		}else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.tvName.setText(list.get(position).getBookname());
		viewHolder.tvPrice.setText(list.get(position).getPrice()+"元");
		String imgUrl = list.get(position).getImg();
		if(TextUtils.isEmpty(imgUrl)) {
			viewHolder.iv.setImageResource(R.drawable.no_photo);
			return view;
		}
		if(!imgUrl.contains("http")) {
			imgUrl = BaseConfig.BASE_SERVICE_URL+imgUrl;
		}
		BitmapManager.getBitmapUtils(context).display(viewHolder.iv, imgUrl, new BitmapLoadCallBack<ImageView>() {

			@Override
			public void onLoadCompleted(ImageView arg0, String arg1,
					Bitmap arg2, BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
				arg0.setImageBitmap(arg2);
			}

			@Override
			public void onLoadFailed(ImageView arg0, String arg1, Drawable arg2) {
				arg0.setImageResource(R.drawable.no_photo);
			}
		});
		return view;
	}
	
	static class ViewHolder{
		ImageView iv;
		TextView tvName;
		TextView tvPrice;
	}

}
