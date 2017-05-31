package com.naijab.pokemonear;

import android.app.Application;
import android.content.Context;

/**
 * Created by Xiltron on 26/4/2560.
 */
public class PokemoNearApplication extends Application {

  private static Context mContext;

  @Override
  public void onCreate() {
    super.onCreate();
    init();
  }

  private void init() {
    mContext = this;
  }

  public static Context getContext() {
    return mContext;
  }

  @Override
  public void onTerminate() {
    super.onTerminate();
  }
}
