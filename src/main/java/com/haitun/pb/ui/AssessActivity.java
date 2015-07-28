package com.haitun.pb.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.haitun.pb.R;
import com.haitun.pb.adapter.CommentAdapter;
import com.haitun.pb.bean.PbComment;
import com.haitun.pb.utils.IP;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EActivity(R.layout.activity_assess)
public class AssessActivity extends Activity implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private final static String pathComment = IP.ip + "/getCommetSer";
    private final static String pathSend = IP.ip + "/SaveCommentSer";

    @ViewById
    ImageView titleLeft;
    @ViewById
    TextView titleCenter;
    @ViewById
    TextView titleRight;
    @ViewById
    ListView viewItemListview;
    @ViewById
    SwipeRefreshLayout listviewRefresh;
    @ViewById
    EditText commentEt;
    @ViewById
    ImageView sendComment;

    @Extra("viewId")
    String viewId;

    private List<PbComment> mlist;
    private CommentAdapter adapter;

    private int hasNum = 0;
    private int okNum;
    private int userId;
    private String Aname;
    private String Bname="null";

    @AfterViews
    public void init(){

        initTitle();
        initData();
        listviewRefresh.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        listviewRefresh.setOnRefreshListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                listviewRefresh.setRefreshing(true);
                ReqComment(viewId, 0, 10);
            }
        }, 200);
    }

    private void initTitle() {
        titleLeft.setVisibility(View.VISIBLE);
        titleCenter.setText("评论");
    }

    @Click({R.id.title_left,R.id.send_comment})
    public void mclick(View v){
        switch (v.getId()){
            case R.id.title_left:
                clickFinish();
                break;
            case R.id.send_comment:
                String comment=commentEt.getText().toString();
                SendComment(comment);
                ResetComment(comment);
                break;
        }
    }

    public void clickFinish(){
        finish();
    }

    public void initData(){

        SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        userId = sp.getInt("userid", 0);
        Aname=sp.getString("username","拍呗");
        mlist = new ArrayList<>();
        adapter = new CommentAdapter(this, R.layout.item_comment, mlist);
        viewItemListview.setAdapter(adapter);
        viewItemListview.setOnScrollListener(this);
        viewItemListview.setOnItemClickListener(this);
    }

    public void ResetComment(String str) {
        commentEt.setText("");
        PbComment comment=new PbComment();
        comment.setAname(Aname);
        comment.setBname(Bname);
        if(!Bname.equals("null")){
            str=str.substring(str.indexOf("：")+1);
        }
        comment.setCContent(str);
        Date date=new Date();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd H:m");
        comment.setCDate(format.format(date));
        mlist.add(comment);
        adapter.notifyDataSetChanged();

    }

    /**
     * 获取数据
     * @param viewId
     * @param hasNum
     * @param num
     */
    public void ReqComment(String viewId, int hasNum, int num) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id", viewId);
        params.put("start", hasNum);
        params.put("num", num);
        client.post(pathComment, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    listviewRefresh.setRefreshing(false);
                    String res = new String(responseBody, "utf-8");
                    JsonData(res);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                listviewRefresh.setRefreshing(false);
                Toast.makeText(AssessActivity.this, "网络出错" + statusCode, Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * 发送评论
     *
     * @param comment
     */
     void SendComment(String comment) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("viewId", viewId);
        params.put("Bname", Bname);
        params.put("comment", comment);

        client.setConnectTimeout(5000);
        client.post(pathSend, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String res = new String(responseBody, "utf-8");
                    //JsonData(res);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
    private void JsonData(String res) {
        if (res.equals("false")) {
            Toast.makeText(AssessActivity.this, "没有评论", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i("TAG", res);
        try {
            JSONArray jsonArray = new JSONArray(res);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject js = jsonArray.getJSONObject(i);
                PbComment pbComment = new PbComment();
                pbComment.setCDate(js.getString("time"));//时间
                pbComment.setAname(js.getString("Aname"));//A名字
                // Log.i("TAG", js.getString("Bname"));

                pbComment.setBname(js.getString("Bname"));//B名字
                pbComment.setCContent(js.getString("comment"));//评论的内容
                mlist.add(pbComment);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setList(mlist);
        adapter.notifyDataSetChanged();
        hasNum = mlist.size();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("TAG", mlist.get(position).getCContent());
        Bname = mlist.get(position).getAname();
        String text = "回复 " + Bname + " ：";
        commentEt.setText(text);
        //光标的位置
        commentEt.setSelection(text.length());
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                // 判断滚动到底部
                if (viewItemListview.getLastVisiblePosition() == (viewItemListview.getCount() - 1)) {
                    ReqComment(viewId, hasNum, 10);
                }
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        mlist.clear();
        ReqComment(viewId, 0, 10);
    }
}
