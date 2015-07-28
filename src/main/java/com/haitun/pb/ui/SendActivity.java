package com.haitun.pb.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.haitun.pb.R;
import com.haitun.pb.utils.Base64;
import com.haitun.pb.utils.IP;
import com.haitun.pb.utils.KeyboardUtils;
import com.haitun.pb.view.MyProgressDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class SendActivity extends Activity implements OnClickListener, AdapterView.OnItemSelectedListener {

    public ImageView title_left;
    public TextView title_center;
    public TextView title_right;

    private TextView location_tv;
    private ImageView view_images;
    private EditText e_infos;
    private Bitmap mBitmap;
    private Spinner mSpinner;
    private Uri imageUri;
    private String photo = null;
    private MyProgressDialog progressDialog = null;

    private String userId;
    private String address;
    private String classStr;
    private String latitude;
    private String longitude;
    SharedPreferences sp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_send);
        initTitle();
        Init();

    }

    public void Init() {
        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        userId = String.valueOf(sp.getInt("userid", 0));
        address = sp.getString("address", "河南省焦作市山阳区");
        latitude = sp.getString("latitude", "0");
        longitude = sp.getString("longitude", "0");
        classStr = "酒店";

        view_images = (ImageView) findViewById(R.id.send_images);
        e_infos = (EditText) findViewById(R.id.e_infos);
        location_tv = (TextView) findViewById(R.id.site_tv);
        mSpinner = (Spinner) findViewById(R.id.select_spinner);

        view_images.setOnClickListener(this);
        mSpinner.setOnItemSelectedListener(this);
        location_tv.setText(address);
    }

    //设置标题栏
    private void initTitle() {
        title_left = (ImageView) findViewById(R.id.title_left);
        title_center = (TextView) findViewById(R.id.title_center);
        title_right = (TextView) findViewById(R.id.title_right);
        title_center.setText("实景");
        title_left.setVisibility(View.VISIBLE);
        title_right.setVisibility(View.VISIBLE);
        title_left.setOnClickListener(this);
        title_right.setText("发送");
        title_right.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_right:
                String infos = e_infos.getText().toString();
                if (photo == null) {
                    Toast.makeText(this, "不发送?", Toast.LENGTH_LONG).show();
                } else {
                    progressDialog = MyProgressDialog.show(this, "发送中...", false, null);
                    reg(getApplicationContext(), photo, infos, userId, address);

                }
                break;
            case R.id.send_images:
                String info = e_infos.getText().toString();
                if (TextUtils.isEmpty(info)) {
                    return;
                }
                KeyboardUtils.closeKeyBoard(SendActivity.this);
                new PopupWindows(this, v);
                break;
            case R.id.title_left:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                File temp = new File(Environment.getExternalStorageDirectory()
                        + "/output_image.jpg");
                try {
                    compressImageFromFile(Uri.fromFile(temp));
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case 2:
                if (data != null) {
                    try {
                        compressImageFromFile(data.getData());
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;
            case 3:
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void compressImageFromFile(Uri srcPath) throws FileNotFoundException {
        //将图片进行压缩
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;//只读边,不读内容
        mBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(srcPath), null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;//
        float ww = 480f;//
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置采样率

        newOpts.inPreferredConfig = Config.ARGB_8888;//该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收

        mBitmap = BitmapFactory.decodeStream(getContentResolver().
                openInputStream(srcPath), null, newOpts);
        view_images.setImageBitmap(mBitmap);

        //将图片用Base64进行打包上传到服务器。
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //将bitmap一字节流输出 Bitmap.CompressFormat.PNG 压缩格式，100：压缩率，baos：字节流
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] buffer = baos.toByteArray();

        //将图片的字节流数据加密成base64字符输出
        photo = Base64.encodeBytes(buffer);
    }

    //用Base64字节流形式通过AsyncHttpClient框架传输网络传输图片信息
    public void reg(final Context context, String photo, String infos, String userId, String address) {
        try {
            RequestParams params = new RequestParams();
            params.put("photo", photo);
            params.put("infos", infos);
            params.put("userId", userId);
            params.put("class", classStr);
            params.put("address", address);
            params.put("latitude", latitude);
            params.put("longitude", longitude);

            String url = IP.ip + "/ImageSer";
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(url, params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    progressDialog.dismiss();
                    String info = new String(responseBody);
                    if (info.equals("true")) {
                        SendActivity.this.finish();

                    } else {
                        Toast.makeText(context, "上传失败", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    progressDialog.dismiss();
                    Toast.makeText(context, "上传失败", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        classStr = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //弹出Pop窗口
    public class PopupWindows extends PopupWindow {

        public PopupWindows(Context mContext, View parent) {

            View view = View
                    .inflate(mContext, R.layout.item_popupwindows, null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.fade_ins));
            LinearLayout ll_popup = (LinearLayout) view
                    .findViewById(R.id.ll_popup);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.push_bottom_in_2));

            setWidth(LayoutParams.FILL_PARENT);
            setHeight(LayoutParams.FILL_PARENT);
            setBackgroundDrawable(new BitmapDrawable());
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            update();
            Button bt1 = (Button) view
                    .findViewById(R.id.item_popupwindows_camera);
            Button bt2 = (Button) view
                    .findViewById(R.id.item_popupwindows_Photo);
            Button bt3 = (Button) view
                    .findViewById(R.id.item_popupwindows_cancel);
            bt1.setOnClickListener(new OnClickListener() {     //调用摄像头照相
                public void onClick(View v) {
                    //创建一个File对象，用于存储拍照后的图片
                    File outputImage = new File(Environment.getExternalStorageDirectory(), "/output_image.jpg");
                    try {
                        if (outputImage.exists()) {
                            outputImage.delete();
                        }
                        outputImage.createNewFile();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    imageUri = Uri.fromFile(outputImage);
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//					intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, 1);     //启动相机程序
                    dismiss();     //关闭Pop窗口
                }
            });
            bt2.setOnClickListener(new OnClickListener() {    //调用系统图库
                public void onClick(View v) {
                    // 下面这句指定调用相机拍照后的照片存储的路径
                    File outputImage = new File(Environment.getExternalStorageDirectory(), "output_image.jpg");
                    try {
                        if (outputImage.exists()) {
                            outputImage.delete();
                        }
                        outputImage.createNewFile();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    imageUri = Uri.fromFile(outputImage);
                    Intent intent = new Intent("android.intent.action.GET_CONTENT");
                    intent.setType("image/*");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, 2);
                    dismiss();
                }
            });
            bt3.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
    }
}
