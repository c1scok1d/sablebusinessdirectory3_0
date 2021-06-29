package com.macinternetservices.sablebusinessdirectory.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.location.Geofence;
import com.macinternetservices.sablebusinessdirectory.Config;
import com.macinternetservices.sablebusinessdirectory.MainActivity;
import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.ui.common.NavigationController;
import com.macinternetservices.sablebusinessdirectory.ui.item.detail.ItemActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import static com.facebook.FacebookSdk.getApplicationContext;

public class GeofenceNotification {
    public static final int NOTIFICATION_ID = 20;
    protected Context context;

    protected NotificationManager notificationManager;
    protected Notification notification;
    String notificationText = "";
    String notificationText2 = "";
    protected SharedPreferences pref;

    public GeofenceNotification(Context context) {
        this.context = context;
        pref=PreferenceManager.getDefaultSharedPreferences(context);

        this.notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }
    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        return dist; // output distance, in MILES
    }

    protected void buildNotificaction(SimpleGeofence simpleGeofence,
                                      int transitionType, int near) throws IOException {

        String firstName = "", lastName = "";
        if (!pref.getString(Constants.USER_NAME, Constants.EMPTY_STRING).isEmpty()) {
            String name = pref.getString(Constants.USER_NAME, Constants.EMPTY_STRING);
            if (name.split("\\w+").length > 1) ;
            {
                //lastName = name.substring((name.lastIndexOf(""+1)));
                firstName = name.substring(0, name.lastIndexOf(' '));
            }
        }
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                if(near > 0){
                    if (!firstName.isEmpty()) {
                        notificationText = "Good news " +firstName+ "!";
                    } else {
                        notificationText = "Good news!";
                    }
                    notificationText2 = "There are " + near + " black owned businesses near you!";
                } else {
                    if (!firstName.isEmpty()) {
                        notificationText = "Oh no "+firstName+"!";

                    } else {
                        notificationText = "Oh no!";
                    }
                    notificationText2 = "There are no businesses listed with us near you! Tap to add a listing now";
                }
                if(near == 1){
                    Toast.makeText(context, "There is " + near + " black owned businesses near you.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "There are " + near + " black owned businesses near you.", Toast.LENGTH_LONG).show();
                }
                transitionEnterNotification(context, notificationText, notificationText2);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                notificationText2 = "You are near " + simpleGeofence.getItem_name();
                notificationText = "Stop in and say Hi!";
                transitionDwellNotification(context, notificationText, notificationText2,simpleGeofence);
                break;

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                notificationText = "Don't miss an opportunity to buy black.";
                notificationText2 = "You are near " + simpleGeofence.getItem_name();
                transitionExitNotification(context, notificationText, notificationText2);
                break;
        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                context)
                .setSmallIcon(R.mipmap.logo_wht_round)
                .setContentTitle(context.getString(R.string.app_name))
                .setStyle(
                        new NotificationCompat.BigTextStyle()
                                .bigText(notificationText)).setAutoCancel(true);

        notification = notificationBuilder.build();
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
    }

    public void displayNotification(SimpleGeofence simpleGeofence,
                                    int transitionType, int near) throws IOException {
        //Geofence geo = simpleGeofence.toGeofence();
        buildNotificaction(simpleGeofence, transitionType, near);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
    public static final String CHANNEL_ID = "Transition Channel";
    private void createNotificationChannel(final Context mContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Transition Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = mContext.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
    private void transitionEnterNotification(final Context mContext,final String message, final String message2){
        createNotificationChannel(mContext);
        Intent notificationIntent = new Intent(mContext, MainActivity.class);

        PendingIntent notificationTapIntent = PendingIntent.getActivity(mContext,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                //.setSubText("Black Owned Business Alert")
                .setContentText(message2)
                .setContentTitle(message)
                .setSmallIcon(R.mipmap.logo_wht_round)
                .setContentIntent(notificationTapIntent) // notification tap action
                .build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notifManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify(new Random().nextInt(), notification);
    }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
    private void transitionDwellNotification(final Context mContext,final String message, final String message2, SimpleGeofence geofence){
        createNotificationChannel(mContext);
        NavigationController navigationController = null;
        Intent notificationIntent = new Intent(context,ItemActivity.class);


        notificationIntent.putExtra(Constants.HISTORY_FLAG, Constants.ONE);
        notificationIntent.putExtra(Constants.ITEM_NAME, geofence.getItem_name());
        notificationIntent.putExtra(Constants.ITEM_ID, geofence.getId());
        notificationIntent.putExtra(Constants.CITY_ID, geofence.getCity_id());//change intent to go to item detail
        //Intent notificationIntent = new Intent(mContext, MainActivity.class);
        PendingIntent notificationTapIntent = PendingIntent.getActivity(mContext,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentTitle(message2)
                .setContentText(message)
                //.setSubText("Black Owned Business Alert")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(getBitmapFromURL(Config.APP_IMAGES_URL + geofence.getImage_id()))
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(getBitmapFromURL(Config.APP_IMAGES_URL + geofence.getImage_id()))
                        .bigLargeIcon(null))
                .setContentIntent(notificationTapIntent) // ontap go to item detail
                .build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notifManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify(new Random().nextInt(), notification);
    }

    private void transitionExitNotification(final Context mContext,final String message, final String message2){
        createNotificationChannel(mContext);
        Intent notificationIntent = new Intent(mContext, MainActivity.class);

        PendingIntent notificationTapIntent = PendingIntent.getActivity(mContext,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                //.setSubText("Black Owned Business Alert")
                .setContentText(message2)
                .setContentTitle(message)
                .setSmallIcon(R.mipmap.logo_wht_round)
                .setContentIntent(notificationTapIntent) // notification tap action
                .build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notifManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify(new Random().nextInt(), notification);
    }
}
