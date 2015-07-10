package com.haitun.pb.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.haitun.pb.R;
import com.haitun.pb.bean.PbView;
import com.haitun.pb.utils.IP;

import java.util.List;

/**
 * Created by 笨笨 on 2015/6/8.
 */
public class ViewAdapter extends ArrayAdapter<PbView> {
    private int resresource;
    private List<PbView> list;
    public ViewAdapter(Context context, int resource, List<PbView> list) {
        super(context, resource, list);
        this.resresource=resource;
        this.list=list;
    }

    public void setList(List<PbView> list) {
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PbView pbView=list.get(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resresource,null);
            viewHolder=new ViewHolder();
            viewHolder.image= (SimpleDraweeView) view.findViewById(R.id.my_image_view);
            viewHolder.time= (TextView) view.findViewById(R.id.tv_item_time);
            viewHolder.name= (TextView) view.findViewById(R.id.tv_item_name);
            viewHolder.ok= (TextView) view.findViewById(R.id.tv_item_ok);
            viewHolder.address= (TextView) view.findViewById(R.id.tv_item_address);
            view.setTag(viewHolder);
        }else{
            view=convertView;
            viewHolder= (ViewHolder) view.getTag();
        }
        viewHolder.name.setText(pbView.getName());
        viewHolder.time.setText(pbView.getVScdate());
        Uri uri = Uri.parse(IP.ip+pbView.getVUrl());
        viewHolder.image.setImageURI(uri);
        viewHolder.address.setText(pbView.getVArea());
        viewHolder.ok.setText(pbView.getVOk());
        return view;
    }
    class ViewHolder{
        SimpleDraweeView image;
        TextView name;
        TextView time;
        TextView ok;
        TextView address;
    }

}
