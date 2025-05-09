package com.far.gpseed.DAL;

import android.content.Context;
import android.database.Cursor;

import com.far.gpseed.Models.SeedLocation;
import com.far.gpseed.Utils.Funciones;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by framirez on 12/27/2017.
 */

public class DAL_References {
    Context mContext;
    public static final String TABLE_NAME = "GeoReferences";
    public static final String TABLE_SCRIPT = "CREATE TABLE "+TABLE_NAME+" (Id text, Latitude double, Longitude double, Description text,Description2 text, SaveDate datetime, ImageUrl text)";
    private static DAL_References mReference;
    private DAL_References(Context context){
        this.mContext = context;
    }

    public static synchronized DAL_References getInstance(Context con){
        if(mReference == null){
            mReference = new DAL_References(con);
        }
        return mReference;
    }


    public void Insert(SeedLocation seed) throws Exception{
        String sql = "Insert into "+TABLE_NAME+" " +
                "(Id,Description,Description2, Latitude, Longitude, SaveDate, ImageUrl) values " +
                "(?, ?, ?, ?, ?, ?, ?)";
        DB.getInstance(mContext).getWritableDatabase().execSQL(sql, new String[]{seed.Id, seed.Description,seed.Description2,Double.toString(seed.Latitude), Double.toString(seed.Longitude), "datetime('now')", null });
    }

    public void Update(SeedLocation seed) throws Exception{
        String sql = "Update  "+TABLE_NAME+" " +
                "set Id = ?,Description = ?,Description2 = ?, Latitude = ?, Longitude = ?, ImageUrl = ? " +
                "Where Id = ?";
        DB.getInstance(mContext).getWritableDatabase().execSQL(sql, new String[]{seed.Id,seed.Description, seed.Description2,Double.toString(seed.Latitude), Double.toString(seed.Longitude),null, seed.Id });
    }


    public void Delete(SeedLocation seed) throws Exception{
        String sql = "Delete from  "+TABLE_NAME+" " +
                     "Where Id = ?";
        DB.getInstance(mContext).getWritableDatabase().execSQL(sql, new String[]{seed.Id});
    }

    public void DeleteAll() throws Exception{
        String sql = "Delete from  "+TABLE_NAME+" ";
        DB.getInstance(mContext).getWritableDatabase().execSQL(sql, null);
    }

    public ArrayList<SeedLocation> GetReferences() throws Exception{
        return GetReferences("1=1", "Description");
    }
    public ArrayList<SeedLocation> GetReferences(String where) throws Exception{
        return GetReferences(where, "Description");
    }

    public ArrayList<SeedLocation> GetReferences(String where, String Order) throws Exception{
        ArrayList<SeedLocation> result = new ArrayList<>();
        String sql = "Select Id as Id, Description as Description,Description2 as Description2, Latitude as Latitude, Longitude as Longitude, SaveDate as SaveDate,ImageUrl as ImageUrl   from  "+TABLE_NAME+" " +
                "Where "+where+" Order by "+Order;
        Cursor c = DB.getInstance(mContext).getReadableDatabase().rawQuery(sql, null);
        while(c.moveToNext()){
            SeedLocation sl = new SeedLocation();
            sl.Id = c.getString(c.getColumnIndex("Id"));
            sl.Description = c.getString(c.getColumnIndex("Description"));
            sl.Description2 = c.getString(c.getColumnIndex("Description2"));
            sl.Longitude = c.getDouble(c.getColumnIndex("Longitude"));
            sl.Latitude = c.getDouble(c.getColumnIndex("Latitude"));

            sl.imageUrls = getImageUrls(sl.Id);

            result.add(sl);
        }
        return result;
    }

    public boolean exist(String Id){
        boolean result = false;
        try {
            String sql = "Select Id as Id, Description as Description,Description2 as Description2, Latitude as Latitude, Longitude as Longitude, SaveDate as SaveDate,ImageUrl as ImageUrl   from  " + TABLE_NAME + " " +
                    "Where trim(Id) = trim('" + Id + "')";
            Cursor c = DB.getInstance(mContext).getReadableDatabase().rawQuery(sql, null);
            if (c.moveToFirst()) {
                result = true;
            }
            c.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<String> getImageUrls(String sID){
        ArrayList<String> data = new ArrayList<>();
        File folder = new File(Funciones.getImagesFolder(mContext)+sID+"/");
        File[] files = folder.listFiles();
        if(files != null && files.length > 0) {
            for (File f : files) {
                data.add(f.getAbsolutePath());
            }
        }
        return data;
    }
}
