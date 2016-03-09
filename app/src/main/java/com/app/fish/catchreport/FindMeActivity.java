package com.app.fish.catchreport;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteCursor;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class FindMeActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMyLocationButtonClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_FINE_LOCATION = 451;
    public static final String FISH_LAKES_DB = "FishAndLakes.db";

    private GoogleMap mMap;
    private View mLayout;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private float mZoom = 11;
    private LatLng mLatLng;
    private ArrayList<Lake> lakes;
    private Lake mClosestLake;
    private TextView mLakeNameTextView;
    private TextView mCountyTextView;
    private Marker mMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_me);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(mGoogleApiClient == null){
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mLayout = findViewById(R.id.map);

        this.lakes = buildLakeList();

        Button returnButton = (Button)findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("ClosestLake", mClosestLake);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

        mLakeNameTextView = (TextView)findViewById(R.id.lakeNameTextView);
        mCountyTextView = (TextView)findViewById(R.id.countyTextView);


    }

    private void findClosestLake(){

        double distance = Double.POSITIVE_INFINITY;
        Location loc = null;
        double d;

        for(Lake l : this.lakes){
            loc = new Location("");
            loc.setLatitude(l.getLat());
            loc.setLongitude(l.getLong());

            d = mLastLocation.distanceTo(loc);

            if(d<distance){
                mClosestLake = l;
                distance = d;
            }
        }

        mLakeNameTextView.setText(mClosestLake.getName());
        mCountyTextView.setText(mClosestLake.getCounty());
        LatLng lakeLatLng = new LatLng(mClosestLake.getLat(), mClosestLake.getLong());
        mMarker.setPosition(lakeLatLng);
    }

    @Override
    public void onMarkerDrag(Marker marker){ }

    @Override
    public void onMarkerDragEnd(Marker marker){
        this.mLastLocation = new Location("");
        this.mLastLocation.setLatitude(marker.getPosition().latitude);
        this.mLastLocation.setLongitude(marker.getPosition().longitude);

        findClosestLake();
    }

    @Override
    public void onMarkerDragStart(Marker marker){ }

    @Override
    protected void onStart(){
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();
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

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestFineLocationPermission();
        }else{
            mMap.setMyLocationEnabled(true);
            mMap.setOnMarkerDragListener(this);
            mMap.setOnMyLocationButtonClickListener(this);
        }
    }

    private void requestFineLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Snackbar.make(mLayout, "Permission is needed to access fine location", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(FindMeActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_FINE_LOCATION
                            );
                        }
                    })
                    .show();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        }
    }

    @Override
    public boolean onMyLocationButtonClick (){

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestFineLocationPermission();
        }else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        findClosestLake();

        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == REQUEST_FINE_LOCATION){
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Snackbar.make(mLayout, "Fine location permissions granted", Snackbar.LENGTH_SHORT).show();
            }else{
                Snackbar.make(mLayout, "Permissions were not granted", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestFineLocationPermission();
        }else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(mLastLocation != null){
                mLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMarker = mMap.addMarker(new MarkerOptions().position(mLatLng).draggable(true));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, mZoom));
                findClosestLake();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private ArrayList<Lake> buildLakeList(){
        ArrayList<Lake> lakes = new ArrayList<Lake>();
        DatabaseHandler db = new DatabaseHandler(this, FISH_LAKES_DB);
        db.openDatabase();
        SQLiteCursor cur;

        cur = db.runQuery("SELECT _id,WaterBodyName,County,Abbreviation,Latitude,Longitude FROM Lakes ", new String[0]);

        while(cur.moveToNext())
        {
            Lake lk = new Lake(cur.getInt(0),cur.getString(1),cur.getString(2),cur.getString(3), cur.getDouble(4), cur.getDouble(5));
            lakes.add(lk);
        }
        cur.close();
        db.close();
        return lakes;
    }

    /*
    @Override
    public void onBackPressed(){

    }*/

}
