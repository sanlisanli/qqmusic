package me.mikasa.music.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;

import java.util.Collections;
import java.util.List;

import me.mikasa.music.R;
import me.mikasa.music.adapter.MusicItemAdapter;
import me.mikasa.music.bean.Music;
import me.mikasa.music.util.Constant;
import woo.mikasa.lib.base.BaseFragment;
import woo.mikasa.xrv.XXRecyclerView;

/**
 * Created by mikasa on 2018/11/14.
 */
public class MusicFragment extends BaseFragment {
    private MusicItemAdapter mAdapter;
    @Override
    protected int setLayoutResId() {
        return R.layout.fragment_music;
    }

    @Override
    protected void initData(Bundle bundle) {
        mAdapter=new MusicItemAdapter(mBaseActivity);
    }

    @Override
    protected void initView() {
        XXRecyclerView recyclerView=mRootView.findViewById(R.id.rv_music);
        //layoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(mBaseActivity));
        //adapter
        recyclerView.setAdapter(mAdapter);
        List<Music>musicList=Constant.getMv();
        Collections.shuffle(musicList);
        mAdapter.refreshData(musicList);
        View bannerView=LayoutInflater.from(mBaseActivity).inflate(R.layout.layout_banner,recyclerView,false);
        recyclerView.addHeaderView(bannerView);
        Banner banner=bannerView.findViewById(R.id.banner);
        banner.setImages(Constant.getBannerImgs()).setImageLoader(new GlideImageLoader()).start();
    }

    @Override
    protected void setListener() {
    }
    private class GlideImageLoader extends ImageLoader{
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(mBaseActivity).load(path)
                    .crossFade(1200)
                    .error(R.drawable.ic_bili)
                    .into(imageView);
        }
    }
}
