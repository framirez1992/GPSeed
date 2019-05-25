package com.far.gpseed.DAL;

import android.content.Context;
import android.database.Cursor;

import com.far.gpseed.Models.Versiculo;
import com.far.gpseed.Utils.Funciones;

import java.util.ArrayList;



public class DAL_Versiculos {
    public static final String TABLE_NAME = "Versiculos";
    public static final String TABLE_SCRIPT = "Create table " + TABLE_NAME + " (verID int, rowguid text, Texto text, Referencia text)";
    private static DAL_Versiculos versiculos = null;
    Context context;

    private DAL_Versiculos(Context context){
     this.context = context;
    }
    public static DAL_Versiculos getInstance(Context cont){
        if(versiculos == null){
            versiculos = new DAL_Versiculos(cont);
        }
        return versiculos;
    }

    public int getCount(){
        int v = 1;
        try {
            String sql = "select count(*) from "+TABLE_NAME;
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            if (c.moveToFirst()) {
                v = c.getInt(0) + 1;// si no hay entonces 0+1 = 1 --> versiculo1
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return v;
    }

    public void insert(Versiculo ver){
        try {
            String sql = "Insert into " + TABLE_NAME + " " +
                    "(verID,     rowguid,               Texto,                 Referencia) Values " +
                    "( "+getCount()+"         ,'" + ver.getrowguid() + "', '" + ver.getTexto() + "',  '" + ver.getReferencia() + "')";
            DB.getInstance(context).getWritableDatabase().execSQL(sql);
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void deleteAllVersiculos(){
        try {
            String sql = "delete from "+TABLE_NAME;
            DB.getInstance(context).getWritableDatabase().execSQL(sql);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public Versiculo getLastVersiculo(){
        Versiculo v = null;
        String texto = "";
        String referencia="";

        String sql = "select Texto as Texto, Referencia as Referencia  from "+TABLE_NAME+" ORDER BY verID DESC LIMIT 1";
        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
        if (c.moveToFirst()) {
          texto =   c.getString(c.getColumnIndex("Texto"));
          referencia = c.getString(c.getColumnIndex("Referencia"));
        }else{
            ArrayList<String> x = Funciones.getVersiculoDiario(context);
             texto =   x.get(0);
             referencia = x.get(1);
        }
        v = new Versiculo(texto, referencia);
        return  v;
    }

}
