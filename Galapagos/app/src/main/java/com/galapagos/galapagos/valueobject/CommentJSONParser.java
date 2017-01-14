package com.galapagos.galapagos.valueobject;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yeji on 2016. 12. 3..
 */

public class CommentJSONParser {
    public static ArrayList<CommentGetData> getCommentItemChartParsing(String responsedJSON) { //게시글 데이터 JSON으로 파싱해주는 함수
        ArrayList<CommentGetData> items = null;

        try {

            JSONObject data = new JSONObject(responsedJSON); //제일 바깥에 맘대로 내가 오브젝트 이름 지어줘도 댐

            String msg = data.getString("msg");
            Log.e("msg", msg);

            JSONArray datas = data.getJSONArray("result");

            int dataSize = datas.length(); //게시글 데이터 배열 몇개인지

            items = new ArrayList<>();
            for (int i = 0; i < dataSize; i++) {
                CommentGetData iteminfo = new CommentGetData();
                JSONObject jsonDatas = datas.getJSONObject(i);

                iteminfo.commentUserId = jsonDatas.optString("commentUserId","null");
                iteminfo.commentUserNicName = jsonDatas.optString("commentUserNicName","null"); //댓글 유저 이름
                iteminfo.commentUserProfilePicture = jsonDatas.optString("commentUserPicture","null"); //댓글 유저 프로필 사진
                iteminfo.commentDate = jsonDatas.optString("commentDate","null"); //댓글 날짜
                iteminfo.commentContents = jsonDatas.optString("commentContent","null"); //댓글 내용

                items.add(iteminfo);
            }

        } catch (JSONException e) {
            Log.e("jjsonparser", e.toString());
        }
        return items;

    }
}
