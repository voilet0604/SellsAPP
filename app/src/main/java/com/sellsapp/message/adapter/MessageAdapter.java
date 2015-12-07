package com.sellsapp.message.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sellsapp.R;
import com.sellsapp.basic.MyBaseAdapter;
import com.sellsapp.basic.widget.XListView;
import com.sellsapp.models.Message;

/**
 * 消息中心的adapter
 */
public class MessageAdapter extends MyBaseAdapter<Message, XListView> {

	public MessageAdapter(Context context, List<Message> list) {
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder holder;
		if(convertView == null) {
			view = View.inflate(context, R.layout.message_item, null);
			holder = new ViewHolder();
			holder.tvContent = (TextView) view.findViewById(R.id.tv_content);
			holder.tvTime = (TextView) view.findViewById(R.id.tv_time);
			view.setTag(holder);
		}else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		//拼接序号
		holder.tvContent.setText(" "+(position+1)+". "+list.get(position).getContent());
		holder.tvTime.setText(list.get(position).getTime());
		return view;
	}

	static class ViewHolder {
		TextView tvContent;
		
		TextView tvTime;
	}
}
