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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import java.util.ArrayList;
import java.util.HashMap;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

  public static final int ZOOM_LEVEL_SIZE = 15;
  public static final int RADIUS_METER = 2000;
  public static final int STROKE_WIDTH = 8;
  public static final int CIRCLE_FILL_COLOR = 0x3000ff00;

  private Double latitudeOrigin = 37.4219877;
  private Double longitudeOrigin = -122.0840578;
  private ArrayList<Integer> pokemonCount = new ArrayList<>();
  private HashMap<String, Marker> hashMapMarker = new HashMap<>();

  private long randomTime;
  private LatLng randomLocation;
  private double randomLatitude;
  private double randomLongitude;

  private MapView mapView;
  private GoogleMap mMap;
  private Marker targetMarker;
  private CountDownTimer countDownTimer;

  private String userEmail = "";
  private String userToken = "";

  private Thread thread;
  private Handler handler = new Handler();
  private BitmapDescriptor icon;

  private TextView pokemonText;
  private SlidingUpPanelLayout slidingLayout;

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

    randomTime = RandomUtility.getInstance().getTime(3000, 4000);

    slidingLayout.addPanelSlideListener(onSlideListener());

    thread = new Thread() {
      public void run() {
        Log.d("Thread Run", "local Thread sleeping");
        getRandomPokemon();
        handler.postDelayed(this, randomTime);
      }
    };
  }

  private void getRandomPokemon() {
    randomLocation = new RandomUtility().getInstance()
        .getLocation(latitudeOrigin, longitudeOrigin, RADIUS_METER);
    randomLatitude = randomLocation.latitude;
    randomLongitude = randomLocation.longitude;
    findPokemon(userToken, String.valueOf(randomLatitude), String.valueOf(randomLongitude));
  }

  private void bindView(View rootView) {
    mapView = (MapView) rootView.findViewById(R.id.map);
    pokemonText = (TextView) rootView.findViewById(R.id.sum_pokemon);
    slidingLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
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
            setMarker(pokemonLocation,
                pokemonName,
                pokemonNumber,
                pokemonID,
                pokemonLife);
            setCamera(pokemonLocation, ZOOM_LEVEL_SIZE);
            showPokemonSum(pokemonCount);

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
    LatLng position = new LatLng(latitudeOrigin, longitudeOrigin);
    mMap = googleMap;
    initMapsView();
    setCircle(position, RADIUS_METER);
    setMarkerCenter(position, userEmail);
    setCamera(position, ZOOM_LEVEL_SIZE);
    randomLocation = RandomUtility.getInstance()
        .getLocation(latitudeOrigin, longitudeOrigin, RADIUS_METER);

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

    final long LifeTime = pokemonLifeTime;
    final long minutesLifeTime = (int) ((LifeTime / 10000000));

    targetMarker = hashMapMarker.get(pokemonID);

    if (targetMarker != null) {
      countDownTimer = new CountDownTimer(minutesLifeTime, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
          Log.i("PokemonLife", "Has life : " + pokemonID + "\n" +
              "Name: " + pokemonName+"\n"+
              "Number: " + pokemonNumber+"\n"+
              "Life is: " + minutesLifeTime);
        }

        @Override
        public void onFinish() {
          targetMarker.remove();
          Log.i("PokemonLife", ": " + pokemonName +" "+ pokemonNumber + " Has Die.");
          LatLng position = new LatLng(latitudeOrigin, longitudeOrigin);
          setCamera(position, ZOOM_LEVEL_SIZE);
        }
      }.start();
    }


  }

  private SlidingUpPanelLayout.PanelSlideListener onSlideListener() {
    return new SlidingUpPanelLayout.PanelSlideListener() {
      @Override
      public void onPanelSlide(View view, float v) {

      }

      @Override
      public void onPanelStateChanged(View panel,
          SlidingUpPanelLayout.PanelState previousState,
          SlidingUpPanelLayout.PanelState newState) {

      }

    };
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

  @Override
  public void onResume() {
    super.onResume();
    mapView.onResume();

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
