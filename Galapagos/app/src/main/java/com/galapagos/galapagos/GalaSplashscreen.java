package com.galapagos.galapagos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.galapagos.galapagos.common.AppBaseActivity;
import com.galapagos.galapagos.common.PropertyManager;

import static java.lang.Thread.sleep;

// 갈라파고스 시작 스플래쉬
public class GalaSplashscreen extends AppBaseActivity {

    // GPS 사용여부 체크
    Boolean isGpsEnabled;

    // 회원가입 여부
    SharedPreferences pref;

    // Animation 사용
    Animation anim;
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galapagos_splashscreen);

        pref = getSharedPreferences("USER", Context.MODE_PRIVATE);
        String memberId = PropertyManager.getInstance().getUserId();
        Log.e("Pref", memberId);



        splashStart();
        if (memberId.equals("")) {
            setMemberJoin();
        } else {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {

                        int waited = 0;
                        // Splash screen pause time
                        while (waited < 1300) {
                            sleep(200);
                            waited += 100;
                        }
                        Intent intent = new Intent(GalaSplashscreen.this,
                                FeedActivity.class);
                        startActivity(intent);
                    } catch (InterruptedException e) {
                        // do nothing
                    } finally {
                        GalaSplashscreen.this.finish();


                    }

                }
            }).start();

        }
    }


    private void splashStart() {

        // 거북이 애니메이션
        anim = AnimationUtils.loadAnimation(this, R.anim.alpha);//거북이 애니메이션
        anim.reset();
        ImageView galapagosTutle = (ImageView) findViewById(R.id.splash_gala_turtle);
        galapagosTutle.clearAnimation();
        galapagosTutle.startAnimation(anim);
    }

    private void setMemberJoin() {

        // JOIN 버튼 애니메이션 차후 나눌 예정
        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        ImageView btnMamberJoin = (ImageView) findViewById(R.id.btn_member_join);
        btnMamberJoin.setVisibility(View.VISIBLE);
        btnMamberJoin.clearAnimation();
        ;
        btnMamberJoin.startAnimation(anim);

        btnMamberJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent
                        = new Intent(getApplicationContext(), UserRegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}
