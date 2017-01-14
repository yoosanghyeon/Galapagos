package com.galapagos.galapagos;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.galapagos.galapagos.common.AppBaseActivity;
import com.galapagos.galapagos.common.GPSDefineConstant;
import com.galapagos.galapagos.common.GalaPermissionCheck;
import com.galapagos.galapagos.common.NetworkDefineConstant;
import com.galapagos.galapagos.common.NetworkManager;
import com.galapagos.galapagos.common.OkHttpManager;
import com.galapagos.galapagos.common.PropertyManager;
import com.galapagos.galapagos.valueobject.FeedMainBoardValue;
import com.galapagos.galapagos.valueobject.StarButtonValue;
import com.galapagos.galapagos.valueobject.IsLikeRevisionValue;
import com.galapagos.galapagos.poivalueobject.GalaGpsValue;
import com.galapagos.galapagos.poivalueobject.POI;
import com.galapagos.galapagos.poivalueobject.SearchPOIInfo;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.galapagos.galapagos.R.id.btn_fab;
import static com.galapagos.galapagos.R.id.btn_favorite_tab;
import static com.galapagos.galapagos.common.GPSDefineConstant.FRIST_START_LOCATION_SERVICE;
import static com.galapagos.galapagos.common.NetworkDefineConstant.GALA_MAIN_CONTENT_SERARCH;

