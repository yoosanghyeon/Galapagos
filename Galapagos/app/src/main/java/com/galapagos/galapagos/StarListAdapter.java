package com.galapagos.galapagos;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.galapagos.galapagos.common.NetworkDefineConstant;
import com.galapagos.galapagos.common.OkHttpManager;
import com.galapagos.galapagos.common.PropertyManager;
import com.galapagos.galapagos.valueobject.StarBoardValue;

import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// StarListAdapter
public class StarListAdapter extends RecyclerView.Adapter<StarListAdapter.ViewHolder> {
    private FavoriteActivity mContext;
    ArrayList<StarBoardValue> iteminfo = new ArrayList<>();


    public StarListAdapter(FavoriteActivity mContext) {
        this.mContext = mContext;
        iteminfo = new ArrayList<>();
    }

    // 즐겨찾기 추가목록 아이템 Click Listener
    public interface onStarListItemClickListener {
        public void onStarItemClicked(StarListAdapter.ViewHolder holder, View view, StarBoardValue value, int position);
    }

    onStarListItemClickListener starListItemClickListener;

    public void setStarListItemClickListener(onStarListItemClickListener clickListener) {
        starListItemClickListener = clickListener;
    }

    public Context getmContext() {
        return mContext;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.star_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setStarListIemClickListener(starListItemClickListener);

        //리턴한 홀더의
        final StarBoardValue itemInfo = iteminfo.get(position);
        final int holderPostion = position;
        final ViewHolder viewHolder = holder;



        holder.imageCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityBranch(view,itemInfo);
            }
        });

        holder.circleUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityBranch(view,itemInfo);
            }
        });

        holder.userNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityBranch(view,itemInfo);
            }
        });

        holder.boardPictureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityBranch(view,itemInfo);
            }
        });

        holder.contentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityBranch(view,itemInfo);
            }
        });

        holder.dateText
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityBranch(view,itemInfo);
            }
        });

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = OkHttpManager.getOkHttpClient();
                        String url = null;
                        final int likeImage;
                        final StarBoardValue starBoardValue = itemInfo;
                        Response response = null;
                        Log.e("Click BOARD ID", starBoardValue.boardId);

                        if (starBoardValue.boardIsLike) {
                            url = String.format(NetworkDefineConstant.LIKE_CANCEL_POST_URL, starBoardValue.boardId);
                            likeImage = R.drawable.empathy_button_un_l;
                            starBoardValue.boardIsLike = false;
                            viewHolder.likeCountText.post(new Runnable() {
                                @Override
                                public void run() {
                                    starBoardValue.boardLikeCount--;
                                    viewHolder.likeCountText.setText("공감 " + starBoardValue.boardLikeCount + " , 비공감 " + starBoardValue.boardRevisionCount);
                                }
                            });


                        } else {
                            url = String.format(NetworkDefineConstant.LIKE_BOARD_POST_URL, itemInfo.boardId);
                            likeImage = R.drawable.empathy_button_use_l;
                            starBoardValue.boardIsLike = true;
                            viewHolder.likeCountText.post(new Runnable() {
                                @Override
                                public void run() {

                                    starBoardValue.boardLikeCount++;
                                    viewHolder.likeCountText.setText("공감 " + starBoardValue.boardLikeCount + " , 비공감 " + starBoardValue.boardRevisionCount);
                                }
                            });
                        }

                        setStarListPositionItem(holderPostion, starBoardValue);
                        Log.e("PositionBoardid", itemInfo.boardId);

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
                                viewHolder.likeButton.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        viewHolder.likeButton.setImageResource(likeImage);
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

        holder.revisionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = OkHttpManager.getOkHttpClient();
                        String url = null;
                        final int likeImage;
                        final StarBoardValue starboardValue = itemInfo;
                        Response response = null;
                        Log.e("Click BOARD ID", itemInfo.boardId);

                        if (starboardValue.boardIsRevision) {
                            url = String.format(NetworkDefineConstant.REVISION_CANCEL_POST_URL, starboardValue.boardId);
                            likeImage = R.drawable.unempathy_button_un_l;
                            starboardValue.boardIsRevision = false;
                            viewHolder.likeCountText.post(new Runnable() {
                                @Override
                                public void run() {
                                    starboardValue.boardRevisionCount--;
                                    viewHolder.likeCountText.setText("공감 " + starboardValue.boardLikeCount + " , 비공감 " + starboardValue.boardRevisionCount);
                                }
                            });


                        } else {
                            url = String.format(NetworkDefineConstant.REVISION_BOARD_POST_URL, itemInfo.boardId);
                            likeImage = R.drawable.unempathy_button_use_l;
                            starboardValue.boardIsRevision = true;
                            viewHolder.likeCountText.post(new Runnable() {
                                @Override
                                public void run() {
                                    starboardValue.boardRevisionCount++;
                                    viewHolder.likeCountText.setText("공감 " + starboardValue.boardLikeCount + " , 비공감 " + starboardValue.boardRevisionCount);
                                }
                            });
                        }

                        setStarListPositionItem(holderPostion, starboardValue);
                        Log.e("PositionBoardid", itemInfo.boardId);

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
                                viewHolder.revisionButton.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        viewHolder.revisionButton.setImageResource(likeImage);
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

        holder.tagText.setText(itemInfo.boardTag);
        holder.userNameText.setText(itemInfo.boardUserNicName);
        holder.contentText.setText(itemInfo.boardContent);
        holder.dateText.setText(itemInfo.boardDate);
        holder.commentCountText.setText("댓글 " + itemInfo.boardCommentCount);
        holder.likeCountText.setText("공감 " + itemInfo.boardLikeCount + " , 비공감 " + itemInfo.boardRevisionCount);

        // 게시글 유저 프로필 사진
        holder.starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = OkHttpManager.getOkHttpClient();
                        String url = null;
                        final int likeImage;
                        StarBoardValue starBoardValue = itemInfo;
                        Response response = null;
                        Log.e("Click BOARD ID", starBoardValue.boardId);

                        if (starBoardValue.boardIsStar) {
                            url = String.format(NetworkDefineConstant.STAR_LIST_DELETE_POST_URL, starBoardValue.boardId);
                            likeImage = R.drawable.star_search_icon_un;
                            starBoardValue.boardIsStar = false;

                        } else {
                            url = String.format(NetworkDefineConstant.STAR_LIST_ADD_POST_URL, starBoardValue.boardId);
                            likeImage = R.drawable.star_search_icon_use;
                            starBoardValue.boardIsStar = true;
                        }

                        Log.e("BoardIsStar", url);
                        setStarListPositionItem(holderPostion, starBoardValue);

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
                                viewHolder.starButton.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        viewHolder.starButton.setImageResource(likeImage);
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

        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent feedDetailIntentTwo = new Intent(mContext, FeedDetailActivity.class);
                feedDetailIntentTwo.putExtra("boardId", itemInfo.boardId);
                feedDetailIntentTwo.putExtra("boardTag", itemInfo.boardTag);
                feedDetailIntentTwo.putExtra("boardUserId", itemInfo.boardUserId);
                feedDetailIntentTwo.putExtra("boardIsLike", itemInfo.boardIsLike);
                feedDetailIntentTwo.putExtra("boardIsRevision", itemInfo.boardIsRevision);
                feedDetailIntentTwo.putExtra("boardIsStar", itemInfo.boardIsStar);
                mContext.startActivity(feedDetailIntentTwo);
                mContext.overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
            }
        });

        if (itemInfo.boardUserProfilePicture != null) {
            String uri = itemInfo.boardUserProfilePicture;
            Glide.with(mContext)
                    .load(uri)
                    .override(1000, 1000)
                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .centerCrop()
                    .thumbnail(0.3f)
                    .into(holder.circleUserImage);
        } else {
            holder.circleUserImage.setImageResource(R.drawable.user_img_66dp);
        }
        // 게시글 사진
        if (itemInfo.boardUserPicture1 != null) {
            String picUrl = itemInfo.boardUserPicture1;
            holder.boardPictureLayout.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(picUrl)
                    .override(1000, 1000)
                    .centerCrop()
                    .thumbnail(0.2f)
                    .into(holder.boardPicture1);
        }

        if (itemInfo.boardIsLike) { // 즐겨찾기 여부
            holder.likeButton.setImageResource(R.drawable.empathy_button_use_l);
        }
        if (itemInfo.boardIsRevision) { //정정요청 여부 정정요청 아닐때
            holder.revisionButton.setImageResource(R.drawable.unempathy_button_use_l);
        }
        if (itemInfo.boardIsStar) { //즐겨찾기 여부 즐겨찾기안했을때
            holder.starButton.setImageResource(R.drawable.star_search_icon_use);
        }
        if (itemInfo.boardisAnonymity.equals("true")) { // 익명여부 판단
            holder.userNameText.setText("익명");
            holder.circleUserImage.setImageResource(R.drawable.user_img_66dp);
        }
        //카테고리데이터에 들어온 값에 따라 카테고리 색상 바꿔주기
        switch (itemInfo.boardCategory) {
            case 8:
                holder.imageCategory.setBackgroundResource(R.drawable.category0);
                break;
            case 1:
                holder.imageCategory.setBackgroundResource(R.drawable.category1);
                break;
            case 2:
                holder.imageCategory.setBackgroundResource(R.drawable.category2);
                break;
            case 3:
                holder.imageCategory.setBackgroundResource(R.drawable.category3);
                break;
            case 4:
                holder.imageCategory.setBackgroundResource(R.drawable.category4);
                break;
            case 5:
                holder.imageCategory.setBackgroundResource(R.drawable.category5);
                break;
            case 6:
                holder.imageCategory.setBackgroundResource(R.drawable.category6);
                break;
            default:
                break;
        }
    }


    private void startActivityBranch(View view, StarBoardValue boardValue) {
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
    public int getItemCount() {
        return iteminfo.size();
    }

    public StarBoardValue findFeedStarBoardValue(int position) {
        return iteminfo.get(position);
    }

    public void setStarListPositionItem(int position, StarBoardValue value) {
        iteminfo.set(position, value);
    }

    public void addAllStarListItem(ArrayList<StarBoardValue> iteminfos) {
        if (iteminfos != null) {
            iteminfo.addAll(iteminfos);
            notifyDataSetChanged();
        }
    }

    public void addStarListItem(StarBoardValue starBoardValue) {
        if (starBoardValue != null) {
            iteminfo.add(starBoardValue);
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        StarBoardValue value;

        //게시글 아이템 내의 위젯들
        public ImageView likeButton;
        public ImageView revisionButton;
        public ImageView commentButton;
        public RelativeLayout imageCategory;
        public TextView tagText;
        public CircleImageView circleUserImage;
        public TextView userNameText;
        public ImageView starButton;
        public TextView dateText;
        public TextView contentText;
        public TextView likeCountText;
        public TextView commentCountText;
        public ImageView boardPicture1;
        public LinearLayout boardPictureLayout;

        public void setStarListIemClickListener(onStarListItemClickListener clickListener) {
            starListItemClickListener = clickListener;
        }

        public ViewHolder(View itemView) {//행에 넣을 아이템들 객체화
            super(itemView);

            //게시글 아이템 내의 위젯들 초기화
            likeButton = (ImageView) itemView.findViewById(R.id.btn_starlist_like);
            revisionButton = (ImageView) itemView.findViewById(R.id.btn_starlist_revision);
            commentButton = (ImageView) itemView.findViewById(R.id.btn_starlist_comment);
            imageCategory = (RelativeLayout) itemView.findViewById(R.id.starlist_realtive_layout);
            tagText = (TextView) itemView.findViewById(R.id.text_starlist_tag);
            circleUserImage = (CircleImageView) itemView.findViewById(R.id.starlist_main_userimage);
            userNameText = (TextView) itemView.findViewById(R.id.text_starlist_username);
            starButton = (ImageView) itemView.findViewById(R.id.btn_starlist_star);
            dateText = (TextView) itemView.findViewById(R.id.text_starlist_date);
            contentText = (TextView) itemView.findViewById(R.id.text_starlist_content);
            likeCountText = (TextView) itemView.findViewById(R.id.text_starlist_like_count);
            commentCountText = (TextView) itemView.findViewById(R.id.text_starlist_commentlist_count);
            boardPicture1 = (ImageView) itemView.findViewById(R.id.img_starlist_board_pic1);
            boardPictureLayout = (LinearLayout) itemView.findViewById(R.id.board_starlist_pic_layout);

        }


    }

}