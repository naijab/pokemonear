package com.naijab.pokemonear.utility;

import com.google.android.gms.maps.model.LatLng;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by Xiltron on 11/5/2560.
 */
public class RandomUtilityTest {


  @Test
  public void getTime_must_be_between_range() {
    long time = RandomUtility.getInstance().getTime(33, 44);

    System.out.println(time);
    Assert.assertTrue(time <= 44);
    Assert.assertTrue(time >= 33);

  }

  @Test
  public void get_location() {

    for (int i = 0; i < 100; i++) {

      double originLatitude = 37.4219877;
      double originLongitude = -122.0840578;
      int radiusMeter = 2000;

      LatLng location = RandomUtility.getInstance().getLocation(originLatitude, originLongitude, radiusMeter);
      double latitude = location.latitude;
      double longitude = location.longitude;

      Assert.assertTrue(latitude != originLatitude);
      Assert.assertTrue(longitude != originLongitude);

      System.out.println(location);
    }

  }
}