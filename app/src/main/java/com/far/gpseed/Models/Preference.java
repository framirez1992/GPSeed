package com.far.gpseed.Models;

import java.util.Calendar;

public class Preference {
    int activarNotifiacion = 1;
    int recurrencia = 1;//diario
    int vibrar = 1;
    long notHora = 0;

    public Preference(int activarNotif, int rec, long horaNotifi, int vibrar){
        this.activarNotifiacion = activarNotif;
        this.recurrencia = rec;
        this.vibrar = vibrar;
        this.notHora = horaNotifi;
    }
    public Preference(){
        activarNotifiacion = 1;
        recurrencia = 1;
        vibrar = 1;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 21);//9:PM
        cal.set(Calendar.MINUTE, 0);
        notHora = cal.getTimeInMillis();
    }

    public int getactivarNotifiacion(){
        return activarNotifiacion;
    }
    public int getRecurrencia(){
        return  recurrencia;
    }
    public long getFechaALanzar(){
        return notHora;
    }
    public int getVibrar(){
        return vibrar;
    }

    public void setFechaALanzar(long fecha){
      this.notHora = fecha;
    }
}
