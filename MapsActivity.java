package com.mehmeteraysurmeli.mymaps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;

    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mMap.clear(); // marker will exist again and previous versiyon is deleted from map
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        //Location permission
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10000,50,locationListener);
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LatLng userLastLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().title("Your location").position(userLastLocation));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastLocation,15));
        }

        mMap.setOnMapLongClickListener(this);



        // Add a marker in istanbul and move the camera
        LatLng istanbul = new LatLng(41.0507928,28.9805459);
        mMap.addMarker(new MarkerOptions().position(istanbul).title("Marker in Istanbul,Beşiktaş"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(istanbul,10));
    }

    //Location permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length > 0){
            if (requestCode == 1){
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = ""; // if user want see any address,can see address
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if (addressList != null && addressList.size() > 0)
                if (addressList.get(0).getThoroughfare() != null)
                    address += " Sokak : " + addressList.get(0).getThoroughfare();
                    if (addressList.get(0).getSubThoroughfare() != null)
                        address += " Number : " + addressList.get(0).getSubThoroughfare();
                        if (addressList.get(0).getCountryName() != null)
                            address += " Country : " + addressList.get(0).getCountryName();
                            if (addressList.get(0).getPhone() != null)
                                address += " Phone : " + addressList.get(0).getPhone();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (address.matches("")){
            address = "No address";
        }
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
    }
}
