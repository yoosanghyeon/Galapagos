package com.galapagos.galapagos.valueobject;

/**
 * Created by yeji on 2016. 12. 1..
 */

public class StarButtonValue {

    public String starBoardId; //스타버튼 클릭한 게시글 고유번호
    public String starUserId; //유저 아이디

    public StarButtonValue() {}

    public StarButtonValue(String boardId, String userId) {

        super();

        this.starBoardId = boardId;
        this.starUserId = userId;
    }
}
