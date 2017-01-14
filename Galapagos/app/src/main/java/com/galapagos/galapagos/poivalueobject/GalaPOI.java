package com.galapagos.galapagos.poivalueobject;


public class GalaPOI {

    public String boardUserId;
    public String boardUserName;
    public int boardCategory;
    public String boardContent;
    public String boardTag;
    public Double boardLatitude;
    public Double boardLongtitude;
    public String boardId;


    @Override
    public String toString() {
        return "GalaPOI{" +
                "boardUserId='" + boardUserId + '\'' +
                ", boardUserName='" + boardUserName + '\'' +
                ", boardCategory=" + boardCategory +
                ", boardContent='" + boardContent + '\'' +
                ", boardTag='" + boardTag + '\'' +
                ", boardLatitude=" + boardLatitude +
                ", boardLongtitude=" + boardLongtitude +
                ", boardId='" + boardId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
