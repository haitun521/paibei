package com.haitun.pb.fragment;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.haitun.pb.R;
import com.haitun.pb.adapter.MyAdapter;
import com.haitun.pb.adapter.ParticirateMyAdapter;
import com.haitun.pb.bean.PbComment;
import com.haitun.pb.utils.IP;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment implements TabHost.OnTabChangeListener {

    private View view;
    private TabHost myTabHost;
    private int[] layRes = new int[]{R.id.tab_aboutme,
            R.id.tab_paticipateme};        //与我相关，我参与的
    private ListView tab_aboutme, tab_participateme;
    private String https = IP.ip;
    private List<PbComment> list_comment = new ArrayList<PbComment>();

    private int id;
    private String aname;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_message, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences sp=getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        id=sp.getInt("userid",0);
        aname=sp.getString("username","拍呗");
        Init();
    }

    private void Init() {
        myTabHost = (TabHost) view.findViewById(R.id.tabhost);
        tab_aboutme = (ListView) view.findViewById(R.id.tab_aboutme);
        tab_participateme = (ListView)view.findViewById(R.id.tab_paticipateme);
        myTabHost.setup();        //建立TabHost对象
        for (int x = 0; x < this.layRes.length; x++) {
            TabHost.TabSpec myTab = myTabHost.newTabSpec("tag" + x);
            if (x == 0) {
                myTab.setIndicator("与我相关");
            } else {
                myTab.setIndicator("我参与的");
            }
            myTab.setContent(this.layRes[x]);
            myTabHost.addTab(myTab);
        }
        getAsyncDatasAbout();
        this.myTabHost.setCurrentTab(0);    //默认显示的标签索引为2
        myTabHost.setOnTabChangedListener(this);
    }

    @Override
    public void onTabChanged(String tabId) {

        if (tabId.equals("tag0")) {    //与我相关的

            getAsyncDatasAbout();
        } else {       //我参与的

            getAsyncDatasParticipate();
        }
    }

    private void getAsyncDatasParticipate() {
        String url = https + "/GetMessageSer";
        RequestParams params = new RequestParams();
        params.put("AId", id);                                        //设置一个固定的值用于测试。
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody.equals("false")) {

                } else {
                    String json = new String(responseBody);

                    try {
                        list_comment.clear();
                        JSONArray array = new JSONArray(json);

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            PbComment comment = new PbComment();
                            comment.setCContent(object.getString("comment"));
                            comment.setCDate(object.getString("cdate"));
                            comment.setAname(aname);
                            list_comment.add(comment);
                        }
                        ParticirateMyAdapter adapter = new ParticirateMyAdapter(getActivity(), list_comment);
                        tab_participateme.setAdapter(adapter);
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable errors) {
                Toast.makeText(MessageFragment.this.getActivity(), "网络出错", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getAsyncDatasAbout() {
        String url = https + "/GetMyMessageSer";
        RequestParams params = new RequestParams();
        params.put("AId", id);                                        //设置一个固定的值用于测试。
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody.equals("false")) {

                } else {
                    String json = new String(responseBody);
                  //  LogUtil.i("json", json.toString());
                    try {
                        list_comment.clear();
                        JSONArray array = new JSONArray(json);
                        //LogUtil.i("array", array.length() + "");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            PbComment comment = new PbComment();
                            comment.setCContent(object.getString("comment"));
                            comment.setCDate(object.getString("cdate"));
                            comment.setBname(object.getString("bName"));
                            list_comment.add(comment);
                        }
                        MyAdapter adapter = new MyAdapter(getActivity(), list_comment);
                        tab_aboutme.setAdapter(adapter);
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable errors) {
                Toast.makeText(MessageFragment.this.getActivity(), "网络出错", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
