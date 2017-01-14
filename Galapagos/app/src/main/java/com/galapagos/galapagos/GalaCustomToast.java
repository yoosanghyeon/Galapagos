package com.galapagos.galapagos;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Arous on 2016-11-28.
 */

public class GalaCustomToast extends Toast {

    Context mContext;

    public GalaCustomToast(Context context) {
        super(context);
        mContext = context;
    }

    public void showToast(String body, int duration) {
        // http://developer.android.com/guide/topics/ui/notifiers/toasts.html
        LayoutInflater inflater;
        View v;
        if (false) {
            Activity act = (Activity) mContext;
            inflater = act.getLayoutInflater();
            v = inflater.inflate(R.layout.gala_toast_layout, null);
        } else {
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.gala_toast_layout, null);
        }
        TextView text = (TextView) v.findViewById(R.id.text_gala_toast);
        text.setText(body);

        show(this, v, duration);
    }

    private void show(Toast toast, View v, int duration) {
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(duration);
        toast.setView(v);
        toast.show();
    }

}
