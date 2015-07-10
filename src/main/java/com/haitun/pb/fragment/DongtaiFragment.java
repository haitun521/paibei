package com.haitun.pb.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.haitun.pb.R;
import com.haitun.pb.adapter.ViewAdapter;
import com.haitun.pb.bean.PbView;
import com.haitun.pb.ui.ViewItemActivity;
import com.haitun.pb.utils.IP;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class DongtaiFragment extends Fragment implements  SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener, AdapterView.OnItemClickListener {


    private View view;
    private static final String TAG = DongtaiFragment.class.getSimpleName();
    private static final String URL = IP.ip + "/viewListSer";
    private SwipeRefreshLayout mRefreshLayout;

    private List<PbView> mDatas=null;
    private ListView mDataLv;
    private ViewAdapter mAdapter;
    private int hasNum = 0; //已经加载的数量
    private String city;
    SharedPreferences sp =null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_dongtai, container, false);
        sp=getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        city=sp.getString("city", "焦作市");
        Log.i("TAG",city);
        intiView();

        return view;
    }

    private void intiView() {

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.rl_listview_refresh);
        mDataLv = (ListView) view.findViewById(R.id.lv_listview_data);
        mRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mRefreshLayout.setOnRefreshListener(this);
        mDataLv.setOnScrollListener(this);
        mDataLv.setOnItemClickListener(this);
        mDatas=new ArrayList<>();
        mAdapter=new ViewAdapter(getActivity(),R.layout.item_normal,mDatas);
        mDataLv.setAdapter(mAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                reqList(0,3,city);
                //mAdapter=new ViewAdapter(getActivity(),R.layout.activity_view_item,mDatas);
            }
        },200);

    }
    /**
     * 刷新
     */
    @Override
    public void onRefresh() {
        hasNum = 0;
        reqList(0, 3, city);
    }
    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                // 判断滚动到底部
                if (mDataLv.getLastVisiblePosition() == (mDataLv.getCount() - 1)) {
                    mRefreshLayout.setRefreshing(true);
                    reqList(hasNum, 3, city);
                }
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        Intent intent = new Intent(this.getActivity(), ViewItemActivity.class);
        intent.putExtra("PbView", mDatas.get(position));
        startActivity(intent);
    }



    private void reqList(int start, int num, String address) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("start", start);
        params.put("num", num);
        params.put("address", address);
        client.setConnectTimeout(5000);
        client.post(URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                mRefreshLayout.setRefreshing(false);
                String res = null;
                try {
                    res = new String(responseBody,"utf-8");
                    Log.i("TAG", res);
                    JsonData(res);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mRefreshLayout.setRefreshing(false);
                Toast.makeText(DongtaiFragment.this.getActivity(), "网络出错", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void JsonData(String res) {
        if(res.equals("false")){
            Toast.makeText(getActivity(),"没有更多了",Toast.LENGTH_SHORT).show();
            return ;
        }
        if(hasNum==0) mDatas.clear();
        try {
            JSONArray json = new JSONArray(res);
            for (int i = 0; i < json.length(); i++) {
                JSONObject js = json.getJSONObject(i);
                PbView p = new PbView();
                p.setVId(js.getString("id"));//viewId
                p.setName(js.getString("name"));//用户名
                p.setVWord(js.getString("word"));//图像的描述
                p.setVScdate(js.getString("time"));//时间
                p.setVUrl(js.getString("imageUrl"));
                p.setVArea(js.getString("address"));
                p.setVOk(js.getString("ok"));//点赞的个数
                mDatas.add(p);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        mAdapter.setList(mDatas);
        mAdapter.notifyDataSetChanged();
        hasNum=mDatas.size();

    }

}
