package com.far.gpseed;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.far.gpseed.Models.SeedLocation;
import com.far.gpseed.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class SeedMap extends FragmentActivity implements OnMapReadyCallback {

    MapFragment mMapFragment;
    GoogleMap mMap;
    SeedLocation actualLocation;
    SeedLocation seedLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seed_map);

        actualLocation = (SeedLocation) getIntent().getExtras().getSerializable("origen");
        seedLocation = (SeedLocation) getIntent().getExtras().getSerializable("seed");

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
       mMap = googleMap;

        LatLng currentLocation = new LatLng(actualLocation.Latitude, actualLocation.Longitude);
        LatLng destino = new LatLng(seedLocation.Latitude, seedLocation.Longitude);



        BitmapDescriptor mb = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.logo));
        mMap.addMarker(new MarkerOptions().position(destino).icon(mb).title(seedLocation.Description));

        mMap.addMarker(new MarkerOptions().position(currentLocation).title(actualLocation.Description));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

        Polyline pl =googleMap.addPolyline(
                new PolylineOptions()
                        .add(currentLocation,destino)
                        .color(Color.RED)
                        .width(5));

    }
}
