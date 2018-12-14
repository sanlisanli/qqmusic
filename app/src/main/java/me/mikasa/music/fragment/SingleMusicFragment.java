package me.mikasa.music.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.mikasa.music.R;
import me.mikasa.music.adapter.SingleMusicAdapter;
import me.mikasa.music.bean.MusicInfo;
import me.mikasa.music.database.DbManager;
import me.mikasa.music.receiver.PlayerManagerReceiver;
import me.mikasa.music.util.Constant;
import me.mikasa.music.util.MusicUtil;
import me.mikasa.music.view.SideBar;
import woo.mikasa.lib.base.BaseFragment;
import woo.mikasa.lib.base.BaseRvAdapter;

/**
 * Created by mikasa on 2018/11/13.
 */
public class SingleMusicFragment extends BaseFragment
        implements BaseRvAdapter.OnRvItemClickListener,SingleMusicAdapter.OnMusicMenuClickListener {
    public static final String UI_UPDATE="me.mikasa.music.fragment.SingleMusicFragment.UI_UPDATE";
    private RecyclerView recyclerView;
    private SideBar sideBar;
    private SingleMusicAdapter mAdapter;
    private SingleMusicUpdateReceiver mReceiver;
    private DbManager manager;
    private List<MusicInfo> musicInfoList = new ArrayList<>();
    @Override
    protected int setLayoutResId() {
        return R.layout.fragment_single_music;
    }

    @Override
    protected void initData(Bundle bundle) {
        register();
        manager=DbManager.getInstance(mBaseActivity);
        mAdapter=new SingleMusicAdapter(mBaseActivity);
    }

    @Override
    protected void initView() {
        Collections.sort(musicInfoList);
        recyclerView=mRootView.findViewById(R.id.local_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mBaseActivity));
        recyclerView.setAdapter(mAdapter);
        sideBar=mRootView.findViewById(R.id.local_music_siderbar);
        TextView sideBarLetter=mRootView.findViewById(R.id.local_music_siderbar_pre_tv);
        sideBar.setTextView(sideBarLetter);
        loadSingleMusic();
    }

    @Override
    protected void setListener() {
        mAdapter.setOnRvItemClickListener(this);
        mAdapter.setOnMusicMenuClickListener(this);
        sideBar.setOnListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String letter) {
                //该字母首次出现的位置
                int position = mAdapter.getPositionForSection(letter.charAt(0));
                if(position != -1){
                    recyclerView.smoothScrollToPosition(position);//sideBarTextView--visibility
                }
            }
        });
    }

    public void loadSingleMusic(){
        musicInfoList = manager.getAllMusic();
        Collections.sort(musicInfoList);
        mAdapter.refreshData(musicInfoList);
        if (musicInfoList.size() == 0){
            sideBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }else {
            sideBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegister();
    }

    @Override
    public void onItemClick(int pos) {
        MusicUtil.setShared(Constant.KEY_LIST,Constant.ALLMUSIC);//??
        playMusic(pos);
    }
    private void playMusic(int pos){
        MusicInfo musicInfo = musicInfoList.get(pos);
        String path = manager.getMusicPath(musicInfo.getId());
        Intent intent = new Intent(PlayerManagerReceiver.ACTION_UPDATE);
        intent.putExtra(Constant.COMMAND, Constant.COMMAND_PLAY);
        intent.putExtra(Constant.KEY_PATH, path);
        mBaseActivity.sendBroadcast(intent);
        MusicUtil.setShared(Constant.KEY_ID,musicInfo.getId());
        //mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMusicMenuClick(int pos) {
        showToast("待开发");
    }
    private void register(){
        try {
            if (mReceiver!=null){
                this.unRegister();
            }
            mReceiver=new SingleMusicUpdateReceiver();
            IntentFilter intentFilter=new IntentFilter();
            intentFilter.addAction(SingleMusicFragment.UI_UPDATE);
            mBaseActivity.registerReceiver(mReceiver,intentFilter);//注册广播接收器
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void unRegister(){
        try {
            if (mReceiver!=null){
                mBaseActivity.unregisterReceiver(mReceiver);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private class SingleMusicUpdateReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            loadSingleMusic();
        }
    }
}
