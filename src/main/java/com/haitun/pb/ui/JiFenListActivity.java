package com.haitun.pb.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.haitun.pb.R;
import com.haitun.pb.adapter.JiFenAdapter;
import com.haitun.pb.utils.IP;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class JiFenListActivity extends Activity implements View.OnClickListener {

    public ImageView title_left;
    public TextView title_center;
    private ListView mlistView;
    private JiFenAdapter adapter;
    private List<String> mlist;
    private int id;
    private final static String path = IP.ip + "/GetViewTimeSer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ji_fen_list);
        initView();
        initTitle();

    }

    private void initView() {
        mlistView = (ListView) findViewById(R.id.jf_listview);
        mlist = new ArrayList<String>();

        adapter = new JiFenAdapter(this, R.layout.item_jifen, mlist);
        mlistView.setAdapter(adapter);
        id = getIntent().getIntExtra("id", 0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ReqList();
            }
        }, 200);

    }

    //得到积分详情
    private void ReqList() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id", id);
        client.setConnectTimeout(5000);
        client.post(path, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String res = null;
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

                Toast.makeText(JiFenListActivity.this, "网络出错", Toast.LENGTH_SHORT).show();
            }


        });
    }

    private void JsonData(String res) {

        mlist.clear();
        try {
            JSONArray jsonArray = new JSONArray(res);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject js = jsonArray.getJSONObject(i);

                mlist.add(js.getString("time"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("TAG",mlist.get(0));
        adapter.setList(mlist);
        adapter.notifyDataSetChanged();

    }

    //设置标题栏
    private void initTitle() {
        title_left = (ImageView) findViewById(R.id.title_left);
        title_center = (TextView) findViewById(R.id.title_center);
        title_center.setText("积分详情");
        title_left.setVisibility(View.VISIBLE);
        title_left.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
