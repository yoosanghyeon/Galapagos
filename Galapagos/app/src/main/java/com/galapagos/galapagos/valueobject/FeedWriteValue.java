package com.galapagos.galapagos.valueobject;

import java.io.File;

/**
 * Created by Arous on 2016-11-29.
 */

public class FeedWriteValue {
    //매칭해서 넣어주는 함수

    public String feedWriteProfilePic; //유저 프로필 사진
    public String feedWriteUserId; //유저 아이디
    public String feedWriteUserNicName; //유저 이름
    public Integer feedWriteCategoryNumber; //카테고리 구별 넘버
    public double feedWriteLat; //게시글 위도
    public double feedWriteLong; //게시글 경도
    public String feedWriteUserTag;
    public String feedWriteContent; //게시글 내용
    public String feedWriteisAnomynity; //익명여부
    public String feedLocationNum; //게시글 작성때만 필요
    public String feedLocationName;
    public File feedPicture; //게시글 사진

    public FeedWriteValue() {
    }


    public FeedWriteValue(String feedWriteProfilePic, String feedWriteUserId, String feedWriteUserNicName, Integer feedWriteCategoryNumber,
                          double feedWriteLat, double feedWriteLong, String feedWriteUserTag, String feedWriteContent,
                          String feedWriteisAnomynity, String feedLocationNum, String feedLocationName, File feedPicture) {

        this.feedWriteProfilePic = feedWriteProfilePic;
        this.feedWriteUserId = feedWriteUserId;
        this.feedWriteUserNicName = feedWriteUserNicName;
        this.feedWriteCategoryNumber = feedWriteCategoryNumber;
        this.feedWriteLat = feedWriteLat;
        this.feedWriteLong = feedWriteLong;
        this.feedWriteUserTag = feedWriteUserTag;
        this.feedWriteContent = feedWriteContent;
        this.feedWriteisAnomynity = feedWriteisAnomynity;
        this.feedLocationNum = feedLocationNum;
        this.feedLocationName = feedLocationName;
        this.feedPicture = feedPicture;
    }

    @Override
    public String toString() {
        return "FeedWriteValue{" +
                "feedWriteProfilePic='" + feedWriteProfilePic + '\'' +
                ", feedWriteUserId='" + feedWriteUserId + '\'' +
                ", feedWriteUserNicName='" + feedWriteUserNicName + '\'' +
                ", feedWriteCategoryNumber=" + feedWriteCategoryNumber +
                ", feedWriteLat=" + feedWriteLat +
                ", feedWriteLong=" + feedWriteLong +
                ", feedWriteUserTag='" + feedWriteUserTag + '\'' +
                ", feedWriteContent='" + feedWriteContent + '\'' +
                ", feedWriteisAnomynity='" + feedWriteisAnomynity + '\'' +
                ", feedLocationNum='" + feedLocationNum + '\'' +
                ", feedLocationName='" + feedLocationName + '\'' +
                ", feedPicture=" + feedPicture +
                '}';
    }
}
