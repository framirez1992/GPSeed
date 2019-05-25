package com.far.gpseed;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.far.gpseed.DAL.DAL_References;
import com.far.gpseed.Models.SeedLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.Serializable;
import java.util.UUID;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    Button btnRefresh, btnSave, btnBuscar, btnMap, btnDeleteAll;
    TextView tvLatitud, tvLongitud, tvResult;
    EditText etNombre, etLatitud, etLongitud;
    LocationCallback mLocationCallback;
    LocationCallback mSeedMapLocationCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.App, R.string.App);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    public void requestUpdates(LocationRequest mlocationRequest) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return;
        }
        LocationServices.getFusedLocationProviderClient(Main.this).requestLocationUpdates(mlocationRequest, mLocationCallback, null);
    }

    public void init(){
        mGoogleApiClient = new GoogleApiClient.Builder(Main.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        mLocationCallback =  new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null) {
                    tvLongitud.setText(Double.toString(locationResult.getLastLocation().getLongitude()));
                    tvLatitud.setText(Double.toString(locationResult.getLastLocation().getLatitude()));
                    LocationServices.getFusedLocationProviderClient(Main.this).removeLocationUpdates(mLocationCallback);

                } else {
                    Toast.makeText(Main.this, "Fallo buscando la geolocalizacion", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                if(!locationAvailability.isLocationAvailable()){
                    Toast.makeText(Main.this, "Geolocalizacion no disponible", Toast.LENGTH_LONG).show();
                }
            }
        };

        mSeedMapLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(Main.this).removeLocationUpdates(mSeedMapLocationCallback);
               // startMap(locationResult.getLastLocation());
               // lol(locationResult.getLastLocation());

                SeedLocation destino = new SeedLocation();
                destino.Latitude = Double.valueOf(etLatitud.getText().toString());
                destino.Longitude = Double.valueOf(etLongitud.getText().toString());
                destino.Description = "Seed";
                IniciarNavegacion(destino);
            }
        };

        btnRefresh = (Button)findViewById(R.id.btnRefresh);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnBuscar = (Button)findViewById(R.id.btnBuscar);
        btnMap = (Button)findViewById(R.id.btnMap);
        btnDeleteAll = (Button)findViewById(R.id.btnDeleteAll);
        etLatitud = (EditText)findViewById(R.id.etLatitud);
        etLongitud = (EditText)findViewById(R.id.etLongitud);

        tvLatitud = (TextView)findViewById(R.id.tvLatitud);
        tvLongitud = (TextView)findViewById(R.id.tvLongitud);
        tvResult = (TextView)findViewById(R.id.tvData);
        etNombre = (EditText)findViewById(R.id.etName);


        addListeners();
    }

    public void addListeners(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });

        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DAL_References.getInstance(Main.this).DeleteAll();
                    String x ="";
                    for(SeedLocation s: DAL_References.getInstance(Main.this).GetReferences()){
                        x+="LAT: "+s.Latitude+" - LON: "+s.Longitude+" \n"+s.Description+"\n\n";
                    }
                    tvResult.setText(x);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(Main.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Main.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                    return;
                }
                LocationRequest mlocationRequest = new LocationRequest();
                mlocationRequest.setInterval(30000);
                mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                LocationServices.getFusedLocationProviderClient(Main.this).requestLocationUpdates(mlocationRequest, mSeedMapLocationCallback, null);
            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    String x ="";
                    for(SeedLocation s: DAL_References.getInstance(Main.this).GetReferences()){
                        x+="LAT: "+s.Latitude+" - LON: "+s.Longitude+" \n"+s.Description+"\n\n";
                    }
                    tvResult.setText(x);
                }catch(Exception e){
                    Toast.makeText(Main.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

    btnRefresh.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (ActivityCompat.checkSelfPermission(Main.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
               Toast.makeText(Main.this, "Acepte los permisos e intente de nuevo", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(Main.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
                return;
            }
            try {
                LocationRequest mlocationRequest = new LocationRequest();
                mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mlocationRequest.setInterval(30000);// 30 segundos

                requestUpdates(mlocationRequest);

            }catch(Exception e){
                e.printStackTrace();
            }

        }
    });

    }

    public void guardar(){
        SeedLocation sl = new SeedLocation();
        sl.Id = UUID.randomUUID().toString();
        sl.Latitude = Double.parseDouble(tvLatitud.getText().toString());
        sl.Longitude = Double.parseDouble(tvLongitud.getText().toString());
        sl.Description = etNombre.getText().toString();

        try {
            DAL_References.getInstance(Main.this).Insert(sl);
            String x ="";
            for(SeedLocation s: DAL_References.getInstance(Main.this).GetReferences()){
                x+="LAT: "+s.Latitude+" - LON: "+s.Longitude+" \n"+s.Description+"\n\n";
            }
            tvResult.setText(x);
        }catch(Exception e){
            Toast.makeText(Main.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }


    public void startMap(Location origen){
        SeedLocation sl = new SeedLocation();
        sl.Latitude = origen.getLatitude();
        sl.Longitude = origen.getLongitude();
        sl.Description = "You";

        SeedLocation destino = new SeedLocation();
        destino.Latitude = 18.4814902;
        destino.Longitude = -69.9407157;
        destino.Description = "Seed";

        Intent i = new Intent(Main.this, SeedMap.class);
        i.putExtra("origen",  sl);
        i.putExtra("seed", destino);
        startActivity(i);
    }

    public void lol(Location origen){

        SeedLocation sl = new SeedLocation();
        sl.Latitude = origen.getLatitude();
        sl.Longitude = origen.getLongitude();
        sl.Description = "You";

        SeedLocation destino = new SeedLocation();
        destino.Latitude = 18.4814902;
        destino.Longitude = -69.9407157;
        destino.Description = "Seed";

        final Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(
                        "https://maps.google.com/maps?" +
                                "saddr="+sl.Latitude+","+sl.Longitude+"&daddr="+destino.Latitude+","+destino.Longitude));
        intent.setClassName(
                "com.google.android.apps.maps",
                "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    /**
     * inicia la navegacion de google maps en la ubicacion seleccionada
     * @param seed
     */
    public void IniciarNavegacion(SeedLocation destino){

        Uri gmmIntentUri = Uri.parse("google.navigation:q="+destino.Latitude+","+destino.Longitude+"&mode=w");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }else{
            Toast.makeText(Main.this, "Aun no tienes GoogleMap.", Toast.LENGTH_LONG).show();
        }


    }
}
