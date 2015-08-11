package com.haitun.pb.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haitun.pb.R;

/**
 * web界面
 */
public class WebActivity extends Activity implements View.OnClickListener {

    private WebView mWebView;
    private ImageView title_left;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        init();

    }

    private void init() {
        initTitle();
        mWebView = (WebView) findViewById(R.id.webView);
        String url=getIntent().getStringExtra(MainActivity.SCAN_CONSTANT);
        Log.i("URL",url);
        mWebView.loadUrl(url);
        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mWebView.getSettings().setJavaScriptEnabled(true);//执行js脚本
        mWebView.getSettings().setBuiltInZoomControls(true);//执行缩放

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);// 使用当前WebView处理跳转
                return true;//true表示此事件在此处被处理，不需要再广播
            }


            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                Toast.makeText(WebActivity.this, description, Toast.LENGTH_SHORT).show();
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title_) {

                title.setText(title_);
            }
        });
    }

    private void initTitle() {

        title_left= (ImageView) findViewById(R.id.title_left);
        title= (TextView) findViewById(R.id.title_center);
        title_left.setVisibility(View.VISIBLE);
        title_left.setOnClickListener(this);
        title.setText("详情");
    }


    @Override
    public void onClick(View v) {

        finish();
    }


    /**
     * 默认点回退键，会退出Activity，需监听按键操作，使回退在WebView内发生
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_BACK&&mWebView.canGoBack()){
            mWebView.goBack();
            return  true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