public class FeedActivity extends AppBaseActivity implements View.OnClickListener, TextWatcher,
        AdapterView.OnItemClickListener, TextView.OnEditorActionListener, CompoundButton.OnCheckedChangeListener { //feed 화면

    // Bottom Tab ImageButton
    ImageButton feedButton;
    ImageButton mapButton;
    ImageButton favoriteButton;

    // Top SerchBar Navi btn
    ImageButton feedNaviBtn;

    // Navigation
    DrawerLayout drawerFeed;

    // Recycler Feed Adater
    FeedListAdapter mAdapter;

    // RecyclerView
    RecyclerView recyclerView;

    // Navi item menu
    LinearLayout notifi;
    LinearLayout profileInfo;
    LinearLayout licenseInfo;
    LinearLayout question;
    SwitchCompat btnNaviPush;

    // Custom Toolbar(Top SerchBar)
    EditText editContentsSerch;
    AutoCompleteTextView autoTextLocationSerch;

    // AutoTextComplete Adater
    ArrayAdapter<POI> autoTextCompleteAdapter;

    FloatingActionButton fab;
    RelativeLayout bottomTab;

    // Serivce Intent
    Intent intentService;


    // 현재 위치명, 위도 경도
    String presentLocation;

    // 네비게이션뷰
    View headerView;


    private StarButtonValue starValue;
    private IsLikeRevisionValue isLikeRevisionValue;

    // 현재 위도 경도 페이징 처리
    String latitude;
    String longtitude;

    public static String SECTION_CONTENT = "noContent";

    // 검색어 저장
    String searchString;

    // 리사이클러뷰 리프레쉬 레이아웃
    RecyclerRefreshLayout recyclerRefreshLayout;

    // 글라이드 전역변수
    public RequestManager mGlideRequestManager;

    private static String SELECTED_URL = NetworkDefineConstant.FEED_SERACH_LOCATION_REQUEST_URL;

    InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_main);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (starValue == null) {
            starValue = new StarButtonValue();
        }
        if (isLikeRevisionValue == null) {
            isLikeRevisionValue = new IsLikeRevisionValue();
        }

        mGlideRequestManager = Glide.with(this);


        // 이벤트을 받을수 있도록 버스 등록
        GpsBusProvider.getInstance().register(this);


        // Bottom tab intiallaze
        feedButton = (ImageButton) findViewById(R.id.btn_feed_tab);
        mapButton = (ImageButton) findViewById(R.id.btn_map_tab);
        favoriteButton = (ImageButton) findViewById(btn_favorite_tab);

        // Top SearchBar Navi btn
        drawerFeed = (DrawerLayout) findViewById(R.id.drawer_feed);
        feedNaviBtn = (ImageButton) findViewById(R.id.btn_navi);

        // Feed RecyclerView Adater
        mAdapter = new FeedListAdapter(FeedActivity.this, mGlideRequestManager);

        // NavigationView item intiallaze
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        // editText intiallaze
        autoTextLocationSerch = (AutoCompleteTextView) findViewById(R.id.edit_location_search);
        editContentsSerch = (EditText) findViewById(R.id.edit_contents_search);

        editContentsSerch.setOnEditorActionListener(this);

        // AutoTextComplete 사용될 adapter
        autoTextCompleteAdapter = new ArrayAdapter<POI>(this, android.R.layout.simple_list_item_1);
        autoTextLocationSerch.setAdapter(autoTextCompleteAdapter);
        autoTextLocationSerch.setOnItemClickListener(this);
        autoTextLocationSerch.addTextChangedListener(this);
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


        // Feed RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.feed_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(FeedActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        LoadMoreScrollListener loadMoreScrollListener = new LoadMoreScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

            }

            @Override
            public void onLoadMore(int page) {
                try {
                    if (SECTION_CONTENT.equals("noContent")) {
                        new FeedMaingAsyncTask(FeedActivity.this).execute(SELECTED_URL,
                                longtitude, latitude, String.valueOf(page));
                    } else {
                        String[] strings = {GALA_MAIN_CONTENT_SERARCH,
                                longtitude, latitude, searchString, String.valueOf(page)};
                        new FeedMaingAsyncTask(FeedActivity.this).execute(strings);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
/*

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener
                (FeedActivity.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (view instanceof ImageView) {
                            return;
                        }
                        if (view instanceof RelativeLayout) {
                            return;
                        }
                        if (view instanceof ImageButton) {
                            return;
                        }
                        if (view instanceof TextView) {
                            return;
                        }

                        Intent feedDetailIntent = new Intent(FeedActivity.this, FeedDetailActivity.class);
                        FeedMainBoardValue boardValue = mAdapter.getFeedItem(position);
                        feedDetailIntent.putExtra("boardId", boardValue.boardId);
                        feedDetailIntent.putExtra("boardTag", boardValue.boardTag);
                        feedDetailIntent.putExtra("boardUserId", boardValue.boardUserId);
                        feedDetailIntent.putExtra("boardIsLike", boardValue.boardIsLike);
                        feedDetailIntent.putExtra("boardIsRevision", boardValue.boardIsRevision);
                        feedDetailIntent.putExtra("boardIsStar", boardValue.boardIsStar);
                        startActivity(feedDetailIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
                        Log.e("FEED_PIC_CLICK", boardValue.toString());
                        Log.e("FEED_POSTION", "FEED :: " + position);

                        return;


                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));
*/


        // Bottom ImageButton ClickListener
        mapButton.setOnClickListener(this);
        favoriteButton.setOnClickListener(this);
        feedNaviBtn.setOnClickListener(this);

        // 헤더뷰
        headerView = navigationView.getHeaderView(0);
        notifi = (LinearLayout) headerView.findViewById(R.id.notifi_layout);
        profileInfo = (LinearLayout) headerView.findViewById(R.id.profileInfo_layout);
        licenseInfo = (LinearLayout) headerView.findViewById(R.id.licenseInfo_layout);
        question = (LinearLayout) headerView.findViewById(R.id.question_layout);
        btnNaviPush = (SwitchCompat) headerView.findViewById(R.id.btn_navi_push);
        notifi.setOnClickListener(this);
        profileInfo.setOnClickListener(this);
        licenseInfo.setOnClickListener(this);
        question.setOnClickListener(this);
        // 헤더뷰 푸쉬버튼 이벤트
        btnNaviPush.setOnCheckedChangeListener(this);

        // 리사이클러 리프레쉬레이아웃
        recyclerRefreshLayout = (RecyclerRefreshLayout) findViewById(R.id.feed_main_refresh_layout);
        recyclerRefreshLayout.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                try {
                    // 상당 스크롤 분기
                    SECTION_CONTENT = "noContent";


                    autoTextLocationSerch.setText("");
                    autoTextLocationSerch.clearFocus();
                    editContentsSerch.setText("");
                    editContentsSerch.clearFocus();
                    locationService();
                    mAdapter.itemClear();
                    recyclerView.removeAllViews();

//                    recyclerRefreshLayout.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("onRefresh()", "error");
                }

            }
        });

        fab = (FloatingActionButton) findViewById(R.id.btn_fab);
        fab.setOnClickListener(this);

        // 피드화면에서 게시글 아이템의 버튼들 클릭했을 때

        bottomTab = (RelativeLayout) findViewById(R.id.bottomtab_relative_layout);


        // 유저 정보 네비뷰 세팅
        setUserNaviProfile();


        // 위치 서비스 처음 시작여부 체크
        if (FRIST_START_LOCATION_SERVICE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


                Boolean isGpsEnabled = ((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (isGpsEnabled) {
                    // 위치정보 서비스 시작
                    locationService();
                } else {
                    // 사용하지 않는다면 설정창으로 이동
                    Intent locationPermissionIntent = new Intent(FeedActivity.this, LocationPermissionActivity.class);
                    locationPermissionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(locationPermissionIntent);
                    return;
                }
            } else {
                GalaPermissionCheck.getInstance().requestLocationPermission(this);
                return;
            }
        } else {
            // 처음 시작 페이지
            String firstPage = "1";
            new FeedMaingAsyncTask(this).execute(NetworkDefineConstant.FEED_SERACH_LOCATION_REQUEST_URL,
                    PropertyManager.getInstance().getLongtitude(), PropertyManager.getInstance().getLatitudei(), firstPage);
            doReverseGeoconding(Double.valueOf(PropertyManager.getInstance().getLatitudei()),
                    Double.valueOf(PropertyManager.getInstance().getLongtitude()));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        // 이벤트 등록해제
        GpsBusProvider.getInstance().unregister(this);
        GPSDefineConstant.FRIST_START_LOCATION_SERVICE = true;

        super.onDestroy();
    }


    double pressedTime;

    //백버튼 누르면 네비드로워 취소되게
    @Override
    public void onBackPressed() {
        if (drawerFeed.isDrawerOpen(Gravity.RIGHT)) {
            drawerFeed.closeDrawer(Gravity.RIGHT);
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

    //메인피드 화면 바텀탭,툴바, 네비메뉴들 클릭
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_map_tab:
                Intent galaMapIntent = new Intent(getApplicationContext(), GalaMapActivity.class);
                startActivity(galaMapIntent);
                finish();
                overridePendingTransition(R.anim.slide_up, R.anim.stay);
                break;
            case btn_favorite_tab:
                Intent favoriteIntent = new Intent(getApplicationContext(), FavoriteActivity.class);
                startActivity(favoriteIntent);
                finish();
                overridePendingTransition(R.anim.slide_up, R.anim.stay);
                break;
            case R.id.btn_navi:
                drawerFeed.openDrawer(Gravity.RIGHT);
                break;
            case btn_fab:
                Intent feedWriteintent = new Intent(getApplicationContext(), FeedWriteActivity.class);
                feedWriteintent.putExtra("presentLocation", presentLocation);
                startActivity(feedWriteintent);
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

    // 상단바 에디트텍스트 이벤트
    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (!textView.getText().toString().equals("") || textView.length() > 0) {

            try {
                SECTION_CONTENT = "content";
                editContentsSerch.clearFocus();
                searchString = textView.getText().toString();
                mAdapter.itemClear();
                new FeedMaingAsyncTask(this).execute(GALA_MAIN_CONTENT_SERARCH,
                        longtitude, latitude, searchString, "1");
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return true;
    }

    // 서비스 시작
    public void locationService() {
        intentService = new Intent(this, GalaLocationService.class);
        startService(intentService);
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
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    // 주소검색 자동완성
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

    // 네비게이션 뷰 유저 프로필 셋팅
    private void setUserNaviProfile() {
        CircleImageView naviUserPic = (CircleImageView) headerView.findViewById(R.id.navi_user_pic);
        TextView naviUserName = (TextView) headerView.findViewById(R.id.navi_user_name);
        TextView naviUserLocation = (TextView) headerView.findViewById(R.id.navi_user_location);


        naviUserName.setText(PropertyManager.getInstance().getNickName());
        naviUserLocation.setText("설정 지역 없음");
        mGlideRequestManager.load(PropertyManager.getInstance().getPicUri()).thumbnail(0.3f).into(naviUserPic);
    }

    // 이벤트가 발생한뒤 수행할 작업
    @Subscribe
    public void finishGPSLoad(GalaGpsValue value) {

        try {
            if (recyclerRefreshLayout.isActivated()) {
                recyclerRefreshLayout.setRefreshing(false);
            }

            latitude = String.valueOf(value.getLatitudeValue());
            longtitude = String.valueOf(value.getLongtitudeValue());
            doReverseGeoconding(value.getLatitudeValue(), value.getLongtitudeValue());
            new FeedMaingAsyncTask(this).execute(NetworkDefineConstant.FEED_SERACH_LOCATION_REQUEST_URL,
                    String.valueOf(value.getLongtitudeValue()), String.valueOf(value.getLatitudeValue()), String.valueOf("1"));

            // 이벤트 종료후 서비스 중지
            if (isMyServiceRunning(GalaLocationService.class)) {
                Intent intent = new Intent("com.galapagos.galapagos.action.PER_LOCATION");
                intent.setPackage("com.galapagos.galapagos");
                stopService(intent);
            }

            recyclerRefreshLayout.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //메인 피드화면 게시글들 받아오는 어싱크
    private class FeedMaingAsyncTask extends AsyncTask<String, Void, ArrayList<FeedMainBoardValue>> {

        Context mContext;

        public FeedMaingAsyncTask(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected void onPreExecute() { //이 함수가 하는 역할
            super.onPreExecute();
            //동그랑땡 다이얼로그 start
            recyclerRefreshLayout.setRefreshing(true);
        }


        @Override
        protected ArrayList<FeedMainBoardValue> doInBackground(String... strings) {

            Response response = null; //응답 담당
            OkHttpClient client; //연결 담당 (서버랑 안드로이드 연결해 주는 아이)
            ArrayList<FeedMainBoardValue> feedDataArray = new ArrayList<>();


            String url = null;

            // param[0] uri, param[1] longtitude, param[2] latitude, param[3] paging
            if (strings.length == 4) {
                url = String.format(strings[0], strings[1], strings[2], strings[3]);
                Log.e("FeedMainUrl", url);
                Log.e("String.Lengt", "" + strings.length);
            } else if (strings.length == 5) {
                // param[0] uri, param[1] longtitude, param[2] latitude, param[3] Search_Edit_Text
                url = String.format(strings[0], strings[1], strings[2], strings[3], strings[4]);
                Log.e("Feed_Serach", url);
            }


            try {
                client = OkHttpManager.getOkHttpClient(); //아이에게 속성 넣어주기

                RequestBody formBody = new FormBody.Builder()
                        .add("userId", PropertyManager.getInstance().getUserId()).build();

                Request request = new Request.Builder() //데이터 받아서 오기
                        .url(url)
                        .post(formBody)
                        .build();

                response = client.newCall(request).execute(); //catch exception 안해주면 빨간줄
                if (response.isSuccessful()) {


                    JSONObject feedMainJson = new JSONObject(response.body().string());
                    String msg = feedMainJson.getString("msg");
                    Log.e("FEED_DATA_PARSING", msg);

                    if (msg.equals("boardList success")) {
                        JSONArray feedDatas = feedMainJson.getJSONArray("data");

                        for (int i = 0; i < feedDatas.length(); i++) {
                            JSONObject feed = feedDatas.getJSONObject(i);
                            FeedMainBoardValue value = new FeedMainBoardValue();

                            value.boardId = feed.optString("_id", "null");
                            value.boardTag = feed.optString("boardTag", "null");
                            value.boardContent = feed.optString("boardContent", "null");
                            value.boardCategory = feed.optInt("boardCategory", 8);
                            value.boardUserNicName = feed.optString("boardUserNicName", "null");
                            value.boardUserId = feed.optString("boardUserId", "null");
                            value.boardCommentCount = feed.optInt("boardCommentCount", 0);
                            value.boardRevisionCount = feed.optInt("boardRevisionCount", 0);
                            value.boardLikeCount = feed.optInt("boardLikeCount", 0);
                            value.boardUserPicture1 = feed.optString("boardContentPicture1", "null");
                            value.boardUserProfilePicture = feed.optString("boardUserPicture", "null");
                            value.boardDate = feed.optString("boardDate", "2016-12");
                            value.boardisAnonymity = feed.optString("boardIsAnomynity", "falsue");


                            value.boardIsLike = feed.optBoolean("boardIsLike", false);
                            value.boardIsRevision = feed.optBoolean("boardIsRevision", false);
                            value.boardIsStar = feed.optBoolean("boardIsStar", false);

                            Log.e("Value", value.toString());
                            feedDataArray.add(value);
                        }

                        return feedDataArray;

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (response != null) {
                    response.close();//
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(ArrayList<FeedMainBoardValue> itemInfo) {
            super.onPostExecute(itemInfo);
            try {
                recyclerRefreshLayout.setRefreshing(false);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
            if (itemInfo != null) {//데이터가 잘 들어와서 널이 아니면 화면에 보여줭
                mAdapter.addiAllFeedItem(itemInfo);
                mAdapter.notifyDataSetChanged();
            } else {
                try {
                    Toast.makeText(mContext,"데이터가 없습니다.",Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }


    // 상단 지역 검색 검색
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mAdapter.itemClear();
        POI poi = autoTextCompleteAdapter.getItem(i);
        Log.e("TopSearch", poi.toString());
        longtitude = String.valueOf(poi.frontLon);
        latitude = String.valueOf(poi.frontLat);
        SELECTED_URL = NetworkDefineConstant.FEED_SERACH_LOCATION_REQUEST_URL;
        new FeedMaingAsyncTask(this).execute(NetworkDefineConstant.FEED_SERACH_LOCATION_REQUEST_URL,
                longtitude, latitude, String.valueOf("1"));
    }


    // 서비스 구동 체크
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

