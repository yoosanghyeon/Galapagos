package com.galapagos.galapagos.common;

/**
 * Created by yeji on 2016. 11. 25..
 */

public class NetworkDefineConstant {

    public static String GALA_COMMON_URL;

    //게시글 작성 post
    public static String SERVER_URL_MYWRITE_BOARD;

    //게시글 수정 post
    public static String MODIFY_BOARD_POST;

    //요청 URL path
    public static String SERVER_URL_REQUEST_ITEM;

    // 키워드로 주소검색
    public static String LOCATION_SERACH_REQUEST_URL;

    // 현재 위치로으로 피드 검색
    public static String FEED_SERACH_LOCATION_REQUEST_URL;

    //즐겨찾기 추가
    public static String STAR_LIST_ADD_POST_URL;

    //즐겨찾기 삭제
    public static String STAR_LIST_DELETE_POST_URL;

    //즐겨찾기 리스트 목록
    public static String STAR_LIST_POST_URL;

    //댓글 리스트 목록 &댓글 edit post
    public static String COMMENT_LIST_POST_GET_URL;

    //내가 작성한 게시글 목록
    public static String MYWRITE_LIST_POST_URL;

    // 게시물 전문 보기
    public static String FEED_DETAIL_URL;

    //좋아요 신청
    public static String LIKE_BOARD_POST_URL;

    //좋아요 취소
    public static String LIKE_CANCEL_POST_URL;

    //정정요청 신청
    public static String REVISION_BOARD_POST_URL;

    //정정요청 취소
    public static String REVISION_CANCEL_POST_URL;

    //프로필 정보 수정 요청
    public static String PROFILE_MODIFY_POST_URL;

    //내가 작성한 게시글 삭제 요청
    public static String MYWRITE_DELETE_POST_URL;

    //권한 허락을 안했을때
    public static String GALA_NO_PERMISSION;

    // 메인에서 컨텐츠 검색
    public static String GALA_MAIN_CONTENT_SERARCH;

    public static String GALA_PSUH_SETTING;


    static {

        GALA_COMMON_URL = "http://35.162.13.239:3000";

        SERVER_URL_MYWRITE_BOARD = GALA_COMMON_URL + "/boards";

        SERVER_URL_REQUEST_ITEM = GALA_COMMON_URL + "/boards";

        FEED_SERACH_LOCATION_REQUEST_URL = GALA_COMMON_URL + "/boards/permission?long=%s&lat=%s&page=%s";

        LOCATION_SERACH_REQUEST_URL = GALA_COMMON_URL + "/boards/localSearch?search=%s";

        STAR_LIST_ADD_POST_URL = GALA_COMMON_URL + "/boards/%s/star";

        STAR_LIST_DELETE_POST_URL = GALA_COMMON_URL + "/boards/%s/starCancel";

        COMMENT_LIST_POST_GET_URL = GALA_COMMON_URL + "/board/%s/comment";

        FEED_DETAIL_URL = GALA_COMMON_URL + "/boards/%s";

        STAR_LIST_POST_URL = GALA_COMMON_URL+"/user/checkList";

        MYWRITE_LIST_POST_URL = GALA_COMMON_URL +"/user/mylist";

        LIKE_BOARD_POST_URL = GALA_COMMON_URL+ "/boards/%s/like";

        LIKE_CANCEL_POST_URL = GALA_COMMON_URL +"/boards/%s/likeCancel";

        REVISION_BOARD_POST_URL = GALA_COMMON_URL+ "/boards/%s/revision";

        REVISION_CANCEL_POST_URL = GALA_COMMON_URL+ "/boards/%s/revisionCancel";

        PROFILE_MODIFY_POST_URL = GALA_COMMON_URL + "/user/edit";

        MYWRITE_DELETE_POST_URL = GALA_COMMON_URL+ "/boards/%s/delete";

        GALA_NO_PERMISSION = GALA_COMMON_URL + "/boards/nopermission/";

        GALA_MAIN_CONTENT_SERARCH = GALA_COMMON_URL + "/boards/%s/%s/content?boardContent=%s&page=%s";

        GALA_PSUH_SETTING = GALA_COMMON_URL + "/user/push";

        MODIFY_BOARD_POST = GALA_COMMON_URL + "/boards/%s/revise";

    }

}
