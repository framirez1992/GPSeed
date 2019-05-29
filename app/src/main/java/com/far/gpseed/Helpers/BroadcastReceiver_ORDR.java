package com.far.gpseed.Helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.far.gpseed.DAL.DAL_Preferences;
import com.far.gpseed.DAL.DAL_Versiculos;
import com.far.gpseed.Models.Preference;
import com.far.gpseed.Models.Versiculo;
import com.far.gpseed.Splash;
import com.far.gpseed.Versiculos;
import java.util.Calendar;

/**
 * Created by framirez on 7/26/2017.
 */

public class BroadcastReceiver_ORDR extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        try {

           if(((intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) ||
                    (intent.getAction().equals(Intent.ACTION_REBOOT) )) && isAlarmaPedida(context)
                   && DAL_Preferences.getInstance(context).getSavedPreference().getactivarNotifiacion()==1) {


                   Intent i = new Intent(context, Versiculos.class);
                   Versiculo v = DAL_Versiculos.getInstance(context).getLastVersiculo();
                   String mensaje = v.getTexto();
                   String versiculo = v.getReferencia();
                   i.putExtra("TEXTO", mensaje);
                   i.putExtra("VERSICULO", versiculo);
                   NotificationManager.notify(context, i, mensaje);


           }else if(intent.getAction().equals(AlarmManager.ACTION_NOTIFICAR)
                   && DAL_Preferences.getInstance(context).getSavedPreference().getactivarNotifiacion()==1){
                Bundle b = intent.getBundleExtra("VDIARIO");
                Intent i = new Intent(context, Versiculos.class);
                String mensaje = b.getString("TEXTO");
                String versiculo = b.getString("VERSICULO");
                i.putExtra("TEXTO", mensaje);
                i.putExtra("VERSICULO", versiculo);
                NotificationManager.notify(context, i, mensaje);
            }

            setNextAlarm(context);

        }catch(Exception e){

            Intent i = new Intent(context, Splash.class);
            String mensaje = "Error cargando frase del dia";
            String versiculo = e.getMessage().toString();
            i.putExtra("TEXTO", mensaje);
            i.putExtra("VERSICULO", versiculo);
            NotificationManager.notify(context, i, mensaje);
        }
    }

    public void setNextAlarm(Context c) throws Exception{

            DAL_Preferences.getInstance(c).updateNextAlarm();

            Preference P = DAL_Preferences.getInstance(c).getSavedPreference();
            AlarmManager am = new AlarmManager(c);
            Calendar Next = Calendar.getInstance();
            Next.setTimeInMillis(P.getFechaALanzar());
            am.setAlarm(Next);

    }

    public boolean isAlarmaPedida(Context c){
        boolean alarmaPerdida = false;
        try {

            Preference p = DAL_Preferences.getInstance(c).getSavedPreference();
            Calendar fechaAlarma = Calendar.getInstance();
            fechaAlarma.setTimeInMillis(p.getFechaALanzar());

            Calendar fechaActual = Calendar.getInstance();

            if (fechaActual.after(fechaAlarma)) {
                alarmaPerdida = true;
            }
        }catch(Exception e){

        }
        return alarmaPerdida;


    }
}
