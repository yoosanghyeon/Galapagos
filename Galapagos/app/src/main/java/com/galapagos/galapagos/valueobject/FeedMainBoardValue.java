package com.galapagos.galapagos.valueobject;

/**
 * Created by yeji on 2016. 11. 25..
 */

public class FeedMainBoardValue {

    public String boardId;
    public String boardUserId; //게시글을 작성하는 사람의 아이디
    public String boardUserNicName;
    public String boardUserProfilePicture;
    public int boardCategory;
    public int boardLocationNum;
    public String boardLocationName;
    public String boardContent;
    public String boardisAnonymity;
    public String boardDate;
    public int boardCommentCount;
    public int boardLat;
    public int boardLong;
    public int boardRevisionCount;
    public String boardTag;
    public int boardLikeCount;
    public String boardUserPicture1;
    public Boolean boardIsLike;
    public Boolean boardIsRevision;
    public Boolean boardIsStar;

    @Override
    public String toString() {
        return "StarBoardValue{" +
                "boardId='" + boardId + '\'' +
                ", boardUserId='" + boardUserId + '\'' +
                ", boardUserNicName='" + boardUserNicName + '\'' +
                ", boardUserProfilePicture='" + boardUserProfilePicture + '\'' +
                ", boardCategory=" + boardCategory +
                ", boardLocationNum=" + boardLocationNum +
                ", boardLocationName='" + boardLocationName + '\'' +
                ", boardContent='" + boardContent + '\'' +
                ", boardisAnonymity=" + boardisAnonymity +
                ", boardDate='" + boardDate + '\'' +
                ", boardCommentCount=" + boardCommentCount +
                ", boardLat=" + boardLat +
                ", boardLong=" + boardLong +
                ", boardRevisionCount=" + boardRevisionCount +
                ", boardTag='" + boardTag + '\'' +
                ", boardLikeCount=" + boardLikeCount +
                ", boardUserPicture1='" + boardUserPicture1 + '\'' +
                ", boardIsLike=" + boardIsLike +
                ", boardIsRevision=" + boardIsRevision +
                ", boardIsStar=" + boardIsStar +
                '}';
    }
}
