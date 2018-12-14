package me.mikasa.music.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import me.mikasa.music.R;
import me.mikasa.music.adapter.MusicItemAdapter;
import me.mikasa.music.util.Constant;
import woo.mikasa.lib.base.BaseFragment;

/**
 * Created by mikasa on 2018/11/14.
 */
public class DiscoveryFragment extends BaseFragment {
    @Override
    protected int setLayoutResId() {
        return R.layout.fragment_discovery;
    }

    @Override
    protected void initData(Bundle bundle) {
    }

    @Override
    protected void initView() {
        RecyclerView recyclerView=mRootView.findViewById(R.id.rv_dis);
        recyclerView.setLayoutManager(new LinearLayoutManager(mBaseActivity));
        MusicItemAdapter adapter=new MusicItemAdapter(mBaseActivity);
        recyclerView.setAdapter(adapter);
        adapter.refreshData(Constant.getMv());
    }

    @Override
    protected void setListener() {
    }
}
