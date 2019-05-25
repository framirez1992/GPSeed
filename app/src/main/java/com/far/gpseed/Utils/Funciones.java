package com.far.gpseed.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.far.gpseed.DAL.DAL_Preferences;
import com.far.gpseed.DAL.DAL_Versiculos;
import com.far.gpseed.Home;
import com.far.gpseed.Models.Versiculo;
import com.far.gpseed.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.jasypt.util.text.BasicTextEncryptor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Funciones {

    public static boolean isGPSProvider(Context context) {
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    public static void showKeyboard(Context context, EditText et){
        et.requestFocus(); //Asegurar que editText tiene focus
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void activarGPS(Activity act){
        final Intent poke = new Intent();
        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
        poke.setData(Uri.parse("3"));
        act.sendBroadcast(poke);
    }

    public static String generateHash(String value) {

        String input = String.valueOf(value);
        String md5 = null;

        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            //Update input string in message digest
            digest.update(input.getBytes(), 0, input.length());
            //Converts message digest value in base 16 (hex)
            md5 = new java.math.BigInteger(1, digest.digest()).toString(16);
        }
        catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5;
    }


    public static String Enc(String text, String pass)throws Exception{
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(pass);
        String myEncryptedText = textEncryptor.encrypt(text);
        return myEncryptedText;
    }

    public static String Dec(String text, String pass) throws Exception{
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(pass);
        String plainText = textEncryptor.decrypt(text);
        return plainText;
    }

    /** Check if this device has a camera */
    public static boolean deviceHasCamera(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public static void createAllFolders(Context c){
        String rutaRaiz = Environment.getExternalStorageDirectory()+"/"+c.getResources().getString(R.string.folder_raiz);
        String rutaImagen = Environment.getExternalStorageDirectory()+"/"+c.getResources().getString(R.string.folder_Image);
        File raiz = new File(rutaRaiz);
        if(!raiz.exists()){
            raiz.mkdirs();
        }
        File rutaImagenes = new File(rutaImagen);
        if(!rutaImagenes.exists()){
            rutaImagenes.mkdirs();
        }
    }
    public static String getImagesFolder(Context c){
        String rutaImagen = Environment.getExternalStorageDirectory()+"/"+c.getResources().getString(R.string.folder_Image)+"/";
        return  rutaImagen;
    }

    public static String getTempImageLocation(Context c){
        String rutaImagen = Funciones.getImagesFolder(c)+"temp.jpg";
        return  rutaImagen;
    }

    public static boolean existTempImage(Context c){
        String rutaImagen = getTempImageLocation(c);
        File f = new File(rutaImagen);
        return f.exists();
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels*2.2f;
        final float roundPy = pixels*3f;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPy, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static void setCircularImage(Context c,ImageView img) {
        String ruraImagen = getTempImageLocation(c);
        Picasso.with(c).load(Uri.fromFile(new File(ruraImagen)))
                .memoryPolicy(MemoryPolicy.NO_CACHE )
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .transform(new CircleTransformation()).fit().into(img);

    }

    public static boolean saveImage(Context context, Bitmap b, String fileName) throws Exception{

        createAllFolders(context);
        boolean result = false;
        File file = new File(Funciones.getImagesFolder(context)+fileName+".jpg");

        if(file.exists()) {
           file.delete();
        }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bytes = stream.toByteArray();
            OutputStream output = null;
            try {
                output = new FileOutputStream(file);
                output.write(bytes);
                result = true;
            } finally {
                if (null != output) {
                    output.close();
                }
            }

         return result;
    }

    public static boolean deleteImage(String url){
        File file = new File(url);
        if(file.exists()){
            return file.delete();
        }
        return false;
    }

    public static float getPixelsFromDp(Context con, int dp){
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, con.getResources().getDisplayMetrics());
        return px;
    }

    public static void createLogoFile(Context context) throws Exception{
        String ruta = getImagesFolder(context)+context.getResources().getString(R.string.name_Image_logo);
        if(new File(ruta).exists()){
            return;
        }else{
            Funciones.createAllFolders(context);
        }
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.card);
        saveImage(context,bitmap,context.getResources().getString(R.string.name_Image_logo));

    }

    public static String getRutaLogo(Context context) throws Exception{
        createLogoFile(context);
        return getImagesFolder(context)+context.getResources().getString(R.string.name_Image_logo)+".jpg";

    }

    public static int getRotation(Context c) {
         final int ORIENTATION_0 = 0;
         final int ORIENTATION_90 = 3;
         final int ORIENTATION_270 = 1;

        Display display = ((WindowManager)
                c.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int screenOrientation = display.getRotation();

        switch (screenOrientation) {
            default:
            case ORIENTATION_0: // Portrait

                return 0;
            case ORIENTATION_90: // Landscape right
                return 90;
            case ORIENTATION_270: // Landscape left
              return 270;
        }

    }

    public static ArrayList<String> getVersiculoDiario(Context con){
        int index = DAL_Versiculos.getInstance(con).getCount();
        String texto ="";
        ArrayList<String> result = new ArrayList<>();

        switch(index){
            case 1: texto = con.getResources().getString(R.string.v1); break;
            case 2: texto = con.getResources().getString(R.string.v2);break;
            case 3: texto = con.getResources().getString(R.string.v3);break;
            case 4: texto = con.getResources().getString(R.string.v4); break;
            case 5: texto = con.getResources().getString(R.string.v5);break;
            case 6: texto = con.getResources().getString(R.string.v6);break;
            case 7: texto = con.getResources().getString(R.string.v7);break;
            case 8: texto = con.getResources().getString(R.string.v8);break;
            case 9: texto = con.getResources().getString(R.string.v9);break;
            case 10: texto = con.getResources().getString(R.string.v10);break;
            case 11: texto = con.getResources().getString(R.string.v11);break;
            case 12: texto = con.getResources().getString(R.string.v12);break;
            case 13: texto = con.getResources().getString(R.string.v13);break;
            case 14: texto = con.getResources().getString(R.string.v14);break;
            case 15: texto = con.getResources().getString(R.string.v15);break;
            case 16: texto = con.getResources().getString(R.string.v16);break;
            case 17: texto = con.getResources().getString(R.string.v17);break;
            case 18: texto = con.getResources().getString(R.string.v18);break;
            case 19: texto = con.getResources().getString(R.string.v19);break;
            case 20: texto = con.getResources().getString(R.string.v20);break;
            case 21: texto = con.getResources().getString(R.string.v21);break;
            case 22: texto = con.getResources().getString(R.string.v22);break;
            case 23: texto = con.getResources().getString(R.string.v23);break;
            case 24: texto = con.getResources().getString(R.string.v24);break;
            case 25: texto = con.getResources().getString(R.string.v25);break;
            case 26: texto = con.getResources().getString(R.string.v26);break;
            case 27: texto = con.getResources().getString(R.string.v27);break;
            case 28: texto = con.getResources().getString(R.string.v28);break;
            case 29: texto = con.getResources().getString(R.string.v29);break;
            case 30: texto = con.getResources().getString(R.string.v30);break;
            case 31: texto = con.getResources().getString(R.string.v31);break;
            case 32: texto = con.getResources().getString(R.string.v32);break;
            case 33: texto = con.getResources().getString(R.string.v33);break;
            case 34: texto = con.getResources().getString(R.string.v34);break;
            case 35: texto = con.getResources().getString(R.string.v35);break;
            case 36: texto = con.getResources().getString(R.string.v36);break;
            case 37: texto = con.getResources().getString(R.string.v37);break;
            case 38: texto = con.getResources().getString(R.string.v38);break;
            case 39: texto = con.getResources().getString(R.string.v39);break;
            case 40: texto = con.getResources().getString(R.string.v40);break;
            case 41: texto = con.getResources().getString(R.string.v41);break;
            case 42: texto = con.getResources().getString(R.string.v42);break;
            case 43: texto = con.getResources().getString(R.string.v23);break;
            case 44: texto = con.getResources().getString(R.string.v44);break;
            case 45: texto = con.getResources().getString(R.string.v45);break;
            case 46: texto = con.getResources().getString(R.string.v46);break;
            case 47: texto = con.getResources().getString(R.string.v47);break;
            case 48: texto = con.getResources().getString(R.string.v48);break;
            case 49: texto = con.getResources().getString(R.string.v49);break;
            case 50: texto = con.getResources().getString(R.string.v50);break;
            case 51: texto = con.getResources().getString(R.string.v51);break;
            case 52: texto = con.getResources().getString(R.string.v52);break;
            case 53: texto = con.getResources().getString(R.string.v53);break;
            case 54: texto = con.getResources().getString(R.string.v54);break;
            case 55: texto = con.getResources().getString(R.string.v55);break;
            case 56: texto = con.getResources().getString(R.string.v56);break;
            case 57: texto = con.getResources().getString(R.string.v57);break;
            case 58: texto = con.getResources().getString(R.string.v58);break;
            case 59: texto = con.getResources().getString(R.string.v59);break;
            case 60: texto = con.getResources().getString(R.string.v60);break;
            case 61: texto = con.getResources().getString(R.string.v61);break;
            case 62: texto = con.getResources().getString(R.string.v62);break;
            case 63: texto = con.getResources().getString(R.string.v63);break;
            case 64: texto = con.getResources().getString(R.string.v64);break;
            case 65: texto = con.getResources().getString(R.string.v65);break;
            case 66: texto = con.getResources().getString(R.string.v66);break;
            case 67: texto = con.getResources().getString(R.string.v67);break;
            case 68: texto = con.getResources().getString(R.string.v68);break;
            case 69: texto = con.getResources().getString(R.string.v69);break;
            case 70: texto = con.getResources().getString(R.string.v70);break;
            case 71: texto = con.getResources().getString(R.string.v71);break;
            case 72: texto = con.getResources().getString(R.string.v72);break;
            case 73: texto = con.getResources().getString(R.string.v73);break;
            case 74: texto = con.getResources().getString(R.string.v74);break;
            case 75: texto = con.getResources().getString(R.string.v75);break;
            case 76: texto = con.getResources().getString(R.string.v76);break;
            case 77: texto = con.getResources().getString(R.string.v77);break;
            case 78: texto = con.getResources().getString(R.string.v78);break;
            case 79: texto = con.getResources().getString(R.string.v79);break;
            case 80: texto = con.getResources().getString(R.string.v80);break;
            case 81: texto = con.getResources().getString(R.string.v81);break;
            case 82: texto = con.getResources().getString(R.string.v82);break;
            default:
                DAL_Versiculos.getInstance(con).deleteAllVersiculos();
                texto = con.getResources().getString(R.string.v1);
                break;

        }

        String cuerpo = texto.split("@")[0];
        String versiculo = texto.split("@")[1];

        result.add(cuerpo);
        result.add(versiculo);

        DAL_Versiculos.getInstance(con).insert(new Versiculo(cuerpo, versiculo));

        return result;
    }


    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    public static void vibrate(Context c, int time){
        try {
            if (DAL_Preferences.getInstance(c).getSavedPreference().getVibrar() == 1) {
                Vibrator v = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
                // time in miliseconds
                v.vibrate(time);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
