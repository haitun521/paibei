package com.haitun.pb.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.haitun.pb.R;
import com.haitun.pb.adapter.ViewAdapter;
import com.haitun.pb.bean.PbView;
import com.haitun.pb.fragment.NavigationFragment;
import com.haitun.pb.utils.IP;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_class)
public class ClassActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener {
    private static final String TAG = ClassActivity.class.getSimpleName();
    private static final String URL = IP.ip + "/viewListSer";
    private static final String[] classStr={"酒店","医院","周边*旅游"};

    @Extra(NavigationFragment.CLASS_KEY)
    int classIndex;

    @ViewById(R.id.rl_listview_refresh)
    SwipeRefreshLayout mRefreshLayout;

    @ViewById(R.id.lv_listview_data)
    ListView mDataLv;

    @ViewById(R.id.title_center)
    TextView mtitle;
    @ViewById(R.id.title_left)
    ImageView mtitle_left;

    List<PbView> mDatas = null;
    ViewAdapter mAdapter;
    int hasNum = 0; //已经加载的数量
    String city;
    SharedPreferences sp = null;

    @AfterViews
    public void intiView() {
        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        city = sp.getString("city", "焦作市");

        mtitle.setText(classStr[classIndex]);
        mtitle_left.setVisibility(View.VISIBLE);

        mRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mRefreshLayout.setOnRefreshListener(this);
        mDataLv.setOnScrollListener(this);

        mDatas = new ArrayList<>();
        mAdapter = new ViewAdapter(this, R.layout.item_normal, mDatas);
        mDataLv.setAdapter(mAdapter);

        initRefresh();
    }

    @Click(R.id.title_left)
    public void mClickLeft() {
        this.finish();
    }

    @UiThread(delay = 200)
    public void initRefresh() {
        mRefreshLayout.setRefreshing(true);
        reqList(0, 4, city);
    }

    /**
     * 刷新
     */
    @Override
    public void onRefresh() {
        hasNum = 0;
        reqList(0, 4, city);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                // 判断滚动到底部
                if (mDataLv.getLastVisiblePosition() == (mDataLv.getCount() - 1)) {
                    mRefreshLayout.setRefreshing(true);
                    reqList(hasNum, 4, city);
                }
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @ItemClick(R.id.lv_listview_data)
    public void mOnItemClick(int position) {
        Intent intent = new Intent(this, ViewItemActivity.class);
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
                String res;
                try {
                    res = new String(responseBody, "utf-8");
                    Log.i("TAG", res);
                    JsonData(res);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mRefreshLayout.setRefreshing(false);
                Toast.makeText(ClassActivity.this, "网络出错", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void JsonData(String res) {
        if (res.equals("false")) {
            Toast.makeText(this, "没有更多了", Toast.LENGTH_SHORT).show();
            return;
        }
        if (hasNum == 0) mDatas.clear();
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        mAdapter.setList(mDatas);
        mAdapter.notifyDataSetChanged();
        hasNum = mDatas.size();

    }

}
