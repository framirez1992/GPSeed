package com.far.gpseed;

import android.Manifest;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.far.gpseed.DAL.DAL_Preferences;
import com.far.gpseed.DAL.DAL_References;
import com.far.gpseed.Helpers.AlarmManager;
import com.far.gpseed.Models.CV;
import com.far.gpseed.Models.Preference;
import com.far.gpseed.Models.SeedLocation;
import com.far.gpseed.Utils.Funciones;
import com.far.gpseed.Utils.SeedAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Home extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, OnRequestPermissionsResultCallback {

    private TextView tvMensaje;
    LinearLayout llSeed, llSearchingLocation,llMsgNoLocation, llSeedOptions, llMySeeds, llSettings, btnCancelar, btnCompartir, btnFoto, btnAceptar;
    ImageView btnGetLocation;
    GoogleApiClient mGoogleApiClient;
    LocationCallback mLocationCallback;
    ProgressBar pbSeed;
    SeedLocation myTempSeed = null;
    GridView gvMySeeds;
    int REQUEST_CODE_FOTO = 1234;

    //CONFIGURATIONS
    CheckBox cbActivarNotificaciones;
    CheckBox cbActivarVibracion;
    Button btnHoraDialog;
    Spinner spnRecurrencia;
    Calendar myCalendar;
    Button btnAceptarConfig;
    String textFormat ="";
    Preference savedPreference;
    BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            showLayout(item.getItemId());
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        init();
        Intent x = getIntent();
        if(x.getData() != null) {
            try {
                String data = x.getData().toString();
                SeedLocation incomingSeed = getSeedFromData(data);
                showDialogSeedOptions(incomingSeed);
            }catch(Exception e){
             Toast.makeText(Home.this, getResources().getString(R.string.msg_error_recibiendoinfo), Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void init() {
        Funciones.createAllFolders(Home.this);

        mGoogleApiClient = new GoogleApiClient.Builder(Home.this)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .addOnConnectionFailedListener(this)
                .build();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Funciones.vibrate(Home.this, 100);
                if(locationResult != null){

                    LocationServices.getFusedLocationProviderClient(Home.this).removeLocationUpdates(mLocationCallback);
                    pbSeed.setVisibility(View.INVISIBLE);

                    myTempSeed = new SeedLocation();
                    myTempSeed.Latitude = locationResult.getLastLocation().getLatitude();
                    myTempSeed.Longitude = locationResult.getLastLocation().getLongitude();
                    myTempSeed.Description = new Date().toString();

                    if(llSeedOptions.getVisibility()== View.INVISIBLE) {
                        llSeedOptions.setVisibility(View.VISIBLE);
                        creacer(btnCancelar, 50);
                        creacer(btnCompartir, 100);
                        creacer(btnFoto, 150);
                        creacer(btnAceptar, 200);
                    }

                    tvMensaje.setText(getResources().getString(R.string.coordenadas)+":\n"+getResources().getString(R.string.latitude)+myTempSeed.Latitude+", "+getResources().getString(R.string.longitude)+myTempSeed.Longitude);
                    tvMensaje.setTextColor(getResources().getColor(R.color.grey_300));
                }else{
                    tvMensaje.setText(getResources().getString(R.string.msg_fallo_intentando));
                    tvMensaje.setTextColor(Color.RED);
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);

                if(!Funciones.isGPSProvider(Home.this)){
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    showNoLocationActivatedMessage();
                }else if (!locationAvailability.isLocationAvailable()) {
                    hideNoLocationActivatedMessage();
                    tvMensaje.setText(getResources().getString(R.string.msg_noesposible));
                    tvMensaje.setTextColor(Color.RED);
                    pbSeed.setVisibility(View.GONE);
                }else{
                    hideNoLocationActivatedMessage();
                }

            }

        };

        btnGetLocation = (ImageView) findViewById(R.id.btnGetLocation);
        tvMensaje = (TextView)findViewById(R.id.tvMensaje);
        llSeed = (LinearLayout)findViewById(R.id.llSeed);
        llSearchingLocation = (LinearLayout)findViewById(R.id.llSearchingLocation);
        llMsgNoLocation = (LinearLayout)findViewById(R.id.llMsgNoLocation);
        llSeedOptions =(LinearLayout)findViewById(R.id.llSeedOptions);
        llMySeeds = (LinearLayout)findViewById(R.id.llMySeeds);
        llSettings = (LinearLayout)findViewById(R.id.llSettings);
        gvMySeeds = (GridView)findViewById(R.id.gvMySeeds);

        pbSeed = (ProgressBar)findViewById(R.id.pbSeed);

        btnAceptar = (LinearLayout) findViewById(R.id.btnGuardar);
        btnCancelar = (LinearLayout) findViewById(R.id.btnCancelar);
        btnFoto = (LinearLayout) findViewById(R.id.btnFoto);
        btnCompartir = (LinearLayout) findViewById(R.id.btnShare);

        if(!Funciones.deviceHasCamera(Home.this)){
         btnFoto.setVisibility(View.GONE);
        }

        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /////////////////////////////////////////////
                //       Activa GPS si esta apagado      ///
                ////////////////////////////////////////////
               // if(!Funciones.isGPSProvider(Home.this)){
                 //   Funciones.activarGPS(Home.this);
                //}else{
                //    hideNoLocationActivatedMessage();
                //}
                ////////////////////////////////////////////

                rebotar(v);

                LocationRequest lr = new LocationRequest();
                lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                lr.setInterval(1000);//1 segundo

                if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(Home.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            getResources().getInteger(R.integer.REQUESTPERMISSION_GPS));
                    return;
                }

                Funciones.vibrate(Home.this, 200);

                LocationServices.getFusedLocationProviderClient(Home.this).requestLocationUpdates(lr, mLocationCallback, null);
                tvMensaje.setText(getResources().getString(R.string.msg_obteniendogps));
                tvMensaje.setTextColor(getResources().getColor(R.color.grey_300));
                pbSeed.setVisibility(View.VISIBLE);
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetLlSeed();
            }
        });


        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add permission for camera and let user grant the permission
                if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, getResources().getInteger(R.integer.REQUESTPERMISSION_FOTO));
                    return;
                }
                startActivityForResult(new Intent(Home.this, CameraA2.class), REQUEST_CODE_FOTO);
            }
        });


        btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(Home.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            getResources().getInteger(R.integer.REQUESTPERMISSION_READFILES_SHARE));
                    return;
                }

                if(Funciones.existTempImage(Home.this)){
                    myTempSeed.imageUrl = Funciones.getTempImageLocation(Home.this);
                }
               Share(myTempSeed);
            }
        });

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            showDialogSave();
            }
        });

        gvMySeeds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SeedLocation sl = (SeedLocation) parent.getAdapter().getItem(position);
                showDialogSeedOptions(sl);
            }
        });

        initConfiguration();
        initializeAlarm();

        if ((ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(Home.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    getResources().getInteger(R.integer.REQUESTPERMISSION_INTRO));
            return;
        }


        }


    public void initConfiguration(){
        cbActivarVibracion = (CheckBox)findViewById(R.id.cbActivarVibracion);
        cbActivarNotificaciones = (CheckBox)findViewById(R.id.cbActivarNotificaciones);
        spnRecurrencia = (Spinner)findViewById(R.id.spnRecurencia);
        btnHoraDialog = (Button) findViewById(R.id.btnDialogoHora);
        btnAceptarConfig =(Button)findViewById(R.id.btnAceptar);

        cbActivarNotificaciones.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    spnRecurrencia.setEnabled(false);
                    spnRecurrencia.setAlpha(0.5f);
                    btnHoraDialog.setEnabled(false);
                }else{
                    spnRecurrencia.setEnabled(true);
                    spnRecurrencia.setAlpha(1f);
                    btnHoraDialog.setEnabled(true);
                }
            }
        });

        btnHoraDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime();
            }
        });

        btnAceptarConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int activarNotificaciones = (cbActivarNotificaciones.isChecked())?1:0;
                    int recurrencia = Integer.parseInt(((CV)spnRecurrencia.getAdapter().
                            getItem(spnRecurrencia.getSelectedItemPosition())).getClave());
                    if(Calendar.getInstance().getTimeInMillis() > myCalendar.getTimeInMillis()){//si la hora para la cual se configuro paso, fijar para la proxima
                        myCalendar.add(Calendar.DAY_OF_MONTH, recurrencia);

                    }
                    long myDate = myCalendar.getTimeInMillis();
                    int vibrar = (cbActivarVibracion.isChecked())?1:0;


                    DAL_Preferences.getInstance(Home.this).savePreference(new Preference(activarNotificaciones, recurrencia, myDate, vibrar));
                    AlarmManager am = new AlarmManager(Home.this);Calendar myC = Calendar.getInstance();
                    Preference myPref = DAL_Preferences.getInstance(Home.this).getSavedPreference();

                    myC.setTimeInMillis(myPref.getFechaALanzar());
                    am.setAlarm(myC);

                    Toast.makeText(Home.this, getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        spnRecurrencia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.grey_300));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                    @NonNull int[] grantResults){
        if(/*requestCode == getResources().getInteger(R.integer.REQUESTPERMISSION_INTRO)
                ||*/ requestCode == getResources().getInteger(R.integer.REQUESTPERMISSION_GPS)){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){//GPS
                tvMensaje.setText("");
                btnGetLocation.callOnClick();
            }else{
                tvMensaje.setText(getResources().getString(R.string.msg_debeaceptarpermisos));
                tvMensaje.setTextColor(Color.RED);
            }
        }

        if(requestCode == getResources().getInteger(R.integer.REQUESTPERMISSION_FOTO)){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED){//CAMARA
                 btnFoto.performClick();
            }else{
                Toast.makeText(Home.this, getResources().getString(R.string.msg_permissionscamera), Toast.LENGTH_SHORT).show();
            }
        }

        if(requestCode == getResources().getInteger(R.integer.REQUESTPERMISSION_READFILES_SHARE)){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                btnCompartir.performClick();
            }else{
                Toast.makeText(Home.this, getResources().getString(R.string.msg_permissionshare), Toast.LENGTH_SHORT).show();
            }
        }


        if(requestCode == getResources().getInteger(R.integer.REQUESTPERMISSION_READFILES_GRID)){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
               //cargar imagen
            }else{
               Toast.makeText(Home.this, getResources().getString(R.string.msg_permissonsviewimagen), Toast.LENGTH_SHORT).show();            }
        }

    }


    public void showLayout(int id){
        llSeed.setVisibility(View.GONE);
        llMySeeds.setVisibility(View.GONE);
        llSettings.setVisibility(View.GONE);

        switch (id){
            case R.id.navigation_home: llSeed.setVisibility(View.VISIBLE);break;
            case R.id.navigation_dashboard: llMySeeds.setVisibility(View.VISIBLE); fillGridMySeeds();break;
            case R.id.navigation_notifications: llSettings.setVisibility(View.VISIBLE); setDefaultConfigurationValues();break;

        }

    }

    public void ResetLlSeed(){
        hideMainOptions();
        myTempSeed = null;
        Funciones.deleteImage(Funciones.getTempImageLocation(Home.this));
        btnGetLocation.setImageResource(R.mipmap.seed);

        pbSeed.setVisibility(View.INVISIBLE);
        tvMensaje.setText("");
        tvMensaje.setTextColor(getResources().getColor(R.color.grey_300));

    }

    public void guardar(final SeedLocation sl){

        AsyncTask<Void, Void, String> at = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    sl.Id = UUID.randomUUID().toString();
                    sl.imageUrl = (Funciones.existTempImage(Home.this))?
                            Funciones.getImagesFolder(Home.this)+sl.Id+".jpg"
                            :"";
                    DAL_References.getInstance(Home.this).Insert(sl);
                    if(Funciones.existTempImage(Home.this)) {
                        Bitmap tempImage = BitmapFactory.decodeFile(Funciones.getTempImageLocation(Home.this));
                        Funciones.saveImage(Home.this, tempImage, sl.Id);
                    }
                }catch(Exception e){
                   return e.getMessage().toString();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(s == null){
                    ResetLlSeed();
                    Toast.makeText(Home.this, getResources().getString(R.string.msg_saved), Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(Home.this, s, Toast.LENGTH_LONG).show();
            }
        };
        at.execute();

    }

    public void editar(SeedLocation sl){
        try {
            DAL_References.getInstance(Home.this).Update(sl);
            Toast.makeText(Home.this, getResources().getString(R.string.edited), Toast.LENGTH_LONG).show();
            fillGridMySeeds();
        }catch(Exception e){
           e.printStackTrace();
        }
    }

    public void fillGridMySeeds(){
    try {
        //ArrayAdapter<SeedLocation> adapter = new ArrayAdapter(Home.this, R.layout.seed_layout, R.id.tvDescripcion,DAL_References.getInstance(Home.this).GetReferences());
        SeedAdapter adapter = new SeedAdapter(Home.this,DAL_References.getInstance(Home.this).GetReferences());
        gvMySeeds.setAdapter(adapter);
    }catch(Exception e){
        e.printStackTrace();
    }
        }

    /**
     * inicia la navegacion de google maps en la ubicacion seleccionada. d = Driving, w = Walking
     * @param seed
     */
    public void IniciarNavegacion(SeedLocation destino, String medio){

        /*String uri = (medio.equals("w-offline"))
                ?"geo:"+destino.Latitude+","+destino.Longitude
                :"google.navigation:q="+destino.Latitude+","+destino.Longitude+"&mode="+medio;*/

        Uri gmmIntentUri = Uri.parse("google.navigation:q="+destino.Latitude+","+destino.Longitude+"&mode="+medio);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }else{
            Toast.makeText(Home.this, getResources().getString(R.string.msg_nogooglemap), Toast.LENGTH_LONG).show();
        }


    }

    public void showDialogSave(){
        final Dialog d = new Dialog(Home.this);
        d.setContentView(R.layout.dialog_save);
        d.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        d.setTitle("Save location");
        final EditText etNombre = (EditText)d.findViewById(R.id.etNombre);
        LinearLayout btnAceptar = (LinearLayout)d.findViewById(R.id.btnAceptar);
        LinearLayout btnCancelar = (LinearLayout)d.findViewById(R.id.btnCancelar);

        etNombre.setText(myTempSeed.Description);
        etNombre.setSelection(etNombre.getText().toString().length());

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTempSeed.Description = etNombre.getText().toString();
                guardar(myTempSeed);
                d.dismiss();
                hideMainOptions();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Funciones.showKeyboard(Home.this,etNombre);
                etNombre.setSelection(0, etNombre.getText().toString().length());
            }
        }, 500);
        d.show();
    }

    public void Share(final SeedLocation sl){
        try {

            AsyncTask<Void, Void, String> as = new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    String data ="";
                    String rutaImagen="";
                    try {
                        data = Double.toString(sl.Latitude) + "," + Double.toString(sl.Longitude) + "," + sl.Description;
                        data = Funciones.Enc(data, getResources().getString(R.string.passEncryption));

                        rutaImagen = (sl.imageUrl != null && new File(sl.imageUrl).exists())
                                ?sl.imageUrl
                                :Funciones.getRutaLogo(Home.this);

                    }catch(Exception e){
                        return "FAIL-"+e.getMessage().toString();
                    }

                     return "OK-"+rutaImagen+"xcut"+data;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);

                    if(s.startsWith("OK-")){
                        String data = s.replace("OK-", "").split("xcut")[1];
                        String rutaImagen = s.replace("OK-", "").split("xcut")[0];

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                getResources().getString(R.string.enlace_app_store)
                                +getResources().getString(R.string.titulo_press_url)
                                +"http://www.far.com/gpseed/-"+data);//WorkGreat
                        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(rutaImagen)));
                        sendIntent.setType("*/*");

                        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
                    }else{
                        Toast.makeText(Home.this, s, Toast.LENGTH_LONG).show();
                    }

                }
            };
            as.execute();
        }catch (Exception e){
            Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void shareMyApp(Button btnCancelar) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id="+getPackageName()));
        startActivity(intent);
    }

    public SeedLocation getSeedFromData(String data) throws Exception {
        String location = data.split("-")[1];
        location = Funciones.Dec(location, getResources().getString(R.string.passEncryption));
        SeedLocation sl = new SeedLocation();
        sl.Latitude = Double.valueOf(location.split(",")[0]);
        sl.Longitude = Double.valueOf(location.split(",")[1]);
        sl.Description = location.split(",")[2];

        return sl;
    }

    public void showDialogSeedOptions(final SeedLocation sl){

        try {
            final Dialog d = new Dialog(Home.this/*, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen*/);
            //d.setTitle("Incoming Seed");

            d.setContentView(R.layout.dialog_incoming_seed);
            final TextView tvLatitud = (TextView) d.findViewById(R.id.tvLatitud);
            final TextView tvLongitud = (TextView) d.findViewById(R.id.tvLongitud);
            final EditText etDescripcion = (EditText) d.findViewById(R.id.etDescripcion);
            final TextInputLayout tilDescripcion = (TextInputLayout)d.findViewById(R.id.tilDescripcion);

            final TableRow trMainOptions = (TableRow)d.findViewById(R.id.trMainOptions);
            final TableRow trGoOptions = (TableRow)d.findViewById(R.id.trGoOptions);
            final TableRow trCrud = (TableRow)d.findViewById(R.id.trCrud);

            LinearLayout btnClose = (LinearLayout) d.findViewById(R.id.btnClose);
            final LinearLayout btnMore = (LinearLayout) d.findViewById(R.id.btnMore);
            final LinearLayout btnBackLlCrud = (LinearLayout) d.findViewById(R.id.btnBackLlCrud);
            final LinearLayout btnGo = (LinearLayout) d.findViewById(R.id.btnGo);
            final LinearLayout btnSave = (LinearLayout) d.findViewById(R.id.btnSave);
            final LinearLayout btnEdit = (LinearLayout) d.findViewById(R.id.btnEdit);
            final LinearLayout btnBackEdicion = (LinearLayout)d.findViewById(R.id.btnBackEdicion);
            final LinearLayout btnDelete = (LinearLayout) d.findViewById(R.id.btnDelete);
            final LinearLayout btnShare = (LinearLayout) d.findViewById(R.id.btnShare);
            final LinearLayout btnBackGoOptions = (LinearLayout)d.findViewById(R.id.btnBackLlGo);
            final LinearLayout btnGoOptionDrive = (LinearLayout)d.findViewById(R.id.btnGoDriving);
            final LinearLayout btnGoOptionWalking = (LinearLayout)d.findViewById(R.id.btnGoWalking);


            ImageView imgFoto = (ImageView)d.findViewById(R.id.imgFoto);
            final TextView tvDescripcion = (TextView)d.findViewById(R.id.tvDescripcion);
            tvDescripcion.setText(sl.Description);

            if(ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                String ruta = (sl.imageUrl != null && new File(sl.imageUrl).exists())
                        ? sl.imageUrl
                        : Funciones.getRutaLogo(Home.this);
                Picasso.with(Home.this).load(Uri.fromFile(new File(ruta))).into(imgFoto);
            }else{
                imgFoto.setBackground(getResources().getDrawable(R.drawable.card));
                    ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            getResources().getInteger(R.integer.REQUESTPERMISSION_READFILES_GRID));
            }

            tvLatitud.setText(Double.toString(sl.Latitude));
            tvLongitud.setText(Double.toString(sl.Longitude));
            etDescripcion.setText(sl.Description);


            btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    esconder(btnMore, 0, 0, new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            esconder(btnGo, 50, 0, new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {

                                    esconder(btnShare, 100, 0, new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {

                                            boolean isCreated = false;
                                            try {
                                                isCreated = (DAL_References.getInstance(Home.this).GetReferences("trim(Id) = trim('" + sl.Id + "')").size() > 0);
                                            }catch(Exception e){
                                                e.printStackTrace();
                                            }

                                                btnMore.setVisibility(View.GONE);
                                                btnGo.setVisibility(View.GONE);
                                                btnShare.setVisibility(View.GONE);
                                                trMainOptions.setVisibility(View.GONE);

                                                trCrud.setVisibility(View.VISIBLE);
                                                btnBackLlCrud.setVisibility(View.VISIBLE);
                                                creacer(btnBackLlCrud, 0);

                                                //SI EXISTE EN LA BASE DE DATOS
                                                if (isCreated) {
                                                    btnSave.setVisibility(View.GONE);
                                                    btnDelete.setVisibility(View.VISIBLE);
                                                    btnEdit.setVisibility(View.VISIBLE);

                                                    creacer(btnDelete, 50);
                                                    creacer(btnEdit, 100);
                                                } else {
                                                    btnDelete.setVisibility(View.GONE);
                                                    btnEdit.setVisibility(View.GONE);
                                                    btnSave.setVisibility(View.VISIBLE);
                                                    creacer(btnSave, 50);
                                                }
                                        }

                                    });
                                }

                            });
                        }
                    });


                }
            });

            btnBackLlCrud.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    boolean isCreated = false;
                    try {
                        isCreated = (DAL_References.getInstance(Home.this).GetReferences("trim(Id) = trim('" + sl.Id + "')").size() > 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (isCreated){
                        esconder(btnEdit, 0, 0, new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                esconder(btnDelete, 50, 0, new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {

                                        esconder(btnBackLlCrud, 100, 0, new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {

                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) {

                                            }

                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                btnEdit.setVisibility(View.GONE);
                                                btnDelete.setVisibility(View.GONE);
                                                btnBackLlCrud.setVisibility(View.GONE);

                                                trCrud.setVisibility(View.GONE);
                                                trMainOptions.setVisibility(View.VISIBLE);

                                                btnMore.setVisibility(View.VISIBLE);
                                                btnGo.setVisibility(View.VISIBLE);
                                                btnShare.setVisibility(View.VISIBLE);

                                                creacer(btnMore, 0);
                                                creacer(btnGo, 50);
                                                creacer(btnShare, 100);

                                            }

                                        });

                                    }

                                });
                            }

                        });
                }else{
                        esconder(btnSave, 50, 0, new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                esconder(btnBackLlCrud, 100, 0, new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        btnSave.setVisibility(View.GONE);
                                        btnBackLlCrud.setVisibility(View.GONE);

                                        trCrud.setVisibility(View.GONE);
                                        trMainOptions.setVisibility(View.VISIBLE);

                                        btnMore.setVisibility(View.VISIBLE);
                                        btnGo.setVisibility(View.VISIBLE);
                                        btnShare.setVisibility(View.VISIBLE);

                                        creacer(btnMore, 0);
                                        creacer(btnGo, 50);
                                        creacer(btnShare, 100);

                                    }

                                });

                            }

                        });
                }


            }
        });


            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                }
            });

            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Share(sl);
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        DAL_References.getInstance(Home.this).Delete(sl);
                        Funciones.deleteImage(sl.imageUrl);
                        d.dismiss();
                        Toast.makeText(Home.this, getResources().getString(R.string.deleted), Toast.LENGTH_LONG).show();
                        fillGridMySeeds();
                    }catch(Exception e){
                        Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        tvDescripcion.setVisibility(View.GONE);
                        tilDescripcion.setVisibility(View.VISIBLE);
                        etDescripcion.setText(tvDescripcion.getText().toString());
                        etDescripcion.setSelection(0, tvDescripcion.getText().toString().length());
                        Funciones.showKeyboard(Home.this,etDescripcion);

                        esconder(btnBackLlCrud, 0, 0, new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }
                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                esconder(btnDelete, 50, 0, new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }
                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        esconder(btnEdit, 100, 0, new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {

                                            }
                                            @Override
                                            public void onAnimationRepeat(Animation animation) {

                                            }
                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                btnBackLlCrud.setVisibility(View.GONE);
                                                btnDelete.setVisibility(View.GONE);
                                                btnEdit.setVisibility(View.GONE);

                                                btnBackEdicion.setVisibility(View.VISIBLE);
                                                btnSave.setVisibility(View.VISIBLE);

                                                creacer(btnBackEdicion, 0);
                                                creacer(btnSave, 50);
                                            }
                                        });


                                    }
                                });
                            }
                        });




                    }catch(Exception e){
                        Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                }
            });

            btnBackEdicion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    esconder(btnSave, 0, 0, new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }  @Override
                        public void onAnimationRepeat(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            esconder(btnBackEdicion, 0, 0, new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }  @Override
                                public void onAnimationRepeat(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {

                                    btnSave.setVisibility(View.GONE);
                                    btnBackEdicion.setVisibility(View.GONE);

                                    btnBackLlCrud.setVisibility(View.VISIBLE);
                                    btnDelete.setVisibility(View.VISIBLE);
                                    btnEdit.setVisibility(View.VISIBLE);

                                    creacer(btnBackLlCrud, 0);
                                    creacer(btnDelete, 50);
                                    creacer(btnEdit, 100);
                                }
                            });
                        }
                    });

                    //btnBackEdicion.setVisibility(View.GONE);
                    //btnSave.setVisibility(View.GONE);
                    tilDescripcion.setVisibility(View.GONE);
                    tvDescripcion.setVisibility(View.VISIBLE);
                    etDescripcion.setText("");

                    //btnBackLlCrud.setVisibility(View.VISIBLE);
                    //btnEdit.setVisibility(View.VISIBLE);
                    //btnDelete.setVisibility(View.VISIBLE);


                }
            });

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(DAL_References.getInstance(Home.this).exist(sl.Id)){
                        sl.Description = etDescripcion.getText().toString();
                        editar(sl);

                        btnBackEdicion.setVisibility(View.GONE);
                        btnSave.setVisibility(View.GONE);
                        btnDelete.setVisibility(View.VISIBLE);
                        btnEdit.setVisibility(View.VISIBLE);
                        btnBackLlCrud.setVisibility(View.VISIBLE);

                        tilDescripcion.setVisibility(View.GONE);
                        tvDescripcion.setVisibility(View.VISIBLE);

                    }else {

                        sl.Id = UUID.randomUUID().toString();
                        sl.Latitude = Double.parseDouble(tvLatitud.getText().toString());
                        sl.Longitude = Double.parseDouble(tvLongitud.getText().toString());
                        sl.Description = etDescripcion.getText().toString();
                        guardar(sl);
                        ResetLlSeed();
                        fillGridMySeeds();
                        Toast.makeText(Home.this, getResources().getString(R.string.msg_saved), Toast.LENGTH_LONG).show();
                    }
                    d.dismiss();


                }
            });

            btnGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    esconder(btnMore, 0, 0, new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                            esconder(btnGo, 50, 0, new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {

                                    esconder(btnShare, 100, 0, new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            btnMore.setVisibility(View.GONE);
                                            btnGo.setVisibility(View.GONE);
                                            btnShare.setVisibility(View.GONE);
                                            trMainOptions.setVisibility(View.GONE);

                                            trGoOptions.setVisibility(View.VISIBLE);
                                            btnBackGoOptions.setVisibility(View.VISIBLE);
                                            btnGoOptionDrive.setVisibility(View.VISIBLE);
                                            btnGoOptionWalking.setVisibility(View.VISIBLE);

                                            creacer(btnBackGoOptions, 0);
                                            creacer(btnGoOptionDrive, 50);
                                            creacer(btnGoOptionWalking, 100);
                                        }

                                    });
                                }

                            });
                        }
                    });
                }
            });

            btnBackGoOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    esconder(btnGoOptionWalking, 0, 0, new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        } @Override
                        public void onAnimationRepeat(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            esconder(btnGoOptionDrive, 50, 0, new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                } @Override
                                public void onAnimationRepeat(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    esconder(btnBackGoOptions, 100, 0, new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        } @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            btnGoOptionWalking.setVisibility(View.GONE);
                                            btnGoOptionDrive.setVisibility(View.GONE);
                                            btnBackGoOptions.setVisibility(View.GONE);

                                            trGoOptions.setVisibility(View.GONE);

                                            trMainOptions.setVisibility(View.VISIBLE);
                                            btnMore.setVisibility(View.VISIBLE);
                                            btnGo.setVisibility(View.VISIBLE);
                                            btnShare.setVisibility(View.VISIBLE);

                                            creacer(btnMore, 0);
                                            creacer(btnGo, 50);
                                            creacer(btnShare, 100);


                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });

            btnGoOptionDrive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IniciarNavegacion(sl, "d");
                }
            });

            btnGoOptionWalking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // if(Funciones.isOnline(Home.this)){
                        IniciarNavegacion(sl, "w");
                    //}else{
                       // IniciarNavegacion(sl, "w-offline");
                      // Toast.makeText(Home.this, "Debe contar con conexion a internet para calcular la ruta a pie", Toast.LENGTH_LONG).show();
                    //}
                }
            });


            d.show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_FOTO && resultCode == RESULT_OK){
            btnGetLocation.setBackground(null);
            if(data.getSerializableExtra("data") != null && ((ArrayList<File>)data.getSerializableExtra("data")).size() >0 ){
                File f = ((ArrayList<File>)data.getParcelableExtra("data")).get(0);
                Picasso.with(Home.this).invalidate(f/*Funciones.getTempImageLocation(Home.this)*/);
                Funciones.setCircularImage(Home.this,f, btnGetLocation);
            }

        }
    }

    public Bitmap rotateBitmap(Bitmap b, int degrees){
        Matrix matrix = new Matrix();

        matrix.postRotate(degrees);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(b,b.getWidth(),b.getHeight(),true);

        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);
        return rotatedBitmap;
    }

    public void showNoLocationActivatedMessage(){
        llSearchingLocation.setVisibility(View.INVISIBLE);
        llMsgNoLocation.setVisibility(View.VISIBLE);
    }
    public void hideNoLocationActivatedMessage(){
        llSearchingLocation.setVisibility(View.VISIBLE);
        llMsgNoLocation.setVisibility(View.INVISIBLE);
    }


    ////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////    CONFIGURATIONS          ///////////////////////////////


    public void setDefaultConfigurationValues(){
        try {

            savedPreference  = DAL_Preferences.getInstance(Home.this).getSavedPreference();
            Calendar fechaSaved = Calendar.getInstance();
            fechaSaved.setTimeInMillis(savedPreference.getFechaALanzar());
            myCalendar = Calendar.getInstance();
            myCalendar.set(Calendar.HOUR_OF_DAY, fechaSaved.get(Calendar.HOUR_OF_DAY));
            myCalendar.set(Calendar.MINUTE, fechaSaved.get(Calendar.MINUTE));
            myCalendar.set(Calendar.SECOND, 0);
            if(android.text.format.DateFormat.is24HourFormat(Home.this)){
                textFormat = "HH:mm a";
            }else {
                textFormat = "hh:mm a";
            }
            SimpleDateFormat format = new SimpleDateFormat(textFormat);
            String textoHora = format.format(myCalendar.getTime());
            btnHoraDialog.setText(textoHora);

            cbActivarVibracion.setChecked((savedPreference.getVibrar()==1));
            cbActivarNotificaciones.setChecked((savedPreference.getactivarNotifiacion()==1));
            spnRecurrencia.setAdapter(getAdapterRecurrencia());
            moveToSavedPosition(savedPreference.getRecurrencia());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setTime(){

        TimePickerDialog tp = new TimePickerDialog(Home.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar myC = Calendar.getInstance();
                myC.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myC.set(Calendar.MINUTE, minute);
                myC.set(Calendar.SECOND, 0);
                myCalendar = myC;
                SimpleDateFormat format = new SimpleDateFormat(textFormat);
                String textoHora = format.format(myCalendar.getTime());
                btnHoraDialog.setText(textoHora);

                SimpleDateFormat xxx = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String horaActual = xxx.format(Calendar.getInstance().getTime());
                String horaSeleccionada = xxx.format(myCalendar.getTime());
                String x = horaActual+" - "+horaSeleccionada;
            }
        }, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), android.text.format.DateFormat.is24HourFormat(Home.this));

        tp.show();


    }
    public ArrayAdapter<CV> getAdapterRecurrencia(){
        ArrayList<CV> list = new ArrayList<>();
        list.add(new CV("1", getResources().getString(R.string.daily)));
        list.add(new CV("7", getResources().getString(R.string.weekly)));

        ArrayAdapter<CV> adapter = new ArrayAdapter<CV>(Home.this, android.R.layout.simple_list_item_1, list);
        return  adapter;
    }

    public void moveToSavedPosition(int value){
        for(int i=0; i<spnRecurrencia.getAdapter().getCount(); i++){
            int x = Integer.parseInt(((CV)spnRecurrencia.getAdapter().getItem(i)).getClave());
            if(value == x){
                spnRecurrencia.setSelection(i);
                break;
            }
        }

    }



    public void initializeAlarm(){
        try {
            if(DAL_Preferences.getInstance(Home.this).getSavedPreference() == null){//Preference Inicial
                DAL_Preferences.getInstance(Home.this).savePreference(new Preference());
                AlarmManager am = new AlarmManager(Home.this);
                Preference myPref = DAL_Preferences.getInstance(Home.this).getSavedPreference();
                Calendar myC = Calendar.getInstance();
                myC.setTimeInMillis(myPref.getFechaALanzar());
                am.setAlarm(myC);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void creacer(View v, int offset){
        ScaleAnimation animation = new ScaleAnimation(0f, 1f, 0f, 1f, v.getPivotX(), v.getPivotY());
        animation.setDuration(100);
        animation.setFillAfter(true);
        animation.setStartOffset(offset);
        v.startAnimation(animation);
    }
    public void esconder(View v, int offset, Animation.AnimationListener al){
        esconder(v,100,offset,al);
    }
    public void esconder(View v, int duracion,int offset, Animation.AnimationListener al){
        ScaleAnimation animation = new ScaleAnimation(1f, 0f, 1f, 0f, v.getPivotX(), v.getPivotY());
        animation.setDuration(duracion);
        animation.setFillAfter(true);
        animation.setStartOffset(offset);
        if(al != null){
            animation.setAnimationListener(al);
        }
        v.startAnimation(animation);
    }

    public void rebotar(View v){
        ScaleAnimation animation = new ScaleAnimation(1f, 0.7f, 1f, 0.7f, v.getPivotX(), v.getPivotY());
        animation.setDuration(100);
        v.startAnimation(animation);
    }

    public void hideMainOptions(){
        esconder(btnAceptar, 50, null);
        esconder(btnFoto, 100, null);
        esconder(btnCompartir, 150, null);
        esconder(btnCancelar, 200, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                llSeedOptions.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
