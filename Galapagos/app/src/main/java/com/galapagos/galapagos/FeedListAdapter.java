package com.galapagos.galapagos;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.galapagos.galapagos.common.NetworkDefineConstant;
import com.galapagos.galapagos.common.OkHttpManager;
import com.galapagos.galapagos.common.PropertyManager;
import com.galapagos.galapagos.valueobject.FeedMainBoardValue;

import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.galapagos.galapagos.R.id.feed_realtive_layout;

public class FeedListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int EMPTY_VIEW = 10;
    // Glide 전역변수
    public RequestManager mGlideRequestManager;
    ArrayList<FeedMainBoardValue> valueList = new ArrayList<>();
    private FeedActivity mContext;


    public FeedListAdapter(Context mContext, RequestManager mGlideRequestManager) {
        this.mContext = (FeedActivity) mContext;
        this.mGlideRequestManager = mGlideRequestManager;
    }


    public Context getmContext() {
        return mContext;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        try {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_main_item, parent, false);
        } catch (Exception e) {
            e.printStackTrace();

        }

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //게시글 아이템 내의 위젯들
        public ImageView likeButton;
        public ImageView revisionButton;
        public ImageView commentButton;
        public RelativeLayout imageCategory;
        public RelativeLayout mainEvent;
        public TextView tagText;
        public CircleImageView circleUserImage;
        public TextView userNameText;
        public ImageView starButtonUn;
        public TextView dateText;
        public TextView contentText;
        public TextView likeCountText;
        public TextView commentCountText;
        public ImageView boardPicture1;
        public LinearLayout boardPictureLayout;
        public LinearLayout likeLayout;
        public LinearLayout feed_main_text_content_layout;
        public FrameLayout image_wrapper_frame;


        public ViewHolder(View itemView) {//행에 넣을 아이템들 객체화
            super(itemView);

            image_wrapper_frame = (FrameLayout) itemView.findViewById(R.id.image_wrapper_frame);
            feed_main_text_content_layout = (LinearLayout) itemView.findViewById(R.id.feed_main_text_content_layout);
            likeLayout = (LinearLayout) itemView.findViewById(R.id.like_layout);
            mainEvent = (RelativeLayout) itemView.findViewById(R.id.feed_realtive_bottom);

            //게시글 아이템 내의 위젯들 초기화
            likeButton = (ImageView) itemView.findViewById(R.id.btn_feed_like);
            revisionButton = (ImageView) itemView.findViewById(R.id.btn_feed_revision);
            commentButton = (ImageView) itemView.findViewById(R.id.btn_feed_comment);
            imageCategory = (RelativeLayout) itemView.findViewById(feed_realtive_layout);
            tagText = (TextView) itemView.findViewById(R.id.text_feed_tag);
            circleUserImage = (CircleImageView) itemView.findViewById(R.id.feed_main_userimage);
            userNameText = (TextView) itemView.findViewById(R.id.text_feed_username);
            starButtonUn = (ImageView) itemView.findViewById(R.id.btn_feed_star_un);
            dateText = (TextView) itemView.findViewById(R.id.text_feed_date);
            contentText = (TextView) itemView.findViewById(R.id.text_feed_content);
            likeCountText = (TextView) itemView.findViewById(R.id.text_main_like_count);
            commentCountText = (TextView) itemView.findViewById(R.id.text_feed_commentlist_count);
            boardPicture1 = (ImageView) itemView.findViewById(R.id.img_feed_board_pic1);
            boardPictureLayout = (LinearLayout) itemView.findViewById(R.id.board_feed_pic_layout);

        }

    }

    private void startActivityBranch(View view, FeedMainBoardValue boardValue) {
        Intent feedDetailIntent = new Intent(view.getContext(), FeedDetailActivity.class);

        feedDetailIntent.putExtra("boardId", boardValue.boardId);
        feedDetailIntent.putExtra("boardTag", boardValue.boardTag);
        feedDetailIntent.putExtra("boardUserId", boardValue.boardUserId);
        feedDetailIntent.putExtra("boardIsLike", boardValue.boardIsLike);
        feedDetailIntent.putExtra("boardIsRevision", boardValue.boardIsRevision);
        feedDetailIntent.putExtra("boardIsStar", boardValue.boardIsStar);
        mContext.startActivity(feedDetailIntent);
        mContext.overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        final FeedMainBoardValue boardValue = valueList.get(position);
        final int viewHodlerPostion = position;

        final ViewHolder rowHolder = (ViewHolder) holder;

        rowHolder.feed_main_text_content_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityBranch(view, boardValue);
            }
        });

        rowHolder.boardPictureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityBranch(view, boardValue);
            }
        });
        rowHolder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityBranch(view, boardValue);
            }
        });
        rowHolder.imageCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityBranch(view, boardValue);
            }
        });

        rowHolder.mainEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view instanceof ImageView) {
                    return;
                }
                startActivityBranch(view, boardValue);
            }
        });

        rowHolder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = OkHttpManager.getOkHttpClient();
                        String url = null;
                        final int likeImage;
                        Response response = null;

                        if (boardValue.boardIsLike) {
                            url = String.format(NetworkDefineConstant.LIKE_CANCEL_POST_URL, boardValue.boardId);
                            likeImage = R.drawable.empathy_button_un_l;
                            boardValue.boardIsLike = false;
                            rowHolder.likeCountText.post(new Runnable() {
                                @Override
                                public void run() {
                                    boardValue.boardLikeCount--;
                                    rowHolder.likeCountText.setText("공감 " +
                                            boardValue.boardLikeCount + " , 비공감 " + boardValue.boardRevisionCount);
                                }
                            });


                        } else {
                            url = String.format(NetworkDefineConstant.LIKE_BOARD_POST_URL, boardValue.boardId);
                            likeImage = R.drawable.empathy_button_use_l;
                            boardValue.boardIsLike = true;
                            rowHolder.likeCountText.post(new Runnable() {
                                @Override
                                public void run() {

                                    boardValue.boardLikeCount++;
                                    rowHolder.likeCountText.setText("공감 " + boardValue.boardLikeCount + " , 비공감 " + boardValue.boardRevisionCount);
                                }
                            });
                        }

                        setPositionItem(viewHodlerPostion, boardValue);

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
                            if (msg.equals("success")) {
                                rowHolder.likeButton.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        rowHolder.likeButton.setImageResource(likeImage);
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
        });
        rowHolder.revisionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = OkHttpManager.getOkHttpClient();
                        String url = null;
                        final int likeImage;
                        FeedMainBoardValue feedMainBoardValue = boardValue;
                        Response response = null;
                        Log.e("Click BOARD ID", boardValue.boardId);

                        if (boardValue.boardIsRevision) {
                            url = String.format(NetworkDefineConstant.REVISION_CANCEL_POST_URL, boardValue.boardId);
                            likeImage = R.drawable.unempathy_button_un_l;
                            boardValue.boardIsRevision = false;
                            rowHolder.likeCountText.post(new Runnable() {
                                @Override
                                public void run() {
                                    boardValue.boardRevisionCount--;
                                    rowHolder.likeCountText.setText("공감 " + boardValue.boardLikeCount + " , 비공감 " + boardValue.boardRevisionCount);
                                }
                            });


                        } else {
                            url = String.format(NetworkDefineConstant.REVISION_BOARD_POST_URL, boardValue.boardId);
                            likeImage = R.drawable.unempathy_button_use_l;
                            boardValue.boardIsRevision = true;
                            rowHolder.likeCountText.post(new Runnable() {
                                @Override
                                public void run() {
                                    boardValue.boardRevisionCount++;
                                    rowHolder.likeCountText.setText("공감 " + boardValue.boardLikeCount + " , 비공감 " + boardValue.boardRevisionCount);
                                }
                            });
                        }

                        setPositionItem(viewHodlerPostion, boardValue);

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
                            if (msg.equals("success")) {
                                rowHolder.revisionButton.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        rowHolder.revisionButton.setImageResource(likeImage);
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
        });

        rowHolder.starButtonUn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = OkHttpManager.getOkHttpClient();
                        String url = null;
                        final int likeImage;
                        Response response = null;
                        Log.e("Click BOARD ID", boardValue.boardId);

                        if (boardValue.boardIsStar) {
                            url = String.format(NetworkDefineConstant.STAR_LIST_DELETE_POST_URL, boardValue.boardId);
                            likeImage = R.drawable.star_search_icon_un;
                            boardValue.boardIsStar = false;

                        } else {
                            url = String.format(NetworkDefineConstant.STAR_LIST_ADD_POST_URL, boardValue.boardId);
                            likeImage = R.drawable.star_search_icon_use;
                            boardValue.boardIsStar = true;
                        }

                        Log.e("BoardIsStar", url);
                        setPositionItem(viewHodlerPostion, boardValue);

                        RequestBody formBody = new FormBody.Builder()
                                .add("userId", PropertyManager.getInstance().getUserId())
                                .build();

                        Request request = new Request.Builder() //데이터 받아서 오기
                                .url(url)
                                .post(formBody)
                                .build();

                        Log.e("StarIs", url);

                        try {
                            response = client.newCall(request).execute(); //catch exception 안해주면 빨간줄
                            JSONObject liekJsonObject = new JSONObject(response.body().string());
                            String msg = liekJsonObject.optString("msg", "fail");
                            if (msg.equals("success")) {
                                rowHolder.starButtonUn.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        rowHolder.starButtonUn.setImageResource(likeImage);
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
        });

        rowHolder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("boardUserId", boardValue.boardUserId);
                intent.putExtra("boardTag", boardValue.boardTag);
                intent.putExtra("boardId", boardValue.boardId);
                mContext.startActivity(intent);
