package com.galapagos.galapagos.valueobject;

/**
 * Created by yeji on 2016. 12. 6..
 */

public class IsLikeRevisionValue {
    public String userId;
    public String boardId;

    public IsLikeRevisionValue(){}

    public IsLikeRevisionValue(String userId, String boardId){
        this.userId = userId;
        this.boardId = boardId;
    }

}
