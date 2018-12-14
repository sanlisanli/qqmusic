package me.mikasa.music.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;

import me.mikasa.music.R;
import me.mikasa.music.fragment.PlayBarFragment;
import woo.mikasa.lib.base.BaseToolbarActivity;

/**
 * Created by mikasa on 2018/11/13.
 */
public abstract class PlayBarBaseActivity extends BaseToolbarActivity {
    private PlayBarFragment playBarFragment;

    @Override
    protected void onResume() {
        super.onResume();
        initPlayBar();
    }

    /**
     * 初始化播放器控制栏
     */

    private void initPlayBar(){
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        if (playBarFragment==null){
            playBarFragment=PlayBarFragment.newInstance();
            transaction.add(R.id.fragment_play_bar,playBarFragment).commit();
        }else {
            transaction.show(playBarFragment).commit();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
}
