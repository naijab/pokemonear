package com.naijab.pokemonear.utility;

import android.content.Context;

/**
 * Created by Xiltron on 9/5/2560.
 */

public class Contextor {

  private static Context context;

  public static void init(Context context) {
    Contextor.context = context;
  }

  public static Context getContext() {
    return context;
  }


}
