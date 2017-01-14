package com.galapagos.galapagos.common;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.galapagos.galapagos.poivalueobject.AddressInfo;
import com.galapagos.galapagos.poivalueobject.AddressInfoResult;
import com.galapagos.galapagos.poivalueobject.PoiSearchResult;
import com.galapagos.galapagos.poivalueobject.SearchPOIInfo;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *example
 */

public class NetworkManager {


    private static NetworkManager instance;
    //SingleTon 패턴을 이용하여 현재앱에서 하나만 생성한다.
    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    private static final int DEFAULT_CACHE_SIZE = 50 * 1024 * 1024;
    private static final String DEFAULT_CACHE_DIR = "miniapp";
    private static final String TMAP_API_KEYS = "ba4e819d-b1e0-37e4-a59d-195b731c823d";

    OkHttpClient mClient;

    private NetworkManager() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(15, TimeUnit.SECONDS);
        builder.writeTimeout(15, TimeUnit.SECONDS);

        mClient = builder.build();
    }

    //요청/응답결과를 처리하기 위한 이벤트용 인터페이스 선언.
    public interface OnResultListener<T> {
        public void onSuccess(Request request, T result);
        public void onFail(Request request, IOException exception);
    }

    private static final int MESSAGE_SUCCESS = 1;
    private static final int MESSAGE_FAIL = 2;

    //Main Thread를 영역으로 루퍼를 생성함.
    NetworkHandler mHandler = new NetworkHandler(Looper.getMainLooper());

    static class NetworkResult<T> {
        Request request;
        OnResultListener<T> listener;
        IOException excpetion;
        T result;
    }
    //요청 및 응답에 대한 결과를 넘겨주기 위한 핸들러
    class NetworkHandler extends Handler {
        public NetworkHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            NetworkResult result = (NetworkResult)msg.obj;
            switch (msg.what) {
                case MESSAGE_SUCCESS :
                    result.listener.onSuccess(result.request, result.result);
                    break;
                case MESSAGE_FAIL :
                    result.listener.onFail(result.request, result.excpetion);
                    break;
            }
        }
    }
    //Gson 객체 생성. 그냥 안드로이드 일반파싱을 진행해도 됨
    Gson gson = new Gson();

    private static final String TMAP_SERVER = "https://apis.skplanetx.com";
    private static final String TMAP_REVERSE_GEOCODING = TMAP_SERVER +
            "/tmap/geo/reversegeocoding?coordType=WGS84GEO&addressType=A02&lat=%s&lon=%s&version=1";

    //위도/경도에 대한 주소를 리턴해주는 메소드
    public Request getTMapReverseGeocoding(Object tag, double lat, double lng, OnResultListener<AddressInfo> listener) {
        //URL에 queryString을 세팅
        String url = String.format(TMAP_REVERSE_GEOCODING, "" + lat, ""+lng);
        //OKHttp3 요청객체에 헤더세팅 및
        Request request = new Request.Builder()
                .url(url)
                .header("Accept","application/json")
                .header("appKey",TMAP_API_KEYS)
                .build();

        final NetworkResult<AddressInfo> result = new NetworkResult<>();
        result.request = request;
        result.listener = listener;

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                result.excpetion = e;
                //실패했을 경우 핸들러에 넘겨준다.
                mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_FAIL, result));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    //성공했을 경우
                    String responseBodyValue = response.body().string();
                    Log.e("getTMapReverseGeocoding", responseBodyValue);
                    AddressInfoResult data = gson.fromJson(responseBodyValue, AddressInfoResult.class);
                    result.result = data.addressInfo;
                    //핸들러에 넘겨준다.
                    mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_SUCCESS, result));
                } else {
                    throw new IOException(response.message());
                }
            }
        });
        return request;
    }

    private static final String TMAP_SEARCH_POI_MARKER = TMAP_SERVER +
            "/tmap/pois/search/" +
            "around?centerLon=%s&centerLat=%s&count=30&page=&reqCoordType=WGS84GEO&\n" +
            "callback=&multiPoint=&radius=33&categories=&resCoordType=WGS84GEO&version=1";
    //포이정보를 찾아 리턴해주는 메소드(TMap Rest)
    public Request getTMapSearchPOIMarker(Object tag, LatLng latLng,
                                    OnResultListener<SearchPOIInfo> listener) throws UnsupportedEncodingException {

        String url = String.format(TMAP_SEARCH_POI_MARKER, ""+latLng.longitude,""+latLng.latitude);
        Request request = new Request.Builder()
                .url(url)
                .header("Accept","application/json")
                .header("appKey",TMAP_API_KEYS)
                .build();

        final NetworkResult<SearchPOIInfo> result = new NetworkResult<>();
        result.request = request;
        result.listener = listener;

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                result.excpetion = e;
                mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_FAIL, result));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBodyValue = response.body().string();
                    Log.e("getTMapSearchPOI", responseBodyValue);
                    PoiSearchResult data = gson.fromJson(responseBodyValue, PoiSearchResult.class);
                    result.result = data.searchPoiInfo;
                    mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_SUCCESS, result));

                } else {
                    throw new IOException(response.message());
                }
            }
        });
        return request;
    }

    private static final String TMAP_SEARCH_POI = TMAP_SERVER +
            "/tmap/pois?searchKeyword=%s&resCoordType=WGS84GEO&version=1";
    //포이정보를 찾아 리턴해주는 메소드(TMap Rest)
    public Request getTMapSearchPOI(Object tag,
                                    String keyword,
                                    OnResultListener<SearchPOIInfo> listener) throws UnsupportedEncodingException {

        String url = String.format(TMAP_SEARCH_POI, URLEncoder.encode(keyword,"utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .header("Accept","application/json")
                .header("appKey",TMAP_API_KEYS


                )
                .build();

        final NetworkResult<SearchPOIInfo> result = new NetworkResult<>();
        result.request = request;
        result.listener = listener;

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                result.excpetion = e;
                mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_FAIL, result));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBodyValue = response.body().string();
                    Log.e("getTMapSearchPOI", responseBodyValue);
                    PoiSearchResult data = gson.fromJson(responseBodyValue, PoiSearchResult.class);
                    if (data != null){
                        result.result = data.searchPoiInfo;
                    }
                    mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_SUCCESS, result));

                } else {
                    throw new IOException(response.message());
                }
            }
        });
        return request;
    }
}
