package com.haitun.pb.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cengalabs.flatui.views.FlatButton;
import com.cengalabs.flatui.views.FlatEditText;
import com.haitun.pb.R;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegsiterActivity extends Activity implements View.OnClickListener {

    private static final String appkey = "81201d96cacb"; // 设置成你自己的appkey
    private static final String appsecret = "1d3d818a9f13b677994a1b3957be2b42";//设置成你自己的appsecret

    public ImageView title_left;
    public TextView title_center;
    public TextView title_right;

    public FlatEditText reg_tel;
    public FlatEditText reg_val;
    public FlatButton next, sendValid;
    BroadcastReceiver finishActivity = new FinishActivity();

    private TimeCount time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_1);
        SMSSDK.initSDK(this, appkey, appsecret);
        EventHandler eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {

                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }

        };

        SMSSDK.registerEventHandler(eh);
        initView();

    }

    private void initView() {
        reg_tel = (FlatEditText) findViewById(R.id.reg_tel);
        reg_val = (FlatEditText) findViewById(R.id.reg_validate);

        sendValid = (FlatButton) findViewById(R.id.send_reg_validate);
        next = (FlatButton) findViewById(R.id.next);
        sendValid.setOnClickListener(this);
        next.setOnClickListener(this);
        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.haitun.pb.ui.finishRegActivity");
        registerReceiver(finishActivity, intentFilter);
        initTitle();
    }

    //设置标题栏
    private void initTitle() {
        title_left = (ImageView) findViewById(R.id.title_left);
        title_center = (TextView) findViewById(R.id.title_center);
        title_right = (TextView) findViewById(R.id.title_right);
        title_center.setText("注册");
        title_left.setVisibility(View.VISIBLE);
        title_right.setVisibility(View.VISIBLE);
        title_left.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.title_left) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            this.finish();
        } else if (v.getId() == R.id.send_reg_validate) {
            String tel = reg_tel.getText().toString().trim();

            if (TextUtils.isEmpty(tel)) {
                return;
            }
            refreshValid(tel);


        } else if (v.getId() == R.id.next) {
            String tel = reg_tel.getText().toString().trim();
            String valid = reg_val.getText().toString().trim();
            if (TextUtils.isEmpty(tel) || TextUtils.isEmpty(valid)) {
                return;
            }
            time.cancel();
            SMSSDK.submitVerificationCode("86", tel, valid);

            // finish();
        }
    }

    private void refreshValid(String tel) {
        SMSSDK.getVerificationCode("86", tel);
        time = new TimeCount(60000, 1000);
        time.start();
    }

    class FinishActivity extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            RegsiterActivity.this.finish();
            Log.i("TAG", "RegsiterActivity.this.finish();");
        }
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            if (result == SMSSDK.RESULT_COMPLETE) {
                //短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {

                    String tel = reg_tel.getText().toString().trim();
                    Intent intent = new Intent(RegsiterActivity.this, RegsiterEndActivity.class);
                    intent.putExtra("tel", tel);
                    startActivity(intent);
                    //注册广播
                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction("com.haitun.pb.ui.finishRegActivity");
                    registerReceiver(finishActivity, intentFilter);

                    Toast.makeText(getApplicationContext(), "验证成功", Toast.LENGTH_SHORT).show();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {//返回支持发送验证码的国家列表
                    //Toast.makeText(getApplicationContext(), "获取国家列表成功", Toast.LENGTH_SHORT).show();

                }
            } else {
                ((Throwable) data).printStackTrace();
            }

        }

    };

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发
            sendValid.setText("重新获取验证码");
            sendValid.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
            sendValid.setClickable(false);
            sendValid.setText("还剩"+millisUntilFinished / 1000 + "秒");
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(finishActivity);
        SMSSDK.unregisterAllEventHandler();

    }

}
