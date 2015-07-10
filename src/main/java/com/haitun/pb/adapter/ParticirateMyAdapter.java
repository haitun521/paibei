package com.haitun.pb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haitun.pb.R;
import com.haitun.pb.bean.PbComment;

import java.util.List;

public class ParticirateMyAdapter  extends BaseAdapter {

	private List<PbComment> list;
	private LayoutInflater mInflater;
	
	public ParticirateMyAdapter(Context context,List<PbComment> list) {
		mInflater = LayoutInflater.from(context);
		this.list = list;
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		View view;
		if(convertView ==null){
			view = mInflater.inflate(R.layout.participate_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.comment = (TextView) view.findViewById(R.id.tv_pcomment);
			viewHolder.cdate = (TextView) view.findViewById(R.id.tv_ptime);
			viewHolder.aName=(TextView) view.findViewById(R.id.tv_pname);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.comment.setText("我评论了："+list.get(position).getCContent());
		viewHolder.cdate.setText(list.get(position).getCDate());
        viewHolder.aName.setText(list.get(position).getAname());
		return view;
	}
	
	 class ViewHolder{
		TextView cdate;
		TextView comment;
		TextView aName;
	}
}
