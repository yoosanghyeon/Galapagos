package com.galapagos.galapagos;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.galapagos.galapagos.common.AppBaseActivity;
import com.galapagos.galapagos.common.GalaPermissionCheck;
import com.galapagos.galapagos.common.NetworkDefineConstant;
import com.galapagos.galapagos.common.OkHttpManager;
import com.galapagos.galapagos.common.PropertyManager;
import com.galapagos.galapagos.valueobject.UserRegisterResultValue;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yeji on 2016. 12. 5..
 */

public class ProfileInfoModifyDialogActivity extends AppBaseActivity implements View.OnClickListener {

    private static final int CAPTURE_IMAGE_USER_INTENT_REQUEST = 404;
    private static final int GRELLERY_IMAGE_USER_INTENT_REQUEST = 405;

    ImageButton closeButton;
    ImageButton photoModifyButton;
    ImageButton infoModifyButton;

    CircleImageView userProfilePic;
    EditText userNicName;

    UserRegisterResultValue.UserProfileModifyValue userModifyValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navi_profile_modify_appbar);

        closeButton = (ImageButton) findViewById(R.id.btn_appbar_close);
        photoModifyButton = (ImageButton) findViewById(R.id.btn_photo_modify);
        infoModifyButton = (ImageButton) findViewById(R.id.btn_info_modify);
        userNicName = (EditText) findViewById(R.id.profile_user_nicname);
        userProfilePic = (CircleImageView) findViewById(R.id.circleImageView);

        //유저 닉네임& 유저 프로필사진 propertyManager에서 불러오기
        userNicName.setText(PropertyManager.getInstance().getNickName());
        Glide.with(this).load(PropertyManager.getInstance().getPicUri()).thumbnail(0.1f).into(userProfilePic);

        closeButton.setOnClickListener(this);
        photoModifyButton.setOnClickListener(this);
        infoModifyButton.setOnClickListener(this);

        userModifyValue = new UserRegisterResultValue.UserProfileModifyValue();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_appbar_close:
                finish();
                break;
            case R.id.btn_photo_modify :
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    GalaPermissionCheck.getInstance().requsetPicturePermission(this);

                } else {
                    UserPicAddDialogFragment picAddDialogFragment = UserPicAddDialogFragment.newInstances();
                    picAddDialogFragment.show(getSupportFragmentManager(), "USER_PIC_RIGS");

                }
                break;
            case R.id.btn_info_modify :
                if (userNicName.getText().equals("") || userNicName == null){
                    GalaCustomToast galaCustomToast = new GalaCustomToast(this);
                    galaCustomToast.showToast("닉네임을 입력해주세요", Toast.LENGTH_SHORT);
                    return;
                }
                userModifyValue.userId = PropertyManager.getInstance().getUserId();
                userModifyValue.userName = userNicName.getText().toString().trim();
                new AsyncProfileModify().execute(userModifyValue);
                finish();
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

        if (requestCode == CAPTURE_IMAGE_USER_INTENT_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri currImageURI = data.getData();
            Glide.with(getApplicationContext())
                    .load(currImageURI)
                    .thumbnail(0.5f)
                    .into(userProfilePic);

            userModifyValue.userPic = new File(getRealPathFromURI(getApplicationContext(), currImageURI));
        }

        if (requestCode == GRELLERY_IMAGE_USER_INTENT_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            Glide.with(getApplicationContext())
                    .load(imageUri)
                    .thumbnail(0.5f)
                    .into(userProfilePic);

            userModifyValue.userPic = new File(getRealPathFromURI(getApplicationContext(), imageUri));
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

    private class AsyncProfileModify extends AsyncTask<UserRegisterResultValue.UserProfileModifyValue, Integer,  String[]> {
        private Context mContext;

        final MediaType pngType = MediaType.parse("image/*");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(UserRegisterResultValue.UserProfileModifyValue... userProfileModifyValues) {
            Log.e("userProfileModifyValues", userProfileModifyValues[0].toString());
            final MediaType pngType = MediaType.parse("image/*");
            ArrayList resultMsg = null;
            Response response = null;
            String[] resultArray = new String[3];
            String msg;
            try {
                OkHttpClient client = OkHttpManager.getOkHttpClient();
                RequestBody fileUploadBody;

                MultipartBody.Builder builder= new MultipartBody.Builder()
                        .setType(MultipartBody.FORM) //파일 업로드시 반드시 설정
                        .addFormDataPart("userId", userProfileModifyValues[0].userId) //기본 쿼리
                        .addFormDataPart("userNicName", userProfileModifyValues[0].userName)
                        .addFormDataPart("useritemKey",PropertyManager.getInstance().getUserItemKey());

                if (userProfileModifyValues[0].userPic != null) {

                    builder.addFormDataPart("file", userProfileModifyValues[0].userPic.getName()
                            , RequestBody.create(pngType, userProfileModifyValues[0].userPic));
                }
                fileUploadBody = builder.build();

                Request request = new Request.Builder()
                        .url(NetworkDefineConstant.PROFILE_MODIFY_POST_URL)
                        .post(fileUploadBody)
                        .build();

                response = client.newCall(request).execute();

                if (response.isSuccessful()){
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    msg = jsonObject.getString("msg");
                    Log.e("USER_REGISTER", msg);
                    if (!msg.equals("개인정보 수정 성공")) {
                        resultArray[0] = "응답처리 안됨";
                        return resultArray;
                    }
                    JSONObject result = jsonObject.getJSONObject("data");
                    String userPicture = result.getString("userPicture");
                    String userNicName = result.getString("userNicName");
                    resultArray[1] = userNicName;
                    Log.e("resultArray[1]",resultArray[1]);
                    if (userPicture != null) {
                        resultArray[2] = userPicture;
                    }
                    return resultArray;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultArray;
        }

        @Override
        protected void onPostExecute(String[] resultMsg) {

            if (resultMsg != null) {

                    PropertyManager.getInstance().setNickName(resultMsg[1]);
                    Log.e("resetNicName", resultMsg[1]);
                    if (resultMsg[2] != null) {
                        PropertyManager.getInstance().setPicUri(resultMsg[2]);
                    }
            }
            super.onPostExecute(resultMsg);
        }
    }

}
