package com.galapagos.galapagos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.galapagos.galapagos.common.GalaPoiCategory;
import com.galapagos.galapagos.common.NetworkDefineConstant;
import com.galapagos.galapagos.common.OkHttpManager;
import com.galapagos.galapagos.common.PropertyManager;
import com.galapagos.galapagos.valueobject.CommentDetailValue;
import com.galapagos.galapagos.valueobject.FeedDetailValue;
import com.galapagos.galapagos.valueobject.StarButtonValue;
import com.galapagos.galapagos.valueobject.IsLikeRevisionValue;

import org.json.JSONArray;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FeedDetailActivity extends AppCompatActivity implements View.OnTouchListener {

    RelativeLayout detailCategory;
    TextView detailKeyword;
    CircleImageView detailUserImage;
    TextView detailUsername;
    ImageView btnDetailStarUn;
    TextView detailTime;
    TextView detailContent;
    TextView detailLikeCount;
    TextView detailCommentCount;
    ImageView btnDetailLike;
    ImageView btnDetailModify;
    ImageView btnDetailComment;

    // 코멘트 처리
    RecyclerView detailCommmentList;
    CommentDetailAdapter commentAdapter;

    // 사진처리
    LinearLayout feedDetailFadeLayout;
    ImageView feedDetailPicture;

    // 앱바 레이아웃
    RelativeLayout detail_applayout;
    ImageButton btnDetailAppbarClose;

    // 보드 ID
    String boardId;
    String boardTag;
    String boardUserId;
    Boolean boardIsLike;
    Boolean boardIsRevision;
    Boolean boardIsStar;
    String fcmService;

    // 공감,비공감 카운팅
    int liekCount;
    int modityCount;


    private StarButtonValue starValue;
    private IsLikeRevisionValue isLikeRevisionValue;

    // ScroolVIew Listenr
    NestedScrollView feedScroolView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_feed_layout);

        if (starValue == null) {
            starValue = new StarButtonValue();
        }
        if (isLikeRevisionValue == null) {
            isLikeRevisionValue = new IsLikeRevisionValue();
        }

        Intent intent = getIntent();
        boardId = intent.getStringExtra("boardId");
        boardTag = intent.getStringExtra("boardTag");
        boardUserId = intent.getStringExtra("boardUserId");
        fcmService = intent.getStringExtra("fcmService");

        detailCategory = (RelativeLayout) findViewById(R.id.feed_detail_category);
        detailKeyword = (TextView) findViewById(R.id.text_detail_keyword);
        detailUserImage = (CircleImageView) findViewById(R.id.image_detail_user);
        detailUsername = (TextView) findViewById(R.id.text_detail_username);
        btnDetailStarUn = (ImageView) findViewById(R.id.btn_detail_star_un);
        detailTime = (TextView) findViewById(R.id.text_detail_time);
        detailContent = (TextView) findViewById(R.id.text_detail_content);
        detailLikeCount = (TextView) findViewById(R.id.text_detail_like_count);
        detailCommentCount = (TextView) findViewById(R.id.text_detail_comment_count);

        // 이벤트 버튼
        btnDetailLike = (ImageView) findViewById(R.id.btn_detail_like);
        btnDetailModify = (ImageView) findViewById(R.id.btn_detail_modify);
        btnDetailComment = (ImageView) findViewById(R.id.btn_detail_comment);
        detailCommmentList = (RecyclerView) findViewById(R.id.feed_detail_comment_list);

        // 사진을 숨기는 레이아웃
        feedDetailFadeLayout = (LinearLayout) findViewById(R.id.feed_detail_fade_layout);
        feedDetailPicture = (ImageView) findViewById(R.id.feed_detail_picture);

        //  앱 레이아웃 버튼

        btnDetailStarUn.setOnTouchListener(this);
        btnDetailLike.setOnTouchListener(this);
        btnDetailModify.setOnTouchListener(this);
        btnDetailComment.setOnTouchListener(this);

        feedScroolView = (NestedScrollView) findViewById(R.id.scroll_feed_detail);


        commentAdapter = new CommentDetailAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        detailCommmentList.setLayoutManager(layoutManager);
        detailCommmentList.setAdapter(commentAdapter);

        new FeedDetailAsyncTask(this).execute(boardId);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if (fcmService != null && fcmService.equals("fcmService")) {
            startActivity(new Intent(this, FeedActivity.class));
            finish();
            overridePendingTransition(R.anim.stay, R.anim.slide_out_right);
        } else {
            finish();
            overridePendingTransition(R.anim.stay, R.anim.slide_out_right);

        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                switch (view.getId()) {
                    case R.id.btn_detail_like:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                OkHttpClient client = OkHttpManager.getOkHttpClient();
                                String url = null;
                                final int likeImage;
                                Response response = null;
                                Log.e("Click BOARD ID", boardId);

                                if (boardIsLike) {
                                    url = String.format(NetworkDefineConstant.LIKE_CANCEL_POST_URL, boardId);
                                    likeImage = R.drawable.empathy_button_un_l;
                                    boardIsLike = false;
                                    detailLikeCount.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            liekCount--;
                                            detailLikeCount.setText("공감 " + liekCount + " , 비공감 " + modityCount);
                                        }
                                    });

                                } else {
                                    detailLikeCount.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            liekCount++;
                                            detailLikeCount.setText("공감 " + liekCount + " , 비공감 " + modityCount);
                                        }
                                    });
                                    url = String.format(NetworkDefineConstant.LIKE_BOARD_POST_URL, boardId);
                                    likeImage = R.drawable.empathy_button_use_l;
                                    boardIsLike = true;
                                }

                                Log.e("PositionBoardid", boardId);

                                RequestBody formBody = new FormBody.Builder()
                                        .add("userId", PropertyManager.getInstance().getUserId())
                                        .build();

                                Request request = new Request.Builder() //데이터 받아서 오기
                                        .url(url)
                                        .post(formBody)
                                        .build();


                                try {
                                    response = client.newCall(request).execute(); //catch exception 안해주면 빨간줄
                                    JSONObject liekJsonObject = new JSONObject(response.body().string());
                                    String msg = liekJsonObject.optString("msg", "fail");
                                    Log.e("LiekDetailMsg", msg);
                                    if (msg.equals("success")) {
                                        btnDetailLike.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                btnDetailLike.setImageResource(likeImage);
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
                        return true;
                    case R.id.btn_detail_modify:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                OkHttpClient client = OkHttpManager.getOkHttpClient();
                                String url = null;
                                final int likeImage;
                                Response response = null;
                                Log.e("Click BOARD ID", boardId);

                                if (boardIsRevision) {
                                    url = String.format(NetworkDefineConstant.REVISION_CANCEL_POST_URL, boardId);
                                    likeImage = R.drawable.unempathy_button_un_l;
                                    boardIsRevision = false;
                                    detailLikeCount.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            modityCount--;
                                            detailLikeCount.setText("공감 " + liekCount + " , 비공감 " + modityCount);
                                        }
                                    });

                                } else {
                                    detailLikeCount.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            modityCount++;
                                            detailLikeCount.setText("공감 " + liekCount + " , 비공감 " + modityCount);
                                        }
                                    });
                                    url = String.format(NetworkDefineConstant.REVISION_BOARD_POST_URL, boardId);
                                    likeImage = R.drawable.unempathy_button_use_l;
                                    boardIsRevision = true;
                                }

                                Log.e("PositionBoardid", boardId);

                                RequestBody formBody = new FormBody.Builder()
                                        .add("userId", PropertyManager.getInstance().getUserId())
                                        .build();

                                Request request = new Request.Builder() //데이터 받아서 오기
                                        .url(url)
                                        .post(formBody)
                                        .build();


                                try {
                                    response = client.newCall(request).execute(); //catch exception 안해주면 빨간줄
                                    JSONObject liekJsonObject = new JSONObject(response.body().string());
                                    String msg = liekJsonObject.optString("msg", "fail");
                                    Log.e("LiekDetailMsg", msg);
                                    if (msg.equals("success")) {
                                        btnDetailModify.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                btnDetailModify.setImageResource(likeImage);
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
                        return true;
                    case R.id.btn_detail_comment:
                        Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
                        intent.putExtra("boardUserId", boardUserId);
                        intent.putExtra("boardTag", boardTag);
                        intent.putExtra("boardId", boardId);
                        startActivity(intent);
                        break;
                    case R.id.btn_detail_star_un:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                OkHttpClient client = OkHttpManager.getOkHttpClient();
                                String url = null;
                                final int likeImage;
                                Response response = null;
                                Log.e("Click BOARD ID", boardId);

                                if (boardIsStar) {
                                    url = String.format(NetworkDefineConstant.STAR_LIST_DELETE_POST_URL, boardId);
                                    likeImage = R.drawable.star_search_icon_un;
                                    boardIsStar = false;

                                } else {
                                    url = String.format(NetworkDefineConstant.STAR_LIST_ADD_POST_URL, boardId);
                                    likeImage = R.drawable.star_search_icon_use;
                                    boardIsStar = true;
                                }

                                Log.e("PositionBoardid", boardId);

                                RequestBody formBody = new FormBody.Builder()
                                        .add("userId", PropertyManager.getInstance().getUserId())
                                        .build();

                                Request request = new Request.Builder() //데이터 받아서 오기
                                        .url(url)
                                        .post(formBody)
                                        .build();


                                try {
                                    response = client.newCall(request).execute(); //catch exception 안해주면 빨간줄
                                    JSONObject liekJsonObject = new JSONObject(response.body().string());
                                    String msg = liekJsonObject.optString("msg", "fail");
                                    Log.e("StarDetailMsg", msg);
                                    if (msg.equals("success")) {
                                        btnDetailModify.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                btnDetailStarUn.setImageResource(likeImage);
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
                        return true;
                }

        }
        return false;
    }

    public void appbarTranslate(int scroll) {
        if (scroll > 0) {
            detail_applayout.setVisibility(View.GONE);
        } else {
            detail_applayout.setVisibility(View.VISIBLE);
        }
    }

    private class FeedDetailAsyncTask extends AsyncTask<String, Integer, FeedDetailValue> {

        OkHttpClient okHttpClient;
        ProgressDialog progressDialog;
        Context mContext;

        public FeedDetailAsyncTask(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected void onPreExecute() {
            try {
                progressDialog = new ProgressDialog(mContext);
                progressDialog.setMessage("로딩 중");
                progressDialog.onTouchEvent(null);
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPreExecute();
        }

        @Override
        protected FeedDetailValue doInBackground(String... strings) {

            okHttpClient = OkHttpManager.getOkHttpClient();


            String url = String.format(NetworkDefineConstant.FEED_DETAIL_URL, strings[0]);

            FormBody formBody = new FormBody.Builder()
                    .add("userId", PropertyManager.getInstance().getUserId())
                    .build();

            Log.e("DetailUserId", PropertyManager.getInstance().getUserId());


            Request request = new Request.Builder() //데이터 받아서 오기
                    .url(url)
                    .post(formBody)
                    .build();

            Log.e("FeedDetail", url);

            FeedDetailValue feedDetailValue = new FeedDetailValue();
            Response response = null;

            try {
                response = okHttpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONObject result = new JSONObject(response.body().string());

                    JSONObject feedDetailJson = result.getJSONObject("data");

                    // 전문 본문
                    feedDetailValue.boardUserId = feedDetailJson.getString("boardUserId");
                    feedDetailValue.boardUserPictrue = feedDetailJson.optString("boardUserPicture");
                    feedDetailValue.boardLat = feedDetailJson.getDouble("boardLat");
                    feedDetailValue.boardLong = feedDetailJson.getDouble("boardLong");
                    feedDetailValue.boardTag = feedDetailJson.getString("boardTag");
                    feedDetailValue.boardDate = feedDetailJson.getString("boardDate");
                    feedDetailValue.boardContent = feedDetailJson.getString("boardContent");
                    feedDetailValue.boardCategory = feedDetailJson.getInt("boardCategory");
                    feedDetailValue.boardNicName = feedDetailJson.optString("boardUserNicName", "null");
                    feedDetailValue.boardIsAnomynity = feedDetailJson.optString("boardIsAnomynity", "false");
                    feedDetailValue.boardCommentCount = feedDetailJson.getInt("boardCommentCount");
                    feedDetailValue.boardContentPicture1 = feedDetailJson.optString("boardContentPicture1", "null");
                    feedDetailValue.boardCommentCount = feedDetailJson.optInt("boardCommentCount", 0);
                    feedDetailValue.boardLikeCount = feedDetailJson.optInt("boardLikeCount", 0);
                    feedDetailValue.boardRevisionCount = feedDetailJson.optInt("boardRevisionCount", 0);


                    feedDetailValue.boardIsLike = feedDetailJson.optBoolean("boardIsLike", false);
                    feedDetailValue.boardIsRevision = feedDetailJson.optBoolean("boardIsRevision", false);
                    feedDetailValue.boardIsStar = feedDetailJson.optBoolean("boardIsStar", false);


                    // 댓글

                    JSONObject commentJson = result.getJSONObject("data1");
                    JSONArray commentArrayJson = commentJson.getJSONArray("comment");
                    for (int i = 0; i < commentArrayJson.length(); i++) {

                        JSONObject commentOneJson = commentArrayJson.getJSONObject(i);
                        CommentDetailValue commentDetailValue = new CommentDetailValue();
                        commentDetailValue.commentBoardId = commentOneJson.optString("commentBoardId");
                        commentDetailValue.commentUserId = commentOneJson.optString("commentUserId");
                        commentDetailValue.commentUserNicName = commentOneJson.optString("commentUserNicName");
                        commentDetailValue.commentUserComtent = commentOneJson.optString("commentContent");
                        commentDetailValue.commentDate = commentOneJson.optString("commentDate");
                        commentDetailValue.commentUserPicture = commentOneJson.optString("commentUserPicture");


                        feedDetailValue.commentArrayList.add(commentDetailValue);

                    }

                    Log.e("FeedDetailParsing", feedDetailValue.toString());

                    return feedDetailValue;

                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (response != null) {
                    response.close();
                }
            }


            return null;
        }


        @Override
        protected void onPostExecute(FeedDetailValue feedDetailValue) {
            super.onPostExecute(feedDetailValue);

            try {
                progressDialog.dismiss();


                if (feedDetailValue != null) {
                    // 카테고리 이미지 선택
                    setCategoryImage(feedDetailValue.boardCategory);
                    // 제목 선택
                    detailKeyword.setText(feedDetailValue.boardTag);
                    if (!feedDetailValue.boardUserPictrue.equals("null")) {
                        // 유저 프로필 사진
                        Glide.with(FeedDetailActivity.this)
                                .load(feedDetailValue.boardUserPictrue)
                                .into(detailUserImage);
                    }else {
                        detailUserImage.setImageResource(R.drawable.user_img_66dp);
                    }

                    if (!feedDetailValue.boardContentPicture1.equals("null")) {
                        feedDetailFadeLayout.setVisibility(View.VISIBLE);
                        Glide.with(FeedDetailActivity.this)
                                .load(feedDetailValue.boardContentPicture1)
                                .override(1000,1000)
                                .into(feedDetailPicture);
                    }

                    // 유저 이름
                    detailUsername.setText(feedDetailValue.boardNicName);

                    // 시간
                    detailTime.setText(feedDetailValue.boardDate);
                    // 컨텐츠 내용
                    detailContent.setText(feedDetailValue.boardContent);
                    detailLikeCount.setText("공감 " + feedDetailValue.boardLikeCount + " , 비공감 " + feedDetailValue.boardRevisionCount);

                    // 뷰포스트 사용할 공감
                    liekCount = feedDetailValue.boardLikeCount;
                    modityCount = feedDetailValue.boardRevisionCount;

                    detailCommentCount.setText("댓글 " + feedDetailValue.boardCommentCount);


                    if (feedDetailValue.boardIsAnomynity.equals("true") && feedDetailValue.boardIsAnomynity != null) {
                        detailUsername.setText("익명");
                        Glide.with(FeedDetailActivity.this)
                                .load(R.drawable.user_img_66dp)
                                .placeholder(R.drawable.imege_up)
                                .error(R.drawable.imege_fail)
                                .into(detailUserImage);
                    }

                    // 공감,비공감, 즐겨찾기 setting
                    boardIsLike = feedDetailValue.boardIsLike;
                    if (boardIsLike) {
                        btnDetailLike.setImageResource(R.drawable.empathy_button_use_l);
                    }

                    boardIsRevision = feedDetailValue.boardIsRevision;
                    if (boardIsRevision) {
                        btnDetailModify.setImageResource(R.drawable.unempathy_button_use_l);
                    }

                    boardIsStar = feedDetailValue.boardIsStar;
                    if (boardIsStar) {
                        btnDetailStarUn.setImageResource(R.drawable.star_search_icon_use);
                    }

                    if (feedDetailValue.commentArrayList != null) {
                        for (int i = 0; i < feedDetailValue.commentArrayList.size(); i++) {
                            commentAdapter.itemAdd(feedDetailValue.commentArrayList.get(i));
                        }
                    }


                } else {
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        // 카테고리 이미지 선택
        private void setCategoryImage(int categoryType) {
            int category = R.drawable.category0;
            switch (categoryType) {
                case GalaPoiCategory.GalaPoiCategoryOthers:
                    category = R.drawable.category0;
                    break;
                case GalaPoiCategory.GalaPoiCategoryFood:
                    category = R.drawable.category1;
                    break;
                case GalaPoiCategory.GalaPoiCategoryPlay:
                    category = R.drawable.category2;
                    break;
                case GalaPoiCategory.GalaPoiCategoryOutLocation:
                    category = R.drawable.category3;
                    break;
                case GalaPoiCategory.GalaPoiCategoryDeal:
                    category = R.drawable.category4;
                    break;
                case GalaPoiCategory.GalaPoiCategoryMetting:
                    category = R.drawable.category5;
                    break;
                case GalaPoiCategory.GalaPoiCategorySuggestion:
                    category = R.drawable.category6;
                    break;
            }
            detailCategory.setBackgroundResource(category);
        }


    }


}
