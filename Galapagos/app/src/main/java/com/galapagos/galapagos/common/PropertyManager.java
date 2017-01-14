package com.galapagos.galapagos.common;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.galapagos.galapagos.GalaApplication;


public class PropertyManager {
    // shadrd user 관리 객체

    private static PropertyManager instance;

    public static PropertyManager getInstance() {
        if (instance == null) {
            instance = new PropertyManager();
        }
        return instance;
    }

    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;

    private PropertyManager() {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(GalaApplication.getGalaContext());
        mEditor = mPrefs.edit();
    }

    private static final String FIELD_USER_ID = "userid";
    private static final String FIELD_USER_NICKNAME = "nickname";
    private static final String FIELD_PIC_URI = "picture";
    private static final String FCM_TOKEN_KEY = "fcmTokenKey";
    private static final String FIELD_USER_ITEMKEY = "useritemkey";
    private static final String Latitude = "latitude";
    private static final String Longtitude = "longtitude";

    public void setUserId(String userid) {
        mEditor.putString(FIELD_USER_ID, userid);
        mEditor.commit();
    }

    public String getUserId() {
        return mPrefs.getString(FIELD_USER_ID, "");
    }

    public void setNickName(String nickname) {
        mEditor.putString(FIELD_USER_NICKNAME, nickname);
        mEditor.commit();
    }

    public String getNickName() {
        return mPrefs.getString(FIELD_USER_NICKNAME, "");
    }

    public void setUserItemKey(String itemKey){
        mEditor.putString(FIELD_USER_ITEMKEY, itemKey);
        mEditor.commit();
    }

    public String getUserItemKey(){
        return mPrefs.getString(FIELD_USER_ITEMKEY, "");
    }

    public void setPicUri(String uri) {
        mEditor.putString(FIELD_PIC_URI, uri);
        mEditor.commit();
    }

    public String getPicUri() {
        return mPrefs.getString(FIELD_PIC_URI, "");
    }


    public void setFcmTokenKey(String fcmTokenKey){
        mEditor.putString(FCM_TOKEN_KEY, fcmTokenKey);
        mEditor.commit();
    }

    public  String getFcmTokenKey() {
        return mPrefs.getString(FCM_TOKEN_KEY,"");
    }

    public void setLatitude(String latitude) {
        mEditor.putString(Latitude, latitude);
        mEditor.commit();
    }

    public String getLatitudei() {
        return mPrefs.getString(Latitude , "");
    }

    public void setLongtitude(String longtitude) {
        mEditor.putString(Longtitude, longtitude);
        mEditor.commit();
    }

    public String getLongtitude() {
        return mPrefs.getString(Longtitude , "");
    }
}
