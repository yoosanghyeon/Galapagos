package com.galapagos.galapagos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.galapagos.galapagos.common.AppBaseActivity;

// QuestionDialog
public class QuestionDialogActivity extends AppBaseActivity {


    ImageView infoCallButton; // infoCall button
    ImageView sendMailButton; // sendMail button
    ImageView closeDialogButton; //close button
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_dialog);
//        setTitle("");
//        this.getWindow().setBackgroundDrawableResource(R.color.colorTransparent);

        // Dialog Button intiallaze
        infoCallButton = (ImageView) findViewById(R.id.btn_info_call);
        sendMailButton = (ImageView) findViewById(R.id.btn_send_mail);
        closeDialogButton = (ImageView) findViewById(R.id.btn_question_dialog_close);

        // Dialog ImageButton ClickListener
        infoCallButton.setOnClickListener(dialogClickListener);
        sendMailButton.setOnClickListener(dialogClickListener);
        closeDialogButton.setOnClickListener(dialogClickListener);

    }
        //Dialog ClickListener
        View.OnClickListener dialogClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.btn_info_call:
                        Intent callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:010-3834-9687")); //call view
                        startActivity(callIntent);
                        break;
                    case R.id.btn_send_mail:
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/html");
                        intent.putExtra(Intent.EXTRA_EMAIL, "they6687@naver.com");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "문의하기");
                        startActivity(Intent.createChooser(intent, "Send Email"));
                        break;
                    case R.id.btn_delete_dialog_close:
                        finish();
                        break;

                }
            }
        };


}
