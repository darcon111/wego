package com.wego.app.servicies;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.wego.app.R;
import com.wego.app.activity.MainActivity;

import org.json.JSONObject;

import java.util.Random;


public class FCMService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage.getFrom());


        try {


            if(remoteMessage.getData()!=null) {




                String body = remoteMessage.getNotification().getBody();
                //String msg = res.getString("msg");

                //
                //Log.d(TAG, "message" + res.getString("title"));
                final NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                final NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setPriority(Notification.PRIORITY_HIGH)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setColor(Color.parseColor("#b01a1a"));

                //builder.setContentTitle(getString(R.string.app_name));

                builder.setContentText(body);
                // Build stack
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                // Add parent activity
                stackBuilder.addParentStack(MainActivity.class);

                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                stackBuilder.addNextIntent(intent);
                // Get PendingIntent from the stack



                PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);

                // Asignar intent y establecer true para notificar como aviso
                builder.setFullScreenIntent(pendingIntent, false);
                // Remove notification when interacting with her
                builder.setAutoCancel(true);

                builder.setDefaults(Notification.DEFAULT_SOUND);

                builder.setDefaults(Notification.DEFAULT_VIBRATE);


                builder.setDefaults(Notification.DEFAULT_LIGHTS);


                int count = 0;
                count = new Random().nextInt(5000 - 5 + 1) + 5;

                builder.setPriority(Notification.PRIORITY_MAX);
                builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                if(Build.VERSION.SDK_INT> Build.VERSION_CODES.KITKAT)
                {
                    builder.setCategory(Notification.CATEGORY_EVENT);
                }


                mNotificationManager.notify(count, builder.build());


                final int finalCount = count;




                Thread thread = new Thread() {
                    public void run() {
                        Looper.prepare();

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do Work
                                handler.removeCallbacks(this);
                                Looper.myLooper().quit();

                                builder.setFullScreenIntent(null,false);
                                mNotificationManager.notify(finalCount,builder.build());

                            }
                        }, 3000);

                        Looper.loop();
                    }
                };
                thread.start();





            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        //Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());


    }

}


