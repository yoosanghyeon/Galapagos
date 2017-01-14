package com.galapagos.galapagos;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.galapagos.galapagos.common.NetworkDefineConstant;
import com.galapagos.galapagos.common.OkHttpManager;
import com.galapagos.galapagos.common.PropertyManager;
import com.galapagos.galapagos.valueobject.StarBoardValue;
import com.galapagos.galapagos.valueobject.StarButtonValue;
import com.galapagos.galapagos.valueobject.StarJSONParser;
import com.galapagos.galapagos.valueobject.IsLikeRevisionValue;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yeji on 2016. 11. 14..
 */

public class StarListFragment extends Fragment { //즐겨찾기 추가목록 화면
    StarListAdapter mAdapter;
    RecyclerView recyclerView;

    public RequestManager mGlideRequestManager;

    private StarButtonValue starValue;
    private IsLikeRevisionValue isLikeRevisionValue;

    String userId;

    public static StarListFragment newInstance() {
        StarListFragment fragment = new StarListFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userId = PropertyManager.getInstance().getUserId();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (starValue == null) {
            starValue = new StarButtonValue();
        }
        if (isLikeRevisionValue == null) {
            isLikeRevisionValue = new IsLikeRevisionValue();
        }

        mAdapter = new StarListAdapter((FavoriteActivity) getActivity());
        recyclerView = (RecyclerView) inflater.inflate(R.layout.favorite_recyclerview, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);
        return recyclerView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //즐겨찾기 추가목록 게시글 받아오는 어싱크 호출
        new AsycTaskStarListItem().execute(userId);
    }

    //즐겨찾기 추가목록 게시글들 받아오는 어싱크
    private class AsycTaskStarListItem extends AsyncTask<String, Integer, ArrayList<StarBoardValue>> {
        private ProgressDialog progressDialog;
        private Context mContext;
        private StarBoardValue feedBoardValue;

        final MediaType pngType = MediaType.parse("image/*");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList<StarBoardValue> doInBackground(String... userID) {

            Log.e("StarListFragmentuserID", userID.toString());
            ArrayList<StarBoardValue> starListBoard = new ArrayList<>();
            Response response = null;
            OkHttpClient toServer = null;

            RequestBody fileUploadBody = null;

            toServer = OkHttpManager.getOkHttpClient(); //서버로 보내는 중간 아이 생성
            try {

                fileUploadBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("userId", userID[0]).build();

                Log.e("MyStarListUrl", NetworkDefineConstant.STAR_LIST_POST_URL);

                //요청 세팅
                Request request = new Request.Builder()
                        .url(NetworkDefineConstant.STAR_LIST_POST_URL)
                        .post(fileUploadBody)
                        .build();


                response = toServer.newCall(request).execute();
                String message = response.body().string(); //response 많이 불러오면 에러남
                Log.e("StarListResponse", message);

                if (response.isSuccessful()) {

                    JSONObject jsonObject = new JSONObject(message);
                    String msg = jsonObject.getString("msg");
                    Log.e("MyStarListmsg", msg);

                    starListBoard = StarJSONParser.getItemChartParsing(message);

                } else {
                    Log.e("StarListRespose", "error");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return starListBoard;
        }

        @Override
        protected void onPostExecute(ArrayList<StarBoardValue> starListBoardValue) {
            super.onPostExecute(starListBoardValue);

            if (starListBoardValue != null) {//데이터가 잘 들어와서 널이 아니면 화면에 보여줭
                mAdapter.addAllStarListItem(starListBoardValue);
                mAdapter.notifyDataSetChanged();
            } else {
                mAdapter.addStarListItem(null);
                mAdapter.notifyDataSetChanged();
            }
        }


    }


}
