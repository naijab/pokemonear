package com.naijab.pokemonear.utility;

import com.google.android.gms.maps.model.LatLng;
import java.util.Random;

public class RandomUtility {

  private static RandomUtility randomUtility;

  public static RandomUtility getInstance() {
    if (randomUtility == null) {
        randomUtility = new RandomUtility();
    }
    return randomUtility;
  }

  private LatLng foundPosition;

  public LatLng getLocation(
      double latitude,
      double longitude,
      int radiusInMeters){

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

    double realLatitude;
    double realLongitude;

    realLatitude = y0 + y;
    realLongitude = x0 + new_x;
    foundPosition = new LatLng(realLatitude, realLongitude);

    return foundPosition;
  }

  public long getTime(long first, long last){
    Random r = new Random();
    return first + (long) (r.nextDouble() * (last - first));
  }




}
