package com.galapagos.galapagos;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.galapagos.galapagos.common.AppBaseActivity;

public class CatChoiceActivity extends AppBaseActivity implements View.OnClickListener {

    int catResultCode;
    Button cat0Button;
    Button cat1Button;
    Button cat2Button;
    Button cat3Button;
    Button cat4Button;
    Button cat5Button;
    Button cat6Button;

    Button closeButton;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choice_cat_dialog_layout);

        cat0Button = (Button) findViewById(R.id.btn_cat8);
        cat1Button = (Button) findViewById(R.id.btn_cat1);
        cat2Button = (Button) findViewById(R.id.btn_cat2);
        cat3Button = (Button) findViewById(R.id.btn_cat3);
        cat4Button = (Button) findViewById(R.id.btn_cat4);
        cat5Button = (Button) findViewById(R.id.btn_cat5);
        cat6Button = (Button) findViewById(R.id.btn_cat6);
        closeButton = (Button) findViewById(R.id.btn_delete_dialog_close);

        cat0Button.setOnClickListener(this);
        cat1Button.setOnClickListener(this);
        cat2Button.setOnClickListener(this);
        cat3Button.setOnClickListener(this);
        cat4Button.setOnClickListener(this);
        cat5Button.setOnClickListener(this);
        cat6Button.setOnClickListener(this);
        closeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cat8:
                catResultCode = 8;
                setResult(catResultCode);
                finish();
                break;
            case R.id.btn_cat1:
                catResultCode = 1;
                setResult(catResultCode);
                finish();
                break;
            case R.id.btn_cat2:
                catResultCode = 2;
                setResult(catResultCode);
                finish();
                break;
            case R.id.btn_cat3:
                catResultCode = 3;
                setResult(catResultCode);
                finish();
                break;
            case R.id.btn_cat4:
                catResultCode = 4;
                setResult(catResultCode);
                finish();
            break;
            case R.id.btn_cat5:
                catResultCode = 5;
                setResult(catResultCode);
                finish();
                break;
            case R.id.btn_cat6:
                catResultCode = 6;
                setResult(catResultCode);
                finish();
                break;
            case R.id.btn_delete_dialog_close:
                finish();
                break;
            default:
                finish();
                break;
        }
    }

}
