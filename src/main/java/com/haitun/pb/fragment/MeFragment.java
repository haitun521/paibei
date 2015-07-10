package com.haitun.pb.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cengalabs.flatui.views.FlatButton;
import com.haitun.pb.R;
import com.haitun.pb.ui.JiFenListActivity;
import com.haitun.pb.ui.LoginActivity;
import com.haitun.pb.utils.IP;
import com.haitun.pb.view.ExpandAnimation;
import com.haitun.pb.view.PullScrollViewLuo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MeFragment extends Fragment implements OnClickListener, PullScrollViewLuo.OnTurnListener {
    private final int MAX_LINES = 4;
    private boolean isClickable = false;
    private TextView tv_info, tv_load_more;
    // 自定义的scrollview
    private PullScrollViewLuo mPullScrollView;
    // about 背景图
    private ImageView img_bigShopLogo;

    private TextView tv_Contact, tv_shopName, tv_phone;
    private TextView tv_ContactContent;
    private TextView tv_address;
    private TextView tv_addressContent;
    private TextView tv_jiFen;
    private boolean isAnimEnd = true;

    private ImageView img_otherContactArrow;
    private ImageView img_address;
    private FlatButton quiet;
    private final static String path = IP.ip + "/GetUserSer";
    private int id;
    private String address;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mPullScrollView.init(img_bigShopLogo);
        mPullScrollView.setOnTurnListener(MeFragment.this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, null);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        id = sp.getInt("userid", 0);
        address = sp.getString("address", "河南省焦作市山阳区");

        tv_shopName = (TextView) view.findViewById(R.id.tv_shopName);
        tv_phone = (TextView) view.findViewById(R.id.tv_phone);
        tv_info = (TextView) view.findViewById(R.id.tv_info);
        tv_info.setMaxLines(MAX_LINES);
        tv_load_more = (TextView) view.findViewById(R.id.tv_loadMore);
        tv_load_more.setOnClickListener(this);

        mPullScrollView = (PullScrollViewLuo) view.findViewById(R.id.sv_about);
        img_bigShopLogo = (ImageView) view.findViewById(R.id.img_bigShopLogo);

        tv_Contact = (TextView) view.findViewById(R.id.tv_otherContact);
        tv_Contact.setOnClickListener(this);
        tv_ContactContent = (TextView) view
                .findViewById(R.id.tv_otherContactContent);

        img_otherContactArrow = (ImageView) view
                .findViewById(R.id.img_otherContactArrow);
        img_otherContactArrow.measure(0, 0);

        tv_address = (TextView) view.findViewById(R.id.tv_address);
        tv_address.setOnClickListener(this);
        tv_addressContent = (TextView) view.findViewById(R.id.tv_addressContent);
        img_address = (ImageView) view.findViewById(R.id.img_address);
        img_address.measure(0, 0);

        tv_jiFen = (TextView) view.findViewById(R.id.tv_jiFen);
        tv_jiFen.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), JiFenListActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        quiet = (FlatButton) view.findViewById(R.id.quiet_btn);
        quiet.setOnClickListener(this);
        ReqUserInfo();
    }

    //得到用户信息
    private void ReqUserInfo() {
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

                Toast.makeText(MeFragment.this.getActivity(), "网络出错", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void JsonData(String res) {

        try {
            JSONObject js = new JSONObject(res);
            tv_shopName.setText(js.getString("name"));
            tv_phone.setText(js.getString("phone"));
            tv_addressContent.setText(address);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_loadMore:
                if (!isClickable) {
                    tv_info.setMaxLines(tv_info.getLineCount());
                    tv_load_more.setText("收起");
                    isClickable = true;
                } else {
                    tv_info.setMaxLines(MAX_LINES);
                    tv_load_more.setText("查看更多");
                    isClickable = false;
                }
                break;
            case R.id.tv_otherContact:

                if (isAnimEnd) {

                    if (tv_ContactContent.getVisibility() == View.GONE) {
                        rotateArrow(0, 90);
                    } else {
                        rotateArrow(90, 0);
                    }

                    ExpandAnimation mAnimation = new ExpandAnimation(
                            tv_ContactContent);
                    mAnimation.setAnimationListener(new ExpandAnimationListener());
                    tv_ContactContent.startAnimation(mAnimation);
                }
                break;
            case R.id.tv_address:

                if (isAnimEnd) {

                    if (tv_addressContent.getVisibility() == View.GONE) {
                        rotateArrowAddress(0, 90);
                    } else {
                        rotateArrowAddress(90, 0);
                    }

                    ExpandAnimation mAnimation = new ExpandAnimation(
                            tv_addressContent);
                    mAnimation.setAnimationListener(new ExpandAnimationListener());
                    tv_addressContent.startAnimation(mAnimation);
                }
                break;

            case R.id.quiet_btn:
                Intent intent=new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
        }
    }

    @Override
    public void onTurn() {

    }


    /**
     * 防止用户频繁操作
     */
    private class ExpandAnimationListener implements AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

            isAnimEnd = false;
        }

        @Override
        public void onAnimationEnd(Animation animation) {

            isAnimEnd = true;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    /**
     * 旋转指示器
     *
     * @param fromDegrees
     * @param toDegrees
     */
    private void rotateArrow(float fromDegrees, float toDegrees) {
        RotateAnimation mRotateAnimation = new RotateAnimation(fromDegrees,
                toDegrees,
                (int) (img_otherContactArrow.getMeasuredWidth() / 2.0),
                (int) (img_otherContactArrow.getMeasuredHeight() / 2.0));
        mRotateAnimation.setDuration(150);
        mRotateAnimation.setFillAfter(true);
        img_otherContactArrow.startAnimation(mRotateAnimation);
    }

    private void rotateArrowAddress(float fromDegrees, float toDegrees) {
        RotateAnimation mRotateAnimation1 = new RotateAnimation(fromDegrees,
                toDegrees,
                (int) (img_address.getMeasuredWidth() / 2.0),
                (int) (img_address.getMeasuredHeight() / 2.0));
        mRotateAnimation1.setDuration(150);
        mRotateAnimation1.setFillAfter(true);
        img_address.startAnimation(mRotateAnimation1);
    }


}
