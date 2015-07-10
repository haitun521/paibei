package com.haitun.pb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.haitun.pb.R;
import com.haitun.pb.bean.PbComment;

import java.util.List;

/**
 * Created by 笨笨 on 2015/6/8.
 */
public class CommentAdapter extends ArrayAdapter<PbComment>{
    private int resresource;
    private List<PbComment> list;
    public CommentAdapter(Context context, int resource, List<PbComment> list) {
        super(context, resource, list);
        this.resresource=resource;
        this.list=list;
    }

    public void setList(List<PbComment> list) {
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PbComment pbComment=list.get(position);
        View view ;
        ViewHolder viewHolder;
        if(convertView==null){
            view=LayoutInflater.from(getContext()).inflate(resresource,null);
            viewHolder=new ViewHolder();
            viewHolder.name= (TextView) view.findViewById(R.id.comment_item_name);
            viewHolder.time= (TextView) view.findViewById(R.id.comment_item_time);
            viewHolder.comment= (TextView) view.findViewById(R.id.comment_tv);
            view.setTag(viewHolder);
        }else {
            view=convertView;
            viewHolder= (ViewHolder) view.getTag();
        }
        if(pbComment.getBname().equals("null")){
            viewHolder.name.setText(pbComment.getAname());
        }else{
            viewHolder.name.setText(pbComment.getAname()+" 回复："+pbComment.getBname());
        }
        viewHolder.time.setText(pbComment.getCDate());
        viewHolder.comment.setText(pbComment.getCContent());
        return view;
    }
    class ViewHolder{
        TextView name;
        TextView time;
        TextView comment;
    }
}
