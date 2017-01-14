package com.galapagos.galapagos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.galapagos.galapagos.common.AppBaseActivity;

/**
 * Created by yeji on 2016. 11. 22..
 */

public class NotifiDialogActivity extends AppBaseActivity {
    TextView appbarTitle;
    ImageButton closeButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navi_notifi_license_appbar);

        appbarTitle = (TextView) findViewById(R.id.text_appbar_title);
        appbarTitle.setText("공지사항");

        closeButton = (ImageButton) findViewById(R.id.btn_appbar_close);
        closeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
