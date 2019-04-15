package com.example.projectchat;

import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


}

    /*
    Vibrator alarmVibrator;

    @Override
    public void onCreate() {
        super.onCreate();
        alarmVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }
    */

/*
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            if (remoteMessage.getData() != null) {
                Map<String, String> dataMap = remoteMessage.getData();
                String title = dataMap.get("title");
                String message = dataMap.get("description");
            }
        }
    }
    */

    /*
        @Override public void onMessageReceived(RemoteMessage remoteMessage) {

            int notificationId = new Random().nextInt(60000);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "admin_id")
                    .setSmallIcon(R.drawable.icon1)  //a resource for your custom small icon
                    .setContentTitle(remoteMessage.getData().get("title")) //the "title" value you sent in your notification
                    .setContentText(remoteMessage.getData().get("message")) //ditto
                    .setAutoCancel(true)  //dismisses the notification on click
                    .setSound(defaultSoundUri);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(notificationId,notificationBuilder.build());
    }
    */
    /*
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().get("Alarm").equals("1")) {
            Log.d("alarm", "on");
            alarmVibrator.vibrate(10000);
        } else {
            Log.d("alarm", "off");
            alarmVibrator.cancel();
        }
    }
}
*/
