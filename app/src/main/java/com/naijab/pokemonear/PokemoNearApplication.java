package com.naijab.pokemonear;

import android.app.Application;
import com.naijab.pokemonear.utility.Contextor;

/**
 * Created by Xiltron on 26/4/2560.
 */

public class PokemoNearApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    init();
  }

  private void init() {
    Contextor.init(getApplicationContext());
  }

  @Override
  public void onTerminate() {
    super.onTerminate();
  }
}
