package com.galapagos.galapagos.valueobject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Arous on 2016-12-06.
 * 게시물 전문 보기
 */

public class FeedDetailValue {

    public String boardUserPictrue;
    public Double boardLat;
    public Double boardLong;
    public String boardTag;
    public String boardDate;
    public String boardContent;
    public int boardCategory;
    public String boardNicName;
    public String boardUserId;
    public int boardCommentCount;
    public int boardRevisionCount;
    public int[] boardRevision;
    public String boardIsAnomynity;
    public int boardLikeCount;
    public Boolean boardIsLike;
    public Boolean boardIsRevision;
    public Boolean boardIsStar;
    public String boardContentPicture1;
    public ArrayList<CommentDetailValue> commentArrayList;



    public FeedDetailValue() {
        commentArrayList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "FeedDetailValue{" +
                "boardUserPictrue='" + boardUserPictrue + '\'' +
                ", boardLat=" + boardLat +
                ", boardLong=" + boardLong +
                ", boardTag='" + boardTag + '\'' +
                ", boardDate='" + boardDate + '\'' +
                ", boardContent='" + boardContent + '\'' +
                ", boardCategory=" + boardCategory +
                ", boardNicName='" + boardNicName + '\'' +
                ", boardUserId='" + boardUserId + '\'' +
                ", boardCommentCount=" + boardCommentCount +
                ", boardRevisionCount=" + boardRevisionCount +
                ", boardRevision=" + Arrays.toString(boardRevision) +
                ", boardIsAnomynity=" + boardIsAnomynity +
                ", boardLikeCount=" + boardLikeCount +
                ", boardIsLike=" + boardIsLike +
                ", boardIsRevision=" + boardIsRevision +
                ", boardIsStar=" + boardIsStar +
                ", boardContentPicture1='" + boardContentPicture1 + '\'' +
                ", commentArrayList=" + commentArrayList +
                '}';
    }
}
