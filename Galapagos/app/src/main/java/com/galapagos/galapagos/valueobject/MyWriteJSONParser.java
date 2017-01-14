package com.galapagos.galapagos.valueobject;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yeji on 2016. 12. 5..
 */

public class MyWriteJSONParser {

    public static ArrayList<MyWriteValue> getItemListParsing(String responsedJSON) { //게시글 데이터 JSON으로 파싱해주는 함수
        ArrayList<MyWriteValue> items = null;

        try {

            JSONObject data = new JSONObject(responsedJSON); //제일 바깥에 맘대로 내가 오브젝트 이름 지어줘도 댐

            String msg = data.getString("msg");

            JSONArray datas = data.getJSONArray("data");

            int dataSize = datas.length(); //게시글 데이터 배열 몇개인지

            items = new ArrayList<>();
            for (int i = 0; i < dataSize; i++) {
                MyWriteValue iteminfo = new MyWriteValue();
                JSONObject jsonDatas = datas.getJSONObject(i);

                iteminfo.boarditemKey = jsonDatas.optString("boarditemKey","null"); //수정하기 때문에 필요함
                iteminfo.boardId = jsonDatas.optString("_id","null"); //게시글 고유 번호
                iteminfo.boardCategory = jsonDatas.optInt("boardCategory",0); //게시글 카테고리 구별 넘버
                iteminfo.boardCommentCount = jsonDatas.optInt("boardCommentCount",0); //게시글 댓글 개수
                iteminfo.boardLat = jsonDatas.optInt("boardLat",0); //게시글 위도
                iteminfo.boardLong = jsonDatas.optInt("boardLong",0); //게시글 경도
                iteminfo.boardRevisionCount = jsonDatas.optInt("boardRevisionCount",0); //게시글 정정요청 개수
                iteminfo.boardLikeCount = jsonDatas.optInt("boardLikeCount",0); //게시글 좋아요 개수
                iteminfo.boardUserId = jsonDatas.optString("boardUserId","null"); //게시글 유저 아이디
                iteminfo.boardUserNicName = jsonDatas.optString("boardUserNicName","null"); //게시글 유저 이름
                iteminfo.boardDate = jsonDatas.optString("boardDate","null"); //게시글 날짜
                iteminfo.boardContent = jsonDatas.optString("boardContent","null"); //게시글 내용
                iteminfo.boardTag = jsonDatas.optString("boardTag","null"); //게시글 제목 태그
                iteminfo.boardIsLike = jsonDatas.optInt("boardIsLike", 0); //게시글 좋아요 여부
                iteminfo.boardIsRevision = jsonDatas.optInt("boardIsRevision", 0); //게시글 정정요청 여부
                iteminfo.boardIsStar = jsonDatas.optInt("boardIsStar", 0); //게시글 즐겨찾기 여부

                //사진 데이터 파싱하는 부분
                iteminfo.boardContentPicture1 = jsonDatas.optString("boardContentPicture1","null"); //게시글 사진
                iteminfo.boardUserProfilePicture = jsonDatas.optString("boardUserPicture","null"); //게시글 유저 프로필 사진
                iteminfo.boardisAnonymity = jsonDatas.optInt("boardisAnonymity",0); //게시글 익명여부

                items.add(iteminfo);
                Log.e("MyWriteParseritem",iteminfo.toString());
            }

        } catch (JSONException e) {
            Log.e("MyWritejsonParser", e.toString());
        }
        return items;

    }
}
