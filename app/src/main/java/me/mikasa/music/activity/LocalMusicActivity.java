package me.mikasa.music.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import me.mikasa.music.R;
import me.mikasa.music.fragment.SingleMusicFragment;

public class LocalMusicActivity extends PlayBarBaseActivity {
    @Override
    protected int setLayoutResId() {
        return R.layout.activity_local_music;
    }

    @Override
    protected void initData() {
        mTitle.setText("本地音乐");
        Intent intent=getIntent();
        Boolean isNull=intent.getBooleanExtra("isnull",false);
        if (isNull){
            final LinearLayout layout=findViewById(R.id.local_nothing_rl);
            layout.setVisibility(View.VISIBLE);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent scanIntent=new Intent(mContext,ScanActivity.class);
                    startActivity(scanIntent);
                    layout.setVisibility(View.GONE);//需在startActivity后面？？
                }
            });
        }
    }

    @Override
    protected void initView() {
        SingleMusicFragment singleMusicFragment=new SingleMusicFragment();
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.replace(R.id.fragment_container,singleMusicFragment);
        transaction.commit();
        ImageView iv=findViewById(R.id.toolbar_menu);
        iv.setVisibility(View.VISIBLE);
        iv.setImageResource(R.drawable.scan_white);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LocalMusicActivity.this,ScanActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initListener() {
    }
}
