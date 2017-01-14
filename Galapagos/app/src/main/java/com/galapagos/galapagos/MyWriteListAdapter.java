package com.galapagos.galapagos;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.galapagos.galapagos.valueobject.MyWriteValue;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyWriteListAdapter extends RecyclerView.Adapter<MyWriteListAdapter.ViewHolder> {
    private Context mContext;
    ArrayList<MyWriteValue> iteminfo = new ArrayList<>();


    // 즐겨찾기 추가목록 아이템 Click Listener
    public interface onMyWriteListClickListener {
        public void onMyWriteItemClicked(ViewHolder holder, View view, MyWriteValue value, int position);
    }

    onMyWriteListClickListener myWriteListClickListener;

    public void setMyWriteListClickListener(onMyWriteListClickListener clickListener) {
        myWriteListClickListener = clickListener;
    }

    public Context getmContext() {
        return mContext;
    }


    public MyWriteListAdapter(Context context) {
        mContext = context;
        iteminfo = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mywritelist_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setMyWriteListClickListener(myWriteListClickListener);

        //리턴한 홀더의
        MyWriteValue itemInfo = iteminfo.get(position);

        holder.tagText.setText(itemInfo.boardTag);
        holder.userNameText.setText(itemInfo.boardUserNicName);
        holder.contentText.setText(itemInfo.boardContent);
        holder.dateText.setText(itemInfo.boardDate);
        holder.commentCountText.setText("댓글 " + itemInfo.boardCommentCount);

        //사진데이터에 사진 URL이 들어올 시 게시글 형태
        if (!itemInfo.boardUserProfilePicture.equals("null")) {
            String uri = itemInfo.boardUserProfilePicture;
            Glide.with(getmContext())
                    .load(uri)
                    .override(1000, 1000)
                    .centerCrop()
                    .into(holder.circleUserImage);
        }

        if (itemInfo.boardContentPicture1 != null) {
            String picUri = itemInfo.boardContentPicture1;
            holder.boardPictureLayout.setVisibility(View.VISIBLE);
            Glide.with(getmContext())
                    .load(picUri)
                    .override(1000, 1000)
                    .centerCrop()
                    .into(holder.boardPicture1);
        }

        //카테고리데이터에 들어온 값에 따라 카테고리 색상 바꿔주기
        switch (itemInfo.boardCategory){
            case 8:
                holder.imageCategory.setImageResource(R.drawable.category0);
                break;
            case 1:
                holder.imageCategory.setImageResource(R.drawable.category1);
                break;
            case 2:
                holder.imageCategory.setImageResource(R.drawable.category2);
                break;
            case 3:
                holder.imageCategory.setImageResource(R.drawable.category3);
                break;
            case 4:
                holder.imageCategory.setImageResource(R.drawable.category4);
                break;
            case 5:
                holder.imageCategory.setImageResource(R.drawable.category5);
                break;
            case 6:
                holder.imageCategory.setImageResource(R.drawable.category6);
                break;
            default:
                break;
        }

    }

    @Override
    public int getItemCount() {
        return iteminfo.size();
    }
    public MyWriteValue findMyWriteValue(int position){
        return iteminfo.get(position);
    }
    public void additemChart(ArrayList<MyWriteValue> iteminfos) {
        iteminfo.addAll(iteminfos);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View itemView;
        MyWriteValue value;

        //게시글 아이템 내의 위젯들
        public ImageView commentButton;
        public ImageView modifyButton;
        public ImageView imageCategory;
        public TextView tagText;
        public CircleImageView circleUserImage;
        public TextView userNameText;
        public ImageView deleteButton;
        public TextView dateText;
        public TextView contentText;
        public TextView commentCountText;
        public ImageView boardPicture1;
        public LinearLayout boardPictureLayout;

        public CardView cardView;
        public RelativeLayout relativeLayout;


        public void setMyWriteListClickListener(onMyWriteListClickListener clickListenr) {
            myWriteListClickListener = clickListenr;
        }

        public ViewHolder(View v) {//행에 넣을 아이템들 객체화
            super(v);
            itemView = v;

            //게시글 아이템 내의 위젯들 초기화
            commentButton = (ImageView) itemView.findViewById(R.id.btn_write_comment);
            modifyButton = (ImageView) itemView.findViewById(R.id.btn_write_modify);
            imageCategory = (ImageView) itemView.findViewById(R.id.img_write_category);
            tagText = (TextView) itemView.findViewById(R.id.text_write_tag);
            circleUserImage = (CircleImageView) itemView.findViewById(R.id.img_write_userimage);
            userNameText = (TextView) itemView.findViewById(R.id.text_write_username);
            deleteButton = (ImageView) itemView.findViewById(R.id.btn_write_delete);
            dateText = (TextView) itemView.findViewById(R.id.text_write_date);
            contentText = (TextView) itemView.findViewById(R.id.text_write_content);
            commentCountText = (TextView) itemView.findViewById(R.id.text_write_comment_count);
            boardPicture1 = (ImageView) itemView.findViewById(R.id.img_write_board_pic1);
            boardPictureLayout = (LinearLayout) itemView.findViewById(R.id.board_write_pic_layout);
            cardView = (CardView) itemView.findViewById(R.id.card_write_list);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.mywrite_relative_layout);

            //setOnClickListener
            commentButton.setOnClickListener(this);
            modifyButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);
            cardView.setOnClickListener(this);
            boardPicture1.setOnClickListener(this);
            contentText.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();

            if (myWriteListClickListener != null) {
                myWriteListClickListener.onMyWriteItemClicked(ViewHolder.this, view, value, position);
            }
        }
    }
}