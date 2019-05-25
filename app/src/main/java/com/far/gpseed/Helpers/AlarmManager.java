package com.far.gpseed.Helpers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.far.gpseed.Utils.Funciones;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mdsoft on 7/26/2017.
 */

public class AlarmManager {
    Context context;
    android.app.AlarmManager myManager;
    private static final int ALARMREQUESTCODE = 1;
    public static final String ACTION_NOTIFICAR = "NOTIFICAR";
    public AlarmManager(Context c){
        this.context = c;
        myManager = (android.app.AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

    }

    public void setAlarm(Calendar c){
        Intent i = new Intent(context, BroadcastReceiver_ORDR.class);
        i.setAction(ACTION_NOTIFICAR);
        Bundle b = new Bundle();
        ArrayList<String> vers = Funciones.getVersiculoDiario(context);
        b.putString("TEXTO", vers.get(0));
        b.putString("VERSICULO", vers.get(1));
        i.putExtra("VDIARIO", b);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, ALARMREQUESTCODE, i, PendingIntent.FLAG_CANCEL_CURRENT);
        myManager.set(android.app.AlarmManager.RTC_WAKEUP,c.getTimeInMillis(), pIntent);

    }

}
