package com.galapagos.galapagos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.galapagos.galapagos.valueobject.CommentDetailValue;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentDetailAdapter extends RecyclerView.Adapter<CommentDetailAdapter.ViewHolder> {
    private Context mContext;
    ArrayList<CommentDetailValue> commentValueList = new ArrayList<>();

    // Comment item ClickListener
    public interface onCommentItemClickListener {
        public void onCommenItemClicked(final CommentDetailAdapter.ViewHolder holder, View view, CommentDetailValue value, int position);
    }

    CommentDetailAdapter.onCommentItemClickListener commentItemClickListener;

    public void setCommentItemClickListener(CommentDetailAdapter.onCommentItemClickListener clickListener) {
        commentItemClickListener = clickListener;
    }

    public Context getmContext() {
        return mContext;
    }


    public CommentDetailAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setCommentItemClickListener(commentItemClickListener);

        holder.commentUserNicName.setText(commentValueList.get(position).commentUserNicName);
        holder.commentContents.setText(commentValueList.get(position).commentUserComtent);
        holder.commentDate.setText(commentValueList.get(position).commentDate);
        // 사진이 있을 경우만
        if (commentValueList.get(position).commentUserPicture != null){
            Glide.with(mContext).load(commentValueList.get(position).commentUserPicture).into(holder.commentUserProfilePic);
        }


    }

    @Override
    public int getItemCount() {
        return commentValueList.size();
    }

    // 아이템 추가
    public void itmeAddAll(ArrayList<CommentDetailValue> list) {
        if (list != null){
            commentValueList.addAll(list);
            notifyDataSetChanged();
        }

    }

    public void itemAdd(CommentDetailValue value){
        if (value != null){
            commentValueList.add(value);
            notifyDataSetChanged();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View itemView;

        //댓글리스트 내의 위젯들
        CircleImageView commentUserProfilePic;
        TextView commentUserNicName;
        TextView commentDate;
        TextView commentContents;


        public void setCommentItemClickListener(onCommentItemClickListener clickListenr) {
            commentItemClickListener = clickListenr;
        }

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            //댓글 리스트 아이템 초기화
            commentUserProfilePic = (CircleImageView) itemView.findViewById(R.id.comment_detail_userimage);
            commentUserNicName = (TextView) itemView.findViewById(R.id.text_comment_username);
            commentDate = (TextView) itemView.findViewById(R.id.text_comment_date);
            commentContents = (TextView) itemView.findViewById(R.id.text_comment_contents);

            //onClickListener
            commentUserProfilePic.setOnClickListener(this);
            commentUserNicName.setOnClickListener(this);
            commentDate.setOnClickListener(this);
            commentContents.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            CommentDetailValue commentValue = commentValueList.get(position);
            if (commentItemClickListener != null) {
                commentItemClickListener.onCommenItemClicked(ViewHolder.this, view, commentValue, position);
            }
        }
    }
}