//                            overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
            }
        });


        //리턴한 홀더의
        FeedMainBoardValue value = valueList.get(position);


        rowHolder.tagText.setText(value.boardTag);
        rowHolder.userNameText.setText(value.boardUserNicName);
        rowHolder.contentText.setText(value.boardContent);
        rowHolder.dateText.setText(value.boardDate);
        rowHolder.commentCountText.setText("댓글 " + value.boardCommentCount);
        rowHolder.likeCountText.setText("공감 " + value.boardLikeCount + " , 비공감 " + value.boardRevisionCount);


        // 게시글 유저 프로필 사진
        if (value.boardUserProfilePicture != null) {
            String uri = value.boardUserProfilePicture;
            Glide.with(mContext)
                    .load(uri)
                    .override(1000, 1000)
                    .centerCrop()
                    .thumbnail(0.3f)
                    .into((rowHolder.circleUserImage));
        }else{
            rowHolder.circleUserImage.setImageResource(R.drawable.user_img_66dp);
        }


        // 게시글 사진
        if (value.boardUserPicture1 != null) {
            String picUrl = value.boardUserPicture1;
            rowHolder.image_wrapper_frame.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(picUrl)
                    .override(1000, 1000)
                    .centerCrop()
                    .thumbnail(0.2f)
                    .into(((ViewHolder) holder).boardPicture1);
        } else {
        }


        if (value.boardIsLike) { // 즐겨찾기 여부
            rowHolder.likeButton.setImageResource(R.drawable.empathy_button_use_l);
        }
        if (value.boardIsRevision) { //정정요청 여부 정정요청 아닐때
            rowHolder.revisionButton.setImageResource(R.drawable.unempathy_button_use_l);
        }
        if (value.boardIsStar) { //즐겨찾기 여부 즐겨찾기안했을때
            rowHolder.starButtonUn.setImageResource(R.drawable.star_search_icon_use);
        }
        if (value.boardisAnonymity.equals("true")) { // 익명여부 판단
            rowHolder.userNameText.setText("익명");
            rowHolder.circleUserImage.setImageResource(R.drawable.user_img_66dp);
        }
        //카테고리데이터에 들어온 값에 따라 카테고리 색상 바꿔주기
        switch (value.boardCategory) {
            case 8:
                rowHolder.imageCategory.setBackgroundResource(R.drawable.category0);
                break;
            case 1:
                rowHolder.imageCategory.setBackgroundResource(R.drawable.category1);
                break;
            case 2:
                rowHolder.imageCategory.setBackgroundResource(R.drawable.category2);
                break;
            case 3:
                rowHolder.imageCategory.setBackgroundResource(R.drawable.category3);
                break;
            case 4:
                rowHolder.imageCategory.setBackgroundResource(R.drawable.category4);
                break;
            case 5:
                rowHolder.imageCategory.setBackgroundResource(R.drawable.category5);
                break;
            case 6:
                rowHolder.imageCategory.setBackgroundResource(R.drawable.category6);
                break;
            default:
                break;
        }
    }


    @Override
    public int getItemCount() {
        return valueList.size();
    }

    public void setPositionItem(int position, FeedMainBoardValue value) {
        valueList.set(position, value);
    }

    public FeedMainBoardValue getFeedItem(int position) {
        return valueList.get(position);
    }

    public void addiAllFeedItem(ArrayList<FeedMainBoardValue> iteminfos) {
        if (iteminfos != null) {
            valueList.addAll(iteminfos);
            notifyDataSetChanged();
        }
    }

    public void addFeedItem(FeedMainBoardValue feedStarBoardValue) {
        if (feedStarBoardValue != null) {
            valueList.add(feedStarBoardValue);
            notifyDataSetChanged();
        }
    }

    public void itemClear() {
        valueList.clear();
        notifyDataSetChanged();
    }


}