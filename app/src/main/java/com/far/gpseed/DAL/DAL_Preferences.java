package com.far.gpseed.DAL;

import android.content.Context;
import android.database.Cursor;

import com.far.gpseed.Models.Preference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DAL_Preferences {
    public static String TABLE_NAME = "Preferences";
    public static String TABLE_NAME_TEMP = "PreferencesTemp";
    public static final String TABLE_SCRIPT = "Create table " + TABLE_NAME + " (activarNotificacion int, NotRecurrencia int, NotHora long, AppVibracion int)";
    private static DAL_Preferences myPref;
    Context context;

    private  DAL_Preferences(Context context){
        this.context = context;
    }

    public static DAL_Preferences  getInstance(Context c){
        if(myPref == null){
          myPref = new DAL_Preferences(c);
        }
        return myPref;
    }
    public void savePreference(Preference p) throws Exception{
      Preference pref = getSavedPreference();
        if(pref == null) {
            String sql = "Insert into "+TABLE_NAME+" (activarNotificacion, NotRecurrencia, NotHora, AppVibracion) Values" +
                    "("+p.getactivarNotifiacion()+", "+p.getRecurrencia()+","+p.getFechaALanzar()+", "+p.getVibrar()+" )";

            DB.getInstance(context).getWritableDatabase().execSQL(sql);
        }else{
            updatePreference(p);
        }

    }

    public void updatePreference(Preference p) throws Exception{
        String sql = "Update  "+TABLE_NAME+" set activarNotificacion= "+p.getactivarNotifiacion()+", NotRecurrencia = "+p.getRecurrencia()+", NotHora = "+p.getFechaALanzar()+", AppVibracion = "+p.getVibrar();
        DB.getInstance(context).getWritableDatabase().execSQL(sql);
    }

    public Preference getSavedPreference() throws Exception {
        Preference p = null;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 0);

        String sql = "select ifnull(activarNotificacion, 1) as activarNotificacion, ifnull(NotRecurrencia, 0) as NotRecurrencia, ifnull(NotHora, "+ cal.getTimeInMillis()+") as NotHora, ifnull(AppVibracion, 1) as AppVibracion " +
                "from "+TABLE_NAME+" ";
        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
        if(c.moveToFirst()){
         p = new Preference(c.getInt(c.getColumnIndex("activarNotificacion")),
                    c.getInt(c.getColumnIndex("NotRecurrencia")),
                    c.getLong(c.getColumnIndex("NotHora")),
                 c.getInt(c.getColumnIndex("AppVibracion")) );
        }c.close();

        return p;
    }

    public void updateNextAlarm () throws Exception{
        Preference p = getSavedPreference();
        if(p != null){
            Calendar old = Calendar.getInstance();
            old.setTimeInMillis(p.getFechaALanzar());//fecha a la que se debia lanzar esta alarma 8/1/17 2:00

            Calendar next = Calendar.getInstance();//fecha a la que se debe lanzar para el dia de hoy
            next.set(Calendar.HOUR_OF_DAY, old.get(Calendar.HOUR_OF_DAY));            // 8/1/17 2:00
            next.set(Calendar.MINUTE, old.get(Calendar.MINUTE));
            next.set(Calendar.SECOND, 0);

            String fechaProxima = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date(next.getTimeInMillis()));

            Calendar hoy = Calendar.getInstance();                                   // 8/1/17/ 2:10
            //Valida si la fecha que se va a colocar como proxima ya paso.
            // esto es en caso de que se haya apagadp el equipo o halla ocurrido algo y la
            //alarma se halla perdido. en dicho caso fija el dia de hoy a la hora configurada.
            //si cuando esto ocurre la hora configurada ya paso para cuando el usuario enncienda el telefono,
            // en dicho caso aumentara +1 dia a la fecha para que la alarma suene.
            String fechaHoy = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date(hoy.getTimeInMillis()));
            if(hoy.after(next)){
                next.add(Calendar.DAY_OF_MONTH, p.getRecurrencia());//8/2/17/ 2:10
            }
            fechaProxima = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date(next.getTimeInMillis()));
            p.setFechaALanzar(next.getTimeInMillis());
            updatePreference(p);

        }
    }
}
