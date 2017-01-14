package com.galapagos.galapagos.valueobject;

/**
 * Created by Arous on 2016-12-06.
 */

public class CommentDetailValue {

    public String commentBoardId;
    public String commentUserId;
    public String commentUserNicName;
    public String commentUserComtent;
    public String commentDate;
    public String commentUserPicture;

    public CommentDetailValue() {
    }

    @Override
    public String toString() {
        return "CommentDetailValue{" +
                "commentBoardId='" + commentBoardId + '\'' +
                ", commentUserId='" + commentUserId + '\'' +
                ", commentUserNicName='" + commentUserNicName + '\'' +
                ", commentUserComtent='" + commentUserComtent + '\'' +
                ", commentDate='" + commentDate + '\'' +
                ", commentUserPicture='" + commentUserPicture + '\'' +
                '}';
    }
}
