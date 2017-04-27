package com.naijab.pokemonear.maps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.naijab.pokemonear.R;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    public static final int ZOOM_LEVEL_SIZE = 18;
    public static final int RADIUS_METER = 2000;
    public static final int MY_LOCATION_REQUEST_CODE = 66;
    public static final int STROKE_WIDTH = 8;
    public static final int CIRCLE_FILL_COLOR = 0x3000ff00;


    private MapView mapView;
    private GoogleMap mMap;
    private int n;


    public MapsFragment() {
        super();
    }

    public static MapsFragment newInstance() {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {

    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        mapView = (MapView) rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng position = new LatLng(13.744728, 100.56340);
        mMap = googleMap;
        initMapsView();
        setCircle(position, RADIUS_METER);
        setMarker(position);
        setCamera(position, ZOOM_LEVEL_SIZE);

        Random rand = new Random();
        n = rand.nextInt(50000) + 2;

        Timer time = new Timer();
        time.schedule(new TimerTask() {
            @Override
            public void run() {
                Random rand = new Random();
                int a = rand.nextInt(20000) + 2;
                getLatLngFromMyLocation(a);
            }
        },0, n);

    }

    private void getLatLngFromMyLocation(final int a) {

        Timer time = new Timer();
        time.schedule(new TimerTask() {
            @Override
            public void run() {
                Random rand = new Random();
                int f = rand.nextInt(99) + 1;
                Log.i("MapFragment","f = " + f);
                Log.i("MapFragment","a = " + a);
            }
        },0, a);
    }

    private void initMapsView() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(false);
        } else {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.setContentDescription("Him is me.");
        }

        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setBuildingsEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void setCircle(LatLng position, int RadiusMeter) {
        CircleOptions circleOptions = new CircleOptions()
                .center(position)
                .radius(RadiusMeter)
                .fillColor(CIRCLE_FILL_COLOR)
                .strokeColor(Color.RED).strokeWidth(STROKE_WIDTH);

        mMap.addCircle(circleOptions);
    }

    private void setMarker(LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .title("Center of World")
//                     .snippet("I am Ultron")
                .position(position);
        Marker locationMarker = mMap.addMarker(markerOptions);
        locationMarker.showInfoWindow();
    }

    private void setCamera(LatLng position, int ZoomLevelSize) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom((position), ZoomLevelSize);
        mMap.animateCamera(cameraUpdate);
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance (Fragment level's variables) State here
    }

}
