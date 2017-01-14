package com.galapagos.galapagos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.galapagos.galapagos.common.AppBaseActivity;
import com.galapagos.galapagos.common.GalaPoiCategory;
import com.galapagos.galapagos.common.NetworkDefineConstant;
import com.galapagos.galapagos.common.OkHttpManager;
import com.galapagos.galapagos.common.PropertyManager;
import com.galapagos.galapagos.valueobject.FeedWriteValue;
import com.galapagos.galapagos.valueobject.ModifyBoardValue;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModifyActivity extends AppBaseActivity implements View.OnClickListener{
    // category select
    private static final int CAT_SLECTED_REQUEST = 401;
    private static final int PICK_FROM_GALLERY = 100;
    private static final int PICK_FROM_CAMERA = 200;

    //카테고리 구별해줄 변수
    int categoryNumber;

    RelativeLayout writeSelectedLayout;
    EditText editWriteTag;
    ImageButton writeCatSelected;
    CircleImageView imageWriteProfile;
    TextView textWriteUserName;
    TextView textWriteUserLocation;
    EditText editFeedWriteContent;
    LinearLayout picUploadLayout;
    ImageView picUploadViewOne;
    ImageButton btnWriteLocation, btnWritePic, btnWriteAnonymity, btnFeedSendConfirm;

    // 현재 위치명, 위도 경도
    String presentLocation;
    Double latitude;
    Double longtitude;

    String userNicName;
    String userID;
    String userPic;

    String boardId;
    String boarditemKey;

    private FeedWriteValue fwValueObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_layout);


        Intent intent = getIntent();
        boardId = intent.getStringExtra("boardId");
        boarditemKey = intent.getStringExtra("boarditemKey");

        //게시글 수정할 때 원본 내용 먼저 세팅시키는 어싱크
        new ModifiyOriginalAsyncTask(this).execute(boardId);

        //게시글 post할 정보들 담을 객체
        if (fwValueObject == null) {
            fwValueObject = new FeedWriteValue();
        }

        //GalaLocationService에서 가져온 현재 위도, 경도


        // 카테고리 레이아웃(상단 색깔)
        writeSelectedLayout = (RelativeLayout) findViewById(R.id.modify_selected_layout);
        // 키워드(제목)
        editWriteTag = (EditText) findViewById(R.id.edit_modify_tag);
        // 게시물 카테고리 선택 버튼()
        writeCatSelected = (ImageButton) findViewById(R.id.modify_cat_select);
        // 게시물 작성시 자신의 사진
        imageWriteProfile = (CircleImageView) findViewById(R.id.image_modify_profile);
        // 유저 이름
        textWriteUserName = (TextView) findViewById(R.id.text_modify_user_name);
        // 현재 유저 위치 또는 지역 검색하는 위치를 보여주는 텍스트뷰
        textWriteUserLocation = (TextView) findViewById(R.id.text_modify_user_location);
        // 유저가 작성한 text
        editFeedWriteContent = (EditText) findViewById(R.id.edit_modify_content);
        // Visialbe 해줄 레이아웃
        picUploadLayout = (LinearLayout) findViewById(R.id.pic_modify_upload_layout);
        // 사진의 이미지뷰
        picUploadViewOne = (ImageView) findViewById(R.id.pic_modify_upload_view1);
        // 지역등록(현재 위치가 default)
        btnWriteLocation = (ImageButton) findViewById(R.id.btn_modify_location);
        // 사진 첨부 버튼
        btnWritePic = (ImageButton) findViewById(R.id.btn_modify_pic);
        // 게시물 익명버튼
        btnWriteAnonymity = (ImageButton) findViewById(R.id.btn_modify_anonymity);
        // 게시물 전송버튼
        btnFeedSendConfirm = (ImageButton) findViewById(R.id.btn_modify_send_confirm);

        // 카테고리 처음에는 무조건 8번 선택
        fwValueObject.feedWriteCategoryNumber = 8;

        categoryNumber = 8;

        // 프로필 셋팅
        Glide.with(this).load(PropertyManager.getInstance().getPicUri()).thumbnail(0.5f).into(imageWriteProfile);

        fwValueObject.feedWriteLat = Double.valueOf(PropertyManager.getInstance().getLatitudei());
        fwValueObject.feedWriteLong = Double.valueOf(PropertyManager.getInstance().getLongtitude());

        doReverseGeocoding(fwValueObject.feedWriteLat, fwValueObject.feedWriteLong);

        Log.e("WriteLocation", fwValueObject.feedWriteLong + " ::: " + fwValueObject.feedWriteLat + "");

        // Btn ClickEvent
        writeCatSelected.setOnClickListener(this);
        btnWritePic.setOnClickListener(this);
        btnWriteLocation.setOnClickListener(this);
        btnFeedSendConfirm.setOnClickListener(this);
        btnWriteAnonymity.setOnClickListener(this);

        // sharedPerferences 에서 저장된 유저 이름 가져오기

        userNicName = PropertyManager.getInstance().getNickName();
        userID = PropertyManager.getInstance().getUserId();
        userPic = PropertyManager.getInstance().getPicUri();

        textWriteUserName.setText(userNicName);

        String location = doReverseGeocoding(fwValueObject.feedWriteLat, fwValueObject.feedWriteLong);
        textWriteUserLocation.setText("현재 위치 " + location);

        fwValueObject.feedWriteisAnomynity = "false";

        //익명게시여부 디폴트값 false
        Log.e("feedWrite 익명 default", "" + fwValueObject.feedWriteisAnomynity);

    }

    // Google GeoCoding 활용해 serchbar edittext에 현재 위치 힌트 설정
    private String doReverseGeocoding(double latitude, double longitude) {
        String myLocation = null;
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(this, Locale.KOREA);
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                myLocation = addresses.get(0).getThoroughfare();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("GEOCODING_ERROR", "지오코딩 구현 안됨");
        }
        return myLocation;
    }

    private static final int FEED_WRITRE_LOCATION_SERACH = 202;

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            //카테고리 선택
            case R.id.modify_cat_select:
                Log.e("button", "Selected");
                Intent intent = new Intent(getApplicationContext(), CatChoiceActivity.class);
                startActivityForResult(intent, CAT_SLECTED_REQUEST);
                break;
            case R.id.btn_modify_pic:
                // 사진선택
                PicAddDialogFragment picAddDialogFragment = PicAddDialogFragment.newInstances();
                picAddDialogFragment.SetOnStartImageLoadListener(new PicAddDialogFragment.OnStartImageLoadListener() {
                    @Override
                    public void StartImageLoad(int i) {
                        checkPermission();
                        switch (i) {
                            case PICK_FROM_GALLERY:
                                Intent intent_gallery = new Intent(Intent.ACTION_PICK);
                                intent_gallery.setType(MediaStore.Images.Media.CONTENT_TYPE);
                                intent_gallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent_gallery, PICK_FROM_GALLERY);
                                break;
                            case PICK_FROM_CAMERA:
                                Intent intent_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent_camera, PICK_FROM_CAMERA);
                                break;
                        }

                    }
                });
                picAddDialogFragment.show(getSupportFragmentManager(), "?ъ쭊泥⑤?");
                break;
            case R.id.btn_modify_location:
                //지역등록 버튼 클릭시
                Intent registLocationIntent = new Intent(getApplicationContext(), RegistLocationWriteActivity.class);
                startActivityForResult(registLocationIntent, FEED_WRITRE_LOCATION_SERACH);
                break;
            case R.id.btn_modify_anonymity:
                //익명버튼 클릭시
                if (fwValueObject.feedWriteisAnomynity.equals("false")) {
                    btnWriteAnonymity.setImageResource(R.drawable.anonymity_button_use);
                    textWriteUserName.setText("익명 게시");
                    fwValueObject.feedWriteisAnomynity = "true";
                } else if (fwValueObject.feedWriteisAnomynity.equals("true")) {
                    btnWriteAnonymity.setImageResource(R.drawable.anonymity_button_un);
                    textWriteUserName.setText(PropertyManager.getInstance().getNickName());
                    fwValueObject.feedWriteisAnomynity = "false";
                }
                Log.e("anonymityclick", "" + fwValueObject.feedWriteisAnomynity);

                break;
            case R.id.btn_modify_send_confirm:
                //게시글 등록 버튼 클릭시

                //body안에 데이터 넣어주기
                // 유저 아이디
                fwValueObject.feedWriteUserId = userID;
                // 유저 프로필 사진
                fwValueObject.feedWriteProfilePic = userPic;
                fwValueObject.feedWriteUserNicName = userNicName;
                //카테고리 넘버 넣기
                fwValueObject.feedWriteCategoryNumber = categoryNumber;
                //게시글 위치 ~동 넣기
                //게시글 위도,경도 넣기
                //게시글 내용 넣기
                fwValueObject.feedWriteContent = editFeedWriteContent.getText().toString();
                //게시글 태그 넣기
                fwValueObject.feedWriteUserTag = editWriteTag.getText().toString();
                //게시글 사진 있으면 넣기

                if (fwValueObject.feedLocationName == null) {
                    fwValueObject.feedLocationName = "";
                }


                Log.e("confirm", "contentcomplete");
                //유효성 체크 //데이터가 입력 되었는지 아닌지 확인
                if (fwValueObject.feedWriteContent == null || fwValueObject.feedWriteContent.length() <= 0) {
                    Toast.makeText(getApplicationContext(), "게시글 내용을 입력하세요", Toast.LENGTH_SHORT).show();
                    editFeedWriteContent.requestFocus();
                    return;
                } else if (fwValueObject.feedWriteUserTag == null || fwValueObject.feedWriteUserTag.length() <= 0) {
                    Toast.makeText(getApplicationContext(), "태그 내용을 입력하세요", Toast.LENGTH_SHORT).show();
                    editWriteTag.requestFocus();
                    return;
                } else if (textWriteUserLocation.getText().equals("") && textWriteUserLocation.getText().length() <= 0) {

                    Toast.makeText(getApplicationContext(), "지역 등록을 해주세요", Toast.LENGTH_SHORT).show();

                } else {

                    if (fwValueObject.feedWriteCategoryNumber == null) {
                        fwValueObject.feedWriteCategoryNumber = 8;
                    }

                    if (fwValueObject.feedLocationName == null) {
                        fwValueObject.feedLocationName = "1";
                    }

                    //수정하기
                    new AsyncModifyBoard(this).execute(fwValueObject);
                    Toast.makeText(getApplicationContext(),"게시글 수정이 완료되었습니다. 새로고침 하세요.",Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(R.anim.slide_out_right, R.anim.stay);
                }


        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (FEED_WRITRE_LOCATION_SERACH == requestCode && resultCode == RESULT_OK) {
            fwValueObject.feedLocationName = data.getStringExtra("locationSub");
            Log.e("feedLocationName", data.getStringExtra("locationSub"));
            fwValueObject.feedLocationNum = data.getStringExtra("locationNum");
            Log.e("LOCATION_SUB", data.getStringExtra("locationSub"));
            textWriteUserLocation.setText("설정 지역 :: " + data.getStringExtra("locationSub"));
            fwValueObject.feedWriteLong = 0;
            fwValueObject.feedWriteLat = 0;
            Log.e("LocationCheck", fwValueObject.feedLocationName + "::" + fwValueObject.feedLocationNum);
        }


        switch (requestCode) {
            case PICK_FROM_GALLERY: {
                if (data != null) {
                    Uri galleryUri = data.getData();
                    picUploadLayout.setVisibility(View.VISIBLE);
                    Glide.with(this).load(galleryUri).thumbnail(0.1f).into(picUploadViewOne);
                    fwValueObject.feedPicture = new File(getRealPathFromURI(getApplicationContext(), galleryUri));
                    break;
                }

            }
            case PICK_FROM_CAMERA: {
                if (data != null) {
                    Uri captureUri = data.getData();
                    picUploadLayout.setVisibility(View.VISIBLE);
                    Glide.with(this).load(captureUri).thumbnail(0.1f).into(picUploadViewOne);
                    fwValueObject.feedPicture = new File(getRealPathFromURI(getApplicationContext(), captureUri));
                    break;
                }

            }
        }
        //카테고리 선택하는 코드
        switch (resultCode) {
            case 8:
                categoryNumber = 8;
                fwValueObject.feedWriteCategoryNumber = categoryNumber;
                writeCatSelected.setImageResource(R.drawable.category0_select_button);
                writeSelectedLayout.setBackgroundResource(R.drawable.category0_frame);
                break;
            case 1:
                categoryNumber = 1;
                fwValueObject.feedWriteCategoryNumber = categoryNumber;
                writeCatSelected.setImageResource(R.drawable.category1_select_button);
                writeSelectedLayout.setBackgroundResource(R.drawable.category1_frame);
                break;
            case 2:
                categoryNumber = 2;
                fwValueObject.feedWriteCategoryNumber = categoryNumber;
                writeCatSelected.setImageResource(R.drawable.category2_select_button);
                writeSelectedLayout.setBackgroundResource(R.drawable.category2_frame);
                break;
            case 3:
                categoryNumber = 3;
                fwValueObject.feedWriteCategoryNumber = categoryNumber;
                writeCatSelected.setImageResource(R.drawable.category3_select_button);
                writeSelectedLayout.setBackgroundResource(R.drawable.category3_frame);
                break;
            case 4:
                categoryNumber = 4;
                fwValueObject.feedWriteCategoryNumber = categoryNumber;
                writeCatSelected.setImageResource(R.drawable.category4_select_button);
                writeSelectedLayout.setBackgroundResource(R.drawable.category4_frame);
                break;
            case 5:
                categoryNumber = 5;
                fwValueObject.feedWriteCategoryNumber = categoryNumber;
                writeCatSelected.setImageResource(R.drawable.category5_select_button);
                writeSelectedLayout.setBackgroundResource(R.drawable.category5_frame);
                break;
            case 6:
                categoryNumber = 6;
                fwValueObject.feedWriteCategoryNumber = categoryNumber;
                writeCatSelected.setImageResource(R.drawable.category6_select_button);
                writeSelectedLayout.setBackgroundResource(R.drawable.category6_frame);
                break;
            default:
                break;

        }

    }

    private final int MY_PERMISSION_REQUEST_STORAGE = 100;

    private void checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to write the permission.
                    Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
                }

                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_REQUEST_STORAGE);

            } else {
                //사용자가 언제나 허락
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //사용자가 퍼미션을 OK했을 경우
                } else {
                    Log.d("파일업로드", "Permission always deny");
                    //사용자가 퍼미션을 거절했을 경우
                }
                break;
        }
    }

    // 실제 단말기에 저장되어 있는 URI를 얻어오는 메서드
    //카메라나 갤러리에서 선택된 URI 우리가 쓸 수 있게 변동시켜주는 함수
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    //게시글 수정시 게시글 입력 및 parsing
    private class AsyncModifyBoard extends android.os.AsyncTask<FeedWriteValue, Integer, String> {
        private Context mContext;

        final MediaType pngType = MediaType.parse("image/*");

        public AsyncModifyBoard(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {/*
                progressDialog = new ProgressDialog(mContext);
                progressDialog.setMessage("서버 입력 중...");
                progressDialog.show();*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(FeedWriteValue... feedWriteValues) {

            Log.e("modifyValue", feedWriteValues[0].toString());

            final MediaType pngType = MediaType.parse("image/*");
            String resultMsg = null;
            Response response = null;
            OkHttpClient toServer = null;

            RequestBody fileUploadBody = null;
            MultipartBody.Builder multipartBody = null;

            toServer = OkHttpManager.getOkHttpClient(); //서버로 보내는 중간 아이 생성


            try {
                if (feedWriteValues[0].feedLocationName == null) {
                    feedWriteValues[0].feedLocationName = "1";
                }

                Log.e("modifyboardProfile", PropertyManager.getInstance().getPicUri());
                multipartBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("boardUserId", feedWriteValues[0].feedWriteUserId)
                        .addFormDataPart("boardUserPicture", PropertyManager.getInstance().getPicUri())
                        .addFormDataPart("boardUserNicName", feedWriteValues[0].feedWriteUserNicName)
                        .addFormDataPart("boardCategory", feedWriteValues[0].feedWriteCategoryNumber.toString())
                        .addFormDataPart("boardContent", feedWriteValues[0].feedWriteContent)
                        .addFormDataPart("boardTag", feedWriteValues[0].feedWriteUserTag)
                        .addFormDataPart("boardIsAnomynity", String.valueOf(feedWriteValues[0].feedWriteisAnomynity))
                        .addFormDataPart("boarditemKey", boarditemKey);

                Log.e("modifiyAnom", "" + feedWriteValues[0].feedWriteisAnomynity);
                Log.e("modifyitemKey",boarditemKey);

                if (feedWriteValues[0].feedPicture != null) {
                    multipartBody.addFormDataPart("boardContentPicture1",
                            feedWriteValues[0].feedPicture.getName(), RequestBody.create(pngType, feedWriteValues[0].feedPicture));
                    Log.e("boardContentPicture1", ""+feedWriteValues[0].feedPicture);
                }

                fileUploadBody = multipartBody.build();

                //요청 세팅
                Request request = new Request.Builder()
                        .url(String.format(NetworkDefineConstant.MODIFY_BOARD_POST, boardId))
                        .post(fileUploadBody)
                        .build();

                response = toServer.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String msg = jsonObject.getString("msg");
                    Log.e("FEED_MODIFY", msg);

                    return resultMsg;
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return resultMsg;
        }

        @Override
        protected void onPostExecute(String result) {//result = 위에서 리턴된 feedWriteResultValue
            // UI도 고치고...
            try {
                /*progressDialog.dismiss();*/
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (result == null) {
                return;
            }
            if (result.equals("")) {

            } else {

            }
        }
    }

    //게시글 원본 내용 먼저 세팅
    private class ModifiyOriginalAsyncTask extends AsyncTask<String, Integer, ModifyBoardValue> {

        OkHttpClient okHttpClient;
        ProgressDialog progressDialog;
        Context mContext;

        public ModifiyOriginalAsyncTask(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected void onPreExecute() {
            try {
                progressDialog = new ProgressDialog(mContext);
                progressDialog.setMessage("데이터 받아오는 중");
                progressDialog.onTouchEvent(null);
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPreExecute();
        }

        @Override
        protected ModifyBoardValue doInBackground(String... strings) {

            okHttpClient = OkHttpManager.getOkHttpClient();


            String url = String.format(NetworkDefineConstant.FEED_DETAIL_URL, strings[0]);

            FormBody formBody = new FormBody.Builder()
                    .add("userId", PropertyManager.getInstance().getUserId())
                    .build();
            Request request = new Request.Builder() //데이터 받아서 오기
                    .url(url)
                    .post(formBody)
                    .build();

            ModifyBoardValue modifyBoardValue = new ModifyBoardValue();
            Response response = null;

            try {
                response = okHttpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONObject result = new JSONObject(response.body().string());

                    JSONObject feedDetailJson = result.getJSONObject("data");

                    // 게시글 수정시 원본내용 먼저 세팅
                    modifyBoardValue.boardUserId = feedDetailJson.getString("boardUserId");
                    modifyBoardValue.boardUserPictrue = feedDetailJson.optString("boardUserPicture");
                    modifyBoardValue.boardTag = feedDetailJson.getString("boardTag");
                    modifyBoardValue.boardContent = feedDetailJson.getString("boardContent");
                    modifyBoardValue.boardCategory = feedDetailJson.getInt("boardCategory");
                    modifyBoardValue.boardNicName = feedDetailJson.optString("boardUserNicName", "null");
                    modifyBoardValue.boardIsAnomynity = feedDetailJson.optString("boardIsAnomynity", "false");
                    modifyBoardValue.boardContentPicture1 = feedDetailJson.optString("boardContentPicture1", "null");

                    Log.e("ModifyBoardParsing", modifyBoardValue.toString());

                    return modifyBoardValue;
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(ModifyBoardValue modifyBoardValue) {
            super.onPostExecute(modifyBoardValue);

            try {
                progressDialog.dismiss();

                    // 게시글 수정시 원본내용 먼저 세팅

                if (modifyBoardValue != null) {
                    // 카테고리 이미지 선택
                    setCategoryImage(modifyBoardValue.boardCategory);
                    // 제목 선택
                    editWriteTag.setText(modifyBoardValue.boardTag);
                    if (!modifyBoardValue.boardUserPictrue.equals("null")) {
                        // 유저 프로필 사진
                        Glide.with(ModifyActivity.this)
                                .load(modifyBoardValue.boardUserPictrue)
                                .error(R.drawable.user_img_66dp)
                                .into(imageWriteProfile);
                    }

                    if (!modifyBoardValue.boardContentPicture1.equals("null")) {
                        picUploadLayout.setVisibility(View.VISIBLE);
                        Glide.with(ModifyActivity.this)
                                .load(modifyBoardValue.boardContentPicture1)
                                .placeholder(R.drawable.imege_up)
                                .error(R.drawable.imege_fail)
                                .into(picUploadViewOne);
                    }

                    // 유저 이름
                    textWriteUserName.setText(modifyBoardValue.boardNicName);
                    // 컨텐츠 내용
                    editFeedWriteContent.setText(modifyBoardValue.boardContent);

                    if (modifyBoardValue.boardIsAnomynity.equals("true") && modifyBoardValue.boardIsAnomynity != null) {
                        textWriteUserName.setText("익명");
                        Glide.with(ModifyActivity.this)
                                .load(R.drawable.user_img_66dp)
                                .placeholder(R.drawable.imege_up)
                                .error(R.drawable.imege_fail)
                                .into(imageWriteProfile);
                    }

                } else {
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        // 카테고리 이미지 선택
        private void setCategoryImage(int categoryType) {
            int category = R.drawable.category0_frame;
            switch (categoryType) {
                case GalaPoiCategory.GalaPoiCategoryOthers:
                    category = R.drawable.category0_frame;
                    break;
                case GalaPoiCategory.GalaPoiCategoryFood:
                    category = R.drawable.category1_frame;
                    break;
                case GalaPoiCategory.GalaPoiCategoryPlay:
                    category = R.drawable.category2_frame;
                    break;
                case GalaPoiCategory.GalaPoiCategoryOutLocation:
                    category = R.drawable.category3_frame;
                    break;
                case GalaPoiCategory.GalaPoiCategoryDeal:
                    category = R.drawable.category4_frame;
                    break;
                case GalaPoiCategory.GalaPoiCategoryMetting:
                    category = R.drawable.category5_frame;
                    break;
                case GalaPoiCategory.GalaPoiCategorySuggestion:
                    category = R.drawable.category6_frame;
                    break;
            }
            writeSelectedLayout.setBackgroundResource(category);
        }

    }

}
