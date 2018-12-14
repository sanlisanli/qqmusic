package me.mikasa.music.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import me.mikasa.music.R;
import me.mikasa.music.fragment.DiscoveryFragment;
import me.mikasa.music.fragment.MusicFragment;
import woo.mikasa.lib.base.BaseActivity;

public class TestActivity extends BaseActivity {
    @Override
    protected int setLayoutResId() {
        return R.layout.activity_test;
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initView() {
        //MusicFragment fragment=new MusicFragment();
        DiscoveryFragment fragment=new DiscoveryFragment();
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction tf=manager.beginTransaction();
        tf.replace(R.id.fragment_test,fragment);
        tf.commit();
    }

    @Override
    protected void initListener() {

    }
}
