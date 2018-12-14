package me.mikasa.music.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.mikasa.music.R;
import me.mikasa.music.fragment.DiscoveryFragment;
import me.mikasa.music.fragment.MineFragment;
import me.mikasa.music.fragment.MusicFragment;
import me.mikasa.music.fragment.PlayBarFragment;
import me.mikasa.music.service.MusicPlayerService;
import woo.mikasa.lib.base.BaseActivity;


public class HomeActivity extends BaseActivity implements View.OnClickListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ViewPager viewPager;
    private List<Fragment>fragmentList=new ArrayList<>(3);
    private TextView mineTitle,musicTitle,discoveryTitle;
    private static long lastPressed=0;
    private ServiceConnection musicConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            //mBinder=(MusicPlayerService.MusicBinder)binder;
            //mBinder.doSomething();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initData() {
        fragmentList.add(new MineFragment());
        fragmentList.add(new MusicFragment());
        fragmentList.add(new DiscoveryFragment());
        Intent musicServiceIntent=new Intent(HomeActivity.this,MusicPlayerService.class);
        bindService(musicServiceIntent,musicConnection,BIND_AUTO_CREATE);
    }

    @Override
    protected void initView() {
        initToolbar();
        drawerLayout=findViewById(R.id.drawerLayout);
        navigationView=findViewById(R.id.navView);
        viewPager=findViewById(R.id.home_vp);
        viewPager.setAdapter(new HomeAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(1);
        musicTitle.setTextSize(18);
        //View headerView = navView.getHeaderView(0);
        //navHeadIv =headerView.findViewById(R.id.nav_head_bg_iv);
        //loadBingPic();
        initPlayBar();
    }
    private void initToolbar(){
        Toolbar toolbar=findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        mineTitle=findViewById(R.id.tv_mine);
        musicTitle=findViewById(R.id.tv_music);
        discoveryTitle=findViewById(R.id.tv_dis);
        mineTitle.setText("我的");
        musicTitle.setText("音乐馆");
        discoveryTitle.setText("发现");
    }
    private void initPlayBar(){
        PlayBarFragment playBarFragment=new PlayBarFragment();
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.replace(R.id.fragment_play_bar,playBarFragment).commit();
    }

    @Override
    protected void initListener() {
        mineTitle.setOnClickListener(this);
        musicTitle.setOnClickListener(this);
        discoveryTitle.setOnClickListener(this);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        mineTitle.setTextSize(18);
                        musicTitle.setTextSize(14);
                        discoveryTitle.setTextSize(14);
                        break;
                    case 1:
                        mineTitle.setTextSize(14);
                        musicTitle.setTextSize(18);
                        discoveryTitle.setTextSize(14);
                        break;
                    case 2:
                        mineTitle.setTextSize(14);
                        musicTitle.setTextSize(14);
                        discoveryTitle.setTextSize(18);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_about_me:
                        showToast("待开发");
                        break;
                    case R.id.nav_logout:
                        showToast("待开发");
                        break;
                }
                closeDrawer();
                return true;
            }
        });
        findViewById(R.id.drawer_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }
    private void closeDrawer(){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(musicConnection);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_mine:
                viewPager.setCurrentItem(0);
                break;
            case R.id.tv_music:
                viewPager.setCurrentItem(1);
                break;
            case R.id.tv_dis:
                viewPager.setCurrentItem(2);
                break;
        }
    }

    class HomeAdapter extends FragmentPagerAdapter{
        HomeAdapter(FragmentManager fm){
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        long current=System.currentTimeMillis();
        if ((current-lastPressed)>2000){
            showToast("再点击一次退出程序");
            lastPressed=current;
        }else {
            finish();
        }
    }
}
