package com.galapagos.galapagos;

import android.app.Application;
import android.content.Context;

/**
 * Created by pyoinsoo on 2016-12-01.
 */

public class GalaApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }
    public static Context getGalaContext(){
        return mContext;
    }
}
