package com.galapagos.galapagos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Arous on 2016-11-22.
 * 회원 가입시 사진 등록
 */

public class UserPicAddDialogFragment extends DialogFragment implements View.OnClickListener {


    ImageView btnClose, btnAddPicCapture, btnGalleryAdd;

    static UserPicAddDialogFragment newInstances() {
        UserPicAddDialogFragment picAddDialogFragment = new UserPicAddDialogFragment();
        return picAddDialogFragment;
    }

    @Override
    public void onStart() {
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.galaDialogStyle);
        View view = (View) inflater.inflate(R.layout.photo_register_dialog, container, false);
        btnClose = (ImageView) view.findViewById(R.id.btn_photo_dialog_close);
        btnAddPicCapture = (ImageView) view.findViewById(R.id.btn_pic_capture_add);
        btnGalleryAdd = (ImageView) view.findViewById(R.id.btn_galler_add);

        btnClose.setOnClickListener(this);
        btnAddPicCapture.setOnClickListener(this);
        btnGalleryAdd.setOnClickListener(this);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_photo_dialog_close:
                dismiss();
                break;
            case R.id.btn_pic_capture_add:
                Toast.makeText(getContext(),"현재 미구현 기능", Toast.LENGTH_SHORT).show();
//                ((UserRegisterActivity) getActivity()).getCapturePhote();
                dismiss();
                break;
            case R.id.btn_galler_add:
                Toast.makeText(getContext(),"현재 미구현 기능", Toast.LENGTH_SHORT).show();
//                ((UserRegisterActivity) getActivity()).getGalleryPhoto();
                dismiss();
                break;
        }

    }
}
