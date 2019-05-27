package com.far.gpseed;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.camerakit.CameraKitView;
import com.far.gpseed.Utils.Funciones;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class CameraA2 extends AppCompatActivity {

    private CameraKitView cameraKitView;
    ArrayList<File> tempPhotos;
    RecyclerView rvTempImages;
    PhotoAdapter adapter;
    TextView tvDone;
    int counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_a2);
        tempPhotos = new ArrayList<>();
        tvDone = (TextView)findViewById(R.id.tvDone);
        cameraKitView = (CameraKitView) findViewById(R.id.camera);
        rvTempImages = (RecyclerView) findViewById(R.id.rvPhotos);
        LinearLayoutManager manager = new LinearLayoutManager(CameraA2.this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        adapter = new PhotoAdapter(CameraA2.this, tempPhotos);
        rvTempImages.setAdapter(adapter);
        rvTempImages.setLayoutManager(manager);


       try {
            cameraKitView.setErrorListener(new CameraKitView.ErrorListener() {
                @Override
                public void onError(CameraKitView cameraKitView, CameraKitView.CameraException e) {
                    Toast.makeText(CameraA2.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            });

            findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   counter++;
                   cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                       @Override
                       public void onImage(CameraKitView cameraKitView, final byte[] capturedImage) {
                           //Picasso.with(CameraA2.this);
                           saveImageTemp(capturedImage);

                       }
                   });
               }
           });


            findViewById(R.id.tvDone).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent();
                    i.putExtra("data", tempPhotos);
                    setResult(RESULT_OK, i);
                    finish();
                }
            });


        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
            cameraKitView.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
            cameraKitView.onResume();

    }

    @Override
    protected void onPause() {
            cameraKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
            cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public void saveImageTemp( byte[] capturedImage){

        AsyncTask<byte[], Void, File> a = new AsyncTask<byte[], Void, File>() {
            @Override
            protected File doInBackground(byte[]... bytes) {
                File result = null;
                byte[] capturedImage = bytes[0];
                try {
                   result = Funciones.saveImage(CameraA2.this, capturedImage, getResources().getString(R.string.name_Image_temp)+counter);
                }catch(Exception e){
                    e.getMessage().toString();
                }
                return result;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                //findViewById(R.id.llControls).setVisibility(View.GONE);
                //fotoTempBitmap = ((BitmapDrawable) fotoTemp.getDrawable()).getBitmap();
            }



            @Override
            protected void onPostExecute(File f) {
                super.onPostExecute(f);
                if(f == null){
                    Toast.makeText(CameraA2.this, "Error:", Toast.LENGTH_LONG).show();
                    return;
                }

                tempPhotos.add(f);
                adapter.notifyDataSetChanged();
                tvDone.setVisibility(View.VISIBLE);

                /*closeCamera();
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                finish();*/


            }
        };


        a.execute(capturedImage);
    }


    public void goToDetail(){
        if(tempPhotos.size() > 0){
            Intent i= new Intent(CameraA2.this,PhotoVisualization.class);
            i.putExtra("data", tempPhotos);
            startActivity(i);
        }
    }

}
