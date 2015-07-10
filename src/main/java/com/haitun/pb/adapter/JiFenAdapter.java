package com.haitun.pb.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.haitun.pb.R;

import java.util.List;

/**
 * Created by 笨笨 on 2015/6/8.
 */
public class JiFenAdapter extends ArrayAdapter<String>{
    private int resresource;
    private List<String> list;
    public JiFenAdapter(Context context, int resource, List<String> list) {
        super(context, resource, list);
        this.resresource=resource;
        this.list=list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view ;
        ViewHolder viewHolder;
        if(convertView==null){
            view=LayoutInflater.from(getContext()).inflate(resresource,null);
            viewHolder=new ViewHolder();
            viewHolder.time= (TextView) view.findViewById(R.id.tv_time);

            view.setTag(viewHolder);
        }else {
            view=convertView;
            viewHolder= (ViewHolder) view.getTag();
        }
        Log.i("list",list.get(position));
        viewHolder.time.setText(list.get(position));
        return view;
    }
    class ViewHolder{
        TextView time;
    }
}
