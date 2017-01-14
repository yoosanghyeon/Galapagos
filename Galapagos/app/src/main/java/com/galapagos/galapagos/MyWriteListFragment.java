package com.galapagos.galapagos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.galapagos.galapagos.common.NetworkDefineConstant;
import com.galapagos.galapagos.common.OkHttpManager;
import com.galapagos.galapagos.common.PropertyManager;
import com.galapagos.galapagos.valueobject.StarButtonValue;
import com.galapagos.galapagos.valueobject.IsLikeRevisionValue;
import com.galapagos.galapagos.valueobject.MyWriteJSONParser;
import com.galapagos.galapagos.valueobject.MyWriteValue;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.util.Log.e;

public class MyWriteListFragment extends Fragment { //내가 작성한 게시글 화면
    MyWriteListAdapter mAdapter;
    RecyclerView recyclerView;

    private StarButtonValue starValue;
    private IsLikeRevisionValue isLikeRevisionValue;

    String boardId;
    String userId;

    public static MyWriteListFragment newInstance(){
        MyWriteListFragment fragment = new MyWriteListFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new MyWriteListAdapter(getContext());

        userId = PropertyManager.getInstance().getUserId();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (starValue == null) {
            starValue = new StarButtonValue();
        }
        if(isLikeRevisionValue == null){
            isLikeRevisionValue = new IsLikeRevisionValue();
        }
        mAdapter = new MyWriteListAdapter(getContext());
        recyclerView = (RecyclerView)inflater.inflate(R.layout.favorite_recyclerview,container,false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);
        mAdapter.setMyWriteListClickListener(new MyWriteListAdapter.onMyWriteListClickListener() {
            @Override
            public void onMyWriteItemClicked(final MyWriteListAdapter.ViewHolder holder, View view, MyWriteValue temp, final int position) {
                view.setOnClickListener(new View.OnClickListener() {
                    final MyWriteValue value = mAdapter.findMyWriteValue(position);
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.btn_write_comment:
                                Intent intent = new Intent(getContext(), CommentActivity.class);
                                intent.putExtra("boardUserId", value.boardUserId);
                                intent.putExtra("boardTag", value.boardTag);
                                intent.putExtra("boardId", value.boardId);
                                startActivity(intent);
                                break;
                            case R.id.btn_write_modify:
                                Intent modifyIntent = new Intent(getContext(), ModifyActivity.class);
                                modifyIntent.putExtra("boardId", value.boardId);
                                modifyIntent.putExtra("boarditemKey",value.boarditemKey);
                                startActivity(modifyIntent);
                                Toast.makeText(getContext(),"수정하기 버튼 눌렸음!!",Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.btn_write_delete:
                                Log.e("value.boardId", "" + value.boardId);
                                boardId = value.boardId;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        deleteMyWriteList();
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getContext(),"게시글이 삭제 되었습니다. 새로고침 하세요",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).start();
                                break;
                            case R.id.img_write_board_pic1:
                                Intent feedDetailIntentTwo = new Intent(getContext(), FeedDetailActivity.class);
                                feedDetailIntentTwo.putExtra("boardId", value.boardId);
                                feedDetailIntentTwo.putExtra("boardTag",value.boardTag);
                                feedDetailIntentTwo.putExtra("boardUserId",value.boardUserId);
                                feedDetailIntentTwo.putExtra("boardIsLike",value.boardIsLike);
                                feedDetailIntentTwo.putExtra("boardIsRevision",value.boardIsRevision);
                                feedDetailIntentTwo.putExtra("boardIsStar",value.boardIsStar);
                                startActivity(feedDetailIntentTwo);
                                break;
                            case R.id.text_write_content:
                                Intent feedDetailIntentThree = new Intent(getContext(), FeedDetailActivity.class);
                                feedDetailIntentThree.putExtra("boardId", value.boardId);
                                feedDetailIntentThree.putExtra("boardTag",value.boardTag);
                                feedDetailIntentThree.putExtra("boardUserId",value.boardUserId);
                                feedDetailIntentThree.putExtra("boardIsLike",value.boardIsLike);
                                feedDetailIntentThree.putExtra("boardIsRevision",value.boardIsRevision);
                                feedDetailIntentThree.putExtra("boardIsStar",value.boardIsStar);
                                startActivity(feedDetailIntentThree);
                                break;
                            default:
                                Intent feedDetailIntent = new Intent(getContext(), FeedDetailActivity.class);
                                feedDetailIntent.putExtra("boardId", value.boardId);
                                feedDetailIntent.putExtra("boardTag",value.boardTag);
                                feedDetailIntent.putExtra("boardUserId",value.boardUserId);
                                feedDetailIntent.putExtra("boardIsLike",value.boardIsLike);
                                feedDetailIntent.putExtra("boardIsRevision",value.boardIsRevision);
                                feedDetailIntent.putExtra("boardIsStar",value.boardIsStar);
                                startActivity(feedDetailIntent);
                                break;
                        }
                    }
                });
            }
        });
        return recyclerView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //내가 게시한 게시글 받아오는 어싱크 호출
        new AsycTaskMyWriteItem().execute(userId);
    }

    //내가 게시한 게시글들 받아오는 어싱크
    private class AsycTaskMyWriteItem extends AsyncTask<String, Integer, ArrayList<MyWriteValue>> {
        private ProgressDialog progressDialog;
        private Context mContext;
        private MyWriteValue myWriteValue;

        final MediaType pngType = MediaType.parse("image/*");
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected ArrayList<MyWriteValue> doInBackground(String... userID) {

            Log.e("userID", userID[0].toString());
            ArrayList<MyWriteValue> myWriteValues = new ArrayList<>();
            Response response = null;
            OkHttpClient toServer = null;

            RequestBody fileUploadBody = null;

            toServer = OkHttpManager.getOkHttpClient(); //서버로 보내는 중간 아이 생성
            try {
                FormBody formBody = new FormBody.Builder().add("userId", userID[0]).build();

                //요청 세팅
                Request request = new Request.Builder()
                        .url(NetworkDefineConstant.MYWRITE_LIST_POST_URL)
                        .post(formBody)
                        .build();


                response = toServer.newCall(request).execute();

                String message = response.body().string();
                Log.e("MyWriteListResponse", message);

                if (response.isSuccessful()) {

                    JSONObject jsonObject = new JSONObject(message);
                    String msg = jsonObject.getString("msg");
                    Log.e("MyWriteListmsg ", msg);

                    myWriteValues = MyWriteJSONParser.getItemListParsing(message);
                }else{
                    Log.e("MyWriteResponse","error");
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return myWriteValues;
        }

        @Override
        protected void onPostExecute(ArrayList<MyWriteValue> myWriteListValues) {
            if (myWriteListValues == null) {
                return;
            }else {
                mAdapter.additemChart(myWriteListValues);
            }

        }

    }

    //내가 작성한 게시글 삭제하는 함수
    public void deleteMyWriteList() {
        Response response = null;
        OkHttpClient toServer = null;

        RequestBody fileUploadBody = null;

        toServer = OkHttpManager.getOkHttpClient(); //서버로 보내는 중간 아이 생성
        try {
            fileUploadBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("userId", userId)
                    .build();

            Request request = new Request.Builder()
                    .url(String.format(NetworkDefineConstant.MYWRITE_DELETE_POST_URL, boardId))
                    .post(fileUploadBody)
                    .build();

            response = toServer.newCall(request).execute();
            Log.e("response",response.body().string());

            if (response.isSuccessful()) {
                JSONObject jsonObject = new JSONObject(response.body().string());
                String msg = jsonObject.getString("msg");
                Log.e("MyWriteDelete", msg);
            }
            else {
                Log.e("MyWriteDelete","삭제 실패");
            }

        } catch (UnknownHostException une) {
            e("aaa", une.toString());
        } catch (UnsupportedEncodingException uee) {
            e("bbb", uee.toString());
        } catch (Exception e) {
            e("ccc", e.toString());
        } finally {
            if (response != null) {
                response.close(); //3.* 이상에서는 반드시 닫아 준다.
            }
        }
    }

}
