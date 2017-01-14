package com.galapagos.galapagos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.galapagos.galapagos.valueobject.CommentGetData;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>  {
    private Context mContext;
    ArrayList<CommentGetData> iteminfo = new ArrayList<>();

    // Comment item ClickListener
    public interface onCommentItemClickListener {
        public void onCommenItemClicked(CommentAdapter.ViewHolder holder, View view, CommentGetData value, int position);
    }

    CommentAdapter.onCommentItemClickListener commentItemClickListener;

    public void setCommentItemClickListener(CommentAdapter.onCommentItemClickListener clickListener){
        commentItemClickListener = clickListener;
    }

    public Context getmContext() {
        return mContext;
    }


    public CommentAdapter(Context context) {
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

        CommentGetData commentInfo = iteminfo.get(position);
        //프로필사
        holder.commentUserNicName.setText(commentInfo.commentUserNicName);
        holder.commentContents.setText(commentInfo.commentContents);
        holder.commentDate.setText(commentInfo.commentDate);
    }

    @Override
    public int getItemCount() {
        return iteminfo.size();
    }
    public void additemChart(ArrayList<CommentGetData> iteminfos) {
        iteminfo.addAll(iteminfos);
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

            CommentGetData commentInfo = iteminfo.get(position);
            if (commentItemClickListener != null) {
                commentItemClickListener.onCommenItemClicked(ViewHolder.this, view, commentInfo, position);
            }
        }
    }
}
