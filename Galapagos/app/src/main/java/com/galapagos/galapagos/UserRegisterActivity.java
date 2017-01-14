package com.galapagos.galapagos;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.galapagos.galapagos.common.AppBaseActivity;
import com.galapagos.galapagos.common.GalaPermissionCheck;
import com.galapagos.galapagos.common.OkHttpManager;
import com.galapagos.galapagos.common.PropertyManager;
import com.galapagos.galapagos.valueobject.UserRegisterValue;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.File;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserRegisterActivity extends AppBaseActivity implements View.OnClickListener {

    // 회원가입시 위젯들
    CircleImageView userPicView;
    ImageButton btnUserRegisterPic;
    ImageButton btnUserConfirmPic;
    ImageButton btnUserResiterClose;
    EditText editUserResiterName;


    private static final int CAPTURE_IMAGE_USER_INTENT_REQUEST = 404;
    private static final int GRELLERY_IMAGE_USER_INTENT_REQUEST = 405;

    UserRegisterValue userRegisterValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_register_layout);
        // 유저 이미지
        userPicView = (CircleImageView) findViewById(R.id.image_user_resiter);

        // 사진,닫기, 전송 버튼
        btnUserRegisterPic = (ImageButton) findViewById(R.id.btn_user_resiter_pic);
        btnUserConfirmPic = (ImageButton) findViewById(R.id.btn_user_resiter_confirm);
        btnUserResiterClose = (ImageButton) findViewById(R.id.btn_user_resiter_close);

        editUserResiterName = (EditText) findViewById(R.id.edit_user_resiter_name);

        btnUserRegisterPic.setOnClickListener(this);
        btnUserConfirmPic.setOnClickListener(this);
        btnUserResiterClose.setOnClickListener(this);

        userRegisterValue = new UserRegisterValue();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_user_resiter_pic:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    GalaPermissionCheck.getInstance().requsetPicturePermission(this);

                } else {
                    UserPicAddDialogFragment picAddDialogFragment = UserPicAddDialogFragment.newInstances();
                    picAddDialogFragment.show(getSupportFragmentManager(), "USER_PIC_RIGS");

                }

                break;
            case R.id.btn_user_resiter_close:
                finish();
                break;
            case R.id.btn_user_resiter_confirm:
                if (editUserResiterName.getText().equals("") || editUserResiterName == null) {
                    GalaCustomToast galaCustomToast = new GalaCustomToast(this);
                    galaCustomToast.showToast("닉네임을 입력해주세요", Toast.LENGTH_SHORT);
                    return;
                }

                userRegisterValue.userId = UUID.randomUUID().toString().replace('-', 'A');
                userRegisterValue.userName = editUserResiterName.getText().toString().trim();
                new UserRegisterAsyncTask(UserRegisterActivity.this).execute(userRegisterValue);
                break;
        }
    }

    public void getCapturePhote() {
        Intent intent_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent_camera, CAPTURE_IMAGE_USER_INTENT_REQUEST);
    }

    public void getGalleryPhoto() {
        Intent intent_gallery = new Intent(Intent.ACTION_PICK);
        intent_gallery.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent_gallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent_gallery, GRELLERY_IMAGE_USER_INTENT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == CAPTURE_IMAGE_USER_INTENT_REQUEST && resultCode == RESULT_OK && data != null) {
                Uri currImageURI = data.getData();
                Glide.with(getApplicationContext())
                        .load(currImageURI)
                        .into(userPicView);

                userRegisterValue.userPic = new File(getRealPathFromURI(getApplicationContext(), currImageURI));
            }

            if (requestCode == GRELLERY_IMAGE_USER_INTENT_REQUEST && resultCode == RESULT_OK && data != null) {
                Uri imageUri = data.getData();
                Glide.with(getApplicationContext())
                        .load(imageUri)
                        .into(userPicView);
                userRegisterValue.userPic = new File(getRealPathFromURI(getApplicationContext(), imageUri));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    // 실제 단말기에 저장되 있는 URI를 얻어오는 메서드
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

    private static final String USER_REGISTER_URI = "http://35.162.13.239:3000/user";

    private class UserRegisterAsyncTask extends AsyncTask<UserRegisterValue, Integer, String[]> {

        Context mContext;
        ProgressDialog progressDialog;
        GalaCustomToast galaCustomToast;


        public UserRegisterAsyncTask(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("네트워크 처리중..");
            progressDialog.show();


            galaCustomToast = new GalaCustomToast(mContext);
            super.onPreExecute();

        }

        @Override
        protected String[] doInBackground(UserRegisterValue... userRegisterValues) {
            final MediaType pngType = MediaType.parse("image/*");

            String[] resultArray = new String[6];
            Response response = null;
            String msg;
            try {

                OkHttpClient client = OkHttpManager.getOkHttpClient();
                RequestBody fileUploadBody;

                String fcmToken = FirebaseInstanceId.getInstance().getToken();

                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM) //파일 업로드시 반드시 설정
                        .addFormDataPart("userId", userRegisterValues[0].userId) //기본 쿼리
                        .addFormDataPart("userNicName", userRegisterValues[0].userName)
                        .addFormDataPart("token", fcmToken);


                if (userRegisterValues[0].userPic != null) {
                    builder.addFormDataPart("file", userRegisterValues[0].userPic.getName()
                            , RequestBody.create(pngType, userRegisterValues[0].userPic));

                }

                fileUploadBody = builder.build();

                Request request = new Request.Builder()
                        .url(USER_REGISTER_URI)
                        .post(fileUploadBody)
                        .build();


                response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    msg = jsonObject.getString("msg");
                    Log.e("USER_REGISTER", msg);
                    if (!msg.equals("register success")) {
                        resultArray[0] = "응답처리 안됨";
                        return resultArray;
                    }
                    JSONObject result = jsonObject.getJSONObject("data");
                    String userPicture = result.getString("userPicture");
                    String userNicName = result.getString("userNicName");
                    String useritemKey = result.getString("useritemKey");
                    Log.e("userNicName", userNicName);
                    resultArray[0] = msg;
                    resultArray[1] = result.getString("userId");
                    resultArray[2] = userRegisterValues[0].userName;
                    resultArray[3] = fcmToken;
                    resultArray[4] = userPicture;
                    Log.e("resultArray[4]", resultArray[4]);
                    resultArray[5] = useritemKey;
                    Log.e("useritemKey",resultArray[5]);
                    return resultArray;

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
        protected void onPostExecute(String[] resultMsg) {
            progressDialog.dismiss();

            if (resultMsg != null) {
                if (resultMsg[0].equals("register success")) {
                    PropertyManager.getInstance().setUserId(resultMsg[1]);
                    PropertyManager.getInstance().setNickName(resultMsg[2]);
                    PropertyManager.getInstance().setFcmTokenKey(resultMsg[3]);
                    if (resultMsg[4] != null) {
                        PropertyManager.getInstance().setPicUri(resultMsg[4]);
                    }
                    PropertyManager.getInstance().setUserItemKey(resultMsg[5]);

                    Intent feedStartIntent = new Intent(UserRegisterActivity.this, FeedActivity.class);
                    startActivity(feedStartIntent);
                    finish();

                } else {
                    galaCustomToast.showToast("네트워크에 이상이 있습니다", Toast.LENGTH_SHORT);
                }
            }

            super.onPostExecute(resultMsg);
        }
    }


}
