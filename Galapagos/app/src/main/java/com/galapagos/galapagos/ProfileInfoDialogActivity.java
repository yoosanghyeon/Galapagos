package com.galapagos.galapagos;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.galapagos.galapagos.common.AppBaseActivity;
import com.galapagos.galapagos.common.PropertyManager;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by yeji on 2016. 11. 22..
 */

public class ProfileInfoDialogActivity extends AppBaseActivity implements View.OnClickListener{

    ImageButton closeButton;
    ImageButton infoModifyButton;

    CircleImageView userProfilePic;
    TextView userNicName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navi_profile_appbar);

        closeButton = (ImageButton) findViewById(R.id.btn_appbar_close);
        infoModifyButton = (ImageButton) findViewById(R.id.btn_info_modify);
        userNicName = (TextView) findViewById(R.id.profile_user_nicname);
        userProfilePic = (CircleImageView) findViewById(R.id.circleImageView);

        //유저 닉네임& 유저 프로필사진 propertyManager에서 불러오기
        userNicName.setText(PropertyManager.getInstance().getNickName());
        Glide.with(this).load(PropertyManager.getInstance().getPicUri()).thumbnail(0.1f).into(userProfilePic);

        closeButton.setOnClickListener(this);
        infoModifyButton.setOnClickListener(this);

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        userNicName.setText(PropertyManager.getInstance().getNickName());
//        Glide.with(this).load(PropertyManager.getInstance().getPicUri()).thumbnail(0.1f).into(userProfilePic);
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_appbar_close:
                finish();
                break;
            case R.id.btn_info_modify :
                //프로필 정보 수정 페이지로 이동
                //Result 값으로 nicname이랑 picture url 가져오기
                Intent intent = new Intent(getApplicationContext(), ProfileInfoModifyDialogActivity.class);
                startActivity(intent);
                break;
        }
    }

}
