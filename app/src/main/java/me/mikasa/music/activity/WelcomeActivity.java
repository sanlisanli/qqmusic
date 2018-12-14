package me.mikasa.music.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Random;

import me.mikasa.music.R;
import me.mikasa.music.util.Constant;
import woo.mikasa.lib.base.BaseActivity;

public class WelcomeActivity extends BaseActivity implements BaseActivity.PermissionListener {
    private static final int delay=2000;

    @Override
    protected void createContentView() {
        //全屏,NoTitle
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(setLayoutResId());
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initView() {
        ImageView imageView=findViewById(R.id.iv_splash);
        Glide.with(mContext).load(getImgId())
                .crossFade(delay)
                .into(imageView);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestPermission();
            }
        },delay);
    }
    private void requestPermission(){
        String[]permissions={Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestRuntimePermission(permissions,this);
    }


    @Override
    protected void initListener() {
    }

    @Override
    public void onGranted() {
        Intent intent=new Intent(WelcomeActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDenied() {
        WelcomeActivity.this.finish();
    }
    private int getImgId(){
        List<Integer>ids=Constant.getWelcomeImgs();
        Random random=new Random(System.currentTimeMillis());
        int i=random.nextInt(ids.size());//生成[0,length)内伪随机int
        return ids.get(i);
    }
}
