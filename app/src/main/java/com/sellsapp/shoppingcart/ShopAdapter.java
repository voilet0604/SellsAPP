package com.sellsapp.shoppingcart;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sellsapp.R;
import com.sellsapp.basic.MyBaseAdapter;
import com.sellsapp.basic.widget.XListView;
import com.sellsapp.models.ShoppingInfo;

/**
 * 购物车的adapter
 */
public class ShopAdapter extends MyBaseAdapter<ShoppingInfo, XListView>{

	public ShopAdapter(Context context, List<ShoppingInfo> list) {
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder holder;
		if(null == convertView) {
			view = View.inflate(context, R.layout.shoping_cat_item, null);
			holder = new ViewHolder();
			holder.tvP = (TextView) view.findViewById(R.id.tv_position);
			holder.tvName = (TextView) view.findViewById(R.id.tv_name);
			holder.tvPice = (TextView) view.findViewById(R.id.tv_pice);
			view.setTag(holder);
		}else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		//序号
		holder.tvP.setText(String.valueOf(position+1));
		holder.tvName.setText(list.get(position).getBookname());
		holder.tvPice.setText(list.get(position).getPrice());
		return view;
	}

	static class ViewHolder{
		
		TextView tvP;
		TextView tvName;
		TextView tvPice;
	}
}
