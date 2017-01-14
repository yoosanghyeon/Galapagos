package com.galapagos.galapagos;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyProjectFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FCMInstanceIDService";

    //발급받은 FCM토근을 업데이트 하는 FCM전용 콜백메소드
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        //필요하다면 다시 FCM토큰을 업데이트 함
        sendRegistrationToServer(refreshedToken);
    }
    /**
     * 필요없다면 다시 보내는 코드를 작성할 필요는 없음
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }
}
