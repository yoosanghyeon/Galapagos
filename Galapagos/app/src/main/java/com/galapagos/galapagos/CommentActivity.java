package com.galapagos.galapagos;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.galapagos.galapagos.valueobject.CommentGetData;
import com.galapagos.galapagos.valueobject.CommentJSONParser;
import com.galapagos.galapagos.valueobject.CommentSendData;
import com.galapagos.galapagos.common.NetworkDefineConstant;
import com.galapagos.galapagos.common.OkHttpManager;
import com.galapagos.galapagos.common.PropertyManager;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {
    // Recycler Feed Adater
    CommentAdapter mAdapter;

    //내가 댓글 작성하는 부분
    EditText commentContent;
    ImageView commentPostButton;

    // RecyclerView
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManger;

    String boardId;
    String boardTag;
    String boardUserId;

    private CommentSendData commentSendData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_list_layout);

        //댓글 post할 정보들 담을 객체
        if (commentSendData == null) {
            commentSendData = new CommentSendData();
        }

        boardUserId = getIntent().getStringExtra("boardUserId");
        boardId = getIntent().getStringExtra("boardId");
        boardTag = getIntent().getStringExtra("boardTag");
        Log.e("CommentBoardId", boardId);

        // Comment RecyclerView Adater
        mAdapter = new CommentAdapter(getApplicationContext());

        //댓글 작성 EditText 부분
        commentContent = (EditText) findViewById(R.id.edit_comment_content);
        commentPostButton = (ImageView) findViewById(R.id.btn_comment_post);

        commentContent.setOnClickListener(this);
        commentPostButton.setOnClickListener(this);

        // Comment RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.comment_recyclerview);
        linearLayoutManger = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);


        recyclerView.setLayoutManager(linearLayoutManger);
        recyclerView.setAdapter(mAdapter);

        //댓글리스트에서 댓글아이템 안의 위젯들 클릭시
        mAdapter.setCommentItemClickListener(new CommentAdapter.onCommentItemClickListener() {
            @Override
            public void onCommenItemClicked(CommentAdapter.ViewHolder holder, View view, CommentGetData value, int position) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.text_comment_username:
                                Toast.makeText(CommentActivity.this, "댓글의 유저 이름", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.text_comment_date:
                                Toast.makeText(CommentActivity.this, "댓글의 날짜", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.text_comment_contents:
                                Toast.makeText(CommentActivity.this, "댓글내용이 클릭댐", Toast.LENGTH_SHORT).show();
                                break;

                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        new CommentItemListAsycTask(this).execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_comment_post:
                //댓글 게시버튼 클릭시
                commentSendData.commentBoardTag = boardTag; //댓글 달 게시글의 태그제목
                commentSendData.boardUserId = boardUserId; //댓글달 게시글을 작성한 유저아이다
                commentSendData.commentUserId = PropertyManager.getInstance().getUserId();
                commentSendData.commentUserNicName = PropertyManager.getInstance().getNickName();
                commentSendData.commentUserPicture = PropertyManager.getInstance().getPicUri();
                commentSendData.commentContent = commentContent.getText().toString();

                //유효성 체크 //데이터가 입력 되었는지 아닌지 확인
                if (commentSendData.commentContent == null || commentSendData.commentContent.length() <= 0) {
                    Toast.makeText(getApplicationContext(), "댓글 내용을 입력하세요", Toast.LENGTH_SHORT).show();
                    commentContent.requestFocus();
                    return;
                } else {
                    //서버로의 저장을 요구(백그라운드로 동작)
                    new CommentSendAsycTask(this).execute(commentSendData);
                    finish();
                    Log.e("async", "complete");
                }
                break;
        }
    }

    //코멘트들 리스트 보여주는 어싱크
    private class CommentItemListAsycTask extends AsyncTask<Void, Void, ArrayList<CommentGetData>> {
        ProgressDialog progressDialog;

        Context mContext;

        public CommentItemListAsycTask(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected void onPreExecute() { //이 함수가 하는 역할
            super.onPreExecute();
            //동그랑땡 다이얼로그 start
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("네트워크 처리중");
            progressDialog.show();
        }

        @Override
        protected ArrayList<CommentGetData> doInBackground(Void... integers) {
            Response response = null; //응답 담당
            OkHttpClient child; //연결 담당 (서버랑 안드로이드 연결해 주는 아이)
            ArrayList<CommentGetData> itemInfo = null;

            try {
                child = OkHttpManager.getOkHttpClient(); //아이에게 속성 넣어주기

                Request request = new Request.Builder() //데이터 받아서 오기
                        .url(String.format(NetworkDefineConstant.COMMENT_LIST_POST_GET_URL, boardId))
                        .build();

                response = child.newCall(request).execute(); //catch exception 안해주면 빨간줄

                if (response.isSuccessful()) { //응답이 성공시
                    itemInfo = CommentJSONParser.getCommentItemChartParsing(response.body().string()); //자바클래스 ItemInfo 형태의 객체안에 ItemJSONParser 클래스 안의 파싱해주는 함수 불러와서 파싱해주기
                    Log.e("TAG", response.body().toString());

                } else {
                    Log.e("요청에러", response.message().toString());
                }
            } catch (Exception e) {
                Log.e("파싱에러", e.toString());
            } finally {
                if (response != null) {
                    response.close();//
                }
            }
            return itemInfo;
        }


        @Override
        protected void onPostExecute(ArrayList<CommentGetData> itemInfo) {
            super.onPostExecute(itemInfo);
            progressDialog.dismiss();
            if (itemInfo != null && itemInfo.size() > 0) {//데이터가 잘 들어와서 널이 아니면 화면에 보여줭
                mAdapter.additemChart(itemInfo);
                mAdapter.notifyDataSetChanged();
            }
        }

    }

    //코멘트 작성 edit 어싱크
    private class CommentSendAsycTask extends AsyncTask<CommentSendData, Integer, String> {
        private Context mContext;

        public CommentSendAsycTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
         /*   progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("서버 입력 중...");
            progressDialog.show();*/
        }

        @Override
        protected String doInBackground(CommentSendData... commentSendDatas) {

            final MediaType pngType = MediaType.parse("image/*");
            String resultMsg = null;
            Response response = null;
            OkHttpClient toServer = null;

            RequestBody fileUploadBody = null;
            MultipartBody.Builder multipartBody = null;

            toServer = OkHttpManager.getOkHttpClient(); //서버로 보내는 중간 아이 생성

            try {
                multipartBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("commentBoardTag", commentSendDatas[0].commentBoardTag)
                        .addFormDataPart("userId", commentSendDatas[0].boardUserId)
                        .addFormDataPart("commentUserId", PropertyManager.getInstance().getUserId())
                        .addFormDataPart("commentUserPicture", PropertyManager.getInstance().getPicUri())
                        .addFormDataPart("commentUserNicName", PropertyManager.getInstance().getNickName())
                        .addFormDataPart("commentContent", commentSendDatas[0].commentContent);

                fileUploadBody = multipartBody.build();

                //요청 세팅
                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.COMMENT_LIST_POST_GET_URL, boardId))
                        .post(fileUploadBody)
                        .build();


                response = toServer.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String msg = jsonObject.getString("msg");
                    Log.e("CommentSend", msg);

                    return resultMsg;
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (response != null) {
                    response.close();
                }
            }


            return resultMsg;
        }

        @Override
        protected void onPostExecute(String result) {//result = 위에서 리턴된 feedWriteResultValue
            // UI도 고치고...
            if (result == null) {
                return;
            }
            if (result.equals("")) {

            } else {

            }
        }
    }
}
