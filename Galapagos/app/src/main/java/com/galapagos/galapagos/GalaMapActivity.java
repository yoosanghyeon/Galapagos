package com.galapagos.galapagos;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.galapagos.galapagos.common.AppBaseActivity;
import com.galapagos.galapagos.common.GPSDefineConstant;
import com.galapagos.galapagos.common.GalaPermissionCheck;
import com.galapagos.galapagos.common.GalaPoiCategory;
import com.galapagos.galapagos.common.NetworkDefineConstant;
import com.galapagos.galapagos.common.NetworkManager;
import com.galapagos.galapagos.common.OkHttpManager;
import com.galapagos.galapagos.common.PropertyManager;
import com.galapagos.galapagos.poivalueobject.GalaGpsValue;
import com.galapagos.galapagos.poivalueobject.GalaPOI;
import com.galapagos.galapagos.poivalueobject.POI;
import com.galapagos.galapagos.poivalueobject.SearchPOIInfo;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.galapagos.galapagos.common.NetworkDefineConstant.GALA_COMMON_URL;


/**
 * Created by Sanghyeon on 2016. 11. 14..
 */

public class GalaMapActivity extends AppBaseActivity implements OnMapReadyCallback, View.OnClickListener,
        TextWatcher, AdapterView.OnItemClickListener, TextView.OnEditorActionListener, GoogleMap.OnMyLocationButtonClickListener
        , CompoundButton.OnCheckedChangeListener {

    // category select
    private static final int CAT_SLECTED_REQUEST = 401;

    // GPS 정보를 받아 올 동안의 다이얼로그 생성
    ProgressDialog mapProgressDialog;

    // Bottom Tab Btn
    ImageButton feedButton;
    ImageButton mapButton;


    ImageButton favoriteButton;

    // Navigation Btn
    ImageButton galaMapNaviBtn;
    DrawerLayout galaMapDrawer;

    // Navi item menu
    LinearLayout notifi;
    LinearLayout profileInfo;
    LinearLayout licenseInfo;
    LinearLayout question;
    SwitchCompat btnNaviPsuh;


    // 티맵 자동검색
    AutoCompleteTextView autoTextLocationSerch;
    EditText editContentsSerch;

    ArrayAdapter<POI> autoTextCompleteAdapter;


    // GoogleMap
    MapView galaMapView;
    GoogleMap mMap;


    // 마커에 대한 POI정보
    // POI정보에 대한 마커정보
    // Serivce intent (현재 위치를 찾는 서비스를 구동시킬것)
    Intent intentService;


    private boolean mResolvingError = false;
    public static int REQUST_RESOLVE_ERROR = 100;


    // 네비게이션 헤더뷰
    View headerView;

    //마커에 대한 POI정보
    HashMap<Marker, GalaPOI> poiResolver = new HashMap<>();


    // 위도 경도
    Double USER_GPS_LONGTITUDE;
    Double USER_GPS_LATITUDE;

    InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galamap_layout);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // 이벤트 등록
        GpsBusProvider.getInstance().register(this);

        USER_GPS_LONGTITUDE = Double.valueOf(PropertyManager.getInstance().getLongtitude());
        USER_GPS_LATITUDE = Double.valueOf(PropertyManager.getInstance().getLatitudei());

        mapProgressDialog = new ProgressDialog(this);
        mapProgressDialog.setMessage("GPS 정보 받아 오는중");
        mapProgressDialog.setCancelable(false);


        // NavigationView item intiallaze
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        // view id,event init
        feedButton = (ImageButton) findViewById(R.id.btn_feed_tab);
        mapButton = (ImageButton) findViewById(R.id.btn_map_tab);
        favoriteButton = (ImageButton) findViewById(R.id.btn_favorite_tab);
        galaMapNaviBtn = (ImageButton) findViewById(R.id.btn_navi);
        galaMapDrawer = (DrawerLayout) findViewById(R.id.drawer_grlamap);

        feedButton.setOnClickListener(this);
        favoriteButton.setOnClickListener(this);
        galaMapNaviBtn.setOnClickListener(this);

        feedButton.setImageResource(R.drawable.feed_tapbar_icon_un);
        mapButton.setImageResource(R.drawable.local_search_tapbar_icon_use);
        favoriteButton.setImageResource(R.drawable.star_search_tapbar_icon_un);

        // editText intiallaze
        autoTextLocationSerch = (AutoCompleteTextView) findViewById(R.id.edit_location_search);
        editContentsSerch = (EditText) findViewById(R.id.edit_contents_search);

        // AutoTextComplete 사용될 adapter
        autoTextCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        autoTextLocationSerch.setAdapter(autoTextCompleteAdapter);
        autoTextLocationSerch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                if (keyEvent.getAction()== KeyEvent.KEYCODE_ENTER && textView){
                inputMethodManager.hideSoftInputFromWindow(autoTextLocationSerch.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                    return true;
//                }
                return false;
            }
        });

        autoTextLocationSerch.addTextChangedListener(this);

        // 네비게이션 헤더뷰 설정
        headerView = navigationView.getHeaderView(0);
        notifi = (LinearLayout) headerView.findViewById(R.id.notifi_layout);
        profileInfo = (LinearLayout) headerView.findViewById(R.id.profileInfo_layout);
        licenseInfo = (LinearLayout) headerView.findViewById(R.id.licenseInfo_layout);
        question = (LinearLayout) headerView.findViewById(R.id.question_layout);
        notifi.setOnClickListener(this);
        profileInfo.setOnClickListener(this);
        licenseInfo.setOnClickListener(this);
        question.setOnClickListener(this);

        btnNaviPsuh = (SwitchCompat) headerView.findViewById(R.id.btn_navi_push);
        btnNaviPsuh.setOnCheckedChangeListener(this);


        // bundle get Error check
        mResolvingError = savedInstanceState != null &&
                savedInstanceState.getBoolean("STATE_RESOLVING_ERROR", false);

        try {
            // 맵뷰 설정
            galaMapView = (MapView) findViewById(R.id.gala_mepview);
            galaMapView.onCreate(savedInstanceState);
            galaMapView.getMapAsync(this);
            MapsInitializer.initialize(getApplicationContext());

        } catch (Exception e) {
            e.printStackTrace();
        }

        setUserNaviProfile();

        editContentsSerch.setOnEditorActionListener(this);
        // 에디터 검색
        autoTextLocationSerch.setOnItemClickListener(this);

        // 처음 시작인지 아닌지 확인
        if (GPSDefineConstant.FRIST_START_LOCATION_SERVICE) {

            // 권한 허용 체크 후 위치 허용
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Boolean isGpsEnabled = ((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (isGpsEnabled) {
                    // 위치정보 서비스 시작

                    locationService();
                    mapProgressDialog.show();

                } else {
                    // 사용하지 않는다면 설정창으로 이동
                    Intent locationPermissionIntent = new Intent(GalaMapActivity.this, LocationPermissionActivity.class);
                    startActivity(locationPermissionIntent);
                    return;
                }
            } else {
                GalaPermissionCheck.getInstance().requestLocationPermission(this);
                onCreate(null);
            }


        } else {
            Log.e("위치서비스 시작 안함", "위치서비스 시작 안함");
            new GetGalaMarkerAsyncTask().execute(
                    new String[]{String.valueOf(USER_GPS_LONGTITUDE),
                            String.valueOf(USER_GPS_LATITUDE)});
//            setTmapPoiMarket(USER_GPS_LATITUDE, USER_GPS_LONGTITUDE);
            doReverseGeoconding(USER_GPS_LATITUDE, USER_GPS_LONGTITUDE);

        }


    }


    // 서치바 상단 컨텐츠 필터 검색
    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

        if (!textView.getText().toString().equals("") || textView.length() > 0) {
            mMap.clear();
            String[] strings = {java.lang.String.valueOf(USER_GPS_LONGTITUDE),
                    java.lang.String.valueOf(USER_GPS_LATITUDE), textView.getText().toString().trim()};
            new GetGalaMarkerAsyncTask().execute(strings);
            inputMethodManager.hideSoftInputFromWindow(autoTextLocationSerch.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        return true;

    }


    // 푸쉬 셋팅
    @Override
    public void onCheckedChanged(final CompoundButton compoundButton, boolean isChecked) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                OkHttpClient client = OkHttpManager.getOkHttpClient();
                String url = NetworkDefineConstant.GALA_PSUH_SETTING;
                Response response = null;


                try {
                    RequestBody formBody = new FormBody.Builder()
                            .add("userId", PropertyManager.getInstance().getUserId())
                            .build();

                    Request request = new Request.Builder() //데이터 받아서 오기
                            .url(url)
                            .post(formBody)
                            .build();

                    response = client.newCall(request).execute(); //catch exception 안해주면 빨간줄
                    JSONObject liekJsonObject = new JSONObject(response.body().string());
                    String msg = liekJsonObject.optString("msg", "fail");

                    Log.e("PsuhSettingMsg", msg);
                    if (msg.equals("pushOn")) {
                        compoundButton.post(new Runnable() {
                            @Override
                            public void run() {
                                compoundButton.setChecked(true);
                            }
                        });
                    } else if (msg.equals("pushOff")) {
                        compoundButton.post(new Runnable() {
                            @Override
                            public void run() {
                                compoundButton.setChecked(false);
                            }
                        });

                    } else {
                        compoundButton.post(new Runnable() {
                            @Override
                            public void run() {
                                compoundButton.setChecked(false);
                            }
                        });
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (response != null) {
                        response.close();
                    }
                }

            }
        }).start();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_feed_tab:
                Intent feedIntent = new Intent(getApplicationContext(), FeedActivity.class);
                startActivity(feedIntent);
                finish();
                overridePendingTransition(R.anim.slide_up, R.anim.stay);
                break;
            case R.id.btn_favorite_tab:
                Intent faviorteIntent = new Intent(getApplicationContext(), FavoriteActivity.class);
                startActivity(faviorteIntent);
                finish();
                overridePendingTransition(R.anim.slide_up, R.anim.stay);
                break;
            case R.id.btn_navi:
                galaMapDrawer.openDrawer(Gravity.RIGHT);
                break;
            case R.id.notifi_layout:
                Intent notifiDialogIntent = new Intent(getApplicationContext(), NotifiDialogActivity.class);
                startActivity(notifiDialogIntent);
                break;
            case R.id.profileInfo_layout:
                Intent profileInfoDialogIntent = new Intent(getApplicationContext(), ProfileInfoDialogActivity.class);
                startActivity(profileInfoDialogIntent);
                break;
            case R.id.licenseInfo_layout:
                Intent licenseInfoDialogIntent = new Intent(getApplicationContext(), LicenseInfoDialogActivity.class);
                startActivity(licenseInfoDialogIntent);
                break;
            case R.id.question_layout:
                Intent questionDialogIntent = new Intent(getApplicationContext(), QuestionDialogActivity.class);
                startActivity(questionDialogIntent);
                break;

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        galaMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        galaMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        // 서비스
        Intent intent = new Intent(this, GalaLocationService.class);
        stopService(intent);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        galaMapView.onDestroy();
        // 이벤트 등록해제
        GpsBusProvider.getInstance().unregister(this);
        GPSDefineConstant.FRIST_START_LOCATION_SERVICE = true;

        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        galaMapView.onLowMemory();
        super.onLowMemory();
    }


    // 네비게이션 뷰 유저 프로필 셋팅

    private void setUserNaviProfile() {
        CircleImageView naviUserPic = (CircleImageView) headerView.findViewById(R.id.navi_user_pic);
        TextView naviUserName = (TextView) headerView.findViewById(R.id.navi_user_name);
        TextView naviUserLocation = (TextView) headerView.findViewById(R.id.navi_user_location);


        naviUserName.setText(PropertyManager.getInstance().getNickName());
        naviUserLocation.setText("설정 지역 없음");
        Glide.with(this).load(PropertyManager.getInstance().getPicUri()).thumbnail(0.5f).into(naviUserPic);
    }


    // 서비스 시작
    private void locationService() {
        intentService = new Intent(GPSDefineConstant.GPS_SERVICE_ACTION_NAME);
        intentService.setPackage(GPSDefineConstant.GALA_PACKAGE_PATH);
        startService(intentService);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        locationService();
        return true;
    }

    // Google Connect Failed FragmentDialog
    private void showErrorDialog(int errorCode) {
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");

    }

    // Google GeoCoding 활용해 serchbar edittext에 현재 위치 힌트 설정
    private String doReverseGeoconding(double latitude, double longitude) {
        String myLocation = null;
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(this, Locale.KOREA);
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                myLocation = addresses.get(0).getThoroughfare();
                autoTextLocationSerch.setHint("현재위치 " + myLocation);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("GEOCODING_ERROR", "지오코딩 구현 안됨");
        }
        return myLocation;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (requestCode == RESULT_OK) {
            }
        }

    }

    double pressedTime;

    @Override
    public void onBackPressed() {
        if (galaMapDrawer.isDrawerOpen(Gravity.RIGHT)) {
            galaMapDrawer.closeDrawer(Gravity.RIGHT);
        } else if (pressedTime == 0) {
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

    // 지도 설정
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);


        // InfoWindowAdapter 설정
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(this, poiResolver));
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (marker.getTitle().length() > 7) {
                    Intent intent = new Intent(GalaMapActivity.this, FeedDetailActivity.class);
                    intent.putExtra("boardId", marker.getTitle());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.stay);

                }
            }
        });
        if (!GPSDefineConstant.FRIST_START_LOCATION_SERVICE) {
            moveMap(new LatLng(USER_GPS_LATITUDE, USER_GPS_LONGTITUDE));
        }

    }

    public void addGalaMarker(GalaPOI poi) {
        MarkerOptions options = new MarkerOptions();
        LatLng latLng = new LatLng(poi.boardLatitude, poi.boardLongtitude);
        options.position(latLng);
        BitmapDescriptor bitmap = null;
        switch (poi.boardCategory) {
            case GalaPoiCategory.GalaPoiCategoryOthers:
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.map_icon_cartegory0);
                break;
            case GalaPoiCategory.GalaPoiCategoryFood:
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.map_icon_cartegory1);
                break;
            case GalaPoiCategory.GalaPoiCategoryPlay:
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.map_icon_cartegory2);
                break;
            case GalaPoiCategory.GalaPoiCategoryOutLocation:
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.map_icon_cartegory3);
                break;
            case GalaPoiCategory.GalaPoiCategoryDeal:
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.map_icon_cartegory4);
                break;
            case GalaPoiCategory.GalaPoiCategoryMetting:
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.map_icon_cartegory5);
                break;
            case GalaPoiCategory.GalaPoiCategorySuggestion:
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.map_icon_cartegory6);
                break;
            default:
                // 디폴트는 디맵 마커
                bitmap = BitmapDescriptorFactory.defaultMarker();
                break;
        }

        options.icon(bitmap);
        options.anchor(0.5f, 1);
        options.title(poi.boardId);
        options.snippet(poi.boardContent);
        Marker marker = mMap.addMarker(options);
        poiResolver.put(marker, poi);

    }

    private void moveMap(LatLng latLng) {
        if (mMap != null) {
            CameraPosition.Builder builder = new CameraPosition.Builder();
            builder.target(latLng);
            builder.zoom(17.5f);
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(builder.build());
            mMap.animateCamera(update);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean("STATE_RESOLVING_ERROR", mResolvingError);
        galaMapView.onSaveInstanceState(outState);
    }

    public static final String DIALOG_ERROR = "dialog_error";


    // 에러 발생시 나올 화면
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance()
                    .getErrorDialog(this.getActivity(), errorCode, REQUST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
        }
    }


