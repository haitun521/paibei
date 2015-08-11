package com.haitun.pb.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cengalabs.flatui.views.FlatButton;
import com.facebook.drawee.view.SimpleDraweeView;
import com.haitun.pb.R;
import com.haitun.pb.bean.PbView;
import com.haitun.pb.utils.IP;
import com.haitun.pb.view.CustomShareBoard;
import com.haitun.pb.view.MyProgressDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;

import org.apache.http.Header;

import java.net.URISyntaxException;
import java.text.DecimalFormat;

public class ViewItemActivity extends Activity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener {

    private final static String pathHeart = IP.ip + "/AddHeartSer";


    public ImageView title_left;
    public TextView title_center;
    public TextView title_right;

    private TextView name_tv;
    private TextView time_tv;
    private TextView describe_tv;
    private TextView address_tv;
    private SimpleDraweeView image_dv;

    private RelativeLayout viewitemclickassess, viewitemclickaddress;

    private TextView reputation_tv;//好评
    private TextView viewitemassess;//评论
    private RatingBar viewitemratingBar;
    private TextView star_num_tv;
    FlatButton assess_btn;

    private String imageUrl;

    private String id;
    private int userId;
    private float numStars;
    private String Aname;
    PbView pbView;

    // 首先在您的Activity中添加如下成员变量
    final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        initTitle();
        initView();

    }

    private void initView() {
        name_tv = (TextView) findViewById(R.id.view_item_name);
        time_tv = (TextView) findViewById(R.id.view_item_time);
        image_dv = (SimpleDraweeView) findViewById(R.id.view_item_image);

        describe_tv = (TextView) findViewById(R.id.view_item_describe);
        address_tv = (TextView) findViewById(R.id.view_item_address);

        viewitemclickaddress = (RelativeLayout) findViewById(R.id.view_item_click_address);
        viewitemclickassess = (RelativeLayout) findViewById(R.id.view_item_click_assess);
        reputation_tv= (TextView)findViewById(R.id.text_reputation);
        viewitemassess = (TextView) findViewById(R.id.view_item_assess);
        viewitemratingBar = (RatingBar) findViewById(R.id.view_item_ratingBar);
        star_num_tv= (TextView) findViewById(R.id.text_star_num);
        assess_btn= (FlatButton) findViewById(R.id.button_assess);

        viewitemratingBar.setOnRatingBarChangeListener(this);
        viewitemclickassess.setOnClickListener(this);
        viewitemclickaddress.setOnClickListener(this);
        assess_btn.setOnClickListener(this);
        initData();
    }

    private void initData() {
        SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        userId = sp.getInt("userid", 0);
        Aname = sp.getString("username", "拍呗");

        pbView = (PbView) getIntent().getSerializableExtra("PbView");
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

        address_tv.setText(pbView.getVArea());
        float rep=Float.parseFloat(pbView.getVOk())/5;
        DecimalFormat format=new DecimalFormat("0.00%");
        reputation_tv.append(format.format(rep));

        viewitemassess.setText(pbView.getPeople()+"人评论");

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
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

        star_num_tv.setVisibility(View.VISIBLE);
        String satisfaction;
        if(rating<2.5){
            satisfaction="一般";
        }else if(rating==5.0){
            satisfaction="非常满意";
        }else {
            satisfaction="满意";
        }
        star_num_tv.setText(getString(R.string.text_star_num) + satisfaction);
        numStars=rating;
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.title_left:
                finish();
                break;
            case R.id.title_right:
                shareImage();
                break;
            case R.id.view_item_click_address://进入地图界面

                accessAddress(address_tv.getText().toString().trim());
                break;
            case R.id.view_item_click_assess://进入评论界面
                accessAssess();
                break;
            case R.id.button_assess:
                clickAssess(numStars);
                break;
        }

    }
    //调用百度地图
    public void accessAddress(String address) {

        Intent intent = null;
        try {
            intent = Intent.getIntent("intent://map/geocoder?address="+ address+"&src=com.haitun.pb#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        startActivity(intent); //启动调用
    }


    public void accessAssess(){
        Intent intent=new Intent(this,AssessActivity_.class);
        intent.putExtra("viewId",id);
        startActivity(intent);
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


    private void clickAssess(float num) {
        final MyProgressDialog progressDialog=MyProgressDialog.show(this,"评价中...",false,null);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id", id);
        //params.put("userId", userId);
        params.put("numStars", String.valueOf(num));
        client.setConnectTimeout(5000);
        client.post(pathHeart, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //String res = new String(responseBody);
                progressDialog.dismiss();
                Toast.makeText(ViewItemActivity.this,"评价成功",Toast.LENGTH_SHORT).show();
                assess_btn.setVisibility(View.GONE);
                viewitemratingBar.setIsIndicator(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                Toast.makeText(ViewItemActivity.this,R.string.error,Toast.LENGTH_SHORT).show();
            }
        });
    }


}
