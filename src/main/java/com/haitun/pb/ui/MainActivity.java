package com.haitun.pb.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haitun.pb.R;
import com.haitun.pb.fragment.MeFragment;
import com.haitun.pb.fragment.MessageFragment;
import com.haitun.pb.fragment.NavigationFragment;
import com.haitun.pb.fragment.NavigationFragment_;


import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener {

    private final static String ACTION = "com.haitun.pb.ui.finishRegActivity";
    private TextView title;
    private ImageView title_left;
    private String[] mstring = {"首页", "消息", "我"};
    private View[] mtabs;
    private List<Fragment> list = new ArrayList<>();
    private NavigationFragment dongtaiFragment;
    private MessageFragment messageFragment;
    private MeFragment meFragment;
    private int index;
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //发送广播
        Intent intent = new Intent(ACTION);
        sendBroadcast(intent);

        intiTitle();
        initTabs();
        initView();
    }

    //标题栏
    private void intiTitle() {
        title = (TextView) findViewById(R.id.title_center);
        title_left = (ImageView) findViewById(R.id.title_left);
        title_left.setImageResource(R.drawable.ic_aspect_ratio_white_24dp);
        title.setText("首页");
        title_left.setVisibility(View.VISIBLE);
        title_left.setOnClickListener(this);

    }

    private void initView() {

        dongtaiFragment = new NavigationFragment_();
        messageFragment = new MessageFragment();
        meFragment = new MeFragment();
        list.add(dongtaiFragment);
        list.add(messageFragment);
        list.add(meFragment);
        getFragmentManager().beginTransaction().add(R.id.id_fragment, dongtaiFragment)
                .commit();
    }

    private void initTabs() {

        mtabs = new View[5];
        mtabs[0] = findViewById(R.id.dongtai);
        mtabs[3] = findViewById(R.id.search);
        mtabs[4] = findViewById(R.id.btn_ck);
        mtabs[1] = findViewById(R.id.message);
        mtabs[2] = findViewById(R.id.me);
        mtabs[0].setSelected(true);
    }

    /**
     * 点击
     *
     * @param v view
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dongtai:
                index = 0;
                break;
            case R.id.search:
                Toast.makeText(this, "敬请期待", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_ck:
                Intent intent = new Intent(this, SendActivity.class);
                startActivity(intent);
                break;
            case R.id.message:
                index = 1;
                break;

            case R.id.me:
                index = 2;
                break;
            case R.id.title_left:
                actionSweep();
                break;
            default:
                break;

        }
        if (index != currentIndex) {
            getFragmentManager().beginTransaction().replace(R.id.id_fragment, list.get(index))
                    .commit();
        }
        title.setText(mstring[index]);
        mtabs[currentIndex].setSelected(false);
        mtabs[index].setSelected(true);
        currentIndex = index;
    }

    public void actionSweep() {
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == 100 || resultCode == Activity.RESULT_OK )&& data != null) {
            String res= data.getStringExtra("result");
            Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
        }

    }

    private static long firstTime;

    /**
     * 连续按两次返回键就退出
     */
    @Override
    public void onBackPressed() {

        if (firstTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            // System.exit(0);
        } else {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
        }
        firstTime = System.currentTimeMillis();
    }

}
