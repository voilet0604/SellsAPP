package com.sellsapp.home;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sellsapp.R;
import com.sellsapp.Public.BitmapManager;
import com.sellsapp.basic.MyBaseAdapter;
import com.sellsapp.models.BookInfo;

//bookinfo的adapter
public class ListResultBookInfoAdapter extends MyBaseAdapter<BookInfo, ListView> {

	public ListResultBookInfoAdapter(Context context, List<BookInfo> list) {
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view ;
		ViewHolder viewHolder;
		if(null == convertView) {
			view  = View.inflate(context, R.layout.resutl_bookinfo_item, null);
			viewHolder = new ViewHolder();
			viewHolder.iv = (ImageView) view.findViewById(R.id.iv_result_img);
			viewHolder.tvName = (TextView) view.findViewById(R.id.tv_result_bookname);
			viewHolder.tvPrice = (TextView) view.findViewById(R.id.tv_result_pric);
			viewHolder.tvPublish = (TextView) view.findViewById(R.id.tv_result_pub);
			view.setTag(viewHolder);
		}else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.tvName.setText(list.get(position).getBookname());
		viewHolder.tvPrice.setText(list.get(position).getPrice()+"元");
		viewHolder.tvPublish.setText(list.get(position).getBeizhu());
		BitmapManager.getBitmapUtils(context).display(viewHolder.iv, "http://192.168.1.120:8080/sells/"+list.get(position).getImg());
		return view;
	}
	
	static class ViewHolder{
		ImageView iv;
		TextView tvName;
		TextView tvPrice;
		TextView tvPublish;
	}

}
