package com.galapagos.galapagos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

/**
 * Created by Arous on 2016-11-22.
 * Feed 게시물 작성시
 */

public class PicAddDialogFragment extends DialogFragment implements View.OnClickListener {

    interface OnStartImageLoadListener{
        void StartImageLoad(int i);
    }

    OnStartImageLoadListener mOnStartImageLoadListener;

    void SetOnStartImageLoadListener(OnStartImageLoadListener m){
        mOnStartImageLoadListener = m;
    }

    //현재 갤러리에서 요청
    private static final int PICK_FROM_GALLERY = 100;
    private static final int PICK_FROM_CAMERA = 200;
    ImageView btnClose, btnAddPicCapture, btnGalleryAdd;

    static PicAddDialogFragment newInstances() {
        PicAddDialogFragment picAddDialogFragment = new PicAddDialogFragment();
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
            case R.id.btn_pic_capture_add: //다이얼로그에서 카메라 버튼 클릭시
                mOnStartImageLoadListener.StartImageLoad(PICK_FROM_CAMERA);
                dismiss();
                break;
            case R.id.btn_galler_add://다이얼로그에서 갤러리 버튼 클릭시
                mOnStartImageLoadListener.StartImageLoad(PICK_FROM_GALLERY);
                dismiss();
                break;
        }

    }
}
