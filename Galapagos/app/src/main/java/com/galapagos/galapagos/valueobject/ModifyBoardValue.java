package com.galapagos.galapagos.valueobject;

/**
 * Created by yeji on 2016. 12. 12..
 */

public class ModifyBoardValue {

    public String boarditemKey;
    public String boardUserPictrue;
    public String boardTag;
    public String boardContent;
    public int boardCategory;
    public String boardNicName;
    public String boardUserId;
    public String boardIsAnomynity;
    public String boardContentPicture1;

    @Override
    public String toString() {
        return "FeedDetailValue{" +
                "boardUserPictrue='" + boardUserPictrue + '\'' +
                ", boardTag='" + boardTag + '\'' +
                ", boarditemKey='" + boarditemKey + '\'' +
                ", boardContent='" + boardContent + '\'' +
                ", boardCategory=" + boardCategory +
                ", boardNicName='" + boardNicName + '\'' +
                ", boardUserId='" + boardUserId + '\'' +
                ", boardIsAnomynity=" + boardIsAnomynity +
                ", boardContentPicture1='" + boardContentPicture1 + '\'' +
                '}';
    }
}
