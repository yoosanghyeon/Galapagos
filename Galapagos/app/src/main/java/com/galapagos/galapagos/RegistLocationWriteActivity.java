package com.galapagos.galapagos;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.galapagos.galapagos.common.AppBaseActivity;
import com.galapagos.galapagos.common.NetworkDefineConstant;
import com.galapagos.galapagos.common.OkHttpManager;
import com.galapagos.galapagos.poivalueobject.LocationSeachResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// 글 작성시 주소 검색
public class RegistLocationWriteActivity extends AppBaseActivity implements TextView.OnEditorActionListener {

    // 글작성시 주소 등록
    EditText editLocationWrite;
    RecyclerView locationSearchRecyclerView;
    LocationSerchListviewAdapter locationRecyclerviewAdapter;


    // 위도 ,경도 ,현재 위치 이름
    ArrayList<LocationSeachResult> results;


    public RegistLocationWriteActivity() {
        results = new ArrayList<>();
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

        if (!textView.getText().toString().equals("") || textView.length() > 0) {
            new LocationObtainAsyncTask(this).execute(textView.getText().toString().trim());
            textView.clearComposingText();
            editLocationWrite.setText("");

        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist_location);

        // edit text
        editLocationWrite = (EditText) findViewById(R.id.edit_location_write);
        editLocationWrite.setOnEditorActionListener(this);

        locationSearchRecyclerView = (RecyclerView) findViewById(R.id.lcotion_registr_recyclerview);
        locationRecyclerviewAdapter = new LocationSerchListviewAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        locationSearchRecyclerView.setLayoutManager(layoutManager);
        locationSearchRecyclerView.setAdapter(locationRecyclerviewAdapter);
        locationRecyclerviewAdapter.setOnLocationItemClickListener(new LocationSerchListviewAdapter.onLocationItemClickListener() {
            @Override
            public void onLocationItemClicked(RecyclerView.ViewHolder viewHolder, View view, LocationSeachResult result, int position) {
                Intent dataLocationIntent = new Intent();
                dataLocationIntent.putExtra("locationName", result.locationName);
                dataLocationIntent.putExtra("locationSub", result.locationSub);
                dataLocationIntent.putExtra("locationNum", String.valueOf(result.locationNum));
                setResult(RESULT_OK, dataLocationIntent);
                finish();
            }
        });


    }

    // 주소를 갖고 오기 위한 AsyncTask
    private class LocationObtainAsyncTask extends AsyncTask<String, Integer, ArrayList<LocationSeachResult>> {

        OkHttpClient client;
        Context mContext;
        GalaCustomToast galaCustomToast;

        public LocationObtainAsyncTask(Context mContext) {
            this.mContext = mContext;
            galaCustomToast = new GalaCustomToast(mContext);
        }


        @Override
        protected void onPreExecute() {

            try{
                results = new ArrayList<>();

                if (locationRecyclerviewAdapter.getItemCount() >0){
                    locationRecyclerviewAdapter.itemClaer();
                    locationSearchRecyclerView.removeAllViews();

                }

                if (results.size() > 0){
                    results.clear();
                }
                super.onPreExecute();

            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        protected ArrayList<LocationSeachResult> doInBackground(String... params) {

            // 위도,경도,지역명

            // OKHTTP3 설정 (TimeOut....)
            client = OkHttpManager.getOkHttpClient();
            String tmap_uri_keyword = NetworkDefineConstant.LOCATION_SERACH_REQUEST_URL;
            Response response = null;

            try {

                String url = String.format(tmap_uri_keyword, params[0]);
                Log.e("LocationSerachURL", url);
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                response = client.newCall(request).execute();

                if (response.isSuccessful()) {

                    JSONObject resultData = new JSONObject(response.body().string());
                    String msg = resultData.getString("msg");
                    Log.e("LocationSearch", msg);
                    if (msg.equals("지역 검색 성공")) {

                        JSONArray jsonArray = resultData.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            LocationSeachResult locationSeachResult = new LocationSeachResult();
                            locationSeachResult.locationName = jsonObject.getString("locationName");
                            locationSeachResult.locationSub = jsonObject.getString("locationSub");
                            locationSeachResult.locationNum = jsonObject.getString("locationNum");
                            Log.e("Location_Search", locationSeachResult.locationSub);
                            results.add(locationSeachResult);

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
            return results;
        }

        @Override
        protected void onPostExecute(ArrayList<LocationSeachResult> result) {
            try {
                if (result != null) {
                    locationRecyclerviewAdapter.addAll(result);

                } else {

                    galaCustomToast.showToast("지역 검색이 없습니다.", Toast.LENGTH_SHORT);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
    }


}
