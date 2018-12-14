package me.mikasa.music.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;

import me.mikasa.music.R;
import me.mikasa.music.fragment.FavorMusicFragment;

public class FavorMusicActivity extends PlayBarBaseActivity{
    private ImageView refreshFavor;
    private FavorMusicFragment fragment;


    @Override
    protected int setLayoutResId() {
        return R.layout.activity_favor_music;
    }

    @Override
    protected void initData() {
        mTitle.setText("收藏音乐");
    }

    @Override
    protected void initView() {
        refreshFavor=findViewById(R.id.toolbar_menu);
        refreshFavor.setVisibility(View.VISIBLE);
        refreshFavor.setImageResource(R.drawable.refresh_white);
        fragment=new FavorMusicFragment();
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction tf=manager.beginTransaction();
        tf.replace(R.id.fragment_container,fragment);
        tf.commit();
    }

    @Override
    protected void initListener() {
        refreshFavor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.loadMusic();
            }
        });
    }
}
