package com.haitun.pb.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.haitun.pb.R;
import com.haitun.pb.adapter.CommentAdapter;
import com.haitun.pb.bean.PbComment;
import com.haitun.pb.bean.PbView;
import com.haitun.pb.utils.IP;
import com.haitun.pb.view.CustomShareBoard;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewItemActivity extends Activity implements View.OnClickListener, AbsListView.OnScrollListener, AdapterView.OnItemClickListener {

    private final static String pathComment = IP.ip + "/getCommetSer";
    private final static String pathHeart = IP.ip + "/AddHeartSer";
    private final static String pathSend = IP.ip + "/SaveCommentSer";

    public ImageView title_left;
    public TextView title_center;
    public TextView title_right;

    private TextView name_tv;
    private TextView time_tv;
    private TextView describe_tv;
    private TextView address_tv;
    private TextView ok_tv;
    private ImageView heart_image;
    private SimpleDraweeView image_dv;
    private List<PbComment> mlist;
    private ListView mlistView;
    private CommentAdapter adapter;

    private EditText comment_tv;
    private ImageView send_image;

    private String imageUrl;
    private int hasNum = 0;
    private String id;
    private int okNum;
    private int userId;
    private String Aname;
    private String Bname="null";
    // 首先在您的Activity中添加如下成员变量
    final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
    private SHARE_MEDIA mPlatform = SHARE_MEDIA.SINA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);
        initTitle();
        initView();

    }

    private void initView() {
        name_tv = (TextView) findViewById(R.id.view_item_name);
        time_tv = (TextView) findViewById(R.id.view_item_time);
        heart_image = (ImageView) findViewById(R.id.image_item_heart);
        image_dv = (SimpleDraweeView) findViewById(R.id.view_item_image);
        ok_tv = (TextView) findViewById(R.id.tv_item_ok);
        mlistView = (ListView) findViewById(R.id.view_item_listview);
        describe_tv = (TextView) findViewById(R.id.view_item_describe);
        address_tv = (TextView) findViewById(R.id.view_item_address);

        mlistView = (ListView) findViewById(R.id.view_item_listview);
        comment_tv = (EditText) findViewById(R.id.comment_et);
        send_image = (ImageView) findViewById(R.id.send_comment);

        initData();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ReqComment(id, 0, 10);
            }
        }, 200);
    }

    private void initData() {
        SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        userId = sp.getInt("userid", 0);
        Aname=sp.getString("username","拍呗");

        PbView pbView = (PbView) getIntent().getSerializableExtra("PbView");
        id = pbView.getVId();
        name_tv.setText(pbView.getName());
        time_tv.setText(pbView.getVScdate());
        imageUrl = IP.ip + pbView.getVUrl();
        image_dv.setImageURI(Uri.parse(imageUrl));
        if (TextUtils.isEmpty(pbView.getVWord())) {
            describe_tv.setVisibility(View.GONE);
        } else {
            describe_tv.setText(pbView.getVWord());
        }
        describe_tv.setText(pbView.getVWord());
        address_tv.setText(pbView.getVArea());

        ok_tv.setText(pbView.getVOk());
        heart_image.setOnClickListener(this);

        okNum = Integer.parseInt(ok_tv.getText().toString().trim()) + 1;
        mlist = new ArrayList<>();
        adapter = new CommentAdapter(this, R.layout.item_comment, mlist);
        mlistView.setAdapter(adapter);
        mlistView.setOnScrollListener(this);
        mlistView.setOnItemClickListener(this);

        send_image.setOnClickListener(this);
    }

    private void initTitle() {
        title_left = (ImageView) findViewById(R.id.title_left);
        title_center = (TextView) findViewById(R.id.title_center);
        title_right = (TextView) findViewById(R.id.title_right);
        title_left.setVisibility(View.VISIBLE);
        title_right.setVisibility(View.VISIBLE);
        title_center.setText("详情");
        title_right.setText("分享");
        title_left.setOnClickListener(this);
        title_right.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.title_left:
                finish();
                break;
            case R.id.title_right:
                shareImage();
            case R.id.image_item_heart:

                clickHeart(okNum);
                break;
            case R.id.send_comment:

                String str = comment_tv.getText().toString().trim();
                if (TextUtils.isEmpty(str)) {
                    return;
                }
                SendComment(str);
                ResetComment(str);

                break;
        }

    }

    private void ResetComment(String str) {
        comment_tv.setText("");
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
     * 分享
     */
    private void shareImage() {
     /* //  mController.openShare(this, false);
        Intent intent=new Intent(this,ShareActivity.class);
        startActivity(intent);*/

        QQZone();


        CustomShareBoard shareBoard = new CustomShareBoard(this);
        shareBoard.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

    }

    private void QQZone() {
        String appId = "100424468";
        String appKey = "c7394704798a158208a74ab60104f0ba";


        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, appId, appKey);
        qZoneSsoHandler.addToSocialSDK();

        UMImage urlImage = new UMImage(this, imageUrl);
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareMedia(urlImage);
        qzone.setShareContent("来自\"拍呗\"的图片");
        mController.setShareMedia(qzone);
    }

    /**
     * 发送评论
     *
     * @param comment
     */
    private void SendComment(String comment) {
        AsyncHttpClient client = new AsyncHttpClient();


        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("viewId", id);
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


    private void clickHeart(int num) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("num", String.valueOf(num));
        client.setConnectTimeout(5000);
        client.post(pathHeart, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                changeHeartNum(res);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void changeHeartNum(String res) {
        if (res.equals("true")) {
            ok_tv.setText(String.valueOf(okNum));
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                // 判断滚动到底部
                if (mlistView.getLastVisiblePosition() == (mlistView.getCount() - 1)) {
                    ReqComment(id, hasNum, 10);
                }
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

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
                    String res = new String(responseBody, "utf-8");
                    JsonData(res);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(ViewItemActivity.this, "网络出错" + statusCode, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void JsonData(String res) {
        if (res.equals("false")) {
            Toast.makeText(ViewItemActivity.this, "没有评论", Toast.LENGTH_SHORT).show();
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
        comment_tv.setText(text);
        //光标的位置
        comment_tv.setSelection(text.length());
    }
}
