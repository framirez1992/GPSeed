package com.far.gpseed.DAL;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.far.gpseed.Models.Versiculo;
import com.far.gpseed.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mdsoft on 12/27/2017.
 */

public class DB extends SQLiteOpenHelper {
    private static DB dataBase = null;
    Context myContext;
    private static final String DBNAME = "GPSEED.db";
    private DB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.myContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        crearEstructura(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if(newVersion > oldVersion){
            recrearEstructura(db);
        }
    }

    public synchronized static DB getInstance(Context context){
        if(dataBase == null){
            dataBase = new DB(context,DBNAME,null, context.getResources().getInteger(R.integer.DBVersion));
        }
        return dataBase;
    }

    public void crearEstructura(SQLiteDatabase db){
        db.execSQL(DAL_References.TABLE_SCRIPT);
        db.execSQL(DAL_Preferences.TABLE_SCRIPT);
        db.execSQL(DAL_Versiculos.TABLE_SCRIPT);
    }

    public void recrearEstructura(SQLiteDatabase db){
        boolean estructuraCreada=false;
        String sql = "SELECT name as NAME FROM sqlite_master where type = 'table'";
        ArrayList<HashMap<String, String>> Tablas = new ArrayList<>();
        HashMap<String, String> hm = new HashMap();
        hm.put("TABLE", DAL_References.TABLE_NAME.toUpperCase());
        hm.put("SCRIPT",DAL_References.TABLE_SCRIPT);
        Tablas.add(hm);
        hm = new HashMap<>();
        hm.put("TABLE", DAL_Preferences.TABLE_NAME.toUpperCase());
        hm.put("SCRIPT",DAL_Preferences.TABLE_SCRIPT);
        Tablas.add(hm);
        hm = new HashMap<>();
        hm.put("TABLE", DAL_Versiculos.TABLE_NAME.toUpperCase());
        hm.put("SCRIPT",DAL_Versiculos.TABLE_SCRIPT);
        Tablas.add(hm);

        ArrayList<String> TablasCreadas = new ArrayList<>();
        try{
            Cursor c = db.rawQuery(sql, null);
            while(c.moveToNext()){
                if(!c.getString(c.getColumnIndex("NAME")).equalsIgnoreCase("ANDROID_METADATA")){
                    TablasCreadas.add(c.getString(c.getColumnIndex("NAME")).toUpperCase());
                }

            }c.close();

            if(Tablas.size() == TablasCreadas.size()){
                estructuraCreada = true;
            }

        }catch(Exception e){

        }
        if(!estructuraCreada){

           for(HashMap<String, String> t: Tablas){
               moveData(db,t.get("TABLE"), t.get("SCRIPT"));
            }
        }
    }

    public void dropTable(SQLiteDatabase db, String Table){
        try {
            String sql = "Drop TABLE " + Table + " ";
            db.execSQL(sql);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void moveData(SQLiteDatabase db,  String tableName,String newScript){
        String tempTableName = tableName+"Temp";
        String script ="Create table "+tempTableName+" ( ";
        String sql = "pragma table_info("+tableName+")";
        Cursor c = db.rawQuery(sql, null);

        String oldColumns="";
        boolean first = true;
        while(c.moveToNext()){
            script+= ((first)?"":", ")+c.getString(c.getColumnIndex("name"))+" "+c.getString(c.getColumnIndex("type"));
            oldColumns+= ((first)?"":", ")+c.getString(c.getColumnIndex("name"));
            first = false;
        }

        if(first){//nunca entro al while (la tabla no existe)
            db.execSQL(newScript);
            return;
        }

        script+=" )";
        oldColumns+=" ";

        db.execSQL(script);

        String selectInsert = "Insert into "+tempTableName+" (" +oldColumns+" ) "
                             +"select "+oldColumns+" from "+tableName;

        db.execSQL(selectInsert);

        dropTable(db, tableName);

        db.execSQL(newScript);

        selectInsert = "Insert into "+tableName+" (" +oldColumns+" ) "
                +"select "+oldColumns+" from "+tempTableName;

        db.execSQL(selectInsert);


    }
}
