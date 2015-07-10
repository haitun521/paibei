package com.haitun.pb.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
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
 * 登录
 */
public class LoginActivity extends Activity {

    public FlatButton login_button;
    public TextView reg_button;
    public FlatEditText username_edt;
    public FlatEditText password_edt;
    public ImageView title_left;
    public TextView title_right;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        initView();

    }

    private void initView() {
        username_edt = (FlatEditText) findViewById(R.id.login_username);
        password_edt = (FlatEditText) findViewById(R.id.login_pwd);
        login_button = (FlatButton) findViewById(R.id.login_btn);
        reg_button = (TextView) findViewById(R.id.register);
        login_button.setOnClickListener(new mClickLister());
        reg_button.setOnClickListener(new mClickLister());

        // initTitle();
    }

    //设置标题栏
    private void initTitle() {
        title_left = (ImageView) findViewById(R.id.title_left);
        title_right = (TextView) findViewById(R.id.title_right);
        title_left.setVisibility(View.INVISIBLE);
        title_right.setVisibility(View.INVISIBLE);
    }

    public class mClickLister implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //登录
            if (v.getId() == R.id.login_btn) {
                Login();

            } else if (v.getId() == R.id.register) {
                //进入注册界面
                Intent intent = new Intent(LoginActivity.this, RegsiterActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        }
    }

    public void Login() {

        final String username = username_edt.getText().toString().trim();
        String password = password_edt.getText().toString().trim();
        String url = IP.ip + "/LoginSer";
        if (TextUtils.isEmpty(username)) {
            return;
        }
        if (TextUtils.isEmpty(password)) {

            return;
        }

        final MyProgressDialog progressDialog=MyProgressDialog.show(this,"登录中...",false,null);

        AsyncHttpClient client = new AsyncHttpClient();
        //连接时间
        client.setConnectTimeout(5000);
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                progressDialog.dismiss();
                int res = Integer.parseInt(new String(responseBody));
                if (res>0) {
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();

                    SharedPreferences.Editor editor= getSharedPreferences("config", Context.MODE_PRIVATE).edit();
                    editor.putInt("userid",res);
                    //editor.putString("username",username);
                    editor.commit();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();

                } else {
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
            }
        });

    }

}
