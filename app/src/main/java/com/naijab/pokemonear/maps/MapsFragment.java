package com.naijab.pokemonear.maps;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.naijab.pokemonear.R;
import com.naijab.pokemonear.maps.pokemon.PokemonManager;
import com.naijab.pokemonear.maps.pokemon.PokemonManager.FindPokemonCallBack;
import com.naijab.pokemonear.utility.RandomUtility;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    public static final int ZOOM_LEVEL_SIZE = 15;
    public static final int RADIUS_METER = 2000;
    public static final int STROKE_WIDTH = 8;
    // TODO ทำเป็น Color Resource แทน
    public static final int CIRCLE_FILL_COLOR = 0x3000ff00;
    private static final double LATITUDE_ORIGIN = 37.4219877;
    private static final double LONGITUDE_ORIGIN = -122.0840578;

    private MapView mapView;
    private TextView pokemonText;

    // TODO ไม่ Save/Restore Instance State?
    // TODO ไม่ต้องใส่ "" ก็ได้ สุดท้ายมันก็เป็น null เพราะเรากำหนดไว้ใน init
    private String userEmail = "";
    private String userToken = "";

    private ArrayList<Integer> pokemonCount = new ArrayList<>();
    private HashMap<String, Marker> hashMapMarker = new HashMap<>();

    private long randomTime;
    private LatLng randomLocation;
    private double randomLatitude;
    private double randomLongitude;

    private GoogleMap mMap;
    private Marker targetMarker;
    private BitmapDescriptor icon;

    private CountDownTimer countDownTimer;
    private Thread thread;
    private Handler handler = new Handler();

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

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
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
        // TODO ไม่ควร Hardcode String แบบนี้ จริงๆทำเป็น SharedPreferenceManager แยกไปต่างหาเลยจะดีกว่า
        SharedPreferences prefs = getActivity()
                .getSharedPreferences("prefs_user", Context.MODE_PRIVATE);
        userEmail = prefs.getString("email", null);
        userToken = prefs.getString("token", null);
    }


    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        bindView(rootView);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // TODO อย่างนี้มันจะเรียกหลังจาก Map Ready นะ
        // TODO เพราะอาจจะเกิดกรณีที่บางเครื่องยัง Init Maps ไม่เสร็จ แต่ Thread ตัวนี้ดันทำงานเสร็จก่อน
        randomTime = RandomUtility.getInstance().getTime(3000, 4000);
        thread = new Thread() {
            public void run() {
                Log.d("Thread Run", "local Thread sleeping");
                getRandomPokemon();
                handler.postDelayed(this, randomTime);
            }
        };
    }

    private void getRandomPokemon() {
        randomLocation = RandomUtility.getInstance()
                .getLocation(LATITUDE_ORIGIN, LONGITUDE_ORIGIN, RADIUS_METER);
        randomLatitude = randomLocation.latitude;
        randomLongitude = randomLocation.longitude;
        findPokemon(userToken, String.valueOf(randomLatitude), String.valueOf(randomLongitude));
    }

    private void bindView(View rootView) {
        mapView = (MapView) rootView.findViewById(R.id.map);
        pokemonText = (TextView) rootView.findViewById(R.id.sum_pokemon);
    }

    private void findPokemon(String token,
                             String latitude,
                             String longitude) {
        PokemonManager.getInstance().findRandomPokemon(token,
                latitude,
                longitude,
                new FindPokemonCallBack() {
                    @Override
                    public void onDetectPokemon(String pokemonID, String pokemonName, String pokemonNumber,
                                                double pokemonLatitude, double pokemonLongitude, long pokemonLife, int pokemonCount) {
                        LatLng pokemonLocation = new LatLng(pokemonLatitude, pokemonLongitude);

                        targetMarker = hashMapMarker.get(pokemonID);
                        if (targetMarker == null) {
                            setMarker(pokemonLocation,
                                    pokemonName,
                                    pokemonNumber,
                                    pokemonID,
                                    pokemonLife);
                            setCamera(pokemonLocation, ZOOM_LEVEL_SIZE);
                            showPokemonSum(pokemonCount);
                        }
                    }

                    @Override
                    public void onDetectPokemonFail(String message) {
                        showToast(message);
                    }

                    @Override
                    public void onServerError(String errorMassage) {
                        showToast(errorMassage);
                    }
                });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng position = new LatLng(LATITUDE_ORIGIN, LONGITUDE_ORIGIN);
        mMap = googleMap;
        initMapsView();
        setCircle(position, RADIUS_METER);
        setMarkerCenter(position, userEmail);
        setCamera(position, ZOOM_LEVEL_SIZE);
        randomLocation = RandomUtility.getInstance()
                .getLocation(LATITUDE_ORIGIN, LONGITUDE_ORIGIN, RADIUS_METER);

    }

    private void setMarkerCenter(LatLng position, String userEmail) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("Name: " + userEmail)
                .position(position);
        Marker locationMarker = mMap.addMarker(markerOptions);
        locationMarker.showInfoWindow();
    }

    private void initMapsView() {
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
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

    private void setMarker(LatLng position, String pokemonName, String pokemonNumber,
                           String pokemonID, long pokemonLife) {

        // TODO ทำเป็นคลาส Utility ไปเลย
        try {
            int id = getResources().getIdentifier("ic_pokemon_no_" + pokemonNumber,
                    "drawable", getActivity().getPackageName());
            icon = BitmapDescriptorFactory.fromResource(id);
        } catch (IllegalArgumentException e) {
            Log.e("icon: ", "" + e);
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("Name: " + pokemonName)
                .snippet("Number: " + pokemonNumber)
                .position(position)
                .icon(icon);
        Marker locationMarker = mMap.addMarker(markerOptions);
        hashMapMarker.put(pokemonID, locationMarker);
        locationMarker.showInfoWindow();
        removeMarker(pokemonID, pokemonName, pokemonNumber, pokemonLife);
    }

    private void setCamera(LatLng position, int ZoomLevelSize) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom((position), ZoomLevelSize);
        mMap.animateCamera(cameraUpdate);
    }

    private void showToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    // TODO ไม่ควรใช้ตัวแปร X เพราะมาอ่านทีหลังแล้วไม่รู้เรื่องว่า x คือค่าอะไร
    private void showPokemonSum(int x) {
        pokemonCount.add(x);
        int sum = 0;
        for (int i = 0; i < pokemonCount.size(); i++) {
            sum += pokemonCount.get(i);
        }

        Log.i("Pokemon count", "data: " + sum);

        setPokemonSum(sum);
    }

    private void setPokemonSum(int pokemonSum) {
        if (pokemonSum != 0) {
            pokemonText.setText(String.valueOf(pokemonSum));
        }
    }

    private void removeMarker(final String pokemonID,
                              final String pokemonName,
                              final String pokemonNumber,
                              final long pokemonLifeTime) {

        // TODO ตัวนี้ไม่ต้อง Final และก็ไม่ได้เอาไปทำอะไรอยู่แล้ว ไม่ต้องสร้างเป็นตัวแปรใหม่แบบนี้ก็ได้
        final long LifeTime = pokemonLifeTime;
        // TODO ทำแบบนี้จะได้อ่านง่ายๆ
//        private static final long SECOND = 1000;
//        final long minutesLifeTime = (int) ((LifeTime / 10000 * SECOND));
        final long minutesLifeTime = (int) ((LifeTime / 10000000));

        targetMarker = hashMapMarker.get(pokemonID);

        if (targetMarker != null) {
            // TODO ทำแบบนี้จะได้อ่านง่ายๆ
//            countDownTimer = new CountDownTimer(minutesLifeTime, 1 * SECOND) {
            countDownTimer = new CountDownTimer(minutesLifeTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.i("PokemonLife", "Has life : " + pokemonID + "\n" +
                            "Name: " + pokemonName + "\n" +
                            "Number: " + pokemonNumber + "\n" +
                            "Life is: " + minutesLifeTime);
                }

                @Override
                public void onFinish() {
                    targetMarker.remove();
                    Log.i("PokemonLife", ": " + pokemonName + " " + pokemonNumber + " Has Die.");
                    LatLng position = new LatLng(LATITUDE_ORIGIN, LONGITUDE_ORIGIN);
                    setCamera(position, ZOOM_LEVEL_SIZE);
                }
            }.start();
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        // TODO Remove ไปตอน onPause แล้วไม่ใช่หรอ?
        handler.removeCallbacks(thread);
        handler.postDelayed(thread, 0);
        Log.d("Thread Run", "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

        handler.removeCallbacks(thread);
        Log.d("Thread Run", "onPause");
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

}
