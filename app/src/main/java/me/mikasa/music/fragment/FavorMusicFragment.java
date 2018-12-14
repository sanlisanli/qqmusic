package me.mikasa.music.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.mikasa.music.R;
import me.mikasa.music.adapter.CommonMusicAdapeter;
import me.mikasa.music.bean.MusicInfo;
import me.mikasa.music.database.DbManager;
import me.mikasa.music.receiver.PlayerManagerReceiver;
import me.mikasa.music.util.Constant;
import me.mikasa.music.util.MusicUtil;
import woo.mikasa.lib.base.BaseFragment;
import woo.mikasa.lib.base.BaseRvAdapter;

/**
 * Created by mikasa on 2018/11/15.
 */
public class FavorMusicFragment extends BaseFragment implements BaseRvAdapter.OnRvItemClickListener {
    private CommonMusicAdapeter mAdapter;
    private DbManager manager;
    private List<MusicInfo> musicInfoList=new ArrayList<>();
    @Override
    protected int setLayoutResId() {
        return R.layout.fragment_favor_music;
    }

    @Override
    protected void initData(Bundle bundle) {
        mAdapter=new CommonMusicAdapeter(mBaseActivity);
        manager=new DbManager(mBaseActivity);
    }

    @Override
    protected void initView() {
        RecyclerView recyclerView=mRootView.findViewById(R.id.rv_favor);
        recyclerView.setLayoutManager(new LinearLayoutManager(mBaseActivity));
        recyclerView.setAdapter(mAdapter);
        loadMusic();
    }
    public void loadMusic(){
        if (musicInfoList.size()>0){
            musicInfoList.clear();
        }
        musicInfoList=manager.getAllMusic(Constant.MYLOVE);
        if (musicInfoList.size()>0){
            mAdapter.refreshData(musicInfoList);
        }
    }

    @Override
    protected void setListener() {
        mAdapter.setOnRvItemClickListener(this);
    }

    @Override
    public void onItemClick(int pos) {
        MusicUtil.setShared(Constant.KEY_LIST,Constant.MYLOVE);
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
    }

}