/*    private void setTmapPoiMarket(Double latitude, Double longtitude) {

        LatLng latLng = new LatLng(latitude, longtitude);
        try {
            NetworkManager.getInstance().getTMapSearchPOIMarker(getApplicationContext(), latLng,
                    new NetworkManager.OnResultListener<SearchPOIInfo>() {
                        @Override
                        public void onSuccess(Request request, SearchPOIInfo result) {
                            if (result != null)
                                for (POI poi : result.pois.poiList) {
                                    GalaPOI galaPOI = new GalaPOI();
                                    galaPOI.boardLat = poi.getLatitude();
                                    galaPOI.boardLong = poi.getLongitude();
                                    // 티맵포이로 카테고리 설정
                                    galaPOI.boardCategory = GalaPoiCategory.GalaPoiCategoryTmap;
                                    ;
                                    galaPOI.boardUserName = poi.name;
                                    galaPOI.boardContent = poi.name + " " + poi.getAddress();
                                    galaPOI.boardId = "1";
                                    addGalaMarker(galaPOI);
                                }
                        }

                        @Override
                        public void onFail(Request request, IOException exception) {
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }


    }*/

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String keyword = editable.toString();
        try {
            NetworkManager.getInstance().getTMapSearchPOI(getApplicationContext(), keyword, new NetworkManager.OnResultListener<SearchPOIInfo>() {
                @Override
                public void onSuccess(Request request, SearchPOIInfo result) {
                    if (result != null) {

                        for (POI p : result.pois.poiList) {

                            Log.e("aftertextChanged", p.getAddress());
                            autoTextCompleteAdapter.add(p);
                            autoTextCompleteAdapter.notifyDataSetChanged();
                        }
                    }

                }

                @Override
                public void onFail(Request request, IOException exception) {

                }
            });

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        POI poi = autoTextCompleteAdapter.getItem(i);
        moveMap(new LatLng(poi.getLatitude(), poi.getLongitude()));
//        setTmapPoiMarket(poi.getLatitude(), poi.getLongitude());
        String[] searchPoiLocation = {String.valueOf(poi.getLongitude()), String.valueOf(poi.getLatitude())};
        new GetGalaMarkerAsyncTask().execute(searchPoiLocation);
        Log.e("onItemClick", poi.name);

    }


    // 이벤트가 발생한뒤 수행할 작업
    @Subscribe
    public void finishGPSLoad(GalaGpsValue value) {
        mapProgressDialog.dismiss();
        doReverseGeoconding(value.getLatitudeValue(), value.getLongtitudeValue());
        new GetGalaMarkerAsyncTask().execute(
                new String[]{String.valueOf(value.getLongtitudeValue()),
                        String.valueOf(value.getLatitudeValue())});
//        setTmapPoiMarket(value.getLatitudeValue(), value.getLongtitudeValue());
        moveMap(new LatLng(value.getLatitudeValue(), value.getLongtitudeValue()));


        if (isMyServiceRunning(GalaLocationService.class)) {
            Intent intent = new Intent(GPSDefineConstant.GPS_SERVICE_ACTION_NAME);
            intent.setPackage(GPSDefineConstant.GALA_PACKAGE_PATH);
            stopService(intent);
        }
    }


    private static final String GALA_MAP_MARKER_URI = GALA_COMMON_URL + "/boards/location/%s/%s/Circle";

    // 갈라파고스 게시물 마커를 갖고 오기 위한 AsyncTask
    private class GetGalaMarkerAsyncTask extends AsyncTask<String, Integer, ArrayList<GalaPOI>> {

        okhttp3.OkHttpClient client;

        ArrayList<GalaPOI> galaPOIs;

        @Override
        protected ArrayList<GalaPOI> doInBackground(String... strings) {

            client = OkHttpManager.getOkHttpClient();

            galaPOIs = new ArrayList<>();

            Response response = null;

            String url = String.format(GALA_MAP_MARKER_URI, strings[0], strings[1]);
            if (strings.length >= 3) {
                String mapContentUrl = GALA_COMMON_URL + "/boards/location/%s/%s/Circle/?boardContent=%s";
                url = String.format(mapContentUrl, strings[0], strings[1], strings[2]);
            }

            Log.e("URI_URI", url);
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONObject searchPoiInfo = jsonObject.getJSONObject("searchPoiInfo");
                    String msg = searchPoiInfo.getString("msg");
                    Log.e("msg", msg);
                    if (msg.equals("find success")) {
                        JSONObject pois = searchPoiInfo.getJSONObject("data");
                        JSONArray poi = pois.getJSONArray("detail");
                        for (int i = 0; i < poi.length(); i++) {
                            JSONObject poiOne = poi.getJSONObject(i);
                            GalaPOI galaPOI = new GalaPOI();
                            galaPOI.boardUserId = poiOne.getString("boardUserId");
                            galaPOI.boardUserName = poiOne.getString("boardUserNicName");
                            galaPOI.boardCategory = poiOne.getInt("boardCategory");
                            galaPOI.boardContent = poiOne.getString("boardContent");
                            galaPOI.boardLatitude = poiOne.getDouble("boardLat");
                            galaPOI.boardLongtitude = poiOne.getDouble("boardLong");
                            galaPOI.boardId = poiOne.getString("_id");

                            galaPOIs.add(galaPOI);
                            Log.e("GalaPoi", galaPOI.toString());
                        }

                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (response != null) {
                    response.close();
                }
            }

            return galaPOIs;
        }


        @Override
        protected void onPostExecute(ArrayList<GalaPOI> galaPOIs) {

            if (galaPOIs != null) {

                // value 저장
                LinkedHashMap<String, GalaPOI> galaPOILinkedHashMap = new LinkedHashMap<>();
                // key 저장, set은 중복으로 저장되지 않는다.
                HashSet<String> strings = new HashSet<>();

                // Map, key 저장
                for (GalaPOI g : galaPOIs) {
                    galaPOILinkedHashMap.put(g.boardLatitude.toString() + g.boardLongtitude.toString(), g);
                    strings.add(g.boardLatitude.toString() + g.boardLongtitude.toString());
                    Log.e("StringLog", g.boardLatitude.toString() + g.boardLongtitude.toString());
                }


                for (String str : strings) {
                    addGalaMarker(galaPOILinkedHashMap.get(str));
                }

            }


            // 반복적으로 마크 그림
            super.onPostExecute(galaPOIs);
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

}
