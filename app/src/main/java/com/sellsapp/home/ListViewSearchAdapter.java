package com.sellsapp.home;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.sellsapp.R;
import com.sellsapp.basic.MyBaseAdapter;
import com.sellsapp.models.BookInfo;

@Deprecated
public class ListViewSearchAdapter extends MyBaseAdapter <BookInfo, ListView>{


	public ListViewSearchAdapter(Context context, List<BookInfo> list) {
		super(context, list);
	}
	//打算展示10-20个热搜书籍，没必要复用
	@SuppressLint("ViewHolder") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = View.inflate(context, R.layout.search_hot_book_item, null);
		TextView tvName = (TextView) view.findViewById(R.id.tv_search_hot_book_name);
		tvName.setText(list.get(position).getBookname());
		return view;
	}

}
