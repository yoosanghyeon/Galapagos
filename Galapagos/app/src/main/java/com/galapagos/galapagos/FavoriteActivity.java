package com.galapagos.galapagos;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.galapagos.galapagos.common.AppBaseActivity;
import com.galapagos.galapagos.common.GPSDefineConstant;

import static com.galapagos.galapagos.R.id.btn_feed_tab;

/**
 * Created by yeji on 2016. 11. 14..
 */

public class FavoriteActivity extends AppBaseActivity implements View.OnClickListener {

    // Bottom Image Button
    ImageButton feedButton;
    ImageButton mapButton;
    ImageButton favoriteButton;

    // favorite view tab && viewpager
    TabLayout tabLayout;
    ViewPager viewPager;
    FavoriteFragmentPagerAdapter favoriteFragmentPagerAdapter;

    static RelativeLayout bottomTab;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_layout);

//        checkPermission()

        // BottomTab
        bottomTab = (RelativeLayout) findViewById(R.id.bottomtab_relative_layout);
        feedButton = (ImageButton) findViewById(R.id.btn_feed_tab);
        mapButton = (ImageButton) findViewById(R.id.btn_map_tab);
        favoriteButton = (ImageButton) findViewById(R.id.btn_favorite_tab);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        favoriteFragmentPagerAdapter = new FavoriteFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(favoriteFragmentPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager); //탭과 뷰페이저 연동

        // Bottom ImageButton ClickListener
        mapButton.setOnClickListener(this);
        feedButton.setOnClickListener(this);


        //bottom tab 버튼 이미지 바뀌는 부분
        feedButton.setImageResource(R.drawable.feed_tapbar_icon_un);
        mapButton.setImageResource(R.drawable.local_search_tapbar_icon_un);
        favoriteButton.setImageResource(R.drawable.star_search_tapbar_icon_use);
    }

    @Override
    protected void onDestroy() {


        if (isMyServiceRunning(GalaLocationService.class)) {
            Intent intent = new Intent(GPSDefineConstant.GPS_SERVICE_ACTION_NAME);
            intent.setPackage(GPSDefineConstant.GALA_PACKAGE_PATH);
            stopService(intent);
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_map_tab:
                Intent galaMapIntent = new Intent(getApplicationContext(), GalaMapActivity.class);
                startActivity(galaMapIntent);
                finish();
                overridePendingTransition(R.anim.slide_up, R.anim.stay);
                break;
            case btn_feed_tab:
                Intent feedIntent = new Intent(getApplicationContext(), FeedActivity.class);
                startActivity(feedIntent);
                finish();
                overridePendingTransition(R.anim.slide_up, R.anim.stay);
                break;
        }
    }

    public static void tabtranslate(int scroll){
        if(scroll>0) {
            bottomTab.setVisibility(View.GONE);

        }
        else{
            bottomTab.setVisibility(View.VISIBLE);
        }
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //백버튼 눌렀을때

    double pressedTime;
    @Override
    public void onBackPressed() {
        if (pressedTime == 0) {
            Toast.makeText(getApplicationContext(), "한번 더 누르시면 앱이 종료됩니다", Toast.LENGTH_SHORT).show();
            pressedTime = System.currentTimeMillis();

        } else {
            int seconds = (int) (System.currentTimeMillis() - pressedTime);

            if (seconds > 2000) {
                Toast.makeText(getApplicationContext(), "한번 더 누르시면 앱이 종료됩니다", Toast.LENGTH_SHORT).show();
                pressedTime = 0;
            } else {
                super.onBackPressed();
            }
        }
    }

}
