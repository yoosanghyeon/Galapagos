package com.galapagos.galapagos.valueobject;

/**
 * Created by yeji on 2016. 12. 5..
 */

public class CommentSendData {

    public String boardUserId; //게시글 쓴 사람인데 피드게시글 겟할때 받음 그걸로 접근
    public String commentBoardTag; //댓글 달 게시글의 제목
    public String commentUserId; //propertyManager 저장해놓은 아이디
    public String commentUserPicture; //propertyManager 저장해놓은 프로필 사진
    public String commentUserNicName; //propertyManager 저장해놓은 닉네임
    public String commentContent; //댓글 내용

    public CommentSendData(){}


    @Override
    public String toString() {
        return "CommentSendData{" +
                "boardUserId='" + boardUserId + '\'' +
                ", commentUserId='" + commentUserId + '\'' +
                ", commentUserPicture='" + commentUserPicture + '\'' +
                ", commentUserNicName='" + commentUserNicName + '\'' +
                ", commentContent='" + commentContent + '\'' +
                '}';
    }
}
