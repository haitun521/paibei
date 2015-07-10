package com.haitun.pb.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cengalabs.flatui.views.FlatButton;
import com.cengalabs.flatui.views.FlatEditText;
import com.haitun.pb.R;
import com.haitun.pb.utils.IP;
import com.haitun.pb.view.MyProgressDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Created by 笨笨 on 2015/6/1.
 */
public class RegsiterEndActivity extends Activity implements View.OnClickListener {
    public ImageView title_left;
    public TextView title_center;
    public TextView title_right;

    public FlatEditText reg_username;
    public FlatEditText reg_password;
    public FlatButton reg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_end);
        initView();

    }

    private void initView() {
        reg_username = (FlatEditText) findViewById(R.id.reg_username);
        reg_password = (FlatEditText) findViewById(R.id.reg_password);
        reg = (FlatButton) findViewById(R.id.regsiter_end);
        reg.setOnClickListener(this);
        initTitle();
    }

    //设置标题栏
    private void initTitle() {
        title_left = (ImageView) findViewById(R.id.title_left);
        title_center = (TextView) findViewById(R.id.title_center);
        title_right = (TextView) findViewById(R.id.title_right);
        title_left.setVisibility(View.VISIBLE);
        title_right.setVisibility(View.VISIBLE);

        title_center.setText("注册");
        title_right.setText("2/2");
        title_left.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.title_left){
            this.finish();
        }else if (v.getId()==R.id.regsiter_end){
            register();

        }
    }
    public void register() {

        final String username = reg_username.getText().toString().trim();
        String password = reg_password.getText().toString().trim();
        String tel=getIntent().getStringExtra("tel");
        String url = IP.ip + "/Register";
        if (TextUtils.isEmpty(username)) {
            return;
        }
        if (TextUtils.isEmpty(password)) {
            return;
        }

        final MyProgressDialog progressDialog=MyProgressDialog.show(this,"注册中...",false,null);

        AsyncHttpClient client = new AsyncHttpClient();
        //连接时间
        client.setConnectTimeout(5000);
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);
        params.put("tel", tel);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                progressDialog.dismiss();
                int res = Integer.parseInt(new String(responseBody));
                if (res>0) {
                    Toast.makeText(RegsiterEndActivity.this, "注册成功", Toast.LENGTH_LONG).show();

                    SharedPreferences.Editor editor= getSharedPreferences("config", Context.MODE_PRIVATE).edit();
                    editor.putInt("userid",res);
                    editor.putString("username",username);

                    editor.commit();

                    Intent intent = new Intent(RegsiterEndActivity.this, MainActivity.class);
                    startActivity(intent);
                    RegsiterEndActivity.this.finish();

                } else {
                    Toast.makeText(RegsiterEndActivity.this, "注册失败", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                Toast.makeText(RegsiterEndActivity.this, "注册失败", Toast.LENGTH_LONG).show();
            }
        });


    }
}
