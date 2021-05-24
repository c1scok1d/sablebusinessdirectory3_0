package com.macinternetservices.sablebusinessdirectory.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.macinternetservices.sablebusinessdirectory.MainActivity;
import com.macinternetservices.sablebusinessdirectory.R;


/**
 * Created by MAC Internet Services on 8/11/16.
 * Contact Email : admin@thesablebusinessdirectory.com
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    public String channelId = "PSMultiCityChannelId1";
    public  String flag,message;
    public String userId ;
    SharedPreferences preferences;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        /* Important : Please don't change this "message" because if you change this, need to update at PHP.  */


        preferences=PreferenceManager.getDefaultSharedPreferences(this);

        if (preferences.getBoolean(Constants.PUSH_NOT_KEY,false)==true) {

            flag = remoteMessage.getData().get("flag");

            message = remoteMessage.getData().get("message");


            if (message == null || message.equals("")) {
                if (remoteMessage.getNotification() != null) {
                    message = remoteMessage.getNotification().getBody();
                }
            }

            showNotification(message, flag);
        }

    }


    private void showNotification(String message,String flag) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean( Constants.NOTI_EXISTS_TO_SHOW, true);
        editor.apply();


        Intent i = new Intent(this,MainActivity.class);
        i.putExtra(Constants.NOTI_MSG, message);
        i.putExtra(Constants.NOTI_FLAG ,flag);
        String displayMessage ;

        i.putExtra(Constants.NOTI_MSG, message);


        Utils.psLog("Message From Server : " + message);

        if (message == null || message.equals("")) {
            i.putExtra(Constants.NOTI_EXISTS_TO_SHOW, false);
            displayMessage = "Welcome from PS Buy Sell";
        } else {
            i.putExtra(Constants.NOTI_EXISTS_TO_SHOW, true);
            displayMessage = message; //"You've received new message.";
        }

        i.putExtra(Constants.NOTI_MSG ,message);
        i.putExtra(Constants.NOTI_FLAG ,flag);

        //i.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP  | Intent.FLAG_ACTIVITY_SINGLE_TOP );
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), Constants.ZERO)
                .setAutoCancel(true)
                .setContentTitle(getString(R.string.notification_alert))
                .setContentText(displayMessage)
                .setSmallIcon(R.drawable.ic_notifications_white)
                .setContentIntent(pendingIntent)
                ;


        // Set Vibrate, Sound and Light
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;

        builder.setDefaults(defaults);
        // Set the content for Notification
        builder.setContentText(displayMessage);
        // Set autocancel
        builder.setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = this.channelId;
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    getString(R.string.notification_alert),
                    NotificationManager.IMPORTANCE_DEFAULT);
            if(manager != null) {
                manager.createNotificationChannel(channel);
            }
            builder.setChannelId(channelId);
        }

        if (manager != null) {

            manager.notify(0, builder.build());

        }

    }

}
