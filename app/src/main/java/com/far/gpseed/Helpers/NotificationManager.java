package com.far.gpseed.Helpers;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.far.gpseed.R;
import com.far.gpseed.Utils.Funciones;

/**
 * Created by mdsoft on 7/26/2017.
 */

public class NotificationManager {

    public static void notify(Context context, Intent i, String mensaje){
// use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), i, 0);

// build notification
// the addAction re-use the same intent to keep the example short
        Notification n  = new Notification.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.palabra_del_dia))
                .setContentText((mensaje.length()<30)?mensaje: mensaje.substring(0, 30)+".....")
                .setSmallIcon(R.mipmap.seed_ico)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .addAction(R.mipmap.seed_ico, context.getResources().getString(R.string.palabra), pIntent).build();


        android.app.NotificationManager notificationManager =
                (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);
        Funciones.vibrate(context, 800);

    }

}
