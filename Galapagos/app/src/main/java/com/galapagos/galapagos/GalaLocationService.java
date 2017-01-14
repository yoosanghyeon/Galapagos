package com.galapagos.galapagos;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.galapagos.galapagos.common.PropertyManager;
import com.galapagos.galapagos.poivalueobject.GalaGpsValue;

import static com.galapagos.galapagos.common.GPSDefineConstant.FRIST_START_LOCATION_SERVICE;

public class GalaLocationService extends Service implements LocationListener {


    // 현재 유저의 위치


    // Google Location(위치정보) 클라이언트
    LocationManager locationManager;

    //


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        // BestProvider 획득
        // 로케이션 매니저 획득
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        try {
            locationManager.requestSingleUpdate(getBestMyProvider(), this, getMainLooper());
        } catch (SecurityException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    // BestProvider 정의
    public Criteria getBestMyProvider() {
        Criteria criteria = new Criteria();
        // 정확도
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        // 전력사용정보
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        // 고도 정보
        criteria.setAltitudeRequired(false);
        // 방위 정보
        criteria.setBearingRequired(false);
        // 속도정보
        criteria.setSpeedRequired(false);
        // 비용
        criteria.setCostAllowed(true);

        return criteria;
    }

    @Override
    public void onLocationChanged(Location location) {
        FRIST_START_LOCATION_SERVICE = false;
        // 프리페런스로 저장
        PropertyManager.getInstance().setLatitude(String.valueOf(location.getLatitude()));
        PropertyManager.getInstance().setLongtitude(String.valueOf(location.getLongitude()));
        Log.e("PropertyManagerLocation", PropertyManager.getInstance().getLatitudei());
        Log.e("LCCATION", String.valueOf("latitude :::" + location.getLatitude() + " ::: lotitude ::: " + location.getLatitude()));
        GpsBusProvider.getInstance().post(new GalaGpsValue(location.getLatitude(), location.getLongitude()));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
