package com.galapagos.galapagos;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.net.URLDecoder;
import java.util.Map;

/*
  실제 FCM을 통한 푸쉬메세지를 받는 곳
 */
public class FCMPushMessageService extends FirebaseMessagingService {

    private static final String TAG = "FCMPushMessageService";

    private static final int PUSH_REQUEST_CODE = 4;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e("Event", "Service Evnent");

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData().get("boardId"));
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        //실제 푸쉬로 넘어온 데이터
        Map<String, String> receiveData = remoteMessage.getData();
        Log.e(TAG + "ID" , String.valueOf(receiveData.get("boardId")));
        Log.e(TAG+"TE", String.valueOf(receiveData.get("text")));
        try {
            //한글은 반드시 디코딩 해준다.

            PushValue pushValue = new PushValue();
            pushValue.boardId = URLDecoder.decode(receiveData.get("boardId"), "UTF-8");
            pushValue.notiBody = URLDecoder.decode(remoteMessage.getNotification().getBody(),"UTF-8");
            sendPushNotification(pushValue);



        } catch (Exception e) {

        }
    }

    private void sendPushNotification(PushValue pushValue) {

        try {
            if (pushValue.boardId != null){
                Log.e("sendPushNotification" , pushValue.boardId);

            }

        }catch (Exception e){

        }

        Intent intent = new Intent(this, FeedDetailActivity.class);
        intent.putExtra("boardId", pushValue.boardId );
        intent.putExtra("fcmService", "fcmService");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, PUSH_REQUEST_CODE, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("갈라파고스")
                .setContentText(pushValue.notiBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        notificationManager.notify(0, notificationBuilder.build());
    }

    private class PushValue{
        String boardId;
        String notiBody;
    }
}
