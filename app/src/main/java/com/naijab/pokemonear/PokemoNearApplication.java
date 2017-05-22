package com.naijab.pokemonear;

import android.app.Application;

/**
 * Created by Xiltron on 26/4/2560.
 */

// TODO พิมพ์ชื่อผิดแน่ะ -- แอพนี้ชื่อ PokemoNear ครับพิมพ์ถูกแล้ว
public class PokemoNearApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    init();
  }

  private void init() {

  }

  @Override
  public void onTerminate() {
    super.onTerminate();
  }
}
