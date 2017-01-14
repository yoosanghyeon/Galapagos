package com.galapagos.galapagos;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by yeji on 2016. 11. 14..
 */

public class FavoriteFragmentPagerAdapter extends FragmentPagerAdapter {

    public FavoriteFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return StarListFragment.newInstance();
            case 1:
                return MyWriteListFragment.newInstance();
            default:
                return null;

        }
    }
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "즐겨찾기 추가 목록";
            case 1:
                return "내가 작성한 게시물";
            default:
                return null;
        }
    }


    @Override
    public int getCount() {//탭개
        return 2;
    }


}
