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
import com.naijab.pokemonear.network.RetrofitService;
import com.naijab.pokemonear.network.ServiceInterface;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.WeakHashMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

  public static final int ZOOM_LEVEL_SIZE = 15;
  public static final int RADIUS_METER = 2000;
  public static final int STROKE_WIDTH = 8;
  public static final int CIRCLE_FILL_COLOR = 0x3000ff00;

  private Double latitudeOrigin = 37.4219877;
  private Double longitudeOrigin = -122.0840578;
  private ArrayList<Integer> pokemonCount = new ArrayList<>();
  private WeakHashMap<String, Marker> hashMapMarker = new WeakHashMap<>();

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

    slidingLayout.addPanelSlideListener(onSlideListener());

    thread = new Thread() {
      public void run() {
        Log.d("Thread Run", "local Thread sleeping");
        getRandomLocation(latitudeOrigin, longitudeOrigin, RADIUS_METER);
        handler.postDelayed(this, getRandomTime());
      }
    };
  }

  private void bindView(View rootView) {
    mapView = (MapView) rootView.findViewById(R.id.map);
    pokemonText = (TextView) rootView.findViewById(R.id.sum_pokemon);
    slidingLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    LatLng position = new LatLng(latitudeOrigin, longitudeOrigin);
    mMap = googleMap;
    initMapsView();
    setCircle(position, RADIUS_METER);
    setMarkerCenter(position, userEmail);
    setCamera(position, ZOOM_LEVEL_SIZE);
  }

  private void setMarkerCenter(LatLng position, String userEmail) {
    MarkerOptions markerOptions = new MarkerOptions();
    markerOptions.title("Name: " + userEmail)
        .position(position);
    Marker locationMarker = mMap.addMarker(markerOptions);
    locationMarker.showInfoWindow();
  }

  private long getRandomTime() {
    Random r = new Random();
    long first = 3000;
    long last = 4000;
    return first + (long) (r.nextDouble() * (last - first));
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

  private void setMarker(LatLng position, String pokemonName, String pokemonNumber, String pokemonID) {

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
  }

  private void setCamera(LatLng position, int ZoomLevelSize) {
    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom((position), ZoomLevelSize);
    mMap.animateCamera(cameraUpdate);
  }

  private void getRandomLocation(Double latitude, Double longitude, int radiusInMeters) {

    double x0 = longitude;
    double y0 = latitude;

    Random random = new Random();

    double radiusInDegrees = radiusInMeters / 111320f;

    double u = random.nextDouble();
    double v = random.nextDouble();
    double w = radiusInDegrees * Math.sqrt(u);
    double t = 2 * Math.PI * v;
    double x = w * Math.cos(t);
    double y = w * Math.sin(t);

    double new_x = x / Math.cos(Math.toRadians(y0));

    double foundLatitude;
    double foundLongitude;

    foundLatitude = y0 + y;
    foundLongitude = x0 + new_x;

    getPokemonAtNear(userToken, Double.toString(foundLatitude), Double.toString(foundLongitude));
  }

  private void getPokemonAtNear(String token, String latitude, String longitude) {
    ServiceInterface apiService = RetrofitService.getRetrofit().create(ServiceInterface.class);
    Call<PokemonCatchableModel> checkServer = apiService.getPokemon(token, latitude, longitude);
    checkServer.enqueue(new Callback<PokemonCatchableModel>() {
      @Override
      public void onResponse(Call<PokemonCatchableModel> call,
          Response<PokemonCatchableModel> response) {
        if (response.isSuccessful()) {
          if (response.body().getMessage().equals("Success")) {

            List<PokemonDataModel> data = response.body().getData();

            Log.i("Pokemon", "Success: " + response.body());

            int pokemonInt = data.size();
            showPokemonSum(pokemonInt);

            for (int i = 0; i < data.size(); i++) {

              String pokemonName = data.get(i).getName();
              String pokemonNumber = data.get(i).getNumber();
              String pokemonID = data.get(i).getId();
              long pokemonLifeTime = data.get(i).getExpiration_timestamp();
              double mLatitude = data.get(i).getLatitude();
              double mLongitude = data.get(i).getLongitude();
              LatLng location = new LatLng(mLatitude, mLongitude);

              targetMarker = hashMapMarker.get(pokemonID);

              if (targetMarker == null) {
                setMarker(location, pokemonName, pokemonNumber, pokemonID);
                removeMarker(pokemonID, pokemonLifeTime);
                setCamera(location, ZOOM_LEVEL_SIZE);
              }else {
                removeMarker(pokemonID, pokemonLifeTime);
              }

              Log.i("Pokemon",
                    "id: " + data.get(i).getId() + "\n" +
                    "name: " + data.get(i).getName());

            }
          } else {
            Log.i("Pokemon", "message: " + response.body().getMessage());
          }
        } else {
          showToast("Server has error.");
          Log.i("Login fail", "Server has error");
        }
      }

      @Override
      public void onFailure(Call<PokemonCatchableModel> call, Throwable t) {
        showToast("Sorry service has error: " + t);
      }
    });
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

  private void removeMarker(String pokemonID, long pokemonLifeTime){

    final String pokemonInID = pokemonID;
    final long LifeTime = pokemonLifeTime;
    final long minutesLifeTime = (int) ((LifeTime / 10000000));

    targetMarker = hashMapMarker.get(pokemonID);

    if(targetMarker!=null){
      countDownTimer = new CountDownTimer(minutesLifeTime, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
          Log.i("PokemonLife", "Has life : "+ pokemonInID + "\n" +
                               "Life is:" + minutesLifeTime);
        }

        @Override
        public void onFinish() {
          targetMarker.remove();
          Log.i("PokemonLife", "" + pokemonInID +" Has Die 555+");
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
