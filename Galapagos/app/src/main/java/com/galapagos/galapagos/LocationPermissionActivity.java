package com.galapagos.galapagos;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

//
public class LocationPermissionActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnClose, btnGPSSetting;

    public static final int GPS_LOCATION_REQUEST = 10;

    GalaCustomToast galaCustomToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_permission);

        btnClose = (ImageButton) findViewById(R.id.btn_user_permission_close);
        btnGPSSetting = (ImageButton) findViewById(R.id.btn_request_permission);

        btnClose.setOnClickListener(this);
        btnGPSSetting.setOnClickListener(this);

        galaCustomToast = new GalaCustomToast(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_user_permission_close:
                Toast.makeText(this,"이 앱은 위치 설정을 꼭 필요로 하여 앱이 종료 됩니다.",Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.btn_request_permission:
                final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, GPS_LOCATION_REQUEST);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Boolean isGpsEnabled = ((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGpsEnabled) {
            Intent intent = new Intent(this, FeedActivity.class);
            intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this,"이 앱은 위치 설정을 꼭 필요로 하여 앱이 종료 됩니다.", Toast.LENGTH_SHORT).show();
        }


    }
}
