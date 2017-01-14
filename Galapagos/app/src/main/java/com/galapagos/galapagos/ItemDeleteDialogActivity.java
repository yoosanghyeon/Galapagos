package com.galapagos.galapagos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.galapagos.galapagos.common.AppBaseActivity;

// Item Delete Dialog
public class ItemDeleteDialogActivity extends AppBaseActivity {
    ImageView yesButton; //yes Button
    ImageView noButton; //no Button
    ImageView closeDialogButton; //close button

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_delete_dialog);

        // Dialog Button intiallaze
        yesButton = (ImageView)findViewById(R.id.btn_delete_dialog_yes);
        noButton = (ImageView)findViewById(R.id.btn_delete_dialog_no);
        closeDialogButton = (ImageView) findViewById(R.id.btn_delete_dialog_close);

        // Dialog ImageButton ClickListener
        yesButton.setOnClickListener(dialogClickListener);
        noButton.setOnClickListener(dialogClickListener);
        closeDialogButton.setOnClickListener(dialogClickListener);
    }

    //Dialog ClickListener
    View.OnClickListener dialogClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_delete_dialog_yes:
                    //게시글 삭제
                    break;
                case R.id.btn_delete_dialog_no:
                    //게시글 미삭제 그냥 창닫기
                    finish();
                    break;
                case R.id.btn_delete_dialog_close:
                    finish();
                    break;

            }
        }
    };
}
