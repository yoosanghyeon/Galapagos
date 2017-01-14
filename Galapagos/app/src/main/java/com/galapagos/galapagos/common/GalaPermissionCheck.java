package com.galapagos.galapagos.common;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.galapagos.galapagos.FeedActivity;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

/**
 * Created by Arous on 2016-11-21.
 */

public class GalaPermissionCheck {

    // SingleTon 생성
    private static GalaPermissionCheck instance;


    public static GalaPermissionCheck getInstance() {
        if (instance == null) {
            instance = new GalaPermissionCheck();
        }

        return instance;
    }


    String servierAction = "com.galapagos.galapagos.action.PER_LOCATION";

    // 피미션 체크 및 비 설정시 지속적으로 권한 설정창으로 이동
    public void requestLocationPermission(final AppBaseActivity permissionActivity) {


        if (ContextCompat.checkSelfPermission(permissionActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            new TedPermission(permissionActivity)
                    .setPermissionListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            try {
                                Intent intent = new Intent(permissionActivity, FeedActivity.class);
                                intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                                permissionActivity.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onPermissionDenied(ArrayList<String> arrayList) {
                            Toast.makeText(permissionActivity, "권한 거부\n", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setRationaleMessage("이 앱을 실행하기 위해서는 위치확인 권한이 필요로 합니다..")
                    .setDeniedMessage("이 앱은 필수적으로 권한을 요청 합니다. [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                    .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                    .setGotoSettingButton(true)
                    .check();

        }

    }

    public boolean requsetPicturePermission(final AppBaseActivity permissionActivity) {

        if (ContextCompat.checkSelfPermission(permissionActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            new TedPermission(permissionActivity)
                    .setPermissionListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted() {

                        }

                        @Override
                        public void onPermissionDenied(ArrayList<String> arrayList) {
                            Toast.makeText(permissionActivity, "권한 거부\n", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setDeniedMessage("사진 기능을 사용하실려면 권한을 허락해주세요..")
                    .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .setGotoSettingButton(true)
                    .check();
        }

        return true;
    }


}


