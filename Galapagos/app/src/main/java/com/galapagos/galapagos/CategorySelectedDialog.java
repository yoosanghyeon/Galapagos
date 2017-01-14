package com.galapagos.galapagos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

/**
 * Created by Arous on 2016-12-02.
 */

public class CategorySelectedDialog extends DialogFragment implements View.OnClickListener {

    Button btnCat0, btnCat1, btnCat2,
            btnCat3, btnCat4, btnCat5,
            btnCat6, btnCatClose;

    @Override
    public void onStart() {
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 다이얼로그 스타일 정의
        setStyle(DialogFragment.STYLE_NORMAL, R.style.galaDialogStyle);

        View view = inflater.inflate(R.layout.choice_cat_dialog_layout, container, false);
        // 버튼 초기화
        btnCat0 = (Button) view.findViewById(R.id.btn_cat8);
        btnCat1 = (Button) view.findViewById(R.id.btn_cat1);
        btnCat2 = (Button) view.findViewById(R.id.btn_cat2);
        btnCat3 = (Button) view.findViewById(R.id.btn_cat3);
        btnCat4 = (Button) view.findViewById(R.id.btn_cat4);
        btnCat5 = (Button) view.findViewById(R.id.btn_cat5);
        btnCat6 = (Button) view.findViewById(R.id.btn_cat6);
        btnCatClose = (Button) view.findViewById(R.id.btn_delete_dialog_close);

        // 클릭 리스너 등록
        btnCat0.setOnClickListener(this);
        btnCat1.setOnClickListener(this);
        btnCat2.setOnClickListener(this);
        btnCat3.setOnClickListener(this);
        btnCat4.setOnClickListener(this);
        btnCat5.setOnClickListener(this);
        btnCat6.setOnClickListener(this);
        btnCatClose.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cat8:
                break;
            case R.id.btn_cat1:
                break;
            case R.id.btn_cat2:
                break;
            case R.id.btn_cat3:
                break;
            case R.id.btn_cat4:
                break;
            case R.id.btn_cat5:
                break;
            case R.id.btn_cat6:
                break;
            case R.id.btn_delete_dialog_close:
                dismiss();
                break;
        }
    }
}
