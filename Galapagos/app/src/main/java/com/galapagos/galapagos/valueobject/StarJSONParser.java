package com.galapagos.galapagos.valueobject;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yeji on 2016. 11. 25..
 */

public class StarJSONParser {// 직접적으로 JSON 형태로 parsing 해주는 곳
    public static ArrayList<StarBoardValue> getItemChartParsing(String responsedJSON) { //게시글 데이터 JSON으로 파싱해주는 함수
        ArrayList<StarBoardValue> items = null;

        try {

            JSONObject data = new JSONObject(responsedJSON); //제일 바깥에 맘대로 내가 오브젝트 이름 지어줘도 댐

            String msg = data.getString("msg");

            JSONArray datas = data.getJSONArray("data");

            int dataSize = datas.length(); //게시글 데이터 배열 몇개인지

            items = new ArrayList<>();
            for (int i = 0; i < dataSize; i++) {
                StarBoardValue iteminfo = new StarBoardValue();
                JSONObject jsonDatas = datas.getJSONObject(i);

                iteminfo.boardId = jsonDatas.optString("_id", "null"); //게시글 고유 번호
                iteminfo.boardCategory = jsonDatas.optInt("boardCategory", 0); //게시글 카테고리 구별 넘버
                iteminfo.boardisAnonymity = jsonDatas.optString("boardisAnonymity", "false"); //게시글 익명여부
                iteminfo.boardCommentCount = jsonDatas.optInt("boardCommentCount", 0); //게시글 댓글 개수
                iteminfo.boardLat = jsonDatas.optInt("boardLat", 0); //게시글 위도
                iteminfo.boardLong = jsonDatas.optInt("boardLong", 0); //게시글 경도
                iteminfo.boardRevisionCount = jsonDatas.optInt("boardRevisionCount", 0); //게시글 정정요청 개수
                iteminfo.boardLikeCount = jsonDatas.optInt("boardLikeCount", 0); //게시글 좋아요 개수
                iteminfo.boardUserId = jsonDatas.optString("boardUserId", "null"); //게시글 유저 아이디
                iteminfo.boardUserNicName = jsonDatas.optString("boardUserNicName", "null"); //게시글 유저 이름
                iteminfo.boardUserProfilePicture = jsonDatas.optString("boardUserPicture", "null"); //게시글 유저 프로필 사진
                iteminfo.boardDate = jsonDatas.optString("boardDate", "null"); //게시글 날짜
                iteminfo.boardContent = jsonDatas.optString("boardContent", "null"); //게시글 내용
                iteminfo.boardTag = jsonDatas.optString("boardTag", "null"); //게시글 제목 태그
                iteminfo.boardIsLike = jsonDatas.optBoolean("boardIsLike", false); //게시글 좋아요 여부
                iteminfo.boardIsRevision = jsonDatas.optBoolean("boardIsRevision", false); //게시글 정정요청 여부
                iteminfo.boardIsStar = jsonDatas.optBoolean("boardIsStar", false); //게시글 즐겨찾기 여부
                //사진 데이터 파싱하는 부분
                iteminfo.boardUserPicture1 = jsonDatas.optString("boardContentPicture1", "null"); //게시글 사진

                items.add(iteminfo);
                Log.e("FeedStarParseritem",iteminfo.toString());
            }

        } catch (JSONException e) {
            Log.e("FeedStarjsonParser", e.toString());
        }
        return items;
    }

}
